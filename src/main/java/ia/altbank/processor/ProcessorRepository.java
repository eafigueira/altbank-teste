package ia.altbank.processor;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class ProcessorRepository implements PanacheRepositoryBase<Processor, UUID> {
}
