package gov.samhsa.c2s.ums.service.mapping;

import gov.samhsa.c2s.ums.domain.Patient;
import gov.samhsa.c2s.ums.service.dto.PatientDto;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
public class PatientToPatientDtoMap extends PropertyMap<Patient, PatientDto> {

        @Override
        protected void configure() {
            map().setId(source.getId());
            map().setFirstName(source.getDemographics().getFirstName());
            map().setMiddleName(source.getDemographics().getMiddleName());
            map().setLastName(source.getDemographics().getLastName());
            map().setGenderCode(source.getDemographics().getAdministrativeGenderCode().getCode());
            map().setBirthDate(source.getDemographics().getBirthDay());
            map().setMrn(source.getMrn());
            map().setSocialSecurityNumber(source.getDemographics().getSocialSecurityNumber());
            using(new TelecomListToTelecomDtoListConverter()).map(source.getDemographics().getTelecoms()).setTelecoms(null);
            using(new AddressListToAddressDtoListConverter()).map(source.getDemographics().getAddresses()).setAddresses(null);
        }

    }

