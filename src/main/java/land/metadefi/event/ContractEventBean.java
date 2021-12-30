package land.metadefi.event;

import io.quarkus.redis.client.RedisClient;
import io.quarkus.runtime.annotations.RegisterForReflection;
import land.metadefi.ChainConfig;
import land.metadefi.entity.ContractEventEntity;
import land.metadefi.entity.LandEntity;
import land.metadefi.error.TokenIdInvalidException;
import land.metadefi.mapper.ContractEventMapper;
import land.metadefi.model.BlockEvent;
import land.metadefi.model.ContractEvent;
import land.metadefi.rest.FtmScanClient;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigInteger;
import java.util.Objects;

@Slf4j
@Named("contractBean")
@RegisterForReflection
@ApplicationScoped
public class ContractEventBean {

    @Inject
    @RestClient
    FtmScanClient ftmScanClient;

    @Inject
    RedisClient redisClient;

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

    void saveContractEvent(ContractEvent event) {
        ContractEventEntity contractEventEntity = ContractEventMapper.INSTANCE.toEntity(event.getDetails());
        contractEventEntity.persist();
    }
}
