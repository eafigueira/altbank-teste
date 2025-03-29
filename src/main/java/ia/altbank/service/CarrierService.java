package ia.altbank.service;

import ia.altbank.dto.CarrierRequest;
import ia.altbank.dto.CarrierResponse;
import ia.altbank.model.Carrier;
import ia.altbank.repository.CarrierRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class CarrierService {
    private final CarrierRepository carrierRepository;

    public Carrier validateCarrier(String clientId, String clientSecret) {
        Carrier carrier = carrierRepository.find("clientId", clientId).firstResult();
        if (carrier == null || !carrier.getClientSecret().equals(clientSecret) || !carrier.isDefaultCarrier()) {
            return null;
        }
        return carrier;
    }

    public List<CarrierResponse> findAll() {
        return carrierRepository.listAll().stream()
                .map(this::toResponse)
                .peek(CarrierResponse::hideSecret)
                .collect(Collectors.toList());
    }

    public CarrierResponse findById(UUID id) {
        return findById(id, false);
    }

    public CarrierResponse findById(UUID id, boolean includeSecret) {
        Carrier carrier = carrierRepository.findById(id);
        CarrierResponse response = toResponse(carrier);
        if (!includeSecret) response.hideSecret();
        return response;
    }

    @Transactional
    public CarrierResponse create(CarrierRequest request) {
        Carrier carrier = new Carrier();
        carrier.setName(request.getName());
        carrier.setDefaultCarrier(request.isDefaultCarrier());
        carrier.setClientId(UUID.randomUUID().toString());
        carrier.setClientSecret(UUID.randomUUID().toString());

        if (request.isDefaultCarrier()) {
            carrierRepository.update("defaultCarrier = false");
        }
        carrierRepository.persist(carrier);
        return toResponse(carrier);
    }

    @Transactional
    public CarrierResponse update(UUID id, CarrierRequest request) {
        Carrier carrier = carrierRepository.findById(id);
        if (carrier != null) {
            carrier.setName(request.getName());
            if (request.isDefaultCarrier()) {
                carrierRepository.update("defaultCarrier = false");
            }
            carrier.setDefaultCarrier(request.isDefaultCarrier());
        }
        return toResponse(carrier);
    }

    @Transactional
    public CarrierResponse regenerateCredentials(UUID id) {
        Carrier carrier = carrierRepository.findById(id);
        if (carrier != null) {
            carrier.setClientId(UUID.randomUUID().toString());
            carrier.setClientSecret(UUID.randomUUID().toString());
        }
        return toResponse(carrier);
    }

    @Transactional
    public void delete(UUID id) {
        Carrier carrier = carrierRepository.findById(id);
        if (carrier != null && carrier.isDefaultCarrier()) {
            throw new IllegalStateException("Cannot delete the default carrier");
        }
        carrierRepository.deleteById(id);
    }

    private CarrierResponse toResponse(Carrier carrier) {
        return CarrierResponse.builder()
                .id(carrier.getId())
                .name(carrier.getName())
                .clientId(carrier.getClientId())
                .clientSecret(carrier.getClientSecret())
                .defaultCarrier(carrier.isDefaultCarrier())
                .build();
    }


}
