package ia.altbank.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Carrier {
    @Id
    private UUID id;

    private String name;

    @Column(unique = true, nullable = false)
    private String clientId;

    private String clientSecret;

    private boolean defaultCarrier;

    @PrePersist
    public void prePersist() {
        this.id = UUID.randomUUID();
    }
}
