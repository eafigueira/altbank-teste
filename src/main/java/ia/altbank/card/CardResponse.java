package ia.altbank.card;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CardResponse {
    private UUID id;
    private CardType type;
    private String number;
    private String cvv;
    private LocalDateTime cvvExpiration;
    private CardStatus status;
}
