package com.aws.samples.petclinic.pet;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Component
public class MedicalRecordStore {

    private AmazonS3 amazonS3;

    public MedicalRecordStore(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public MedicalRecord save(MedicalRecord medicalRecord) {
        medicalRecord.setId(UUID.randomUUID().toString());

        amazonS3.putObject(new PutObjectRequest(
                "cmr-lambda",
                "medical-record/" + medicalRecord.getId(),
                new ByteArrayInputStream(medicalRecord.getRecord().getBytes()),
                null));

        return medicalRecord;
    }
}