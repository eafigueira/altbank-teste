package ia.altbank.card;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class CardResource {

    private final CardService cardService;

    CardResponse create(UUID accountId, CardType cardType) {
        return cardService.createCard(accountId, cardType);
    }

    void delete(UUID cardId) {
        cardService.delete(cardId);
    }

    CardResponse get(UUID cardId) {
        return cardService.getCard(cardId);
    }

    List<CardResponse> getAll(UUID accountId) {
        return cardService.getAll(accountId);
    }

    CardResponse activate(UUID cardId) {
        return cardService.activateCard(cardId);
    }

    CardResponse deactivate(UUID cardId) {
        return cardService.deactivateCard(cardId);
    }
}
