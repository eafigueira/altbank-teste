package ia.altbank.model;

import ia.altbank.enums.CardStatus;
import ia.altbank.enums.CardType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Card {
    @Id
    private UUID id;

    @ManyToOne(optional = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    private CardType type;

    private String number;
    private Integer cvv;
    private LocalDateTime cvvExpiration;

    @Enumerated(EnumType.STRING)
    private CardStatus status;

    private String deliveryTrackingId;
    private String deliveryStatus;
    private LocalDateTime deliveryDate;
    private String deliveryReturnReason;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.status = CardStatus.CREATED;
    }
}
