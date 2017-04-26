package gov.samhsa.c2s.ums.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface UserPatientRelationshipRepository extends JpaRepository<UserPatientRelationship, Long> {

    Optional<UserPatientRelationship> findOneByIdUserIdAndIdPatientId(Long userId, String patientId);

    List<UserPatientRelationship> findAllByIdUserIdAndIdPatientId(Long userId, String patientId);

}
