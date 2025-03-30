package ia.altbank.customer;

import ia.altbank.card.CardDeliveryRequest;
import ia.altbank.card.CardDeliveryResponse;
import ia.altbank.card.CardRequest;
import ia.altbank.card.CardResponse;
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
public class CustomerResource {

    private final CustomerService service;

    @POST
    public Response createCustomer(@Valid CreateCustomerRequest request) {
        CreateCustomerResponse response = service.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") UUID id, @Valid UpdateCustomerRequest request) {
        service.update(id, request);
        return Response.status(Response.Status.OK).build();
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") UUID id) {
        service.delete(id);
    }

    @GET
    @Path("/{id}")
    public CustomerDTO findById(@PathParam("id") UUID id) {
        return service.findOne(id);
    }

    @GET
    public List<CustomerDTO> listAll(@QueryParam("page") @DefaultValue("0") int page,
                                     @QueryParam("size") @DefaultValue("20") int size) {
        return service.listAll(page, size);
    }

    @POST
    @Path("/{customerId}/accounts")
    public Response addAccount(@PathParam("customerId") UUID customerId) {
        CustomerAccountResponse response = service.createAccount(customerId);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }
    @GET
    @Path("/{customerId}/accounts")
    public List<CustomerAccountResponse> listAccounts(@PathParam("customerId") UUID customerId) {
        return service.listAccounts(customerId);
    }

    @DELETE
    @Path("/{customerId}/accounts/{accountId}")
    public void deactivateAccount(@PathParam("customerId") UUID customerId,
                             @PathParam("accountId") UUID accountId) {
        service.deactivateAccount(customerId, accountId);
    }

    @GET
    @Path("/{customerId}/accounts/{accountId}/cards")
    public List<CardResponse> listCards(@PathParam("customerId") UUID customerId,
                                        @PathParam("accountId") UUID accountId) {
        return service.listCards(customerId, accountId);
    }

    @POST
    @Path("/{customerId}/accounts/{accountId}/cards")
    public CardResponse createCard(@PathParam("customerId") UUID customerId,
                                   @PathParam("accountId") UUID accountId,
                                   @Valid CardRequest request) {
        return service.createCard(customerId, accountId, request);
    }

    @DELETE
    @Path("/{customerId}/accounts/{accountId}/cards/{cardId}")
    public void inactivateCard(@PathParam("customerId") UUID customerId,
                               @PathParam("accountId") UUID accountId,
                               @PathParam("cardId") UUID cardId) {
        service.inactivateCard(customerId, accountId, cardId);
    }

    @POST
    @Path("/{customerId}/accounts/{accountId}/cards/{cardId}/delivery-requests")
    public CardDeliveryResponse createCardPhysicalDeliveryRequest(
            @PathParam("customerId") UUID customerId,
            @PathParam("accountId") UUID accountId,
            @PathParam("cardId") UUID cardId,
            @Valid CardDeliveryRequest request) {
        return service.createCardDeliveryRequest(customerId, accountId, cardId, request);
    }

    @GET
    @Path("/{customerId}/accounts/{accountId}/cards/{cardId}/delivery-requests")
    public List<CardDeliveryResponse> listCardPhysicalDeliveryRequest(
            @PathParam("customerId") UUID customerId,
            @PathParam("accountId") UUID accountId,
            @PathParam("cardId") UUID cardId) {
        return service.listCardsDeliveryRequest(customerId, accountId, cardId);
    }

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
