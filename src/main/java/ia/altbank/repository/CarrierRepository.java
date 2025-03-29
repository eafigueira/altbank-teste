package ia.altbank.repository;

import ia.altbank.model.Carrier;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class CarrierRepository implements PanacheRepositoryBase<Carrier, UUID> {
}
