package ia.altbank.customer;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CustomerAccountResponse {
    private UUID accountId;

    public CustomerAccountResponse(UUID accountId) {
        this.accountId = accountId;
    }
}
