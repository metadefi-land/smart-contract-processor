package land.metadefi.event;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.quarkus.runtime.annotations.RegisterForReflection;
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
        JsonObject parameter = event.getDetails().getNonIndexedParameters().get(0);
        JsonElement type = parameter.get("type");
        JsonElement value = parameter.get("value");
        log.info("Parameter 1: {}", type.getAsString());
        log.info("Parameter 2: {}", value.getAsInt());
    }
}
