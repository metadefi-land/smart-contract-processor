package land.metadefi.camel;

import com.google.gson.Gson;
import land.metadefi.model.NFT;
import org.apache.camel.Converter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Converter(generateLoader = true)
public class MintNFTConverter {

    @Converter
    public static InputStream convertToInputStream(NFT NFT) {
        String json = new Gson().toJson(NFT);
        return new ByteArrayInputStream(json.getBytes());
    }

}
