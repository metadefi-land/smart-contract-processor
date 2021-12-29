package land.metadefi.model.redis;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenLocker {
    String tokenId;
    Boolean isLocked;
}
