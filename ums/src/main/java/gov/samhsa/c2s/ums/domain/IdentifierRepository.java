package gov.samhsa.c2s.ums.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdentifierRepository extends JpaRepository<Identifier, Long> {
    Optional<Identifier> findByValueAndIdentifierSystem(String value, IdentifierSystem system);

    Optional<Identifier> findByValueAndIdentifierSystemSystem(String value, String system);
}
