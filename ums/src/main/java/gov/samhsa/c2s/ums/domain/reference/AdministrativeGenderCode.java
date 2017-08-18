package gov.samhsa.c2s.ums.domain.reference;


import gov.samhsa.c2s.common.i18n.I18nEnabled;

import javax.persistence.Entity;


/**
 * The Class AdministrativeGenderCode.
 */
@Entity
public class AdministrativeGenderCode extends AbstractLocalDBLookupCodedConcept implements I18nEnabled {

    @Override
    public String getIdAsString() {
        return longToString(getId());
    }
}
