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
    private String tracking_id;
    @NotNull(message = "Status is required")
    private String delivery_status;
    private LocalDateTime delivery_date;
    @NotNull(message = "Return Reason is required")
    private String delivery_return_reason;
    @NotNull(message = "Delivery Address is required")
    private String delivery_address;
}
