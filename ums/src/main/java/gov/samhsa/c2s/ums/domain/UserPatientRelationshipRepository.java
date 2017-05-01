package gov.samhsa.c2s.ums.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserPatientRelationshipRepository extends JpaRepository<UserPatientRelationship, Long> {

    Optional<UserPatientRelationship> findOneByIdUserIdAndIdPatientId(Long userId, Long patientId);

    List<UserPatientRelationship> findAllByIdUserIdAndIdPatientId(Long userId, Long patientId);

}
