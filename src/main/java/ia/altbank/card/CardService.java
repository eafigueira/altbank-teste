package ia.altbank.card;

import ia.altbank.account.Account;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    public Card createCard(Account account, CardType cardType) {
        Card card = new Card();
        card.setAccount(account);
        card.setType(cardType);
        card.setNumber(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        card.setStatus(CardStatus.CREATED);
        cardRepository.persist(card);
        return card;
    }

    public void deleteByAccountId(UUID accountId) {
        cardRepository.delete("account.id = ?1", accountId);
    }
}
