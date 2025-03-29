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
    private final CardService cardService;

    @Transactional
    public void cancelByCustomerId(UUID customerId) {
        Account account = findByCustomerId(customerId);
        account.setStatus(AccountStatus.DELETED);
        repository.persist(account);

        cardService.deleteByAccountId(account.getId());
    }

    @Transactional
    public void cancel(UUID id) {
        Account account = findById(id);
        account.setStatus(AccountStatus.DELETED);
        repository.persist(account);

        cardService.deleteByAccountId(account.getId());
    }

    private Account findByCustomerId(UUID id) {
        return repository.findByCustomerId(id).orElseThrow(() -> new RuntimeException("Account not found"));
    }

    private Account findById(UUID id) {
        return repository.findByIdOptional(id).orElseThrow(() -> new RuntimeException("Account not found"));
    }
}
