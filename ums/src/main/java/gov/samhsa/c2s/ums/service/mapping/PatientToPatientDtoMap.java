package gov.samhsa.c2s.ums.service.mapping;

import gov.samhsa.c2s.ums.domain.Patient;
import gov.samhsa.c2s.ums.service.dto.PatientDto;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientToPatientDtoMap extends PropertyMap<Patient, PatientDto> {
    @Autowired
    private PatientToMrnConverter patientToMrnConverter;

    @Autowired
    private PatientToSsnConverter patientToSsnConverter;

    @Override
    protected void configure() {
        map().setId(source.getId());
        map().setFirstName(source.getDemographics().getFirstName());
        map().setMiddleName(source.getDemographics().getMiddleName());
        map().setLastName(source.getDemographics().getLastName());
        map().setGenderCode(source.getDemographics().getAdministrativeGenderCode().getCode());
        map().setBirthDate(source.getDemographics().getBirthDay());
        using(patientToMrnConverter).map(source).setMrn(null);
        using(patientToSsnConverter).map(source).setSocialSecurityNumber(null);
        using(new TelecomListToTelecomDtoListConverter()).map(source.getDemographics().getTelecoms()).setTelecoms(null);
        using(new AddressListToAddressDtoListConverter()).map(source.getDemographics().getAddresses()).setAddresses(null);
    }
}

