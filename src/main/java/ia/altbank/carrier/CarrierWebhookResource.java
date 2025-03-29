package ia.altbank.carrier;

import ia.altbank.card.WebhookService;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@Path("/webhook/carrier")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class CarrierWebhookResource {
    private final WebhookService webhookService;
    private final CarrierService carrierService;

    public Response receiveDeliveryUpdate(@HeaderParam("X-Client-Id") String clientId,
                                          @HeaderParam("X-Client-Secret") String clientSecret,
                                          @Valid CardDeliveryWebhookRequest payload) {

        Carrier carrier = carrierService.validateCarrier(clientId, clientSecret);

        if (carrier == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        boolean success = webhookService.processDelivery(payload);
        return success ? Response.ok().build() : Response.status(Response.Status.NOT_FOUND).build();
    }
}
