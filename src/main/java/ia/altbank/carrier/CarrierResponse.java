package ia.altbank.carrier;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CarrierResponse {
    private UUID id;
    private String name;
    private String documentNumber;
    private String clientId;
    private String clientSecret;
    private boolean defaultCarrier;

    public void hideAuthInfo() {
        this.clientSecret = null;
        this.clientId = null;
    }
}
