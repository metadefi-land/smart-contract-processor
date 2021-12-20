package land.metadefi.model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BlockEventDetail {
    Integer number;
    String hash;
    Integer timestamp;
    String nodeName;
}
