package gov.samhsa.c2s.ums.service.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.IClientExecutable;
import ca.uhn.fhir.rest.gclient.ICreate;
import ca.uhn.fhir.rest.gclient.ICreateTyped;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.IUpdate;
import ca.uhn.fhir.rest.gclient.IUpdateTyped;
import ca.uhn.fhir.rest.gclient.IUpdateWithQuery;
import ca.uhn.fhir.rest.gclient.IUpdateWithQueryTyped;
import ca.uhn.fhir.rest.server.EncodingEnum;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import gov.samhsa.c2s.ums.config.UmsProperties;
import gov.samhsa.c2s.ums.domain.Address;
import gov.samhsa.c2s.ums.domain.Demographics;
import gov.samhsa.c2s.ums.domain.Identifier;
import gov.samhsa.c2s.ums.domain.IdentifierSystem;
import gov.samhsa.c2s.ums.domain.Telecom;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.reference.AdministrativeGenderCode;
import gov.samhsa.c2s.ums.domain.reference.CountryCode;
import gov.samhsa.c2s.ums.domain.reference.StateCode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static gov.samhsa.c2s.common.unit.matcher.ArgumentMatchers.matching;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Telecom.System.class, Telecom.Use.class})
public class FhirPatientServiceImplTest {
    final String lastName = "lastName";
    final String firstName = "firstName";
    final String gender = "MALE";
    final String system1 = "mrn";
    final String systemValue1 = "c2s-mrn";
    final String system2 = "ssn";
    final String systemValue2 = "1234";
    final String address1 = "address1";
    final String address2 = "address2";
    final String city = "city";
    final String stateCodeValue = "stateCode";
    final String postalCode = "postalCode";
    final String countryCodeValue = "countryCode";
    final String phone = "PHONE";
    final String telecomUse = "HOME";
    final String telecomValue = "1234456";

    @Mock
    private UmsProperties umsProperties;

    @Mock
    private FhirContext fhirContext;

    @Mock
    private FhirValidator fhirValidator;

    @Mock
    private UmsProperties.Fhir fhir;

    @Mock
    private IGenericClient fhirClient;

    @Mock
    private User user;

    @Mock
    private ValidationResult validationResult;

    @Mock
    private UmsProperties.Fhir.Publish publish;

    @Mock
    private IClientExecutable iClientExecutable;

    @InjectMocks
    private FhirPatientServiceImpl fhirPatientService;

