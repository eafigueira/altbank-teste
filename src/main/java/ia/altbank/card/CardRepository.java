package ia.altbank.card;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CardRepository implements PanacheRepositoryBase<CardEntity, UUID> {
    public List<CardEntity> findAllByAccountId(UUID id) {
        return find("account.id = ?1", id).list();
    }
}
