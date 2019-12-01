package com.aws.samples.petclinic.pet;

import java.io.Serializable;

public class MedicalRecord implements Serializable {

    private String id;
    private String record;

    public MedicalRecord() {
    }

    public MedicalRecord(String id, String record) {
        this.id = id;
        this.record = record;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }
}