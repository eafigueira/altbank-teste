package ia.altbank.carrier;

import ia.altbank.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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

    @Column(unique = true, nullable = false, name = "document_number")
    private String documentNumber;

    @Column(unique = true, nullable = false, name = "client_id")
    private String clientId;

    @Column(name = "client_secret", nullable = false)
    private String clientSecret;

    @Column(name = "default_carrier", nullable = false)
    private boolean defaultCarrier;

}
