package ia.altbank.repository;

import ia.altbank.model.Account;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class AccountRepository implements PanacheRepositoryBase<Account, UUID> {
}
