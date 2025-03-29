package ia.altbank.dto;

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
}
