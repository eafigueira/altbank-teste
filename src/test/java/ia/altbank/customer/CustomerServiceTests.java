package ia.altbank.customer;

import ia.altbank.account.AccountEntity;
import ia.altbank.account.AccountService;
import ia.altbank.account.AccountStatus;
import ia.altbank.card.*;
import ia.altbank.exception.NotFoundException;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomerServiceTests {
    private CustomerService customerService;

    private CustomerRepository customerRepository;
    private AccountService accountService;
    private CardService cardService;

    @BeforeEach
    void setup() {
        customerRepository = mock(CustomerRepository.class);
        accountService = mock(AccountService.class);
        cardService = mock(CardService.class);
        customerService = new CustomerService(accountService, cardService, customerRepository);
    }

    @Nested
    class CustomerTestsSuccess {

        @Test
        void shouldCreateCustomerSuccessfully() {
            String documentNumber = "12345678901234";
            CustomerStatus status = CustomerStatus.ACTIVE;

            var customerAddressRequest = CustomerAddressRequest.builder()
                    .city("City")
                    .complement("Complement")
                    .neighborhood("Neighborhood")
                    .number("Number")
                    .state("State")
                    .street("Street")
                    .zipCode("ZipCode")
                    .build();

            var createCustomerRequest = CreateCustomerRequest.builder()
                    .documentNumber(documentNumber)
                    .email("fulano@gmail.com")
                    .name("Fulano de Tal")
                    .address(customerAddressRequest)
                    .build();

            CustomerEntity expectedCustomer = new CustomerEntity();
            expectedCustomer.setDocumentNumber(documentNumber);
            expectedCustomer.setStatus(status);
            expectedCustomer.setAddress(new CustomerAddress(customerAddressRequest));

            PanacheQuery<CustomerEntity> queryMock = mock(PanacheQuery.class);
            when(customerRepository.find(eq("documentNumber = ?1 AND status = ?2"), Optional.ofNullable(any()), eq(status)))
                    .thenReturn(queryMock);
            when(queryMock.firstResultOptional()).thenReturn(Optional.empty());
            when(accountService.createAccount(any(CustomerEntity.class))).thenReturn(new AccountEntity());
            when(cardService.createCard(any(AccountEntity.class), any(CardType.class))).thenReturn(CardResponse.builder().build());

            customerService.create(createCustomerRequest);

            ArgumentCaptor<CustomerEntity> captor = ArgumentCaptor.forClass(CustomerEntity.class);
            verify(customerRepository).persist(captor.capture());
            CustomerEntity persisted = captor.getValue();
            assertEquals(documentNumber, persisted.getDocumentNumber());
            assertEquals("Fulano de Tal", persisted.getName());
            assertEquals("fulano@gmail.com", persisted.getEmail());
            assertEquals(CustomerStatus.ACTIVE, persisted.getStatus());
        }

        @Test
        void shouldUpdateCustomerWhenDocumentNumberUnchanged() {
            UUID customerId = UUID.randomUUID();

            var updateAddress = CustomerAddressRequest.builder()
                    .city("New City")
                    .complement("New Complement")
                    .neighborhood("New Neighborhood")
                    .number("123")
                    .state("New State")
                    .street("New Street")
                    .zipCode("99999-000")
                    .build();

            var updateRequest = UpdateCustomerRequest.builder()
                    .name("Novo Nome")
                    .email("novo@email.com")
                    .documentNumber("12345678901234")
                    .address(updateAddress)
                    .build();

            CustomerEntity existingCustomer = new CustomerEntity();
            existingCustomer.setId(customerId);
            existingCustomer.setDocumentNumber("12345678901234");
            existingCustomer.setName("Antigo Nome");
            existingCustomer.setEmail("antigo@email.com");
            existingCustomer.setAddress(new CustomerAddress());
            existingCustomer.setStatus(CustomerStatus.ACTIVE);

            PanacheQuery<CustomerEntity> queryMock = mock(PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(queryMock);
            when(queryMock.firstResultOptional())
                    .thenReturn(Optional.of(existingCustomer));

            customerService.update(customerId, updateRequest);

            assertEquals("Novo Nome", existingCustomer.getName());
            assertEquals("novo@email.com", existingCustomer.getEmail());
            assertEquals("New City", existingCustomer.getAddress().getCity());
            assertEquals(CustomerStatus.ACTIVE, existingCustomer.getStatus());

            verify(customerRepository).persist(any(CustomerEntity.class));
        }

        @Test
        void shouldUpdateCustomerWhenDocumentNumberIsChanged() {
            UUID customerId = UUID.randomUUID();

            String oldDocument = "11122233344";
            String newDocument = "55566677788";

            var updateAddress = CustomerAddressRequest.builder()
                    .city("Updated City")
                    .complement("Updated Complement")
                    .neighborhood("Updated Neighborhood")
                    .number("123")
                    .state("Updated State")
                    .street("Updated Street")
                    .zipCode("99999-000")
                    .build();

            var updateRequest = UpdateCustomerRequest.builder()
                    .name("Nome Atualizado")
                    .email("atualizado@email.com")
                    .documentNumber(newDocument)
                    .address(updateAddress)
                    .build();

            CustomerEntity existingCustomer = new CustomerEntity();
            existingCustomer.setId(customerId);
            existingCustomer.setName("Nome Antigo");
            existingCustomer.setEmail("antigo@email.com");
            existingCustomer.setDocumentNumber(oldDocument);
            existingCustomer.setAddress(new CustomerAddress());
            existingCustomer.setStatus(CustomerStatus.ACTIVE);

            PanacheQuery<CustomerEntity> activeQueryMock = mock(PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(activeQueryMock);
            when(activeQueryMock.firstResultOptional())
                    .thenReturn(Optional.of(existingCustomer));

            PanacheQuery<CustomerEntity> documentQueryMock = mock(PanacheQuery.class);
            when(customerRepository.find(eq("documentNumber = ?1 AND status = ?2"), eq(newDocument), eq(CustomerStatus.ACTIVE)))
                    .thenReturn(documentQueryMock);
            when(documentQueryMock.firstResultOptional())
                    .thenReturn(Optional.empty());

            customerService.update(customerId, updateRequest);

            assertEquals("Nome Atualizado", existingCustomer.getName());
            assertEquals("atualizado@email.com", existingCustomer.getEmail());
            assertEquals(newDocument, existingCustomer.getDocumentNumber());
            assertEquals("Updated City", existingCustomer.getAddress().getCity());
        }

        @Test
        void shouldFindCustomerSuccessfully() {
            UUID customerId = UUID.randomUUID();

            CustomerEntity customer = new CustomerEntity();
            customer.setId(customerId);
            customer.setName("Fulano");
            customer.setEmail("fulano@email.com");
            customer.setDocumentNumber("12345678900");
            customer.setStatus(CustomerStatus.ACTIVE);
            customer.setAddress(CustomerAddress.builder()
                    .street("Rua A")
                    .number("123")
                    .neighborhood("Centro")
                    .city("Cidade")
                    .state("UF")
                    .zipCode("12345-678")
                    .complement("Apto 1")
                    .build()
            );

            PanacheQuery<CustomerEntity> queryMock = mock(PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(queryMock);
            when(queryMock.firstResultOptional()).thenReturn(Optional.of(customer));

            CustomerDTO result = customerService.findOne(customerId);

            assertNotNull(result);
            assertEquals("Fulano", result.getName());
            assertEquals("12345678900", result.getDocumentNumber());
            assertEquals("fulano@email.com", result.getEmail());
            assertEquals("Cidade", result.getAddress().getCity());
        }

        @Test
        void shouldDeleteCustomerSuccessfully() {
            UUID customerId = UUID.randomUUID();

            CustomerEntity customer = new CustomerEntity();
            customer.setId(customerId);
            customer.setName("Fulano");
            customer.setEmail("fulano@email.com");
            customer.setDocumentNumber("12345678900");
            customer.setStatus(CustomerStatus.ACTIVE);
            customer.setAddress(new CustomerAddress());

            PanacheQuery<CustomerEntity> queryMock = mock(PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(queryMock);
            when(queryMock.firstResultOptional()).thenReturn(Optional.of(customer));
            customerService.delete(customerId);

            assertEquals(CustomerStatus.INACTIVE, customer.getStatus());
            verify(accountService).inactivateByCustomerId(customerId);
            verify(customerRepository).persist(customer);
        }

        @Test
        void shouldListAllCustomersWithPagination() {
            int page = 0;
            int size = 2;

            CustomerAddress address = CustomerAddress.builder()
                    .street("Rua A")
                    .number("123")
                    .neighborhood("Centro")
                    .city("Cidade")
                    .state("UF")
                    .zipCode("12345-678")
                    .complement("Apto 1")
                    .build();

            CustomerEntity customer1 = new CustomerEntity();
            customer1.setId(UUID.randomUUID());
            customer1.setName("Cliente 1");
            customer1.setEmail("cliente1@example.com");
            customer1.setDocumentNumber("123");
            customer1.setStatus(CustomerStatus.ACTIVE);
            customer1.setAddress(address);

            CustomerEntity customer2 = new CustomerEntity();
            customer2.setId(UUID.randomUUID());
            customer2.setName("Cliente 2");
            customer2.setEmail("cliente2@example.com");
            customer2.setDocumentNumber("456");
            customer2.setStatus(CustomerStatus.ACTIVE);
            customer2.setAddress(address);

            List<CustomerEntity> customerList = List.of(customer1, customer2);
            PanacheQuery<CustomerEntity> queryMock = mock(PanacheQuery.class);
            when(customerRepository.find("status = ?1", CustomerStatus.ACTIVE)).thenReturn(queryMock);
            when(queryMock.page(any(Page.class))).thenReturn(queryMock);
            when(queryMock.list()).thenReturn(customerList);

            List<CustomerDTO> result = customerService.listAll(page, size);

            assertEquals(2, result.size());
            assertEquals("Cliente 1", result.get(0).getName());
            assertEquals("Cliente 2", result.get(1).getName());
        }

        @Test
        void shouldInactivateCustomerAndCallAccountServiceOnDelete() {
            UUID customerId = UUID.randomUUID();

            CustomerEntity existingCustomer = new CustomerEntity();
            existingCustomer.setId(customerId);
            existingCustomer.setDocumentNumber("12345678900");
            existingCustomer.setName("Fulano");
            existingCustomer.setEmail("fulano@email.com");
            existingCustomer.setStatus(CustomerStatus.ACTIVE);
            existingCustomer.setAddress(CustomerAddress.builder().city("Cidade").build());

            PanacheQuery<CustomerEntity> queryMock = mock(PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(queryMock);
            when(queryMock.firstResultOptional()).thenReturn(Optional.of(existingCustomer));

            customerService.delete(customerId);

            assertEquals(CustomerStatus.INACTIVE, existingCustomer.getStatus());

            verify(accountService).inactivateByCustomerId(customerId);
            verify(customerRepository).persist(existingCustomer);
        }
    }

    @Nested
    class CustomerTestsFailure {

        @Test
        void shouldThrowExceptionWhenCustomerNotFoundOnUpdate() {
            UUID nonExistentId = UUID.randomUUID();

            var updateRequest = UpdateCustomerRequest.builder()
                    .name("Novo Nome")
                    .email("novo@email.com")
                    .documentNumber("98765432100")
                    .address(CustomerAddressRequest.builder()
                            .street("Rua Teste")
                            .number("123")
                            .city("Cidade")
                            .state("UF")
                            .zipCode("00000-000")
                            .neighborhood("Bairro")
                            .complement("Apto")
                            .build())
                    .build();

            PanacheQuery<CustomerEntity> queryMock = mock(PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", nonExistentId, CustomerStatus.ACTIVE))
                    .thenReturn(queryMock);
            when(queryMock.firstResultOptional()).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                customerService.update(nonExistentId, updateRequest);
            });
            verify(customerRepository, never()).persist(any(CustomerEntity.class));
            assertEquals("Customer not found", exception.getMessage());
        }

        @Test
        void shouldThrowNotFoundExceptionWhenCustomerNotFound() {
            UUID customerId = UUID.randomUUID();
            PanacheQuery<CustomerEntity> queryMock = mock(PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(queryMock);
            when(queryMock.firstResultOptional()).thenReturn(Optional.empty());
            assertThrows(NotFoundException.class, () -> customerService.findOne(customerId));
        }

        @Test
        void shouldThrowExceptionWhenDocumentNumberAlreadyExistsOnUpdate() {
            UUID customerId = UUID.randomUUID();
            String currentDocument = "12345678900001";
            String newDocument = "99999999999999";

            CustomerEntity existingCustomer = new CustomerEntity();
            existingCustomer.setId(customerId);
            existingCustomer.setName("Cliente Atual");
            existingCustomer.setEmail("atual@email.com");
            existingCustomer.setDocumentNumber(currentDocument);
            existingCustomer.setAddress(CustomerAddress.builder().city("Cidade").build());
            existingCustomer.setStatus(CustomerStatus.ACTIVE);

            CustomerEntity conflictingCustomer = new CustomerEntity();
            conflictingCustomer.setId(UUID.randomUUID());
            conflictingCustomer.setDocumentNumber(newDocument);
            conflictingCustomer.setStatus(CustomerStatus.ACTIVE);

            PanacheQuery<CustomerEntity> findByIdQuery = mock(PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(findByIdQuery);
            when(findByIdQuery.firstResultOptional()).thenReturn(Optional.of(existingCustomer));

            PanacheQuery<CustomerEntity> findByDocQuery = mock(PanacheQuery.class);
            when(customerRepository.find("documentNumber = ?1 AND status = ?2", newDocument, CustomerStatus.ACTIVE))
                    .thenReturn(findByDocQuery);
            when(findByDocQuery.firstResultOptional()).thenReturn(Optional.of(conflictingCustomer));

            var updateRequest = UpdateCustomerRequest.builder()
                    .name("Novo Nome")
                    .email("novo@email.com")
                    .documentNumber(newDocument)
                    .address(CustomerAddressRequest.builder().city("Nova Cidade").build())
                    .build();

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                customerService.update(customerId, updateRequest);
            });

            assertEquals("Customer with document number 99999999999999 already exists", exception.getMessage());
            verify(customerRepository, never()).persist(any(CustomerEntity.class));
        }

        @Test
        void shouldThrowExceptionWhenDocumentNumberAlreadyExistsOnCreate() {
            String documentNumber = "12345678900001";

            CustomerEntity existingCustomer = new CustomerEntity();
            existingCustomer.setId(UUID.randomUUID());
            existingCustomer.setDocumentNumber(documentNumber);
            existingCustomer.setStatus(CustomerStatus.ACTIVE);

            PanacheQuery<CustomerEntity> queryMock = mock(PanacheQuery.class);
            when(customerRepository.find("documentNumber = ?1 AND status = ?2", documentNumber, CustomerStatus.ACTIVE))
                    .thenReturn(queryMock);
            when(queryMock.firstResultOptional()).thenReturn(Optional.of(existingCustomer));

            var request = CreateCustomerRequest.builder()
                    .name("Novo Cliente")
                    .email("novo@email.com")
                    .documentNumber(documentNumber)
                    .address(CustomerAddressRequest.builder()
                            .city("Cidade")
                            .street("Rua A")
                            .number("123")
                            .neighborhood("Centro")
                            .zipCode("00000-000")
                            .state("SP")
                            .complement("Apto 1")
                            .build())
                    .build();

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                customerService.create(request);
            });

            assertEquals("Customer with document number 12345678900001 already exists", exception.getMessage());

            verify(customerRepository, never()).persist(any(CustomerEntity.class));
            verify(accountService, never()).createAccount(any());
            verify(cardService, never()).createCard(any(), any());
        }

        @Test
        void shouldThrowExceptionWhenCustomerNotFoundOnDelete() {
            UUID nonExistentId = UUID.randomUUID();

            PanacheQuery<CustomerEntity> queryMock = mock(PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", nonExistentId, CustomerStatus.ACTIVE))
                    .thenReturn(queryMock);
            when(queryMock.firstResultOptional()).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                customerService.delete(nonExistentId);
            });

            assertEquals("Customer not found", exception.getMessage());

            verify(customerRepository, never()).persist(any(CustomerEntity.class));
            verify(accountService, never()).inactivateByCustomerId(any());
        }
    }

    @Nested
    class AccountTestsSuccess {

        @Test
        void shouldCreateAccountSuccessfully() {
            UUID customerId = UUID.randomUUID();
            UUID accountId = UUID.randomUUID();

            CustomerEntity customer = new CustomerEntity();
            customer.setId(customerId);
            customer.setName("Fulano");
            customer.setEmail("fulano@email.com");
            customer.setDocumentNumber("12345678900");
            customer.setStatus(CustomerStatus.ACTIVE);
            customer.setAddress(new CustomerAddress());

            PanacheQuery<CustomerEntity> queryMock = mock(PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(queryMock);
            when(queryMock.firstResultOptional()).thenReturn(Optional.of(customer));

            AccountEntity account = new AccountEntity();
            account.setId(accountId);
            account.setCustomer(customer);
            when(accountService.createAccount(customer)).thenReturn(account);

            when(cardService.createCard(account, CardType.PHYSICAL)).thenReturn(CardResponse.builder().build());

            CustomerAccountResponse response = customerService.createAccount(customerId);

            assertEquals(accountId, response.getAccountId());
            verify(accountService).createAccount(customer);
            verify(cardService).createCard(account, CardType.PHYSICAL);
        }

        @Test
        void shouldListAccountsSuccessfully() {
            UUID customerId = UUID.randomUUID();

            CustomerEntity customer = new CustomerEntity();
            customer.setId(customerId);
            customer.setStatus(CustomerStatus.ACTIVE);

            AccountEntity acc1 = new AccountEntity();
            acc1.setId(UUID.randomUUID());

            AccountEntity acc2 = new AccountEntity();
            acc2.setId(UUID.randomUUID());

            List<AccountEntity> accounts = List.of(acc1, acc2);

            PanacheQuery<CustomerEntity> queryMock = mock(PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(queryMock);
            when(queryMock.firstResultOptional()).thenReturn(Optional.of(customer));
            when(accountService.findByCustomerId(customerId, AccountStatus.ACTIVE)).thenReturn(accounts);

            List<CustomerAccountResponse> result = customerService.listAccounts(customerId);

            assertEquals(2, result.size());
            assertTrue(result.stream().anyMatch(r -> r.getAccountId().equals(acc1.getId())));
            assertTrue(result.stream().anyMatch(r -> r.getAccountId().equals(acc2.getId())));
            verify(accountService).findByCustomerId(customerId, AccountStatus.ACTIVE);
        }

        @Test
        void shouldDeactivateAccountSuccessfully() {
            UUID customerId = UUID.randomUUID();
            UUID accountId = UUID.randomUUID();

            // Cliente ativo
            CustomerEntity customer = new CustomerEntity();
            customer.setId(customerId);
            customer.setStatus(CustomerStatus.ACTIVE);

            PanacheQuery<CustomerEntity> queryMock = mock(PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(queryMock);
            when(queryMock.firstResultOptional()).thenReturn(Optional.of(customer));

            // Conta ativa que será encontrada por ID
            AccountEntity account = new AccountEntity();
            account.setId(accountId);
            account.setCustomer(customer);
            account.setStatus(AccountStatus.ACTIVE);

            // Mock da lista de contas do cliente
            when(accountService.findByCustomerId(customerId, AccountStatus.ACTIVE))
                    .thenReturn(List.of(account));

            // Execução
            customerService.deactivateAccount(customerId, accountId);

            // Verificação
            verify(accountService).deactivateAccount(account);
        }
    }

    @Nested
    class AccountTestsFailure {

        @Test
        void shouldThrowExceptionWhenCustomerNotFoundOnAccountCreation() {
            UUID customerId = UUID.randomUUID();

            PanacheQuery<CustomerEntity> queryMock = mock(PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(queryMock);
            when(queryMock.firstResultOptional()).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                customerService.createAccount(customerId);
            });

            assertEquals("Customer not found", exception.getMessage());
            verify(accountService, never()).createAccount(any());
            verify(cardService, never()).createCard(any(), any());
        }

        @Test
        void shouldThrowExceptionWhenCustomerNotFoundOnListAccounts() {
            UUID customerId = UUID.randomUUID();

            PanacheQuery<CustomerEntity> queryMock = mock(PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(queryMock);
            when(queryMock.firstResultOptional()).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                customerService.listAccounts(customerId);
            });

            assertEquals("Customer not found", exception.getMessage());
            verify(accountService, never()).findByCustomerId(any(), any());
        }

        @Test
        void shouldThrowExceptionWhenAccountNotFoundOnDeactivate() {
            UUID customerId = UUID.randomUUID();
            UUID accountId = UUID.randomUUID();

            CustomerEntity customer = new CustomerEntity();
            customer.setId(customerId);
            customer.setStatus(CustomerStatus.ACTIVE);

            PanacheQuery<CustomerEntity> queryMock = mock(PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(queryMock);
            when(queryMock.firstResultOptional()).thenReturn(Optional.of(customer));

            when(accountService.findByCustomerId(customerId, AccountStatus.ACTIVE))
                    .thenReturn(List.of());

            NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                customerService.deactivateAccount(customerId, accountId);
            });

            assertEquals("Account not found", exception.getMessage());
            verify(accountService, never()).deactivateAccount(any());
        }
    }

    @Nested
    class CardTestsSuccess {

        @Test
        void shouldListCardsSuccessfully() {
            UUID customerId = UUID.randomUUID();
            UUID accountId = UUID.randomUUID();

            CustomerEntity customer = new CustomerEntity();
            customer.setId(customerId);
            customer.setStatus(CustomerStatus.ACTIVE);

            AccountEntity account = new AccountEntity();
            account.setId(accountId);

            PanacheQuery<CustomerEntity> customerQuery = mock(PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(customerQuery);
            when(customerQuery.firstResultOptional()).thenReturn(Optional.of(customer));

            when(accountService.findByCustomerId(customerId, AccountStatus.ACTIVE))
                    .thenReturn(List.of(account));

            when(cardService.listCards(account, CardStatus.CREATED, CardStatus.ACTIVE, CardStatus.INACTIVE))
                    .thenReturn(List.of(CardResponse.builder().id(UUID.randomUUID()).build()));

            List<CardResponse> cards = customerService.listCards(customerId, accountId);

            assertEquals(1, cards.size());
        }

        @Test
        void shouldCreateCardSuccessfully() {
            UUID customerId = UUID.randomUUID();
            UUID accountId = UUID.randomUUID();
            UUID cardId = UUID.randomUUID();

            CustomerEntity customer = new CustomerEntity();
            customer.setId(customerId);
            customer.setStatus(CustomerStatus.ACTIVE);

            AccountEntity account = new AccountEntity();
            account.setId(accountId);

            PanacheQuery<CustomerEntity> customerQuery = mock(PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(customerQuery);
            when(customerQuery.firstResultOptional()).thenReturn(Optional.of(customer));

            when(accountService.findByCustomerId(customerId, AccountStatus.ACTIVE))
                    .thenReturn(List.of(account));

            when(cardService.createCard(account, CardType.PHYSICAL))
                    .thenReturn(CardResponse.builder().id(cardId).build());

            CardRequest request = new CardRequest("PHYSICAL");
            CardResponse response = customerService.createCard(customerId, accountId, request);

            assertEquals(cardId, response.getId());
        }

        @Test
        void shouldActivateCardSuccessfully() {
            UUID customerId = UUID.randomUUID();
            UUID accountId = UUID.randomUUID();
            UUID cardId = UUID.randomUUID();

            CustomerEntity customer = new CustomerEntity();
            customer.setId(customerId);
            customer.setStatus(CustomerStatus.ACTIVE);

            AccountEntity account = new AccountEntity();
            account.setId(accountId);

            PanacheQuery<CustomerEntity> customerQuery = mock(PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(customerQuery);
            when(customerQuery.firstResultOptional()).thenReturn(Optional.of(customer));

            when(accountService.findByCustomerId(customerId, AccountStatus.ACTIVE))
                    .thenReturn(List.of(account));

            customerService.activateCard(customerId, accountId, cardId);

            verify(cardService).activateCard(cardId);
        }

        @Test
        void shouldInactivateCardSuccessfully() {
            UUID customerId = UUID.randomUUID();
            UUID accountId = UUID.randomUUID();
            UUID cardId = UUID.randomUUID();

            CustomerEntity customer = new CustomerEntity();
            customer.setId(customerId);
            customer.setStatus(CustomerStatus.ACTIVE);

            AccountEntity account = new AccountEntity();
            account.setId(accountId);

            PanacheQuery<CustomerEntity> customerQuery = mock(PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(customerQuery);
            when(customerQuery.firstResultOptional()).thenReturn(Optional.of(customer));

            when(accountService.findByCustomerId(customerId, AccountStatus.ACTIVE))
                    .thenReturn(List.of(account));

            customerService.inactivateCard(customerId, accountId, cardId);

            verify(cardService).inactivateCard(cardId);
        }

        @Test
        void shouldCreateCardDeliveryRequestSuccessfully() {
            UUID customerId = UUID.randomUUID();
            UUID accountId = UUID.randomUUID();
            UUID cardId = UUID.randomUUID();
            UUID carrierId = UUID.randomUUID();

            CustomerEntity customer = new CustomerEntity();
            customer.setId(customerId);
            customer.setStatus(CustomerStatus.ACTIVE);
            customer.setAddress(CustomerAddress.builder().city("Cidade").build());

            AccountEntity account = new AccountEntity();
            account.setId(accountId);

            PanacheQuery<CustomerEntity> customerQuery = mock(PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(customerQuery);
            when(customerQuery.firstResultOptional()).thenReturn(Optional.of(customer));

            when(accountService.findByCustomerId(customerId, AccountStatus.ACTIVE))
                    .thenReturn(List.of(account));

            CardDeliveryRequest request = CardDeliveryRequest.builder().carrierId(carrierId).build();
            CardDeliveryResponse expected = CardDeliveryResponse.builder().cardId(cardId).carrierId(carrierId).build();

            when(cardService.createCardDeliveryRequest(cardId, carrierId, customer.getAddress())).thenReturn(expected);

            CardDeliveryResponse response = customerService.createCardDeliveryRequest(customerId, accountId, cardId, request);

            assertEquals(expected.getCardId(), response.getCardId());
        }

        @Test
        void shouldListCardDeliveryRequestsSuccessfully() {
            UUID customerId = UUID.randomUUID();
            UUID accountId = UUID.randomUUID();
            UUID cardId = UUID.randomUUID();

            CustomerEntity customer = new CustomerEntity();
            customer.setId(customerId);
            customer.setStatus(CustomerStatus.ACTIVE);

            AccountEntity account = new AccountEntity();
            account.setId(accountId);

            PanacheQuery<CustomerEntity> customerQuery = mock(PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(customerQuery);
            when(customerQuery.firstResultOptional()).thenReturn(Optional.of(customer));

            when(accountService.findByCustomerId(customerId, AccountStatus.ACTIVE))
                    .thenReturn(List.of(account));

            CardDeliveryResponse delivery = CardDeliveryResponse.builder().cardId(cardId).build();
            when(cardService.listCardsDeliveryRequest(cardId)).thenReturn(List.of(delivery));

            List<CardDeliveryResponse> responses = customerService.listCardsDeliveryRequest(customerId, accountId, cardId);

            assertEquals(1, responses.size());
            assertEquals(cardId, responses.get(0).getCardId());
        }

        @Test
        void shouldCancelCardDeliveryRequestSuccessfully() {
            UUID customerId = UUID.randomUUID();
            UUID accountId = UUID.randomUUID();
            UUID cardId = UUID.randomUUID();
            UUID deliveryRequestId = UUID.randomUUID();

            CustomerEntity customer = new CustomerEntity();
            customer.setId(customerId);
            customer.setStatus(CustomerStatus.ACTIVE);

            AccountEntity account = new AccountEntity();
            account.setId(accountId);

            PanacheQuery<CustomerEntity> customerQuery = mock(PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(customerQuery);
            when(customerQuery.firstResultOptional()).thenReturn(Optional.of(customer));

            when(accountService.findByCustomerId(customerId, AccountStatus.ACTIVE))
                    .thenReturn(List.of(account));

            customerService.cancelCardDeliveryRequest(customerId, accountId, cardId, deliveryRequestId);

            verify(cardService).cancelCardDeliveryRequest(cardId, deliveryRequestId);
        }

        @Test
        void shouldCreateVirtualCardSuccessfully() {
            UUID customerId = UUID.randomUUID();
            UUID accountId = UUID.randomUUID();
            UUID cardId = UUID.randomUUID();

            CustomerEntity customer = new CustomerEntity();
            customer.setId(customerId);
            customer.setStatus(CustomerStatus.ACTIVE);

            AccountEntity account = new AccountEntity();
            account.setId(accountId);

            PanacheQuery<CustomerEntity> customerQuery = mock(PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(customerQuery);
            when(customerQuery.firstResultOptional()).thenReturn(Optional.of(customer));

            when(accountService.findByCustomerId(customerId, AccountStatus.ACTIVE)).thenReturn(List.of(account));

            doNothing().when(cardService).checkCardPhysicalDeliveryRequest(account.getId());

            when(cardService.createCard(account, CardType.VIRTUAL)).thenReturn(CardResponse.builder().id(cardId).build());

            CardRequest request = new CardRequest("VIRTUAL");
            CardResponse response = customerService.createCard(customerId, accountId, request);

            assertEquals(cardId, response.getId());
            verify(cardService).checkCardPhysicalDeliveryRequest(account.getId());
        }

        @Test
        void shouldThrowExceptionWhenInvalidCardTypeProvided() {
            UUID customerId = UUID.randomUUID();
            UUID accountId = UUID.randomUUID();

            CustomerEntity customer = new CustomerEntity();
            customer.setId(customerId);
            customer.setStatus(CustomerStatus.ACTIVE);

            AccountEntity account = new AccountEntity();
            account.setId(accountId);

            PanacheQuery<CustomerEntity> customerQuery = mock(PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(customerQuery);
            when(customerQuery.firstResultOptional()).thenReturn(Optional.of(customer));

            when(accountService.findByCustomerId(customerId, AccountStatus.ACTIVE)).thenReturn(List.of(account));

            CardRequest invalidRequest = new CardRequest("INVALID_TYPE");

            assertThrows(IllegalArgumentException.class, () ->
                    customerService.createCard(customerId, accountId, invalidRequest)
            );
        }

    }

    @Nested
    class CardTestsFailure {

        @Test
        void shouldThrowExceptionWhenCustomerNotFoundOnListCards() {
            UUID customerId = UUID.randomUUID();
            UUID accountId = UUID.randomUUID();

            var customerQuery = mock(io.quarkus.hibernate.orm.panache.PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(customerQuery);
            when(customerQuery.firstResultOptional()).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class, () ->
                    customerService.listCards(customerId, accountId)
            );

            assertEquals("Customer not found", exception.getMessage());
            verify(cardService, never()).listCards(any(), any());
        }

        @Test
        void shouldThrowExceptionWhenCustomerNotFoundOnCreateCard() {
            UUID customerId = UUID.randomUUID();
            UUID accountId = UUID.randomUUID();

            var customerQuery = mock(io.quarkus.hibernate.orm.panache.PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(customerQuery);
            when(customerQuery.firstResultOptional()).thenReturn(Optional.empty());

            CardRequest request = new CardRequest(CardType.PHYSICAL.name());

            NotFoundException exception = assertThrows(NotFoundException.class, () ->
                    customerService.createCard(customerId, accountId, request)
            );

            assertEquals("Customer not found", exception.getMessage());
            verify(cardService, never()).createCard(any(), any());
        }

        @Test
        void shouldThrowExceptionWhenCustomerNotFoundOnActivateCard() {
            UUID customerId = UUID.randomUUID();
            UUID accountId = UUID.randomUUID();
            UUID cardId = UUID.randomUUID();

            var customerQuery = mock(io.quarkus.hibernate.orm.panache.PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(customerQuery);
            when(customerQuery.firstResultOptional()).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class, () ->
                    customerService.activateCard(customerId, accountId, cardId)
            );

            assertEquals("Customer not found", exception.getMessage());
            verify(cardService, never()).activateCard(any(CardEntity.class));
        }

        @Test
        void shouldThrowExceptionWhenCustomerNotFoundOnInactivateCard() {
            UUID customerId = UUID.randomUUID();
            UUID accountId = UUID.randomUUID();
            UUID cardId = UUID.randomUUID();

            var customerQuery = mock(io.quarkus.hibernate.orm.panache.PanacheQuery.class);
            when(customerRepository.find("id = ?1 AND status = ?2", customerId, CustomerStatus.ACTIVE))
                    .thenReturn(customerQuery);
            when(customerQuery.firstResultOptional()).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class, () ->
                    customerService.inactivateCard(customerId, accountId, cardId)
            );

            assertEquals("Customer not found", exception.getMessage());
            verify(cardService, never()).inactivateCard(any());
        }
    }



}
