package gov.samhsa.c2s.ums.domain.reference;


import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;


/**
 * The Class AdministrativeGenderCode.
 */
@Entity
@SequenceGenerator(name = "idgener", sequenceName = "GENDER_SEQ", initialValue = 1)
public class AdministrativeGenderCode extends AbstractLocalDBLookupCodedConcept {

}
