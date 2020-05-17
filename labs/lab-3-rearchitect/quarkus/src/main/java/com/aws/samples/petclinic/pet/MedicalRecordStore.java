package com.aws.samples.petclinic.pet;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class MedicalRecordStore {

    @Inject
    S3Client s3Client;

    @ConfigProperty(name = "bucketName")
    String bucketName;

    public MedicalRecord save(MedicalRecord medicalRecord) {
        medicalRecord.setId(UUID.randomUUID().toString());

        s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key("medical-record/" + medicalRecord.getId())
                    .build(),
                RequestBody.fromString(medicalRecord.getRecord()));

        return medicalRecord;
    }
}
