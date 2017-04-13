package gov.samhsa.c2s.ums.service;


import gov.samhsa.c2s.ums.config.UmsProperties;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class MrnServiceImpl.
 */
@Service
public class MrnServiceImpl implements MrnService {

    private final UmsProperties umsProperties;

    @Autowired
    public MrnServiceImpl(UmsProperties umsProperties) {
        this.umsProperties = umsProperties;
    }

    @Override
    public String generateMrn() {
        //TODO: Make sure the randomly generating MRN does not exist in UMS database
        return generateRandomMrn();
    }

    /**
     * Generate random mrn.
     *
     * @return the string
     */
    private String generateRandomMrn() {
        StringBuilder localIdIdBuilder = new StringBuilder();
        if (null != umsProperties.getMrn().getPrefix()) {
            localIdIdBuilder.append(umsProperties.getMrn().getPrefix());
            localIdIdBuilder.append(".");
        }
        localIdIdBuilder.append(RandomStringUtils
                .randomAlphanumeric((umsProperties.getMrn().getLength())));
        return localIdIdBuilder.toString().toUpperCase();
    }
}