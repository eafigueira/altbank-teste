package ia.altbank.customer;

import ia.altbank.account.AccountEntity;
import ia.altbank.account.AccountService;
import ia.altbank.account.AccountStatus;
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


    private CustomerEntity findCustomerActive(UUID customerId) {
        return customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE).firstResultOptional().orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    private void validateCustomerByDocumentNumberActive(String documentNumber) {
        var customer = customerRepository.find("documentNumber = ?1 AND status = ?2", documentNumber, CustomerStatus.ACTIVE).firstResultOptional();
        if (customer.isPresent()) {
            throw new IllegalStateException("Customer with document number " + documentNumber + " already exists");
        }
    }

    private CustomerEntity createNewCustomer(CreateCustomerRequest request) {
        CustomerEntity customer = new CustomerEntity();
        customer.setName(request.getName());
        customer.setDocumentNumber(request.getDocumentNumber());
        customer.setEmail(request.getEmail());

        CustomerAddress address = new CustomerAddress(request.getAddress());
        customer.setAddress(address);
        return customer;
    }

    @Transactional
    public CreateCustomerResponse create(CreateCustomerRequest request) {

        validateCustomerByDocumentNumberActive(request.getDocumentNumber());

        CustomerEntity customer = createNewCustomer(request);

        customerRepository.persist(customer);
        AccountEntity account = accountService.createAccount(customer);
        CardResponse newCard = cardService.createCard(account, CardType.PHYSICAL);

        return CreateCustomerResponse.builder()
                .customerId(customer.getId())
                .accountId(account.getId())
                .cardId(newCard.getId())
                .address(new AddressDTO(customer.getAddress()))
                .status(customer.getStatus())
                .build();
    }

    @Transactional
    public void update(UUID id, @Valid UpdateCustomerRequest request) {
        CustomerEntity customer = findCustomerActive(id);

        if (!customer.getDocumentNumber().equals(request.getDocumentNumber())) {
            validateCustomerByDocumentNumberActive(request.getDocumentNumber());
        }

        customer.setDocumentNumber(request.getDocumentNumber());
        customer.setEmail(request.getEmail());
        customer.setName(request.getName());
        customer.setAddress(new CustomerAddress(request.getAddress()));
        customerRepository.persist(customer);
    }

    @Transactional
    public void delete(UUID id) {
        CustomerEntity customer = findCustomerActive(id);
        accountService.inactivateByCustomerId(id);
        customer.setStatus(CustomerStatus.INACTIVE);
        customerRepository.persist(customer);
    }

    public CustomerDTO findOne(UUID id) {
        CustomerEntity customer = findCustomerActive(id);
        return new CustomerDTO(customer);
    }

    public List<CustomerDTO> listAll(int page, int size) {
        return customerRepository.find("status = ?1", CustomerStatus.ACTIVE)
                .page(Page.of(page, size))
                .list()
                .stream()
                .map(CustomerDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomerAccountResponse createAccount(UUID customerId) {
        CustomerEntity customer = findCustomerActive(customerId);
        var account = accountService.createAccount(customer);
        cardService.createCard(account, CardType.PHYSICAL);
        return new CustomerAccountResponse(account.getId());
    }

    public List<CustomerAccountResponse> listAccounts(UUID customerId) {
        findCustomerActive(customerId);
        return accountService.findByCustomerId(customerId, AccountStatus.ACTIVE)
                .stream()
                .map(account -> new CustomerAccountResponse(account.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deactivateAccount(UUID customerId, UUID accountId) {
        findCustomerActive(customerId);
        var account = accountService.findByCustomerId(customerId, AccountStatus.ACTIVE)
                .stream()
                .filter(acc -> acc.getId().equals(accountId)).findFirst()
                .orElseThrow(() -> new NotFoundException("Account not found"));

        accountService.deactivateAccount(account);
    }


    public List<CardResponse> listCards(UUID customerId, UUID accountId) {
        findCustomerActive(customerId);
        var account = accountService.findByCustomerId(customerId, AccountStatus.ACTIVE)
                .stream()
                .filter(acc -> acc.getId().equals(accountId)).findFirst()
                .orElseThrow(() -> new NotFoundException("Account not found"));

        return cardService.listCards(account, CardStatus.ACTIVE, CardStatus.CREATED);

    }

    @Transactional
    public CardResponse createCard(UUID customerId, UUID accountId, @Valid CardRequest request) {
        findCustomerActive(customerId);
        var account = accountService.findByCustomerId(customerId, AccountStatus.ACTIVE)
                .stream()
                .filter(acc -> acc.getId().equals(accountId)).findFirst()
                .orElseThrow(() -> new NotFoundException("Account not found"));
        if (request.getType().equals(CardType.VIRTUAL.name())) {
            cardService.checkCardPhysicalDeliveryRequest(account.getId());
        } else {
            cardService.checkNumberOfPhysicalCardsInAccount(account.getId());
        }
        return cardService.createCard(account, CardType.valueOf(request.getType()));
    }

    @Transactional
    public void inactivateCard(UUID customerId, UUID accountId, UUID cardId) {
        findCustomerActive(customerId);
        accountService.findByCustomerId(customerId, AccountStatus.ACTIVE)
                .stream()
                .filter(acc -> acc.getId().equals(accountId)).findFirst()
                .orElseThrow(() -> new NotFoundException("Account not found"));
        cardService.inactivateCard(cardId);
    }

    @Transactional
    public CardDeliveryResponse createCardDeliveryRequest(UUID customerId, UUID accountId, UUID cardId, CardDeliveryRequest request) {
        var customer = findCustomerActive(customerId);
        accountService.findByCustomerId(customerId, AccountStatus.ACTIVE)
                .stream()
                .filter(acc -> acc.getId().equals(accountId)).findFirst()
                .orElseThrow(() -> new NotFoundException("Account not found"));
        return cardService.createCardDeliveryRequest(cardId, request.getCarrierId(), customer.getAddress());
    }

    public List<CardDeliveryResponse> listCardsDeliveryRequest(UUID customerId, UUID accountId, UUID cardId) {
        findCustomerActive(customerId);
        accountService.findByCustomerId(customerId, AccountStatus.ACTIVE)
                .stream()
                .filter(acc -> acc.getId().equals(accountId)).findFirst()
                .orElseThrow(() -> new NotFoundException("Account not found"));
        return cardService.listCardsDeliveryRequest(cardId);
    }

    @Transactional
    public void cancelCardDeliveryRequest(UUID customerId, UUID accountId, UUID cardId, UUID deliveryRequestId) {
        findCustomerActive(customerId);
        accountService.findByCustomerId(customerId, AccountStatus.ACTIVE)
                .stream()
                .filter(acc -> acc.getId().equals(accountId)).findFirst()
                .orElseThrow(() -> new NotFoundException("Account not found"));
        cardService.cancelCardDeliveryRequest(cardId, deliveryRequestId);
    }
}
