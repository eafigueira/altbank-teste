package ia.altbank.card;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CardDeliveryRequest {
    @NotNull(message = "Carrier Id is required")
    private UUID carrierId;
}
