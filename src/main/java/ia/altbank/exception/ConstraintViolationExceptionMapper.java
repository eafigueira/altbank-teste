package ia.altbank.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;
import java.util.stream.Collectors;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(ConstraintViolationException e) {
        List<ErrorResponse.ErrorItem> errors = e.getConstraintViolations()
                .stream()
                .map(violation -> {
                    String field = violation.getPropertyPath().toString();
                    String message = violation.getMessage();
                    return new ErrorResponse.ErrorItem(field, message);
                })
                .collect(Collectors.toList());

        ErrorResponse errorBody = new ErrorResponse("Validation failed", errors);
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorBody)
                .build();
    }
}
