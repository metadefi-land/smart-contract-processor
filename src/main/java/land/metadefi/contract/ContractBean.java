package land.metadefi.contract;

import io.quarkus.redis.client.RedisClient;
import io.quarkus.runtime.annotations.RegisterForReflection;
import land.metadefi.ChainConfig;
import land.metadefi.entity.*;
import land.metadefi.enumrable.MintStatus;
import land.metadefi.enumrable.NFTType;
import land.metadefi.error.*;
import land.metadefi.mapper.ContractEventMapper;
import land.metadefi.mapper.MintMapper;
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
import java.util.Arrays;
import java.util.Objects;

@Slf4j
@Named("contractBean")
@RegisterForReflection
@ApplicationScoped
public class ContractBean {

    @Inject
    @RestClient
    FtmScanClient ftmScanClient;

    @Inject
    RedisClient redisClient;

    @Inject
    ChainConfig chainConfig;

    public void mintNFT(MintNFT mintNFT) {
//        BigInteger tokenId = BigInteger.valueOf(10000);

        // Lock NFT with token ID
        redisClient.set(Arrays.asList(mintNFT.getTokenId().toString(), Boolean.TRUE.toString()));

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
        }
        // Add to Mint History
        mintHistory = MintMapper.INSTANCE.toEntity(mintNFT);
        mintHistory.setStatus(MintStatus.PENDING.getStatus());
        mintHistory.persist();

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
        if ((epoch - Long.parseLong(ct.getTimeStamp())) > chainConfig.maxExpiredTime()) {
            clear(mintNFT);
            throw new TransactionExpiredException("Txn hash: " + mintNFT.getTxnHash());
        }

        // TODO: Check compare value function
        // Validate value
        NFTEntity nftEntity = getEntityByType(mintNFT);
        if (Objects.isNull(nftEntity)) {
            clear(mintNFT);
            throw new TokenIdInvalidException("TokenID invalid: " + mintNFT.getTokenId());
        }
        if (!BalanceUtils.compareValue(BalanceUtils.weiToEther(ct.getValue()),
            BigDecimal.valueOf(nftEntity.getValue()))) {
            clear(mintNFT);
            throw new ValueNotEqualException();
        }
    }

    NFTEntity getEntityByType(MintNFT mintNFT) {
        if (mintNFT.getType().contentEquals(NFTType.LAND.getName()))
            return LandEntity.find("tokenId", mintNFT.getTokenId()).firstResult();
        if (mintNFT.getType().contentEquals(NFTType.HERO.getName()))
            return HeroEntity.find("tokenId", mintNFT.getTokenId()).firstResult();

        throw new NFTTypeInvalidException();
    }

    void saveContractEvent(ContractEvent event) {
        ContractEventEntity contractEventEntity = ContractEventMapper.INSTANCE.toEntity(event.getDetails());
        contractEventEntity.persist();
    }

    void clear(MintNFT mintToken) {
        redisClient.del(Arrays.asList(mintToken.getTokenId().toString(), Boolean.TRUE.toString()));
    }
}
