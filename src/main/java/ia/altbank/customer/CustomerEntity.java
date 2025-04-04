package ia.altbank.customer;

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
@Table(name = "customers")
public class CustomerEntity extends BaseEntity {

    private String name;
    @Column(nullable = false, name = "document_number")
    private String documentNumber;
    private String email;

    @Enumerated(EnumType.STRING)
    private CustomerStatus status;

    @Embedded
    private CustomerAddress address;
}
