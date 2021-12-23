package land.metadefi.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@RegisterForReflection
public class MintToken {
    String address;
    String txnHash;
    String amount;
    BigInteger tokenId;
}
