package ia.altbank.service;

import ia.altbank.dto.CreateCustomerRequest;
import ia.altbank.dto.CreateCustomerResponse;
import ia.altbank.dto.UpdateCustomerRequest;
import ia.altbank.enums.CardStatus;
import ia.altbank.enums.CardType;
import ia.altbank.enums.CustomerStatus;
import ia.altbank.model.Account;
import ia.altbank.model.Address;
import ia.altbank.model.Card;
import ia.altbank.model.Customer;
import ia.altbank.repository.AccountRepository;
import ia.altbank.repository.CardRepository;
import ia.altbank.repository.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final AccountService accountService;

    @Transactional
    public CreateCustomerResponse create(CreateCustomerRequest request) {
        Customer customer = createCustomer(request);
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

    public Customer createCustomer(CreateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setDocumentNumber(request.getDocumentNumber());
        customer.setEmail(request.getEmail());

        Address address = new Address();
        address.setCity(request.getAddress().city());
        address.setComplement(request.getAddress().complement());
        address.setNeighborhood(request.getAddress().neighborhood());
        address.setNumber(request.getAddress().number());
        address.setState(request.getAddress().state());
        address.setStreet(request.getAddress().street());
        address.setZipCode(request.getAddress().zipCode());
        customer.setAddress(address);
        return customer;
    }

    @Transactional
    public void update(UUID id, @Valid UpdateCustomerRequest request) {
        Customer customer = findById(id);
        customer.setName(request.getName());
        customerRepository.persist(customer);
    }

    @Transactional
    public void delete(UUID id) {
        Customer customer = findById(id);
        customer.setStatus(CustomerStatus.DELETED);
        customerRepository.persist(customer);
        accountService.cancelByCustomerId(id);
    }

    private Customer findById(UUID id) {
        return customerRepository.findByIdOptional(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }
}
