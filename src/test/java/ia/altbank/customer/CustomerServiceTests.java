package ia.altbank.customer;

import ia.altbank.account.AccountEntity;
import ia.altbank.account.AccountService;
import ia.altbank.card.CardResponse;
import ia.altbank.card.CardService;
import ia.altbank.card.CardType;
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
    }
}
