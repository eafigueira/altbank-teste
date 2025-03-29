package ia.altbank.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private String message;
    private List<ErrorItem> errors;

    @Data
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorItem {
        private String field;
        private String message;
    }
}