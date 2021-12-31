package land.metadefi.contract;

import com.google.gson.Gson;
import io.quarkus.redis.client.RedisClient;
import io.quarkus.runtime.annotations.RegisterForReflection;
import land.metadefi.ChainConfig;
import land.metadefi.camel.MintNFTConverter;
import land.metadefi.entity.*;
import land.metadefi.enumrable.MintStatus;
import land.metadefi.enumrable.NFTType;
import land.metadefi.error.*;
import land.metadefi.mapper.ContractEventMapper;
import land.metadefi.mapper.MintMapper;
import land.metadefi.model.ContractEvent;
import land.metadefi.model.NFT;
import land.metadefi.model.rest.ChainTransaction;
import land.metadefi.model.rest.FtmScanResponse;
import land.metadefi.rest.FtmScanClient;
import land.metadefi.utils.BalanceUtils;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
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

    public InputStream mintNFT(NFT nft) {
        // Lock nft with token ID
        redisClient.set(Arrays.asList(nft.getTokenId().toString(), Boolean.TRUE.toString()));

        FtmScanResponse ftmScanResponse = ftmScanClient.listNormalTransaction(chainConfig.apiKey(),
            nft.getAddress(),
            chainConfig.startBlock(),
            chainConfig.endBlock(),
            chainConfig.module(),
            chainConfig.action(),
            chainConfig.sort(),
            chainConfig.page(),
            chainConfig.offset()
        );
        if (ftmScanResponse.getResult().isEmpty()) {
            clear(nft);
            throw new TransactionNotFoundException();
        }

        ChainTransaction ct = ftmScanResponse.getResult().parallelStream().filter(i -> i.getHash().contentEquals(nft.getTxnHash())).findFirst().orElseThrow(TransactionNotFoundException::new);
        validateChainTransaction(nft, ct);

        // Check txnHash in history
        MintHistoryEntity mintHistory = MintHistoryEntity.find("txnHash", nft.getTxnHash()).firstResult();
        if (Objects.nonNull(mintHistory)) {
            clear(nft);
            throw new TransactionExistException();
        }
        // Add to Mint History
        mintHistory = MintMapper.INSTANCE.toEntity(nft);
        mintHistory.setStatus(MintStatus.PENDING.getStatus());
        mintHistory.persist();

        // TODO: Send mint message to queue `contract-processor`

        // Remove lock nft with token ID
        clear(nft);

        // TODO: Replace with Camel TypeConverter in the feature
        return MintNFTConverter.convertToInputStream(nft);
    }

    public InputStream test(NFT NFT) {
        String json = new Gson().toJson(NFT);
        return new ByteArrayInputStream(json.getBytes());
    }

    void validateChainTransaction(NFT nft, ChainTransaction ct) {
        // Validate txnHash
        if (!ct.getFrom().contentEquals(nft.getAddress())) {
            clear(nft);
            throw new TransactionFromInvalidException();
        }
        if (!ct.getTo().contentEquals(chainConfig.contractAddress())) {
            clear(nft);
            throw new TransactionToInvalidException();
        }

        // Validate transaction created time
        long epoch = System.currentTimeMillis() / 1000;
        if ((epoch - Long.parseLong(ct.getTimeStamp())) > chainConfig.maxExpiredTime()) {
            clear(nft);
            throw new TransactionExpiredException("Txn hash: " + nft.getTxnHash());
        }

        // TODO: Check compare value function
        // Validate value
        NFTEntity nftEntity = getEntityByType(nft);
        if (Objects.isNull(nftEntity)) {
            clear(nft);
            throw new TokenIdInvalidException("TokenID invalid: " + nft.getTokenId());
        }
        if (!BalanceUtils.compareValue(BalanceUtils.weiToEther(ct.getValue()),
            BigDecimal.valueOf(nftEntity.getValue()))) {
            clear(nft);
            throw new ValueNotEqualException();
        }
    }

    NFTEntity getEntityByType(NFT nft) {
        if (nft.getType().contentEquals(NFTType.LAND.getName()))
            return LandEntity.find("tokenId", nft.getTokenId()).firstResult();
        if (nft.getType().contentEquals(NFTType.HERO.getName()))
            return HeroEntity.find("tokenId", nft.getTokenId()).firstResult();

        throw new NFTTypeInvalidException();
    }

    void saveContractEvent(ContractEvent event) {
        ContractEventEntity contractEventEntity = ContractEventMapper.INSTANCE.toEntity(event.getDetails());
        contractEventEntity.persist();
    }

    void clear(NFT mintToken) {
        redisClient.del(Arrays.asList(mintToken.getTokenId().toString(), Boolean.TRUE.toString()));
    }
}
