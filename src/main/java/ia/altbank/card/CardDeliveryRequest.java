package ia.altbank.card;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDeliveryRequest {
    @NotNull(message = "Carrier Id is required")
    private UUID carrierId;
}
