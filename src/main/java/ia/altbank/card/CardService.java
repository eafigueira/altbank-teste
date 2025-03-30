package ia.altbank.card;

import ia.altbank.account.AccountEntity;
import ia.altbank.account.AccountService;
import ia.altbank.exception.NotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final AccountService accountService;
    private final CardDeliveryService cardDeliveryService;

    private CardEntity generateNewCard(AccountEntity account, CardType cardType) {
        CardEntity card = new CardEntity();
        card.setAccount(account);
        card.setType(cardType);
        card.setNumber(generateRandomCardNumber());
        card.setStatus(CardStatus.CREATED);
        return card;
    }

    private String generateRandomCvv() {
        var random = new Random();
        return String.format("%03d", random.nextInt(999) + 1);
    }

    private String generateRandomCardNumber() {
        var random = new Random();
        StringBuilder result = new StringBuilder();

        result.append(random.nextInt(9) + 1);
        for (int i = 0; i < 15; i++) {
            result.append(random.nextInt(10));
        }
        return result.toString();
    }

    private AccountEntity findAccount(UUID accountId) {
        return accountService.findByIdOptional(accountId).orElseThrow(() -> new NotFoundException("Account not found"));
    }

    private CardEntity findCard(UUID cardId) {
        return cardRepository.findByIdOptional(cardId).orElseThrow(() -> new NotFoundException("Card not found"));
    }

    private CardResponse toResponse(CardEntity card) {
        return CardResponse.builder()
                .type(card.getType())
                .number(card.getNumber())
                .status(card.getStatus())
                .cvv(card.getCvv())
                .cvvExpiration(card.getCvvExpiration())
                .id(card.getId())
                .build();
    }

    public CardEntity createCard(AccountEntity account, CardType cardType) {
        CardEntity card = generateNewCard(account, cardType);
        card.setCvv(generateRandomCvv());
        cardRepository.persist(card);
        return card;
    }

    @Transactional
    public CardResponse createCard(UUID accountId, CardType cardType) {
       var account = findAccount(accountId);
        CardEntity card = generateNewCard(account, cardType);
        card.setCvv(generateRandomCvv());
        cardRepository.persist(card);
        return toResponse(card);
    }

    @Transactional
    public void delete(UUID cardId) {
        var card = findCard(cardId);
        cardRepository.delete(card);
    }

    public CardResponse getCard(UUID cardId) {
        var card = findCard(cardId);
        return toResponse(card);
    }

    public List<CardResponse> getAll(UUID accountId) {
        findAccount(accountId);
        return cardRepository.list("account.id = ?1", accountId).stream().map(this::toResponse).toList();
    }

    public CardResponse activateCard(UUID cardId) {
        var card = findCard(cardId);
        if (CardStatus.ACTIVE.equals(card.getStatus())) {
            throw new IllegalStateException("Card is already active");
        }
        if (CardType.PHYSICAL.equals(card.getType())) {
            cardDeliveryService.checkCardDeliveryRequest(card.getId());
        }
        card.setStatus(CardStatus.ACTIVE);
        cardRepository.persist(card);
        return toResponse(card);
    }

    private void deactivateCard(CardEntity card) {
        card.setStatus(CardStatus.INACTIVE);
        cardRepository.persist(card);
    }

    public CardResponse deactivateCard(UUID cardId) {
        var card = findCard(cardId);
        deactivateCard(card);
        return toResponse(card);
    }
    public void inactivateCardsByAccountId(UUID accountId) {
        cardRepository.list("account.id = ?1", accountId).forEach(this::deactivateCard);
    }
}
