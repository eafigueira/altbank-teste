package ia.altbank.model;

import ia.altbank.enums.CardStatus;
import ia.altbank.enums.CardType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cards")
public class Card extends BaseEntity {

    @ManyToOne(optional = false)
    private Account account;
    @Enumerated(EnumType.STRING)
    private CardType type;
    private String number;
    private Integer cvv;
    @Column(name = "cvv_expiration")
    private LocalDateTime cvvExpiration;
    @Enumerated(EnumType.STRING)
    private CardStatus status;
    @Column(name = "delivery_tracking_id")
    private String deliveryTrackingId;
    @Column(name = "delivery_status")
    private String deliveryStatus;
    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;
    @Column(name = "delivery_return_reason")
    private String deliveryReturnReason;

    @PrePersist
    public void prePersist() {
        this.status = CardStatus.CREATED;
    }
}
