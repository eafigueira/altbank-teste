package ia.altbank.account;

import ia.altbank.card.CardService;
import ia.altbank.customer.Customer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CardService cardService;

    public void deleteByCustomerId(UUID customerId) {
        findByCustomerId(customerId)
                .forEach(account -> {
                            cardService.deleteByAccountId(account.getId());
                            var accountFound = accountRepository.findById(account.getId());
                            accountRepository.delete(accountFound);
                        }
                );
    }

    public List<Account> findByCustomerId(UUID customerId) {
        return accountRepository.find("customer.id = ?1", customerId).list();
    }

    public Account createAccount(Customer customer) {
        Account account = new Account();
        account.setCustomer(customer);
        accountRepository.persist(account);
        return account;
    }

    public void deleteById(UUID accountId) {
        accountRepository.delete("id = ?1", accountId);
    }
}
