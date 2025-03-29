package ia.altbank.card;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class CardService {

    private final CardRepository repository;

    @Transactional
    public void deleteByAccountId(UUID accountId) {
        repository.delete("account.id = ?1", accountId);
    }
}
