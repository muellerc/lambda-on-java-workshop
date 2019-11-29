package com.aws.samples.petclinic.pet;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

public class MedicalRecordStore {

    private S3Client s3Client;

    public MedicalRecordStore(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public MedicalRecord save(MedicalRecord medicalRecord) {
        medicalRecord.setId(UUID.randomUUID().toString());

        s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket("cmr-lambda")
                    .key("medical-record/" + medicalRecord.getId())
                    .build(),
                RequestBody.fromString(medicalRecord.getRecord()));

        return medicalRecord;
    }
}
