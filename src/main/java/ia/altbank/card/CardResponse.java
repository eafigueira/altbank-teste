package ia.altbank.card;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardResponse {
    private UUID id;
    private CardType type;
    private String number;
    private String cvv;
    private LocalDateTime cvvExpiration;
    private CardStatus status;
}
