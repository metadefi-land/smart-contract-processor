package land.metadefi.rest;

import land.metadefi.model.rest.FtmScanResponse;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "ftm-scan")
public interface FtmScanClient {

    @GET
    FtmScanResponse listNormalTransaction(
        @QueryParam("apikey") String apiKey,
        @QueryParam("address") String address,
        @QueryParam("startblock") String startBlock,
        @QueryParam("endblock") String endBlock,
        @QueryParam("module") @DefaultValue("account") String module,
        @QueryParam("action") @DefaultValue("txlist") String action,
        @QueryParam("sort") @DefaultValue("asc") String sort,
        @QueryParam("page") @DefaultValue("asc") String page,
        @QueryParam("offset") @DefaultValue("asc") String offset
    );

}
