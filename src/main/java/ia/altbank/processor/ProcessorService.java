package ia.altbank.processor;

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
public class ProcessorService {

    private final ProcessorRepository repository;

    private ProcessorEntity findProcessorActive(UUID processorId) {
        return repository.find("id = ?1 AND status = ?2", processorId, ProcessorStatus.ACTIVE).firstResultOptional()
                .orElseThrow(() -> new NotFoundException("Processor not found"));
    }


    @Transactional
    public ProcessorResponse create(ProcessorRequest request) {
        ProcessorEntity processor = new ProcessorEntity();
        processor.setName(request.getName());
        processor.setClientId(UUID.randomUUID().toString());
        processor.setClientSecret(UUID.randomUUID().toString());
        processor.setStatus(ProcessorStatus.ACTIVE);

        repository.persist(processor);
        return toResponse(processor);
    }

    @Transactional
    public void update(UUID id, ProcessorRequest request) {
        ProcessorEntity processor = findProcessorActive(id);
        processor.setName(request.getName());
        repository.persist(processor);
    }
    @Transactional
    public ProcessorResponse regenerateCredentials(UUID id) {
        ProcessorEntity processor = findProcessorActive(id);
        processor.setClientId(UUID.randomUUID().toString());
        processor.setClientSecret(UUID.randomUUID().toString());
        repository.persist(processor);

        return toResponse(processor);
    }

    public List<ProcessorResponse> list(int page, int size) {
        return repository.find("status = ?1", ProcessorStatus.ACTIVE)
                .page(Page.of(page, size))
                .list()
                .stream()
                .map(this::toResponse)
                .peek(ProcessorResponse::hideAuthInfo)
                .collect(Collectors.toList());
    }

    public ProcessorResponse findOne(UUID id) {
        ProcessorEntity processor = findProcessorActive(id);
        return toResponse(processor);
    }
    @Transactional
    public void inactivate(UUID id) {
        ProcessorEntity processor = findProcessorActive(id);
        processor.setStatus(ProcessorStatus.INACTIVE);
        repository.persist(processor);
    }

    private ProcessorResponse toResponse(ProcessorEntity processor) {
        return ProcessorResponse.builder()
                .id(processor.getId())
                .name(processor.getName())
                .clientId(processor.getClientId())
                .clientSecret(processor.getClientSecret())
                .status(processor.getStatus())
                .build();
    }
}
