package ia.altbank.customer;

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
    public void update(@PathParam("id") UUID id, @Valid UpdateCustomerRequest request) {
        service.update(id, request);
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
}
