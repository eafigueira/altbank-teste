package ia.altbank.card;

import ia.altbank.carrier.CarrierRepository;
import ia.altbank.customer.CustomerAddress;
import ia.altbank.exception.NotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class CardDeliveryRequestService {

    private final CardDeliveryRequestRepository cardDeliveryRequestRepository;
    private final CarrierRepository carrierRepository;

    public CardDeliveryResponse getLastDeliveryCardRequest(UUID cardId) {
        return cardDeliveryRequestRepository.find("card.id = ?1", cardId)
                .stream()
                .max(Comparator.comparing(CardDeliveryRequestEntity::getCreatedAt))
                .map(this::toResponse)
                .orElse(null);

    }

    public CardDeliveryResponse createCardDeliveryRequest(CardEntity card, UUID carrierId, CustomerAddress address) {
        var carrier = carrierRepository.find("id = ?1", carrierId).firstResultOptional().orElseThrow(() -> new NotFoundException("Carrier not found"));

        CardDeliveryRequestEntity deliveryRequest = new CardDeliveryRequestEntity();
        deliveryRequest.setCard(card);
        deliveryRequest.setCarrier(carrier);
        deliveryRequest.setTrackingCode(UUID.randomUUID().toString());
        deliveryRequest.setDeliveryStatus(DeliveryStatus.PENDING);
        deliveryRequest.setDeliveryAddress(makeStringAddress(address));

        cardDeliveryRequestRepository.persist(deliveryRequest);

        return toResponse(deliveryRequest);
    }

    private String makeStringAddress(CustomerAddress address) {
        StringBuilder addressBuilder = new StringBuilder();
        addressBuilder.append(address.getStreet()).append(", ");
        if (address.getNumber() != null) {
            addressBuilder.append(address.getNumber()).append(", ");
        }
        if (address.getComplement() != null) {
            addressBuilder.append(address.getComplement()).append(", ");
        }
        if (address.getNeighborhood() != null) {
            addressBuilder.append(address.getNeighborhood()).append(", ");
        }
        if (address.getCity() != null) {
            addressBuilder.append(address.getCity()).append(", ");
        }
        if (address.getState() != null) {
            addressBuilder.append(address.getState()).append(", ");
        }
        if (address.getZipCode() != null) {
            addressBuilder.append(address.getZipCode());
        }
        return addressBuilder.toString();
    }

    public List<CardDeliveryResponse> listCardsDeliveryRequest(CardEntity card) {
        return cardDeliveryRequestRepository.find("card.id = ?1", card.getId())
                .stream().map(this::toResponse).toList();
    }

    public void cancelCardDeliveriesByCardId(CardEntity card) {
        cardDeliveryRequestRepository.find("card.id = ?1", card.getId())
                .stream()
                .forEach(cardDeliveryRequestEntity -> cancelCardDeliveryRequest(cardDeliveryRequestEntity.getCard(), cardDeliveryRequestEntity.getId()));
    }

    private CardDeliveryResponse toResponse(CardDeliveryRequestEntity deliveryRequestEntity) {
        return CardDeliveryResponse.builder()
                .id(deliveryRequestEntity.getId())
                .carrierId(deliveryRequestEntity.getCarrier().getId())
                .trackingCode(deliveryRequestEntity.getTrackingCode())
                .createdAt(deliveryRequestEntity.getCreatedAt())
                .deliveryStatus(deliveryRequestEntity.getDeliveryStatus())
                .deliveryAddress(deliveryRequestEntity.getDeliveryAddress())
                .deliveredAt(deliveryRequestEntity.getDeliveredAt())
                .build();
    }

    public void cancelCardDeliveryRequest(CardEntity card, UUID deliveryRequestId) {
        var cardDeliveryRequest = cardDeliveryRequestRepository.find("card.id = ?1", card.getId())
                .stream()
                .filter(cardDeliveryRequestEntity -> deliveryRequestId.equals(cardDeliveryRequestEntity.getId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Card delivery request not found"));
        cardDeliveryRequest.setDeliveryStatus(DeliveryStatus.CANCELED);
        cardDeliveryRequestRepository.persist(cardDeliveryRequest);
    }
}
