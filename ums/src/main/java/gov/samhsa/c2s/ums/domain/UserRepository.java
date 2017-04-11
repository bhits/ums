package gov.samhsa.c2s.ums.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Patient, Long> {
    Optional<User> findOneByIdAndIsDeleted(Long userId, boolean isDeleted);
}
