package ia.altbank.processor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessorResponse {
    private UUID id;
    private String name;
    private String clientId;
    private String clientSecret;
    private ProcessorStatus status;

    public void hideAuthInfo() {
        this.clientSecret = null;
        this.clientId = null;
    }
}
