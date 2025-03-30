package ia.altbank.card;

import ia.altbank.exception.EnumValidator;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardRequest {
    @NotBlank(message = "Type id is required")
    @EnumValidator(enumClass = CardType.class, message = "Invalid card type: PHYSICAL, VIRTUAL")
    private String type;

}
