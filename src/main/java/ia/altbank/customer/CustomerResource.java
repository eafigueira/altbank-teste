package ia.altbank.customer;

import ia.altbank.card.CardDeliveryRequest;
import ia.altbank.card.CardDeliveryResponse;
import ia.altbank.card.CardRequest;
import ia.altbank.card.CardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Operações relacionadas aos clientes contas e cartões")
public class CustomerResource {

    private final CustomerService service;

    @Operation(summary = "Cria um novo cliente", description = "Cadastra um novo cliente e cria automaticamente uma conta e um cartão físico.")
    @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso")
    @POST
    public Response createCustomer(@Valid CreateCustomerRequest request) {
        CreateCustomerResponse response = service.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @Operation(summary = "Atualiza os dados de um cliente")
    @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") UUID id, @Valid UpdateCustomerRequest request) {
        service.update(id, request);
        return Response.status(Response.Status.OK).build();
    }

    @Operation(summary = "Remove um cliente e todas as suas contas e cartões")
    @ApiResponse(responseCode = "204", description = "Cliente removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") UUID id) {
        service.delete(id);
    }

    @Operation(summary = "Busca um cliente pelo ID")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    @GET
    @Path("/{id}")
    public CustomerDTO findById(@PathParam("id") UUID id) {
        return service.findOne(id);
    }

    @Operation(summary = "Lista todos os clientes com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso")
    @GET
    public List<CustomerDTO> listAll(@QueryParam("page") @DefaultValue("0") int page,
                                     @QueryParam("size") @DefaultValue("20") int size) {
        return service.listAll(page, size);
    }

    @Operation(summary = "Cria uma nova conta para o cliente")
    @ApiResponse(responseCode = "201", description = "Conta criada com sucesso")
    @POST
    @Path("/{customerId}/accounts")
    public Response addAccount(@PathParam("customerId") UUID customerId) {
        CustomerAccountResponse response = service.createAccount(customerId);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @Operation(summary = "Lista todas as contas de um cliente")
    @ApiResponse(responseCode = "200", description = "Lista de contas retornada com sucesso")
    @GET
    @Path("/{customerId}/accounts")
    public List<CustomerAccountResponse> listAccounts(@PathParam("customerId") UUID customerId) {
        return service.listAccounts(customerId);
    }

    @Operation(summary = "Desativa uma conta de um cliente")
    @ApiResponse(responseCode = "204", description = "Conta desativada com sucesso")
    @ApiResponse(responseCode = "404", description = "Conta nao encontrada")
    @DELETE
    @Path("/{customerId}/accounts/{accountId}")
    public void deactivateAccount(@PathParam("customerId") UUID customerId,
                             @PathParam("accountId") UUID accountId) {
        service.deactivateAccount(customerId, accountId);
    }

    @Operation(summary = "Lista todos os cartões de uma conta")
    @ApiResponse(responseCode = "200", description = "Lista de cartões retornada com sucesso")
    @ApiResponse(responseCode = "404", description = "Conta nao encontrada")
    @GET
    @Path("/{customerId}/accounts/{accountId}/cards")
    public List<CardResponse> listCards(@PathParam("customerId") UUID customerId,
                                        @PathParam("accountId") UUID accountId) {
        return service.listCards(customerId, accountId);
    }

    @Operation(summary = "Cria um novo cartão para uma conta")
    @ApiResponse(responseCode = "201", description = "Cartão criado com sucesso")
    @ApiResponse(responseCode = "404", description = "Conta nao encontrada")
    @POST
    @Path("/{customerId}/accounts/{accountId}/cards")
    public Response createCard(@PathParam("customerId") UUID customerId,
                                   @PathParam("accountId") UUID accountId,
                                   @Valid CardRequest request) {
        var newCard = service.createCard(customerId, accountId, request);
        return Response.status(Response.Status.CREATED).entity(newCard).build();
    }

    @Operation(summary = "Inativa um cartão")
    @ApiResponse(responseCode = "204", description = "Cartão inativado com sucesso")
    @ApiResponse(responseCode = "404", description = "Cartão nao encontrado")
    @PUT
    @Path("/{customerId}/accounts/{accountId}/cards/{cardId}/inactivate")
    public void inactivateCard(@PathParam("customerId") UUID customerId,
                               @PathParam("accountId") UUID accountId,
                               @PathParam("cardId") UUID cardId) {
        service.inactivateCard(customerId, accountId, cardId);
    }

    @Operation(summary = "Ativa um cartão")
    @ApiResponse(responseCode = "204", description = "Cartão ativado com sucesso")
    @ApiResponse(responseCode = "404", description = "Cartão nao encontrado")
    @PUT
    @Path("/{customerId}/accounts/{accountId}/cards/{cardId}/activate")
    public void activateCard(@PathParam("customerId") UUID customerId,
                               @PathParam("accountId") UUID accountId,
                               @PathParam("cardId") UUID cardId) {
        service.activateCard(customerId, accountId, cardId);
    }

    @Operation(summary = "Cria uma nova solicitação de entrega de um cartão")
    @ApiResponse(responseCode = "201", description = "Solicitação de entrega de cartão criada com sucesso")
    @ApiResponse(responseCode = "404", description = "Cartão nao encontrado")
    @POST
    @Path("/{customerId}/accounts/{accountId}/cards/{cardId}/delivery-requests")
    public Response createCardPhysicalDeliveryRequest(
            @PathParam("customerId") UUID customerId,
            @PathParam("accountId") UUID accountId,
            @PathParam("cardId") UUID cardId,
            @Valid CardDeliveryRequest request) {
        var deliveryRequest = service.createCardDeliveryRequest(customerId, accountId, cardId, request);
        return Response.status(Response.Status.CREATED).entity(deliveryRequest).build();
    }

    @Operation(summary = "Lista todas as solicitações de entrega de um cartão")
    @ApiResponse(responseCode = "200", description = "Lista de solicitações de entrega de cartão retornada com sucesso")
    @ApiResponse(responseCode = "404", description = "Cartão nao encontrado")
    @GET
    @Path("/{customerId}/accounts/{accountId}/cards/{cardId}/delivery-requests")
    public List<CardDeliveryResponse> listCardPhysicalDeliveryRequest(
            @PathParam("customerId") UUID customerId,
            @PathParam("accountId") UUID accountId,
            @PathParam("cardId") UUID cardId) {
        return service.listCardsDeliveryRequest(customerId, accountId, cardId);
    }

    @Operation(summary = "Cancela uma solicitação de entrega de um cartão")
    @ApiResponse(responseCode = "204", description = "Solicitação de entrega de cartão cancelada com sucesso")
    @ApiResponse(responseCode = "404", description = "Solicitação de entrega de cartão nao encontrada")
    @DELETE
    @Path("/{customerId}/accounts/{accountId}/cards/{cardId}/delivery-requests/{deliveryRequestId}")
    public void cancelCardPhysicalDeliveryRequest(
            @PathParam("customerId") UUID customerId,
            @PathParam("accountId") UUID accountId,
            @PathParam("cardId") UUID cardId,
            @PathParam("deliveryRequestId") UUID deliveryRequestId
        ) {
        service.cancelCardDeliveryRequest(customerId, accountId, cardId, deliveryRequestId);
    }

}
