package ia.altbank.controller;

import ia.altbank.dto.CreateCustomerRequest;
import ia.altbank.dto.CreateCustomerResponse;
import ia.altbank.dto.CustomerDTO;
import ia.altbank.dto.UpdateCustomerRequest;
import ia.altbank.service.CustomerService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

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
    public void update(@PathParam("id") UUID id, @Valid UpdateCustomerRequest request) {
        service.update(id, request);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        service.delete(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}")
    public CustomerDTO findById(@PathParam("id") UUID id) {
        return service.findOne(id);
    }
}
