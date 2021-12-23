package land.metadefi.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@MongoEntity(collection = "users")
public class UserEntity extends PanacheMongoEntity {
    String gameId;
    String username;
    String password;
    String telegramId;
    String twitter;
    String discord;
    String email;
    String address;
    Integer status;
    Timestamp createdAt;
    Timestamp updatedAt;
}
