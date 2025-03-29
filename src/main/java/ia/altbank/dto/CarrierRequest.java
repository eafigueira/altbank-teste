package ia.altbank.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarrierRequest {
    @NotNull(message = "Name is required")
    private String name;
    private boolean defaultCarrier;

}
