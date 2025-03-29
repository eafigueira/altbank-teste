package ia.altbank.carrier;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class CarrierRepository implements PanacheRepositoryBase<Carrier, UUID> {
}
