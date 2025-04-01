package ia.altbank.card;

import ia.altbank.exception.EnumValidator;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardRequest {
    @NotBlank(message = "Type id is required")
    @EnumValidator(enumClass = CardType.class, message = "Invalid card type: PHYSICAL, VIRTUAL")
    private String type;

}
