package ia.altbank.controller;

import ia.altbank.dto.CarrierRequest;
import ia.altbank.dto.CarrierResponse;
import ia.altbank.service.CarrierService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
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
    public List<CarrierResponse> listAll() {
        return carrierService.findAll();
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
    public CarrierResponse update(@PathParam("id") UUID id, CarrierRequest request) {
        return carrierService.update(id, request);
    }

    @PUT
    @Path("/{id}/credentials")
    public CarrierResponse regenerateCredentials(@PathParam("id") UUID id) {
        return carrierService.regenerateCredentials(id);
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") UUID id) {
        carrierService.delete(id);
    }
}
