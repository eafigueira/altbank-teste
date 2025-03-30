package ia.altbank.card;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class CardDeliveryRequestRepository implements PanacheRepositoryBase<CardDeliveryRequestEntity, UUID> {
}
