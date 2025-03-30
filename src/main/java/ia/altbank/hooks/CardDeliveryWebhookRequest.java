package ia.altbank.hooks;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDeliveryWebhookRequest {
    @NotNull(message = "Tracking Id is required")
    private String trackingId;
    @NotNull(message = "Status is required")
    private String deliveryStatus;
    private LocalDateTime deliveryDate;
    @NotNull(message = "Return Reason is required")
    private String deliveryReturnReason;
    @NotNull(message = "Delivery Address is required")
    private String deliveryAddress;
}
