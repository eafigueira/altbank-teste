package ia.altbank.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class CarrierResponse {
    private UUID id;
    private String name;
    private String clientId;
    private String clientSecret;
    private boolean defaultCarrier;

    public void hideSecret() {
        this.clientSecret = null;
    }
}
