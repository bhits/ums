package gov.samhsa.c2s.ums.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserActivationRepository extends JpaRepository<UserActivation, Long> {
    Optional<UserActivation> findOneByEmailToken(String emailToken);

    Optional<UserActivation> findOneByUserId(Long userId);

    Optional<UserActivation> findOneByEmailTokenAndVerificationCode(String emailToken, String verificationCode);

    List<UserActivation> findAll();
}
