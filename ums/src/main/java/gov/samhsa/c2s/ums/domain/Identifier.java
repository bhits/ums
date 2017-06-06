package gov.samhsa.c2s.ums.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Audited
@Table(indexes = @Index(columnList = "value,identifier_system_id", name = "identifier_idx", unique = true))
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Identifier {
    @Id
    @GeneratedValue
    private Long id;
    @NotBlank
    private String value;
    @ManyToOne
    @NotNull
    private IdentifierSystem identifierSystem;

    public static Identifier of(String value, IdentifierSystem system) {
        return new Identifier(null, value, system);
    }

    public static Identifier of(Long id, String value, IdentifierSystem system) {
        return new Identifier(id, value, system);
    }
}
