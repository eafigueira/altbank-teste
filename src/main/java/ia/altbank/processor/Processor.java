package ia.altbank.processor;

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
@Table(name = "processors")
public class Processor extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false, name = "client_id")
    private String clientId;

    @Column(nullable = false, name = "client_secret")
    private String clientSecret;

    @Enumerated(EnumType.STRING)
    private ProcessorStatus status;

}
