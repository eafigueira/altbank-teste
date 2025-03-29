package ia.altbank.account;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class AccountRepository implements PanacheRepositoryBase<Account, UUID> {
    public Optional<Account> findByCustomerId(UUID id) {
        return find("customer.id = ?1", id).firstResultOptional();
    }
}
