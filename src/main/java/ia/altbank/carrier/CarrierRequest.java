package ia.altbank.carrier;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarrierRequest {
    @NotNull(message = "Name is required")
    private String name;
    @NotNull(message = "Document number is required")
    private String documentNumber;
    private boolean defaultCarrier;

}
