package gov.samhsa.c2s.ums.service.fhir;


import gov.samhsa.c2s.ums.config.UmsProperties;
import gov.samhsa.c2s.ums.service.dto.UserDto;
import org.hl7.fhir.dstu3.model.Patient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FhirPatientServiceImpl implements FhirPatientService {

    private final UmsProperties umsProperties;
    private final ModelMapper modelMapper;

    @Autowired
    public FhirPatientServiceImpl(UmsProperties umsProperties, ModelMapper modelMapper) {
        this.umsProperties = umsProperties;
        this.modelMapper = modelMapper;
    }


    @Override
    public Patient getFhirPatient(UserDto userDto) {
        return modelMapper.map(userDto, Patient.class);
    }



}
