package gov.samhsa.c2s.ums.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdentifierSystemRepository extends JpaRepository<IdentifierSystem, Long> {

    Optional<IdentifierSystem> findBySystem(String system);
}
