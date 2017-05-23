package gov.samhsa.c2s.ums.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(indexes = @Index(columnList = "system", name = "system_idx", unique = true))
@Data
public class IdentifierSystem {
    @Id
    @GeneratedValue
    private Long id;
    private String system;
    private String display;
    private String oid;
    private boolean systemGenerated;
}
