package gov.samhsa.c2s.ums.service.dto;

import ca.uhn.fhir.parser.JsonParser;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class FhirStu3PatientSerializer extends JsonSerializer<FhirPatientDto> {

    private final JsonParser fhirJsonParser;


    public FhirStu3PatientSerializer(JsonParser fhirJsonParser) {
        this.fhirJsonParser = fhirJsonParser;
    }

    @Override
    public void serialize(FhirPatientDto patient, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        final String json = fhirJsonParser.encodeResourceToString(patient.getPatient());
        jsonGenerator.writeRawValue(json);
    }
}
