package ia.altbank.card;

import ia.altbank.account.AccountEntity;
import ia.altbank.customer.CustomerAddress;
import ia.altbank.exception.NotFoundException;
import ia.altbank.hooks.ChangeCVVWebhookRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static java.util.UUID.fromString;

@ApplicationScoped
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CardDeliveryRequestService cardDeliveryRequestService;

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


    public CardResponse createCard(AccountEntity account, CardType cardType) {
        CardEntity card = generateNewCard(account, cardType);
        card.setCvv(generateRandomCvv());
        card.setCvvExpiration(LocalDateTime.now().plusYears(3));
        cardRepository.persist(card);
        return toResponse(card);
    }

    private void inactivateCard(CardEntity card) {
        cardDeliveryRequestService.cancelCardDeliveriesByCardId(card);
        card.setStatus(CardStatus.INACTIVE);
        cardRepository.persist(card);
    }

    public void activateCard(CardEntity card) {
        card.setStatus(CardStatus.ACTIVE);
        cardRepository.persist(card);
    }

    public void inactivateCardsByAccountId(UUID accountId) {
        cardRepository.list("account.id = ?1", accountId).forEach(this::inactivateCard);
    }

    public List<CardResponse> listCards(AccountEntity account, CardStatus... statuses) {
        return cardRepository.list("account.id = ?1 AND status IN ?2", account.getId(), List.of(statuses)).stream().map(this::toResponse).toList();
    }

    public void inactivateCard(UUID cardId) {
        var card = cardRepository.findByIdOptional(cardId).orElseThrow(() -> new NotFoundException("Card not found"));
        inactivateCard(card);
    }

    public void activateCard(UUID cardId) {
        var card = cardRepository.findByIdOptional(cardId).orElseThrow(() -> new NotFoundException("Card not found"));
        activateCard(card);
    }

    public void checkCardPhysicalDeliveryRequest(UUID accountId) {
        var cards = cardRepository.find("account.id = ?1 AND type = ?2", accountId, CardType.PHYSICAL).list();
        if (!cards.isEmpty()) {
            //--> get first card
            var card = cards.get(0);
            var lastDelivery = cardDeliveryRequestService.getLastDeliveryCardRequest(card.getId());
            if (lastDelivery == null || lastDelivery.getDeliveryStatus() != DeliveryStatus.DELIVERED) {
                throw new IllegalStateException("A physical card has not been delivered yet");
            }
        }
    }

    public void checkNumberOfPhysicalCardsInAccount(UUID accountId) {
        var count = cardRepository.find("account.id = ?1 AND type = ?2", accountId, CardType.PHYSICAL).count();
        if (count > 1) {
            throw new IllegalStateException("Maximum number of physical cards reached");
        }
    }

    public CardDeliveryResponse createCardDeliveryRequest(UUID cardId, UUID carrierId, CustomerAddress address) {
        var card = cardRepository.findByIdOptional(cardId).orElseThrow(() -> new NotFoundException("Card not found"));
        if (card.getType() != CardType.PHYSICAL) {
            throw new IllegalStateException("Card is not physical");
        }
        return cardDeliveryRequestService.createCardDeliveryRequest(card, carrierId, address);
    }

    public List<CardDeliveryResponse> listCardsDeliveryRequest(UUID cardId) {
        var card = cardRepository.findByIdOptional(cardId).orElseThrow(() -> new NotFoundException("Card not found"));
        return cardDeliveryRequestService.listCardsDeliveryRequest(card);
    }

    public void cancelCardDeliveryRequest(UUID cardId, UUID deliveryRequestId) {
        var card = cardRepository.findByIdOptional(cardId).orElseThrow(() -> new NotFoundException("Card not found"));
        cardDeliveryRequestService.cancelCardDeliveryRequest(card, deliveryRequestId);
    }

    @Transactional
    public void processCVVUpdate(@Valid ChangeCVVWebhookRequest changeCVVWebhookRequest) {
        var card = cardRepository.find("account.id = ?1 AND id = ?2 AND status <> ?3",
                        fromString(changeCVVWebhookRequest.getAccount_id()),
                        fromString(changeCVVWebhookRequest.getCard_id()),
                        CardStatus.INACTIVE
                )
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Card not found"));

        if (!CardType.VIRTUAL.equals(card.getType())) {
            throw new IllegalStateException("Card is not virtual");
        }
        card.setCvv(changeCVVWebhookRequest.getNext_cvv());
        card.setCvvExpiration(changeCVVWebhookRequest.getExpiration_date());
        card.setStatus(CardStatus.ACTIVE);
        cardRepository.persist(card);
    }
}
