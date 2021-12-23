package land.metadefi;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "chain")
public interface ChainConfig {

    @WithName("api.key")
    String apiKey();

    @WithName("api.module")
    String module();

    @WithName("api.action")
    String action();

    @WithName("api.start-block")
    String startBlock();

    @WithName("api.end-block")
    String endBlock();

    @WithName("contract.address")
    String contractAddress();

    @WithName("contract.abi")
    String contractAbi();
}