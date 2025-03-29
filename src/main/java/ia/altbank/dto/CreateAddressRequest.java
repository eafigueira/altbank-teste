package ia.altbank.dto;

import ia.altbank.model.Address;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for {@link Address}
 */
public record CreateAddressRequest(
        @NotBlank(message = "Street is required")
        String street,
        String number,
        String complement,
        @NotBlank(message = "Neighborhood is required")
        String neighborhood,
        @NotBlank(message = "City is required")
        String city,
        @NotBlank(message = "State is required")
        String state,
        @NotBlank(message = "Zip Code is required")
        String zipCode
) {
}