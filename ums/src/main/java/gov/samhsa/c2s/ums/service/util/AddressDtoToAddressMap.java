package gov.samhsa.c2s.ums.service.util;

import gov.samhsa.c2s.ums.domain.Address;
import gov.samhsa.c2s.ums.domain.reference.CountryCode;
import gov.samhsa.c2s.ums.domain.reference.CountryCodeRepository;
import gov.samhsa.c2s.ums.domain.reference.StateCode;
import gov.samhsa.c2s.ums.domain.reference.StateCodeRepository;
import gov.samhsa.c2s.ums.service.dto.AddressDto;
import org.modelmapper.AbstractConverter;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddressDtoToAddressMap extends PropertyMap<AddressDto, Address> {

    private final StateCodeConverter stateCodeConverter;
    private final CountryCodeConverter countryCodeConverter;

    public AddressDtoToAddressMap(StateCodeConverter stateCodeConverter, CountryCodeConverter countryCodeConverter) {
        this.stateCodeConverter = stateCodeConverter;
        this.countryCodeConverter = countryCodeConverter;
    }

    @Override
    protected void configure() {
        using(stateCodeConverter).map(source).setStateCode(null);
        using(countryCodeConverter).map(source).setCountryCode(null);
    }


    /**
     * Converts {@link  AddressDto } to {@link StateCode}}
     */
    @Component
    private static class StateCodeConverter extends AbstractConverter<AddressDto, StateCode> {

        private final StateCodeRepository stateCodeRepository;

        @Autowired
        public StateCodeConverter(StateCodeRepository stateCodeRepository) {
            this.stateCodeRepository = stateCodeRepository;
        }

        @Override
        protected StateCode convert(AddressDto source) {
            return stateCodeRepository.findByCode(source.getStateCode());
        }
    }


    /**
     * Converts {@link  AddressDto } to {@link CountryCode}}
     */
    @Component
    private static class CountryCodeConverter extends AbstractConverter<AddressDto, CountryCode> {

        private final CountryCodeRepository countryCodeRepository;

        @Autowired
        public CountryCodeConverter(CountryCodeRepository CountryCodeRepository) {
            this.countryCodeRepository = CountryCodeRepository;
        }

        @Override
        protected CountryCode convert(AddressDto source) {
            return countryCodeRepository.findByCode(source.getCountryCode());
        }
    }
}

