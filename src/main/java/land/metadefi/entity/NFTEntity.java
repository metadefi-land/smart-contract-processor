package land.metadefi.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.sql.Timestamp;

@Getter
@Setter
public abstract class NFTEntity extends PanacheMongoEntity {
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
