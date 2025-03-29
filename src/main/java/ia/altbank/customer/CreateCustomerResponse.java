package ia.altbank.customer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class CreateCustomerResponse {
    private UUID customerId;
    private UUID accountId;
    private UUID cardId;
    private AddressDTO address;
}
