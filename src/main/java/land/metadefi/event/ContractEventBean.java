package land.metadefi.event;

import io.quarkus.redis.client.RedisClient;
import io.quarkus.redis.client.reactive.ReactiveRedisClient;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.vertx.redis.client.RedisAPI;
import land.metadefi.ChainConfig;
import land.metadefi.entity.ContractEventEntity;
import land.metadefi.entity.LandEntity;
import land.metadefi.entity.MintHistoryEntity;
import land.metadefi.error.*;
import land.metadefi.mapper.ContractEventMapper;
import land.metadefi.mapper.MintMapper;
import land.metadefi.model.BlockEvent;
import land.metadefi.model.ContractEvent;
import land.metadefi.model.MintNFT;
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
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

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
    RedisClient redisClient;

    @Inject
    RedisAPI redisApi;

    @Inject
    ReactiveRedisClient reactiveRedisClient;

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

    public void testEvent(String event) throws ExecutionException, InterruptedException {
        log.info("event: {}", event);
        String address = "0x9B2Bb6290fb910a960Ec344cDf2ae60ba89647f6";
        String txnHash = "0x5cb48f8bd1ee56d8898c005e1ea5d26e7fcebf3a2bd3993e01d857f50bf73ad7";
        String amount = "1021550060411908747500000";
        BigInteger tokenId = BigInteger.valueOf(10000);
        MintNFT mintNFT = new MintNFT();
        mintNFT.setTokenId(tokenId);
        mintNFT.setAddress(address);
        mintNFT.setAmount(amount);
        mintNFT.setTxnHash(txnHash);

        // Lock NFT with token ID
        redisClient.set(Arrays.asList(tokenId.toString(), Boolean.TRUE.toString()));

        FtmScanResponse ftmScanResponse = ftmScanClient.listNormalTransaction(chainConfig.apiKey(),
            mintNFT.getAddress(),
            chainConfig.startBlock(),
            chainConfig.endBlock(),
            chainConfig.module(),
            chainConfig.action(),
            chainConfig.sort(),
            chainConfig.page(),
            chainConfig.offset()
        );
        if (ftmScanResponse.getResult().isEmpty()) {
            clear(mintNFT);
            throw new TransactionNotFoundException();
        }

        ChainTransaction ct = ftmScanResponse.getResult().parallelStream().filter(i -> i.getHash().contentEquals(mintNFT.getTxnHash())).findFirst().orElseThrow(TransactionNotFoundException::new);
        validateChainTransaction(mintNFT, ct);

        // Check txnHash in history
        MintHistoryEntity mintHistory = MintHistoryEntity.find("txnHash", mintNFT.getTxnHash()).firstResult();
        if (Objects.nonNull(mintHistory)) {
            clear(mintNFT);
            throw new TransactionExistException();
        } else {
            // Add to Mint History
            mintHistory = MintMapper.INSTANCE.toEntity(mintNFT);
            mintHistory.persist();
        }

        // TODO: Send mint message to queue `contract-processor`

        // Remove lock NFT with token ID
        clear(mintNFT);
    }

    void validateChainTransaction(MintNFT mintNFT, ChainTransaction ct) {
        // Validate txnHash
        if (!ct.getFrom().contentEquals(mintNFT.getAddress())) {
            clear(mintNFT);
            throw new TransactionFromInvalidException();
        }
        if (!ct.getTo().contentEquals(chainConfig.contractAddress())) {
            clear(mintNFT);
            throw new TransactionToInvalidException();
        }

        // Validate transaction created time
        long epoch = System.currentTimeMillis() / 1000;
        if ((epoch - Long.parseLong(ct.getTimeStamp())) > MAX_EXPIRED_TIME) {
            clear(mintNFT);
            throw new TransactionExpiredException("Txn hash: " + mintNFT.getTxnHash());
        }

        // TODO: Check compare value function
        // Validate value
        LandEntity landEntity = LandEntity.find("tokenId", mintNFT.getTokenId()).firstResult();
        if (Objects.isNull(landEntity)) {
            clear(mintNFT);
            throw new TokenIdInvalidException("TokenID invalid: " + mintNFT.getTokenId());
        }
        if (!BalanceUtils.compareValue(BalanceUtils.weiToEther(ct.getValue()),
            BigDecimal.valueOf(landEntity.getValue()))) {
            clear(mintNFT);
            throw new ValueNotEqualException();
        }
    }

    void saveContractEvent(ContractEvent event) {
        ContractEventEntity contractEventEntity = ContractEventMapper.INSTANCE.toEntity(event.getDetails());
        contractEventEntity.persist();
    }

    void clear(MintNFT mintToken) {
        redisClient.del(Arrays.asList(mintToken.getTokenId().toString(), Boolean.TRUE.toString()));
    }
}
