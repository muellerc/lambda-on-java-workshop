package com.aws.samples.petclinic.pet;

import java.io.Serializable;

public class Pet implements Serializable {

    private String id;
    private String name;
    private String type;
    private String birthday;

    public Pet() {
    }

    public Pet(String id, String name, String type, String birthday) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.birthday = birthday;
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
}
