package ia.altbank.service;

import ia.altbank.enums.AccountStatus;
import ia.altbank.model.Account;
import ia.altbank.repository.AccountRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;

    @Transactional
    public void deleteByCustomerId(UUID id) {
        Account account = findByCustomerId(id);
        account.setStatus(AccountStatus.DELETED);
        repository.persist(account);
    }

    private Account findByCustomerId(UUID id) {
        return repository.findByCustomerId(id).orElseThrow(() -> new RuntimeException("Account not found"));
    }
}
