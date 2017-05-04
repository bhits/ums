package gov.samhsa.c2s.ums.domain;

import gov.samhsa.c2s.ums.domain.valueobject.UserPatientRelationshipId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPatientRelationship {

    @EmbeddedId
    private UserPatientRelationshipId id;
}
