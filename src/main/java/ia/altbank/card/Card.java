package ia.altbank.card;

import ia.altbank.account.Account;
import ia.altbank.base.BaseEntity;
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
@Table(name = "cards")
public class Card extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    @Enumerated(EnumType.STRING)
    private CardType type;
    private String number;
    private Integer cvv;
    @Column(name = "cvv_expiration")
    private LocalDateTime cvvExpiration;
    @Enumerated(EnumType.STRING)
    private CardStatus status;
//    @Column(name = "delivery_tracking_id")
//    private String deliveryTrackingId;
//    @Column(name = "delivery_status")
//    private String deliveryStatus;
//    @Column(name = "delivery_date")
//    private LocalDateTime deliveryDate;
//    @Column(name = "delivery_return_reason")
//    private String deliveryReturnReason;

    @PrePersist
    public void prePersist() {
        this.status = CardStatus.CREATED;
    }
}
