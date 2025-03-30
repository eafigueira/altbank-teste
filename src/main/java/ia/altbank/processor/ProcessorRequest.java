package ia.altbank.processor;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessorRequest {
    @NotBlank(message = "Name is required")
    private String name;
}
