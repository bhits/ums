package gov.samhsa.c2s.ums.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(indexes = @Index(columnList = "value,system_id", name = "identifier_idx", unique = true))
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Identifier {
    @Id
    @GeneratedValue
    private Long id;
    private String value;
    @ManyToOne
    private IdentifierSystem system;
}
