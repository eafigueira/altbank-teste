package ia.altbank.carrier;

import ia.altbank.customer.CustomerDTO;
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

    public Carrier validateCarrier(String clientId, String clientSecret) {
        Carrier carrier = carrierRepository.find("clientId", clientId).firstResult();
        if (carrier == null || !carrier.getClientSecret().equals(clientSecret) || !carrier.isDefaultCarrier()) {
            return null;
        }
        return carrier;
    }

    public List<CarrierResponse> findAll(int page, int size) {
        return carrierRepository.findAll()
                .page(Page.of(page, size))
                .list()
                .stream()
                .map(this::toResponse)
                .peek(CarrierResponse::hideAuthInfo)
                .collect(Collectors.toList());
    }

    public CarrierResponse findById(UUID id) {
        return findById(id, false);
    }

    public CarrierResponse findById(UUID id, boolean includeAuthInfo) {
        var result = carrierRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Carrier not found"));
        CarrierResponse response = toResponse(result);
        if (!includeAuthInfo) response.hideAuthInfo();
        return response;
    }

    @Transactional
    public CarrierResponse create(CarrierRequest request) {
        var foundCarrier = carrierRepository.find("documentNumber = ?1", request.getDocumentNumber()).firstResult();
        if (foundCarrier != null)
            throw new IllegalStateException("Carrier with document number " + request.getDocumentNumber() + " already exists");

        Carrier carrier = new Carrier();
        carrier.setName(request.getName());
        carrier.setDocumentNumber(request.getDocumentNumber());
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
        if (carrier == null) {
            throw new NotFoundException("Carrier not found");
        }
        if (!carrier.getDocumentNumber().equals(request.getDocumentNumber())) {
            var foundCarrier = carrierRepository.find("documentNumber = ?1", request.getDocumentNumber()).firstResult();
            if (foundCarrier != null) {
                throw new IllegalStateException("Carrier with document number " + request.getDocumentNumber() + " already exists");
            }
        }
        carrier.setName(request.getName());
        carrier.setDocumentNumber(request.getDocumentNumber());
        if (request.isDefaultCarrier()) {
            carrierRepository.update("defaultCarrier = false");
        }
        carrier.setDefaultCarrier(request.isDefaultCarrier());
        return toResponse(carrier);
    }

    @Transactional
    public CarrierResponse regenerateCredentials(UUID id) {
        Carrier carrier = carrierRepository.findById(id);
        if (carrier == null) {
            throw new NotFoundException("Carrier not found");
        }
        carrier.setClientId(UUID.randomUUID().toString());
        carrier.setClientSecret(UUID.randomUUID().toString());

        return toResponse(carrier);
    }

    @Transactional
    public void delete(UUID id) {
        Carrier carrier = carrierRepository.findById(id);
        if (carrier != null && carrier.isDefaultCarrier()) {
            throw new IllegalStateException("Cannot delete the default carrier");
        }
        carrierRepository.delete(carrier);
    }

    private CarrierResponse toResponse(Carrier carrier) {
        return CarrierResponse.builder()
                .id(carrier.getId())
                .name(carrier.getName())
                .documentNumber(carrier.getDocumentNumber())
                .clientId(carrier.getClientId())
                .clientSecret(carrier.getClientSecret())
                .defaultCarrier(carrier.isDefaultCarrier())
                .build();
    }
}
