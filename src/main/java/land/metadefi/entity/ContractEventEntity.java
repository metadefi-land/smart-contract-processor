package land.metadefi.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import land.metadefi.model.ContractEventParameter;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@Setter
@MongoEntity(collection = "contract-events")
public class ContractEventEntity extends PanacheMongoEntity {
    ObjectId id;
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
    String eventId;
}
