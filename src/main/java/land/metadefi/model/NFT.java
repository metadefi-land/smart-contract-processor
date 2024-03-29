package land.metadefi.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@RegisterForReflection
public class NFT {
    String type;
    String symbol;
    String address;
    String txnHash;
    BigInteger tokenId;
}
