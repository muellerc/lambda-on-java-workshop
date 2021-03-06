package com.aws.samples.petclinic.pet;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.util.UUID;

@Singleton
public class MedicalRecordStore {

    private AmazonS3 amazonS3;
    private String petsBucketName;

    @Inject
    public MedicalRecordStore(AmazonS3 amazonS3, @Named(value = "petsBucketName") String petsBucketName) {
        this.amazonS3 = amazonS3;
        this.petsBucketName = petsBucketName;
    }

    public MedicalRecord save(MedicalRecord medicalRecord) {
        medicalRecord.setId(UUID.randomUUID().toString());

        amazonS3.putObject(new PutObjectRequest(
                petsBucketName,
                "medical-record/" + medicalRecord.getId(),
                new ByteArrayInputStream(medicalRecord.getRecord().getBytes()),
                null));

        return medicalRecord;
    }
}
