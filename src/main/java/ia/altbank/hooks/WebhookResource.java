package ia.altbank.hooks;

import ia.altbank.card.CardDeliveryRequestService;
import ia.altbank.carrier.CarrierEntity;
import ia.altbank.carrier.CarrierService;
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

    @POST
    @Path("/delivery")
    public Response receiveDeliveryUpdate(@HeaderParam("X-Client-Id") String clientId,
                                          @HeaderParam("X-Client-Secret") String clientSecret,
                                          @Valid CardDeliveryWebhookRequest payload) {
        CarrierEntity carrier = carrierService.validateCarrier(clientId, clientSecret);

        if (carrier == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        cardDeliveryRequestService.processDelivery(payload);
        return Response.noContent().build();
    }
}
