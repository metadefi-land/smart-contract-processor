package land.metadefi.event;

import io.quarkus.runtime.annotations.RegisterForReflection;
import land.metadefi.ChainConfig;
import land.metadefi.entity.ContractEventEntity;
import land.metadefi.entity.LandEntity;
import land.metadefi.entity.MintHistoryEntity;
import land.metadefi.error.*;
import land.metadefi.mapper.ContractEventMapper;
import land.metadefi.mapper.MintMapper;
import land.metadefi.model.BlockEvent;
import land.metadefi.model.ContractEvent;
import land.metadefi.model.MintToken;
import land.metadefi.model.rest.ChainTransaction;
import land.metadefi.model.rest.FtmScanResponse;
import land.metadefi.rest.FtmScanClient;
import land.metadefi.utils.BalanceUtils;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

@Slf4j
@Named("contractEventBean")
@RegisterForReflection
@ApplicationScoped
public class ContractEventBean {

    static final Integer MAX_EXPIRED_TIME = 1800;

    @Inject
    @RestClient
    FtmScanClient ftmScanClient;

    @Inject
    ChainConfig chainConfig;

    public void blockEvent(BlockEvent event) {
        log.info("ID: {}", event.getId());
        log.info("Type: {}", event.getType());
    }

    public void mintLandEvent(ContractEvent event) {
        // TODO: Update contract address and token ID
        log.info("Route: {}", event.getId());
        log.info("Route: {}", event.getType());

        BigInteger tokenId = BigInteger.valueOf(1111111111);
        String contractAddress = "";

        saveContractEvent(event);

        LandEntity landEntity = LandEntity.find("tokenId", tokenId).firstResult();
        if (Objects.isNull(landEntity))
            throw new TokenIdInvalidException("TokenID invalid: " + tokenId);

        log.info("Update contract address [{}] for token ID [{}]", contractAddress, tokenId);
        landEntity.setContractAddress(contractAddress);
        landEntity.update();
    }

    public void mintHeroEvent(ContractEvent event) {

    }

    public void testEvent(String event) {
        log.info("event: {}", event);
        String address = "0x9B2Bb6290fb910a960Ec344cDf2ae60ba89647f6";
        String txnHash = "0x5cb48f8bd1ee56d8898c005e1ea5d26e7fcebf3a2bd3993e01d857f50bf73ad7";
        String amount = "1021550060411908747500000";
        MintToken mintToken = new MintToken();
        mintToken.setAddress(address);
        mintToken.setAmount(amount);
        mintToken.setTxnHash(txnHash);
        FtmScanResponse result = ftmScanClient.listNormalTransaction(chainConfig.apiKey(), mintToken.getAddress(), chainConfig.startBlock(), chainConfig.endBlock(), chainConfig.module(), chainConfig.action(), "desc");
        if (result.getResult().isEmpty())
            throw new TransactionNotFoundException();

        // Validate txnHash
        ChainTransaction ct = result.getResult().parallelStream().filter(i -> i.getHash().contentEquals(mintToken.getTxnHash())).findFirst().orElseThrow(TransactionNotFoundException::new);
        if (!ct.getFrom().contentEquals(address))
            throw new TransactionFromInvalidException();
        if (!ct.getTo().contentEquals(chainConfig.contractAddress()))
            throw new TransactionToInvalidException();

        // Validate transaction created time
        long epoch = System.currentTimeMillis() / 1000;
        if ((epoch - Long.parseLong(ct.getTimeStamp())) > MAX_EXPIRED_TIME)
            throw new TransactionExpiredException("Txn hash: " + txnHash);

        // TODO: Check compare value function
        // Validate value
        LandEntity landEntity = LandEntity.find("tokenId", mintToken.getTokenId()).firstResult();
        if (Objects.isNull(landEntity))
            throw new TokenIdInvalidException("TokenID invalid: " + mintToken.getTokenId());
        if (!BalanceUtils.compareValue(BalanceUtils.weiToEther(ct.getValue()),
            BigDecimal.valueOf(landEntity.getValue())))
            throw new ValueNotEqualException();

        // Check txnHash in history
        MintHistoryEntity mintHistory = MintHistoryEntity.find("txnHash", mintToken.getTxnHash()).firstResult();
        if (Objects.nonNull(mintHistory))
            throw new TransactionExistException();
        else {
            // Add to Mint History
            mintHistory = MintMapper.INSTANCE.toEntity(mintToken);
            mintHistory.persist();
        }

        // TODO: Connect to chain and mint token

    }

    void saveContractEvent(ContractEvent event) {
        ContractEventEntity contractEventEntity = ContractEventMapper.INSTANCE.toEntity(event.getDetails());
        contractEventEntity.persist();
    }
}
