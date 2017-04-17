package gov.samhsa.c2s.ums.domain;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserPatientRelationshipRepository extends JpaRepository<UserPatientRelationship, Long> {
}
