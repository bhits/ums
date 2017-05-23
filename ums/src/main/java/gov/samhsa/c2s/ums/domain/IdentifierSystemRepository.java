package gov.samhsa.c2s.ums.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IdentifierSystemRepository extends JpaRepository<IdentifierSystem, Long> {
    Optional<IdentifierSystem> findOneBySystem(String system);

    List<IdentifierSystem> findAllBySystemGenerated(boolean systemGenerated);
}
