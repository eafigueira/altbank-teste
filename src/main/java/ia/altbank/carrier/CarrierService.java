package ia.altbank.carrier;

import ia.altbank.exception.NotFoundException;
import io.quarkus.panache.common.Page;
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

    private CarrierEntity findCarrierActive(UUID carrierId) {
        return carrierRepository.find("id = ?1 AND status = ?2", carrierId, CarrierStatus.ACTIVE).firstResultOptional().orElseThrow(() -> new NotFoundException("Carrier not found"));
    }

    public CarrierEntity validateCarrier(String clientId, String clientSecret) {
        CarrierEntity carrier = carrierRepository.find("clientId", clientId).firstResult();
        if (carrier == null || !carrier.getClientSecret().equals(clientSecret)) {
            return null;
        }
        return carrier;
    }

    public List<CarrierResponse> findAll(int page, int size) {
        return carrierRepository.find("status = ?1", CarrierStatus.ACTIVE)
                .page(Page.of(page, size))
                .list()
                .stream()
                .map(this::toResponse)
                .peek(CarrierResponse::hideAuthInfo)
                .collect(Collectors.toList());
    }

    public CarrierResponse findById(UUID id, boolean includeAuthInfo) {
        var result = findCarrierActive(id);
        CarrierResponse response = toResponse(result);
        if (!includeAuthInfo) response.hideAuthInfo();
        return response;
    }

    @Transactional
    public CarrierResponse create(CarrierRequest request) {
        var foundCarrier = carrierRepository.find("documentNumber = ?1 AND status = ?2", request.getDocumentNumber(), CarrierStatus.ACTIVE).firstResult();
        if (foundCarrier != null)
            throw new IllegalStateException("Carrier with document number " + request.getDocumentNumber() + " already exists");

        CarrierEntity carrier = new CarrierEntity();
        carrier.setName(request.getName());
        carrier.setDocumentNumber(request.getDocumentNumber());
        carrier.setClientId(UUID.randomUUID().toString());
        carrier.setClientSecret(UUID.randomUUID().toString());
        carrier.setStatus(CarrierStatus.ACTIVE);

        carrierRepository.persist(carrier);
        return toResponse(carrier);
    }

    @Transactional
    public CarrierResponse update(UUID id, CarrierRequest request) {
        CarrierEntity carrier = findCarrierActive(id);
        if (!carrier.getDocumentNumber().equals(request.getDocumentNumber())) {
            var foundCarrier = carrierRepository.find("documentNumber = ?1", request.getDocumentNumber()).firstResult();
            if (foundCarrier != null) {
                throw new IllegalStateException("Carrier with document number " + request.getDocumentNumber() + " already exists");
            }
        }
        carrier.setName(request.getName());
        carrier.setDocumentNumber(request.getDocumentNumber());
        carrierRepository.persist(carrier);
        return toResponse(carrier);
    }

    @Transactional
    public CarrierResponse regenerateCredentials(UUID id) {
        CarrierEntity carrier = findCarrierActive(id);
        carrier.setClientId(UUID.randomUUID().toString());
        carrier.setClientSecret(UUID.randomUUID().toString());
        carrierRepository.persist(carrier);

        return toResponse(carrier);
    }

    @Transactional
    public void inactivate(UUID id) {
        CarrierEntity carrier = findCarrierActive(id);
        carrier.setStatus(CarrierStatus.INACTIVE);
        carrierRepository.persist(carrier);
    }

    private CarrierResponse toResponse(CarrierEntity carrier) {
        return CarrierResponse.builder()
                .id(carrier.getId())
                .name(carrier.getName())
                .documentNumber(carrier.getDocumentNumber())
                .clientId(carrier.getClientId())
                .clientSecret(carrier.getClientSecret())
                .status(carrier.getStatus())
                .build();
    }
}
