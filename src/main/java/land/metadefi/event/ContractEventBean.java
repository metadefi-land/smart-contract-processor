package land.metadefi.event;

import io.quarkus.runtime.annotations.RegisterForReflection;
import land.metadefi.entity.ContractEventEntity;
import land.metadefi.mapper.ContractEventMapper;
import land.metadefi.model.BlockEvent;
import land.metadefi.model.ContractEvent;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Slf4j
@Named("contractEventBean")
@RegisterForReflection
@ApplicationScoped
public class ContractEventBean {

    public void blockEvent(BlockEvent event) {
        log.info("ID: {}", event.getId());
        log.info("Type: {}", event.getType());
    }

    public void counterUpdatedEvent(ContractEvent event) {
        log.info("Route: {}", event.getId());
        log.info("Route: {}", event.getType());

        ContractEventEntity entity = ContractEventMapper.INSTANCE.toEntity(event.getDetails());
        entity.persist();
    }
}
