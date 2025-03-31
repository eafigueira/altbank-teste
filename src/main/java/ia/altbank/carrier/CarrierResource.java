package ia.altbank.carrier;

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

@Path("/carriers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
@Tag(name = "Carriers", description = "Operações relacionadas aos transportadoras")
public class CarrierResource {
    private final CarrierService carrierService;

    @Operation(summary = "Lista todos os transportadoras com paginação")
    @ApiResponse(responseCode = "200", description = "Lista de transportadoras retornada com sucesso")
    @GET
    public List<CarrierResponse> listAll(@QueryParam("page") @DefaultValue("0") int page,
                                         @QueryParam("size") @DefaultValue("20") int size) {
        return carrierService.findAll(page, size);
    }

    @Operation(summary = "Busca uma transportadora pelo id")
    @ApiResponse(responseCode = "200", description = "Transportadora retornada com sucesso")
    @ApiResponse(responseCode = "404", description = "Transportadora nao encontrada")
    @GET
    @Path("/{id}")
    public CarrierResponse findById(@PathParam("id") UUID id) {
        return carrierService.findById(id, true);
    }

    @Operation(summary = "Cria uma novo transportadora")
    @ApiResponse(responseCode = "201", description = "Transportadora criada com sucesso")
    @POST
    public Response create(@Valid CarrierRequest request) {
        var newCarrier = carrierService.create(request);
        return Response.status(Response.Status.CREATED).entity(newCarrier).build();
    }

    @Operation(summary = "Atualiza os dados de uma transportadora")
    @ApiResponse(responseCode = "200", description = "Transportadora atualizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Transportadora nao encontrada")
    @PUT
    @Path("/{id}")
    public void update(@PathParam("id") UUID id, CarrierRequest request) {
        carrierService.update(id, request);
    }

    @Operation(summary = "Regenera as credenciais de uma transportadora")
    @ApiResponse(responseCode = "200", description = "Credenciais regeneradas com sucesso")
    @ApiResponse(responseCode = "404", description = "Transportadora nao encontrada")
    @PUT
    @Path("/{id}/credentials")
    public CarrierResponse regenerateCredentials(@PathParam("id") UUID id) {
        return carrierService.regenerateCredentials(id);
    }

    @Operation(summary = "Inativa uma transportadora")
    @ApiResponse(responseCode = "204", description = "Transportadora inativada com sucesso")
    @ApiResponse(responseCode = "404", description = "Transportadora nao encontrada")
    @DELETE
    @Path("/{id}")
    public void inactivate(@PathParam("id") UUID id) {
        carrierService.inactivate(id);
    }
}
