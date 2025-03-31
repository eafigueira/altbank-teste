package ia.altbank.processor;

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

@Path("/processors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
@Tag(name = "Processadoras", description = "Operações relacionadas as processadoras")
public class ProcessorResource {

    private final ProcessorService service;

    @Operation(summary = "Cria uma nova processadora")
    @ApiResponse(responseCode = "201", description = "Processadora criada com sucesso")
    @POST
    public Response create(@Valid ProcessorRequest request) {
        var response = service.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @Operation(summary = "Atualiza os dados de uma processadora")
    @ApiResponse(responseCode = "200", description = "Processadora atualizada com sucesso")
    @ApiResponse(responseCode = "404", description = "Processadora nao encontrada")
    @PUT
    @Path("/{id}")
    public void update(@PathParam("id") UUID id, @Valid ProcessorRequest request) {
        service.update(id, request);
    }

    @Operation(summary = "Busca uma processadora pelo id")
    @ApiResponse(responseCode = "200", description = "Processadora retornada com sucesso")
    @ApiResponse(responseCode = "404", description = "Processadora nao encontrada")
    @GET
    @Path("/{id}")
    public ProcessorResponse findOne(@PathParam("id") UUID id) {
        return service.findOne(id);
    }

    @Operation(summary = "Regenera as credenciais de uma processadora")
    @ApiResponse(responseCode = "200", description = "Credenciais regeneradas com sucesso")
    @ApiResponse(responseCode = "404", description = "Processadora nao encontrada")
    @PUT
    @Path("/{id}/credentials")
    public ProcessorResponse regenerateCredentials(@PathParam("id") UUID id) {
        return service.regenerateCredentials(id);
    }

    @Operation(summary = "Inativa uma processadora")
    @ApiResponse(responseCode = "204", description = "Processadora inativada com sucesso")
    @ApiResponse(responseCode = "404", description = "Processadora nao encontrada")
    @DELETE
    @Path("/{id}")
    public void inactivate(@PathParam("id") UUID id) {
        service.inactivate(id);
    }

    @Operation(summary = "Busca todas as processadoras com paginacao")
    @ApiResponse(responseCode = "200", description = "Processadoras retornadas com sucesso")
    @GET
    public List<ProcessorResponse> listAll(@QueryParam("page") @DefaultValue("0") int page,
                                           @QueryParam("size") @DefaultValue("20") int size) {
        return service.list(page, size);
    }
}
