package com.aws.samples.petclinic.pet;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;

@RegisterForReflection
public class PetRecord implements Serializable {

    private String id;
    private String name;
    private String birthday;
    private String type;
    private String medicalRecord;

    public PetRecord() {
    }

    public PetRecord(String name, String birthday, String type, String medicalRecord) {
        this.name = name;
        this.birthday = birthday;
        this.type = type;
        this.medicalRecord = medicalRecord;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBirthday() {
        return this.birthday;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMedicalRecord() {
        return this.medicalRecord;
    }

    public void setMedicalRecord(String medicalRecord) {
        this.medicalRecord = medicalRecord;
    }
}
