package ia.altbank.service;

import ia.altbank.enums.CardStatus;
import ia.altbank.model.Card;
import ia.altbank.repository.CardRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class CardService {

    private final CardRepository repository;

    @Transactional
    public void deleteByAccountId(UUID accountId) {
        List<Card> cards = findAllByAccountId(accountId);
        cards.forEach(Card -> Card.setStatus(CardStatus.DELETED));
        repository.persist(cards);
    }

    private List<Card> findAllByAccountId(UUID accountId) {
        return repository.findAllByAccountId(accountId);
    }
}
