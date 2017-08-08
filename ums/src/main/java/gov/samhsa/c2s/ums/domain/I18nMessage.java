package gov.samhsa.c2s.ums.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class I18nMessage {
    @Id
    @GeneratedValue
    private long id;

    private String key;

    private String description;

    private String message;

    private String locale;
}