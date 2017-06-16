package gov.samhsa.c2s.ums.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The Class Telecom.
 */
@Entity
@Audited
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "demographics")
public class Telecom {
    /**
     * The id.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The code.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private System system;

    /**
     * The telecom use code.
     */
    @NotNull
    @Size(max = 150)
    private String value;

    @Column(name = "`use`")
    @Enumerated(EnumType.STRING)
    private Use use = Use.HOME;

    @ManyToOne
    private Demographics demographics;

    public enum Use {
        // IMPORTANT: The order of these values matter. When sorted, HOME will come before WORK.
        // This order is currently being used when selecting an email address to send the activation email
        HOME,
        WORK
    }

    public enum System {
        // IMPORTANT: The order of these values matter. When sorted, PHONE will come before EMAIL.
        PHONE,
        EMAIL
    }
}
