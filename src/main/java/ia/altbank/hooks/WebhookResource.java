package ia.altbank.hooks;

import ia.altbank.card.CardDeliveryRequestService;
import ia.altbank.card.CardService;
import ia.altbank.carrier.CarrierEntity;
import ia.altbank.carrier.CarrierService;
import ia.altbank.processor.ProcessorEntity;
import ia.altbank.processor.ProcessorService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@Path("/webhook")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class WebhookResource {
    private final CarrierService carrierService;
    private final CardDeliveryRequestService cardDeliveryRequestService;
    private final ProcessorService processorService;
    private final CardService cardService;

    @POST
    @Path("/delivery")
    public Response receiveDeliveryUpdate(@HeaderParam("X-Client-Id") String clientId,
                                          @HeaderParam("X-Client-Secret") String clientSecret,
                                          @Valid CardDeliveryWebhookRequest payload) {
        CarrierEntity carrier = carrierService.getByCredentials(clientId, clientSecret);

        if (carrier == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        cardDeliveryRequestService.processDelivery(payload);
        return Response.noContent().build();
    }

    @POST
    @Path("/update-cvv")
    public Response receiveCVVUpdate(@HeaderParam("X-Client-Id") String clientId,
                                     @HeaderParam("X-Client-Secret") String clientSecret,
                                     @Valid ChangeCVVWebhookRequest payload) {
        ProcessorEntity processor = processorService.getByCredentials(clientId, clientSecret);

        if (processor == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        cardService.processCVVUpdate(payload);
        return Response.noContent().build();
    }
}
