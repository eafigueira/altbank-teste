package ia.altbank.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateAddressRequest {
        @NotBlank(message = "Street is required")
        private String street;
        private String number;
        private String complement;

        @NotBlank(message = "Neighborhood is required")
        private String neighborhood;

        @NotBlank(message = "City is required")
        private String city;

        @NotBlank(message = "State is required")
        private String state;

        @NotBlank(message = "Zip Code is required")
        private String zipCode;
}