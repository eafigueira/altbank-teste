package ia.altbank.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateCustomerRequest {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Document Number is required")
    private String documentNumber;
    @NotBlank(message = "Email is required")
    private String email;
    @NotNull
    private CustomerAddressRequest address;
}
