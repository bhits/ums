package gov.samhsa.c2s.ums.service.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.instance.model.api.IBaseResource;

import javax.xml.bind.annotation.XmlRootElement;

@JsonDeserialize(using = FhirStu3PatientDeserializer.class)
@JsonSerialize(using = FhirStu3PatientSerializer.class)
@XmlRootElement(name = "FhirPatientDto")
public class FhirPatientDto {

    private final Patient patient;

    public FhirPatientDto(IBaseResource patient) {
        this.patient = (Patient)patient;
    }

    public Patient getPatient(){
        return this.patient;
    }
}
