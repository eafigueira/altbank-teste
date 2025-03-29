package ia.altbank.model;

import ia.altbank.enums.CarrierStatus;
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
public class Carrier extends BaseEntity {

    private String name;

    @Column(unique = true, nullable = false, name = "client_id")
    private String clientId;

    @Column(name = "client_secret", nullable = false)
    private String clientSecret;

    @Column(name = "default_carrier", nullable = false)
    private boolean defaultCarrier;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CarrierStatus status;

    @PrePersist
    public void prePersist() {
        this.status = CarrierStatus.ACTIVE;
    }
}
