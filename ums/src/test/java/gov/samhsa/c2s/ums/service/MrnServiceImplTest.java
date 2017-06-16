package gov.samhsa.c2s.ums.service;

import gov.samhsa.c2s.ums.config.UmsProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MrnServiceImplTest {
    //TODO:Test of generateRandomMrn should be done according to where the generateRandomMrn is moved(domain of ums).
    @Mock
    UmsProperties umsProperties;

    @InjectMocks
    MrnServiceImpl mrnServiceImpl;

    @Test
    public void testGenerateMrn_Given_ThereIsaPrefixInMrn() {
        //Arrange
        UmsProperties.Mrn mrn = mock(UmsProperties.Mrn.class);
        String prefix = "prefix";
        int length = 6;
        when(umsProperties.getMrn()).thenReturn(mrn);
        when(mrn.getPrefix()).thenReturn(prefix);
        when(mrn.getLength()).thenReturn(length);

        //Act
        String generatedMrn = mrnServiceImpl.generateMrn();

        //Assert
        assertEquals(13, generatedMrn.length());
        assertEquals("PREFIX-", generatedMrn.substring(0, 7));
        assertTrue(generatedMrn.equals(generatedMrn.toUpperCase()));
    }

    @Test
    public void testGenerateMrn_Given_ThereIsNoPrefixInMrn() {
        //Arrange
        UmsProperties.Mrn mrn = mock(UmsProperties.Mrn.class);
        String prefix = null;
        int length = 6;
        when(umsProperties.getMrn()).thenReturn(mrn);
        when(mrn.getPrefix()).thenReturn(prefix);
        when(mrn.getLength()).thenReturn(length);

        //Act
        String generatedMrn = mrnServiceImpl.generateMrn();

        //Assert
        assertEquals(6, generatedMrn.length());
        assertTrue(generatedMrn.equals(generatedMrn.toUpperCase()));
    }

}
