package land.metadefi.model.rest;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RegisterForReflection
public class FtmScanResponse {
    String status;
    String message;
    List<ChainTransaction> result;
}
