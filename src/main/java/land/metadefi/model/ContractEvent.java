package land.metadefi.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractEvent {
    String id;
    String type;
    ContractEventDetail details;
    Integer retries;
}
