package ia.altbank.customer;

import ia.altbank.account.Account;
import ia.altbank.account.AccountRepository;
import ia.altbank.account.AccountService;
import ia.altbank.card.*;
import ia.altbank.exception.NotFoundException;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountService accountService;
    private final CardService cardService;

    @Transactional
    public CreateCustomerResponse create(CreateCustomerRequest request) {
        var foundCustomer = customerRepository.find("documentNumber = ?1", request.getDocumentNumber()).firstResult();
        if (foundCustomer != null) {
            throw new IllegalStateException("Customer with document number " + request.getDocumentNumber() + " already exists");
        }

        Customer customer = createCustomer(request);
        customerRepository.persist(customer);
        Account account = accountService.createAccount(customer);

        Card newCard = cardService.createCard(account, CardType.PHYSICAL);

        return CreateCustomerResponse.builder()
                .customerId(customer.getId())
                .accountId(account.getId())
                .cardId(newCard.getId())
                .address(new AddressDTO(customer.getAddress()))
                .build();
    }

    private Customer createCustomer(CreateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setDocumentNumber(request.getDocumentNumber());
        customer.setEmail(request.getEmail());

        Address address = new Address(request.getAddress());
        customer.setAddress(address);
        return customer;
    }

    @Transactional
    public void update(UUID id, @Valid UpdateCustomerRequest request) {
        Customer customer = findById(id);
        if (!customer.getDocumentNumber().equals(request.getDocumentNumber())) {
            var foundCustomer = customerRepository.find("documentNumber = ?1", request.getDocumentNumber()).firstResult();
            if (foundCustomer != null) {
                throw new IllegalStateException("Customer with document number " + request.getDocumentNumber() + " already exists");
            }
        }
        customer.setDocumentNumber(request.getDocumentNumber());
        customer.setEmail(request.getEmail());
        customer.setName(request.getName());
        customer.setAddress(new Address(request.getAddress()));
        customerRepository.persist(customer);

    }

    @Transactional
    public void delete(UUID id) {
        Customer customer = findById(id);
        accountService.deleteByCustomerId(id);
        customerRepository.delete(customer);
    }

    public CustomerDTO findOne(UUID id) {
        Customer customer = findById(id);
        return new CustomerDTO(customer);
    }

    private Customer findById(UUID id) {
        return customerRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    public List<CustomerDTO> listAll(int page, int size) {
        return customerRepository.findAll()
                .page(Page.of(page, size))
                .list()
                .stream()
                .map(CustomerDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomerAccountResponse createAccount(UUID customerId) {
        Customer customer = findById(customerId);
        var account = accountService.createAccount(customer);
        return new CustomerAccountResponse(account.getId());
    }

    public List<CustomerAccountResponse> listAccounts(UUID customerId) {
        return accountService.findByCustomerId(customerId)
                .stream()
                .map(account -> new CustomerAccountResponse(account.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAccount(UUID customerId, UUID accountId) {
        accountService.findByCustomerId(customerId)
                .stream()
                .filter(account -> account.getId().equals(accountId)).findFirst()
                .orElseThrow(() -> new NotFoundException("Account not found"));

        cardService.deleteByAccountId(accountId);
        accountService.deleteById(accountId);
    }
}
