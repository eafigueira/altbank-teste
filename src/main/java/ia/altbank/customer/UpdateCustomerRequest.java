package ia.altbank.customer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateCustomerRequest {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Document Number is required")
    private String documentNumber;
    private String email;
    @Valid
    @NotNull
    private CustomerAddressRequest address;
}
