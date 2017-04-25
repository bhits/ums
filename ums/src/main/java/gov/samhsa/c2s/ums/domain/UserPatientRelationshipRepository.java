package gov.samhsa.c2s.ums.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserPatientRelationshipRepository extends JpaRepository<UserPatientRelationship, Long> {

    Optional<UserPatientRelationship> findOneByUserIdAndPatientId(Long userId, String patientId);

}
