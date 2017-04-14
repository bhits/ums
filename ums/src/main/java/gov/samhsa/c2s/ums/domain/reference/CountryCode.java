package gov.samhsa.c2s.ums.domain.reference;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;

/**
 * The Class CountryCode.
 */
@Entity
@SequenceGenerator(name = "idgener", sequenceName = "COUNTRYCODE_SEQ", initialValue = 1)
public class CountryCode extends AbstractLocalDBLookupCodedConcept {


}
