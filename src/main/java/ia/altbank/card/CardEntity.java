package ia.altbank.card;

import ia.altbank.account.AccountEntity;
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
public class CardEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity account;
    @Enumerated(EnumType.STRING)
    private CardType type;
    private String number;
    private String cvv;
    @Column(name = "cvv_expiration")
    private LocalDateTime cvvExpiration;
    @Enumerated(EnumType.STRING)
    private CardStatus status;
}
