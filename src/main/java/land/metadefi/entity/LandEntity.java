package land.metadefi.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.sql.Timestamp;

@Getter
@Setter
@MongoEntity(collection = "lands")
public class LandEntity extends PanacheMongoEntity {
    BigInteger tokenId;
    String title;
    Integer attribute;
    String contractAddress;
    String image;
    Integer status;
    Double value;
    Timestamp createdAt;
    Timestamp updatedAt;
}
