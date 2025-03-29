package ia.altbank.account;

import ia.altbank.customer.Customer;
import ia.altbank.base.BaseEntity;
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
public class Account extends BaseEntity {
    @ManyToOne(optional = false)
    private Customer customer;
}
