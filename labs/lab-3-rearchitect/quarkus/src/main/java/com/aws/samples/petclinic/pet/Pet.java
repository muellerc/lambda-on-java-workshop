package com.aws.samples.petclinic.pet;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Pet {

    private String id;
    private String name;
    private String birthday;
    private String type;

    public Pet() {
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
