package ia.altbank.processor;

import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Path("/processors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class ProcessorResource {

    private final ProcessorService service;

    @POST
    public Response create(@Valid ProcessorRequest request) {
        var response = service.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    public void update(@PathParam("id") UUID id, @Valid ProcessorRequest request) {
        service.update(id, request);
    }

    @GET
    @Path("/{id}")
    public ProcessorResponse findOne(@PathParam("id") UUID id) {
        return service.findOne(id);
    }

    @PUT
    @Path("/{id}/credentials")
    public ProcessorResponse regenerateCredentials(@PathParam("id") UUID id) {
        return service.regenerateCredentials(id);
    }


    @DELETE
    @Path("/{id}")
    public void inactivate(@PathParam("id") UUID id) {
        service.inactivate(id);
    }

    @GET
    public List<ProcessorResponse> listAll(@QueryParam("page") @DefaultValue("0") int page,
                                           @QueryParam("size") @DefaultValue("20") int size) {
        return service.list(page, size);
    }
}
