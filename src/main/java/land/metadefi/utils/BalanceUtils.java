package land.metadefi.utils;

import lombok.experimental.UtilityClass;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.util.Objects;

@UtilityClass
public class BalanceUtils {

    public static BigDecimal weiToEther(String wei) {
        return Convert.fromWei(wei, Convert.Unit.ETHER);
    }

    public static boolean compareValue(BigDecimal priceOriginal, BigDecimal price) {
        return Objects.equals(priceOriginal, price);
    }

}
