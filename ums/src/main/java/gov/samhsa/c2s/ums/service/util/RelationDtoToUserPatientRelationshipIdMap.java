package gov.samhsa.c2s.ums.service.util;

import gov.samhsa.c2s.ums.domain.Patient;
import gov.samhsa.c2s.ums.domain.PatientRepository;
import gov.samhsa.c2s.ums.domain.Relationship;
import gov.samhsa.c2s.ums.domain.RelationshipRepository;
import gov.samhsa.c2s.ums.domain.RoleRepository;
import gov.samhsa.c2s.ums.domain.User;
import gov.samhsa.c2s.ums.domain.UserPatientRelationship;
import gov.samhsa.c2s.ums.domain.UserRepository;
import gov.samhsa.c2s.ums.domain.valueobject.RelationshipRoleId;
import gov.samhsa.c2s.ums.domain.valueobject.UserPatientRelationshipId;
import gov.samhsa.c2s.ums.service.dto.RelationDto;
import org.modelmapper.AbstractConverter;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Converts {@link  RelationDto } to {@link UserPatientRelationship}}
 */
@Component
public class RelationDtoToUserPatientRelationshipIdMap extends PropertyMap<RelationDto, UserPatientRelationshipId> {

    private final UserConverter userConverter;
    private final PatientConverter patientConverter;
    private final RelationshipConverter relationshipConverter;

    public RelationDtoToUserPatientRelationshipIdMap(UserConverter userConverter, PatientConverter patientConverter, RelationshipConverter relationshipConverter) {
        this.userConverter = userConverter;
        this.patientConverter = patientConverter;
        this.relationshipConverter = relationshipConverter;
    }

    @Override
    protected void configure() {
        using(userConverter).map(source).setUser(null);
        using(patientConverter).map(source).setPatient(null);
        using(relationshipConverter).map(source).setRelationship(null);
    }


    /**
     * Converts {@link  RelationDto } to {@link User}}
     */
    @Component
    private static class UserConverter extends AbstractConverter<RelationDto, User> {

        private final UserRepository userRepository;

        @Autowired
        public UserConverter(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @Override
        protected User convert(RelationDto source) {
            return userRepository.findOne(source.getUserId());
        }
    }

    /**
     * Converts {@link  RelationDto } to {@link Patient}}
     */
    @Component
    private static class PatientConverter extends AbstractConverter<RelationDto, Patient> {

        private final PatientRepository patientRepository;

        @Autowired
        public PatientConverter(PatientRepository patientRepository) {
            this.patientRepository = patientRepository;
        }

        @Override
        protected Patient convert(RelationDto source) {
            return patientRepository.findOne(source.getPatientId());
        }
    }


    /**
     * Converts {@link  RelationDto } to {@link Relationship}}
     */
    @Component
    private static class RelationshipConverter extends AbstractConverter<RelationDto, Relationship> {

        private final RelationshipRepository relationshipRepository;
        private final RoleRepository roleRepository;


        @Autowired
        public RelationshipConverter(RelationshipRepository relationshipRepository, RoleRepository roleRepository) {
            this.relationshipRepository = relationshipRepository;
            this.roleRepository = roleRepository;
        }

        @Override
        protected Relationship convert(RelationDto source) {
            return Relationship.builder().id(RelationshipRoleId.builder().role(roleRepository.findByCode(source.getRelationshipCode())).build()).build();
    }
}
}