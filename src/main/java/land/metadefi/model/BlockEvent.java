package land.metadefi.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockEvent {
    String id;
    String type;
    BlockEventDetail details;
    Integer retries;
}
