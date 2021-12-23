package land.metadefi.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RegisterForReflection
public class BlockEvent {
    String id;
    String type;
    BlockEventDetail details;
    Integer retries;
}
