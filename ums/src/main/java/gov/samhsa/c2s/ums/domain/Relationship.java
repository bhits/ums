package gov.samhsa.c2s.ums.domain;

import gov.samhsa.c2s.ums.domain.valueobject.RelationshipRoleId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Relationship {

    @EmbeddedId
    private RelationshipRoleId id;
}
