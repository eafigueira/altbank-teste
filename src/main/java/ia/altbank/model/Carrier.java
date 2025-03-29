package ia.altbank.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

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
}
