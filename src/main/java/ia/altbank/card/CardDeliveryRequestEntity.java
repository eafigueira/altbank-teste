package ia.altbank.card;

import ia.altbank.base.BaseEntity;
import ia.altbank.carrier.CarrierEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "card_delivery_requests")
public class CardDeliveryRequestEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "card_id")
    private CardEntity card;

    @ManyToOne(optional = false)
    @JoinColumn(name = "carrier_id")
    private CarrierEntity carrier;

    @Column(name = "tracking_code", nullable = false)
    private String trackingCode;

    @Column(name = "delivery_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
}
