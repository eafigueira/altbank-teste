package ia.altbank.account;

import ia.altbank.base.BaseEntity;
import ia.altbank.customer.CustomerEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class AccountEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @PrePersist
    public void prePersist() {
        if (status == null) status = AccountStatus.ACTIVE;
    }
}
