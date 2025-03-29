package ia.altbank.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class CreateCustomerRequest {
    private String name;
    private String documentNumber;
    private String email;
}
