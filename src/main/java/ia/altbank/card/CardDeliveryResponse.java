package ia.altbank.card;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardDeliveryResponse {
    private UUID id;
    private UUID carrierId;
    private String trackingCode;
    private DeliveryStatus deliveryStatus;
    private String deliveryAddress;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;
}
