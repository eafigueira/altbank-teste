package ia.altbank.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

@Provider
public class IllegalStateExceptionMapper implements ExceptionMapper<IllegalStateException> {
    @Override
    public Response toResponse(IllegalStateException exception) {
        ErrorResponse error = new ErrorResponse("State conflict",
                List.of(new ErrorResponse.ErrorItem(null, exception.getMessage()))
        );
        return Response.status(Response.Status.CONFLICT)
                .entity(error)
                .build();
    }
}
