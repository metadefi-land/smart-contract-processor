package land.metadefi.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChainTransaction {
    String blockNumber;
    String timeStamp;
    String hash;
    String nonce;
    String blockHash;
    String transactionIndex;
    String from;
    String to;
    String value;
    String gas;
    String gasPrice;
    String isError;
    @JsonProperty("txreceipt_status")
    String txReceiptStatus;
    String input;
    String contractAddress;
    String cumulativeGasUsed;
    String gasUsed;
    String confirmations;
}
