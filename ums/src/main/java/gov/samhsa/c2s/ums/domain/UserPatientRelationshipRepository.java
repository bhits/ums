package gov.samhsa.c2s.ums.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UserPatientRelationshipRepository extends JpaRepository<UserPatientRelationship, Long> {


    List<UserPatientRelationship> findAllByIdUserIdAndIdPatientId(Long userId, Long patientId);

    List<UserPatientRelationship> findAllByIdUserId(Long userId);

}
