package ia.altbank.customer;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class AddressDTO implements Serializable {
    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private String zipCode;

    public AddressDTO(CustomerAddress address) {
        this.street = address.getStreet();
        this.number = address.getNumber();
        this.complement = address.getComplement();
        this.neighborhood = address.getNeighborhood();
        this.city = address.getCity();
        this.state = address.getState();
        this.zipCode = address.getZipCode();
    }
}