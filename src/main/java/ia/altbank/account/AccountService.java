package ia.altbank.account;

import ia.altbank.card.CardService;
import ia.altbank.exception.NotFoundException;
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
        cardService.deleteByAccountId(account.getId());
        repository.delete(account);
    }

    private Account findByCustomerId(UUID id) {
        return repository.findByCustomerId(id)
                .orElseThrow(() -> new NotFoundException("Account not found"));
    }

}
