package land.metadefi.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MongoEntity(collection = "heroes")
public class HeroEntity extends NFTEntity {

}
