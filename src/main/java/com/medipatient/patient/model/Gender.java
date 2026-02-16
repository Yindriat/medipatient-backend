package com.medipatient.patient.model;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    MALE,
    FEMALE,
    OTHER;

    // Permet à Swagger d'accepter "Male", "male", "MALE" sans erreur 400
    @JsonCreator
    public static Gender fromString(String value) {
        if (value == null) return null;
        return Gender.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }

}
