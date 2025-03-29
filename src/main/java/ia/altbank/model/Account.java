package ia.altbank.model;

import ia.altbank.enums.AccountStatus;
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
@Table(name = "accounts")
public class Account extends BaseEntity {
    @ManyToOne(optional = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @PrePersist
    public void prePersist() {
        this.status = AccountStatus.ACTIVE;
    }
}
