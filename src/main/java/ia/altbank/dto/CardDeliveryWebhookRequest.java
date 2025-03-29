package ia.altbank.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDeliveryWebhookRequest {
    private String trackingId;
    private String deliveryStatus;
    private LocalDateTime deliveryDate;
    private String deliveryReturnReason;
    private String deliveryAddress;
}
