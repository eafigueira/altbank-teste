package ia.altbank.card;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class WebhookService {
    private final CardRepository cardRepository;

    @Transactional
    public boolean processDelivery(CardDeliveryWebhookRequest payload) {
        Card card = cardRepository.find("deliveryTrackingId", payload.getTrackingId()).firstResult();
        if (card == null) return false;

//        card.setDeliveryStatus(payload.getDeliveryStatus());
//        card.setDeliveryDate(payload.getDeliveryDate());
//        card.setDeliveryReturnReason(payload.getDeliveryReturnReason());

//        if ("DELIVERED".equalsIgnoreCase(payload.getDeliveryStatus())) {
//            card.setStatus(CardStatus.DELIVERED);
//        }

        return true;
    }
}
