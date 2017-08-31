package gov.samhsa.c2s.ums.service.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.io.IOException;

public class FhirStu3PatientDeserializer extends JsonDeserializer<FhirPatientDto> {

    private final ca.uhn.fhir.parser.JsonParser fhirJsonParser;


    public FhirStu3PatientDeserializer(ca.uhn.fhir.parser.JsonParser fhirJsonParser) {
        this.fhirJsonParser = fhirJsonParser;
    }

    @Override
    public FhirPatientDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        final String jsonAsString = jsonParser.readValueAsTree().toString();
        final IBaseResource iBaseResource = fhirJsonParser.parseResource(jsonAsString);
        return new FhirPatientDto(iBaseResource);

    }
}
