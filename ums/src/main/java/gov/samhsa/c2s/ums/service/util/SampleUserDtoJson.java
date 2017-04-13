package gov.samhsa.c2s.ums.service.util;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.samhsa.c2s.ums.service.dto.AddressDto;
import gov.samhsa.c2s.ums.service.dto.TelecomDto;
import gov.samhsa.c2s.ums.service.dto.UserDto;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

public class SampleUserDtoJson {

    public static void main(String[] args) {

        ObjectMapper mapper = new ObjectMapper();

        //For testing
        UserDto userDto = createDummyUserDto();

        try {
            //Convert object to JSON string and save into file directly
            mapper.writeValue(new File("C:\\userDto.json"), userDto);

            //Convert object to JSON string
            String jsonInString = mapper.writeValueAsString(userDto);
            System.out.println(jsonInString);

            //Convert object to JSON string and pretty print
            jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userDto);
            System.out.println(jsonInString);


        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static UserDto createDummyUserDto() {

        TelecomDto emailDto = TelecomDto.builder().system("email").value("alice.recruit@mailinator.com").build();
        TelecomDto phoneDto = TelecomDto.builder().system("phone").value("4433932726").build();
        AddressDto addressDto = AddressDto.builder()
                                            .streetAddressLine("1111 Main Street")
                                            .city("Columbia")
                                            .stateCode("MD")
                                            .postalCode("22222")
                                            .build();
        return UserDto.builder()
                .id(4L)
                .firstName("Alice")
                .lastName("Recruit")
                .genderCode("female")
                .address(addressDto)
                .birthDate(LocalDate.now())
                .telecom(Arrays.asList(emailDto,phoneDto))
                .build();


    }
}
