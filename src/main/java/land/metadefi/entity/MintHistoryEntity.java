package land.metadefi.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@MongoEntity(collection = "mint-histories")
public class MintHistoryEntity extends PanacheMongoEntity {
    String address;
    String txnHash;
    String amount;
    BigInteger tokenId;
}
