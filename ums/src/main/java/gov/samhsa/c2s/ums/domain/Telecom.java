package gov.samhsa.c2s.ums.domain;


import lombok.Data;
import lombok.ToString;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@ToString(exclude="user")
public class Telecom {
    /**
     * The id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The code.
     */
    @NotNull
    @Size(max = 30)
    private String system;

    /**
     * The telecom use code.
     */
    @NotNull
    @Size(max = 30)
    private String value;

    @ManyToOne
    private User user;



}
