package gov.samhsa.c2s.ums.domain;


import lombok.Data;
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
    @Size(max = 30)
    private String value;


    @Column(name="`use`")
    @Enumerated(EnumType.STRING)
    private Use use;



    @ManyToOne
    private Demographics demographics;


   public enum Use{
        HOME,
        WORK
    }

    public enum System{
        PHONE,
        EMAIL
    }



}
