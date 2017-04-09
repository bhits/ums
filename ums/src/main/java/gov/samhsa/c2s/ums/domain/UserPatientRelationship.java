package gov.samhsa.c2s.ums.domain;

import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@Data
public class UserPatientRelationship {

    @EmbeddedId
    private UserPatientRelationshipId id;
}
