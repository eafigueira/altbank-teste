package ia.altbank.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException exception) {
        ErrorResponse error = new ErrorResponse("Resource not found",
                List.of(new ErrorResponse.ErrorItem(null, exception.getMessage()))
        );
        return Response.status(Response.Status.NOT_FOUND)
                .entity(error)
                .build();
    }
}
