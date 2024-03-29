package land.metadefi.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContractEventDetail {
    String name;
    String filterId;
    String nodeName;
    List<ContractEventParameter> indexedParameters;
    List<ContractEventParameter> nonIndexedParameters;
    String transactionHash;
    Integer logIndex;
    Long blockNumber;
    String blockHash;
    String address;
    String status;
    String eventSpecificationSignature;
    String networkName;
    Integer timestamp;
    String id;
}
