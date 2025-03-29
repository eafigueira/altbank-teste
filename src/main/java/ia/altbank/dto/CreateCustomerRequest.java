package ia.altbank.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

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
    private CreateAddressRequest address;
}
