package ia.altbank.carrier;

import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Path("/carriers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class CarrierResource {
    private final CarrierService carrierService;

    @GET
    public List<CarrierResponse> listAll(@QueryParam("page") @DefaultValue("0") int page,
                                         @QueryParam("size") @DefaultValue("20") int size) {
        return carrierService.findAll(page, size);
    }

    @GET
    @Path("/{id}")
    public CarrierResponse findById(@PathParam("id") UUID id) {
        return carrierService.findById(id, true);
    }

    @POST
    public CarrierResponse create(@Valid CarrierRequest request) {
        return carrierService.create(request);
    }

    @PUT
    @Path("/{id}")
    public void update(@PathParam("id") UUID id, CarrierRequest request) {
        carrierService.update(id, request);
    }

    @PUT
    @Path("/{id}/credentials")
    public CarrierResponse regenerateCredentials(@PathParam("id") UUID id) {
        return carrierService.regenerateCredentials(id);
    }

    @PUT
    @Path("/{id}/activate")
    public void activate(@PathParam("id") UUID id) {
        carrierService.activate(id);
    }

    @PUT
    @Path("/{id}/inactivate")
    public void inactivate(@PathParam("id") UUID id) {
        carrierService.inactivate(id);
    }
}
