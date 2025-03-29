package ia.altbank.service;

import ia.altbank.dto.CreateCustomerRequest;
import ia.altbank.dto.CreateCustomerResponse;
import ia.altbank.enums.CardStatus;
import ia.altbank.enums.CardType;
import ia.altbank.model.Account;
import ia.altbank.model.Card;
import ia.altbank.model.Customer;
import ia.altbank.repository.AccountRepository;
import ia.altbank.repository.CardRepository;
import ia.altbank.repository.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;

    @Transactional
    public CreateCustomerResponse createCustomer(CreateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setDocumentNumber(request.getDocumentNumber());
        customer.setEmail(request.getEmail());
        customerRepository.persist(customer);

        Account account = new Account();
        account.setCustomer(customer);
        accountRepository.persist(account);

        Card card = new Card();
        card.setAccount(account);
        card.setType(CardType.PHYSICAL);
        card.setNumber(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        card.setStatus(CardStatus.CREATED);
        cardRepository.persist(card);

        return CreateCustomerResponse.builder()
                .customerId(customer.getId())
                .accountId(account.getId())
                .cardId(card.getId())
                .build();
    }
}
