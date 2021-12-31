package land.metadefi;

import land.metadefi.model.BlockEvent;
import land.metadefi.model.ContractEvent;
import land.metadefi.model.NFT;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CamelRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("rabbitmq:contract-events?queue=BlockEvent&routingKey=blockEvents")
            .log("Queue: BlockEvents")
            .unmarshal().json(JsonLibrary.Gson, BlockEvent.class)
            .bean("contractEventBean", "blockEvent");

        from("rabbitmq:contract-events?queue=CounterUpdatedEvent&routingKey=contractEvents.CounterUpdatedEvent")
            .log("Queue: CounterUpdatedEvent")
            .unmarshal().json(JsonLibrary.Gson, ContractEvent.class)
            .bean("contractEventBean", "contractEvent");

        from("rabbitmq:contracts?queue=mint-pre-check&routingKey=mint.pre-check")
            .log("Queue: contract-pre-check")
            .unmarshal().json(JsonLibrary.Gson, NFT.class)
            .bean("contractBean", "mintNFT")
            .process(e -> e.getMessage().removeHeader("CamelRabbitmqRoutingKey"))
            .marshal().jaxb()
            .to("rabbitmq:contracts?queue=mint-processor&routingKey=mint.processor");
    }
}