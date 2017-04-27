package gov.samhsa.c2s.ums.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Data
public class Patient {
    /**
     * The id.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The Medical Record Number.
     */
    private String mrn;

    @OneToOne
    private Demographics demographics;

}
