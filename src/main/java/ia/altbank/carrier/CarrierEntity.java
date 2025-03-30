package ia.altbank.carrier;

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
@Table(name = "carriers")
public class CarrierEntity extends BaseEntity {

    private String name;

    @Column(unique = true, nullable = false, name = "document_number")
    private String documentNumber;

    @Column(unique = true, nullable = false, name = "client_id")
    private String clientId;

    @Column(nullable = false, name = "client_secret")
    private String clientSecret;

    @Enumerated(EnumType.STRING)
    private CarrierStatus status;

    @PrePersist
    public void prePersist() {
        if (status == null) status = CarrierStatus.ACTIVE;
    }

}
