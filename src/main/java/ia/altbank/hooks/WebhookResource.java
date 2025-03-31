package ia.altbank.hooks;

import ia.altbank.card.CardDeliveryRequestService;
import ia.altbank.card.CardService;
import ia.altbank.carrier.CarrierEntity;
import ia.altbank.carrier.CarrierService;
import ia.altbank.processor.ProcessorEntity;
import ia.altbank.processor.ProcessorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@Path("/webhook")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
@Tag(name = "Webhooks para atualizacao de informações de entrega e alteração de CVV")
public class WebhookResource {
    private final CarrierService carrierService;
    private final CardDeliveryRequestService cardDeliveryRequestService;
    private final ProcessorService processorService;
    private final CardService cardService;

    @Operation(summary = "Recebe informacoes de entrega de cartao")
    @ApiResponse(responseCode = "204", description = "Informacoes de entrega de cartao recebidas com sucesso")
    @ApiResponse(responseCode = "401", description = "Credenciais invalidas")
    @ApiResponse(responseCode = "404", description = "Cartao nao encontrado")
    @ApiResponse(responseCode = "409", description = "Indica algum problema para prosseguir com a atualicao")
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

    @Operation(summary = "Recebe informacoes de alteracao de CVV")
    @ApiResponse(responseCode = "204", description = "Informacoes de alteracao de CVV recebidas com sucesso")
    @ApiResponse(responseCode = "401", description = "Credenciais invalidas")
    @ApiResponse(responseCode = "404", description = "Cartao nao encontrado")
    @ApiResponse(responseCode = "409", description = "Indica algum problema para prosseguir com a atualicao")
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
