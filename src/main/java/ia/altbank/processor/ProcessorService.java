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
        ProcessorEntity processor = repository.findByIdOptional(id).orElseThrow(() -> new NotFoundException("Processor not found"));
        processor.setName(request.getName());
        repository.persist(processor);
    }
    @Transactional
    public ProcessorResponse regenerateCredentials(UUID id) {
        ProcessorEntity processor = repository.findByIdOptional(id).orElseThrow(() -> new NotFoundException("Processor not found"));
        processor.setClientId(UUID.randomUUID().toString());
        processor.setClientSecret(UUID.randomUUID().toString());
        repository.persist(processor);

        return toResponse(processor);
    }

    public List<ProcessorResponse> list(int page, int size) {
        return repository.findAll()
                .page(Page.of(page, size))
                .list()
                .stream()
                .map(this::toResponse)
                .peek(ProcessorResponse::hideAuthInfo)
                .collect(Collectors.toList());
    }

    public ProcessorResponse findOne(UUID id) {
        ProcessorEntity processor = repository.findByIdOptional(id).orElseThrow(() -> new NotFoundException("Processor not found"));
        return toResponse(processor);
    }
    @Transactional
    public void inactivate(UUID id) {
        ProcessorEntity processor = repository.findByIdOptional(id).orElseThrow(() -> new NotFoundException("Processor not found"));
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