    @Before
    public void setup() {
        LocalDate localDate = LocalDate.now();
        AdministrativeGenderCode administrativeGenderCode = mock(AdministrativeGenderCode.class);
        UmsProperties.Mrn mrn = PowerMockito.mock(UmsProperties.Mrn.class);
        UmsProperties.Ssn ssn = PowerMockito.mock(UmsProperties.Ssn.class);
        when(umsProperties.getMrn()).thenReturn(mrn);
        when(mrn.getCodeSystem()).thenReturn("mrn");
        when(umsProperties.getSsn()).thenReturn(ssn);
        when(ssn.getCodeSystem()).thenReturn("ssn");

        Demographics demographics = mock(Demographics.class);
        when(user.getDemographics()).thenReturn(demographics);
        when(demographics.getLastName()).thenReturn(lastName);
        when(demographics.getFirstName()).thenReturn(firstName);
        when(demographics.getBirthDay()).thenReturn(localDate);
        when(demographics.getAdministrativeGenderCode()).thenReturn(administrativeGenderCode);
        when(administrativeGenderCode.toString()).thenReturn(gender);

        Identifier identifier1 = mock(Identifier.class);
        Identifier identifier2 = mock(Identifier.class);
        List<Identifier> identifiers = new ArrayList<>();
        identifiers.add(identifier1);
        identifiers.add(identifier2);
        IdentifierSystem identifierSystem1 = mock(IdentifierSystem.class);
        IdentifierSystem identifierSystem2 = mock(IdentifierSystem.class);

        when(demographics.getIdentifiers()).thenReturn(identifiers);
        when(identifier1.getIdentifierSystem()).thenReturn(identifierSystem1);
        when(identifierSystem1.getSystem()).thenReturn(system1);

        when(identifier2.getIdentifierSystem()).thenReturn(identifierSystem2);
        when(identifierSystem2.getSystem()).thenReturn(system2);

        when(identifier1.getValue()).thenReturn(systemValue1);
        when(identifier2.getValue()).thenReturn(systemValue2);

        Address address = mock(Address.class);
        List<Address> addresses = new ArrayList<>();
        addresses.add(address);
        when(demographics.getAddresses()).thenReturn(addresses);

        StateCode stateCode = mock(StateCode.class);
        CountryCode countryCode = mock(CountryCode.class);
        when(address.getLine2()).thenReturn(address2);
        when(address.getLine1()).thenReturn(address1);
        when(address.getCity()).thenReturn(city);
        when(address.getStateCode()).thenReturn(stateCode);
        when(stateCode.getDisplayName()).thenReturn(stateCodeValue);
        when(address.getPostalCode()).thenReturn(postalCode);
        when(address.getCountryCode()).thenReturn(countryCode);
        when(countryCode.getDisplayName()).thenReturn(countryCodeValue);

        Telecom telecom = mock(Telecom.class);
        List<Telecom> telecoms = new ArrayList<>();
        telecoms.add(telecom);

        when(demographics.getTelecoms()).thenReturn(telecoms);
        Telecom.System system = PowerMockito.mock(Telecom.System.class);
        when(telecom.getSystem()).thenReturn(system);
        when(system.toString()).thenReturn(phone);
        Telecom.Use use = PowerMockito.mock(Telecom.Use.class);
        when(telecom.getUse()).thenReturn(use);
        when(use.toString()).thenReturn(telecomUse);
        when(telecom.getValue()).thenReturn(telecomValue);

        when(fhirValidator.validateWithResult(any(org.hl7.fhir.dstu3.model.Patient.class))).thenReturn(validationResult);
        when(validationResult.isSuccessful()).thenReturn(true);
        when(umsProperties.getFhir()).thenReturn(fhir);
        when(fhir.getPublish()).thenReturn(publish);
        when(publish.getEncoding()).thenReturn(EncodingEnum.JSON);
        when(iClientExecutable.encodedJson()).thenReturn(iClientExecutable);
    }

    @Test
    public void testUpdateFhirPatientWithLimitedField_Given_UseCreateForUpdateIsTrue() {
        //Arrange
        when(publish.isUseCreateForUpdate()).thenReturn(true);

        ICreate iCreate = mock(ICreate.class);
        when(fhirClient.create()).thenReturn(iCreate);
        ICreateTyped iCreateTyped = mock(ICreateTyped.class);
        when(iCreate.resource(any(org.hl7.fhir.dstu3.model.Patient.class))).thenReturn(iCreateTyped);
        MethodOutcome methodOutcome = mock(MethodOutcome.class);
        when(iCreateTyped.execute()).thenReturn(methodOutcome);

        //Act
        fhirPatientService.updateFhirPatientWithLimitedField(user);

        //Assert
        verify(fhirClient, times(1)).create();
        verify(iCreate, times(1)).resource(argThat(matching((org.hl7.fhir.dstu3.model.Patient p) -> p.getName().get(0).getFamily().equals(lastName))));
    }

    @Test
    public void testUpdateFhirPatientWithLimitedField_Given_UseCreateForUpdateIsFalse() {
        //Arrange
        when(publish.isUseCreateForUpdate()).thenReturn(false);
        IUpdate iUpdate = mock(IUpdate.class);
        when(fhirClient.update()).thenReturn(iUpdate);
        IUpdateTyped iUpdateTyped = mock(IUpdateTyped.class);
        when(iUpdate.resource(any(org.hl7.fhir.dstu3.model.Patient.class))).thenReturn(iUpdateTyped);
        IUpdateWithQuery iUpdateWithQuery = mock(IUpdateWithQuery.class);
        when(iUpdateTyped.conditional()).thenReturn(iUpdateWithQuery);
        IUpdateWithQueryTyped iUpdateWithQueryTyped = mock(IUpdateWithQueryTyped.class);
        when(iUpdateWithQuery.where(any(ICriterion.class))).thenReturn(iUpdateWithQueryTyped);

        //Act
        fhirPatientService.updateFhirPatientWithLimitedField(user);

        //Assert
        verify(fhirClient, times(1)).update();
        verify(iUpdate, times(1)).resource(argThat(matching((org.hl7.fhir.dstu3.model.Patient p) -> p.getName().get(0).getFamily().equals(lastName))));
    }
}
