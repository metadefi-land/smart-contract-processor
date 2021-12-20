package land.metadefi;

import land.metadefi.model.BlockEvent;
import land.metadefi.model.ContractEvent;
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
            .bean("contractEventBean", "counterUpdatedEvent");
    }
}