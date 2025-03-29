package ia.altbank.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class CustomerDTO {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String name;
    private String documentNumber;
    private String email;
    private AddressDTO address;

    public CustomerDTO(Customer customer) {
        this.id = customer.getId();
        this.createdAt = customer.getCreatedAt();
        this.updatedAt = customer.getUpdatedAt();
        this.name = customer.getName();
        this.documentNumber = customer.getDocumentNumber();
        this.email = customer.getEmail();
        this.address = new AddressDTO(customer.getAddress());
    }
}