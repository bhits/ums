package gov.samhsa.c2s.ums.service.mapping;

import gov.samhsa.c2s.ums.domain.Telecom;
import gov.samhsa.c2s.ums.service.dto.TelecomDto;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelecomListToTelecomDtoListConverter extends AbstractConverter<List<Telecom>, List<TelecomDto>> {

    @Override
    protected List<TelecomDto> convert(List<Telecom> source) {
        List<TelecomDto> telecomDtoList = new ArrayList<>();

        if (source != null && source.size() > 0) {

            for (Telecom tempTelecom : source) {
                TelecomDto tempTelecomDto = new TelecomDto();
                tempTelecomDto.setValue(tempTelecom.getValue());
                if (tempTelecom.getSystem() != null)
                    tempTelecomDto.setSystem(tempTelecom.getSystem().toString());
                if (tempTelecom.getUse() != null)
                    tempTelecomDto.setUse(tempTelecom.getUse().toString());
                telecomDtoList.add(tempTelecomDto);
            }
        }
        return telecomDtoList;
    }
}
