package ia.altbank.customer;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerAddress {
    @Column(nullable = false, length = 150)
    private String street;
    @Column(length = 50)
    private String number;
    @Column(length = 50)
    private String complement;
    @Column(nullable = false, length = 50)
    private String neighborhood;
    @Column(nullable = false, length = 50)
    private String city;
    @Column(nullable = false, length = 30)
    private String state;
    @Column(nullable = false, length = 10)
    private String zipCode;

    public CustomerAddress(@NotNull CustomerAddressRequest address) {
        this.street = address.getStreet();
        this.number = address.getNumber();
        this.complement = address.getComplement();
        this.neighborhood = address.getNeighborhood();
        this.city = address.getCity();
        this.state = address.getState();
        this.zipCode = address.getZipCode();
    }
}
