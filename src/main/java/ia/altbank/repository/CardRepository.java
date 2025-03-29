package ia.altbank.repository;

import ia.altbank.model.Card;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class CardRepository implements PanacheRepositoryBase<Card, UUID> {
}
