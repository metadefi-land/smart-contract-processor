package land.metadefi.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RegisterForReflection
public class ContractEvent {
    String id;
    String type;
    ContractEventDetail details;
    Integer retries;
}
