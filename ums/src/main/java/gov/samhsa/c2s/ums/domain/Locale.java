package gov.samhsa.c2s.ums.domain;

import gov.samhsa.c2s.ums.domain.reference.AbstractLocalDBLookupCodedConcept;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;

@Entity
@SequenceGenerator(name="idgener", sequenceName="LOCALE_SEQ", initialValue = 1)
public class Locale extends AbstractLocalDBLookupCodedConcept {

}
