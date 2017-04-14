package gov.samhsa.c2s.ums.domain.reference;


import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;

/**
 * The Class StateCode.
 */
@Entity
@SequenceGenerator(name = "idgener", sequenceName = "STATE_SEQ", initialValue = 1)
public class StateCode extends AbstractLocalDBLookupCodedConcept {


}
