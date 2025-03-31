package ia.altbank.account;

import ia.altbank.card.CardService;
import ia.altbank.customer.CustomerEntity;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CardService cardService;

    public void deactivateAccount(AccountEntity account) {
        account.setStatus(AccountStatus.INACTIVE);
        accountRepository.persist(account);
    }

    public void inactivateByCustomerId(UUID customerId) {
        findByCustomerId(customerId, AccountStatus.ACTIVE)
                .forEach(account -> {
                    deactivateAccount(account);
                    cardService.inactivateCardsByAccountId(account.getId());
                });
    }

    public List<AccountEntity> findByCustomerId(UUID customerId, AccountStatus status) {
        return accountRepository.find("customer.id = ?1 AND status = ?2", customerId, status).list();
    }

    public AccountEntity createAccount(CustomerEntity customer) {
        AccountEntity account = new AccountEntity();
        account.setCustomer(customer);
        accountRepository.persist(account);
        return account;
    }
}
