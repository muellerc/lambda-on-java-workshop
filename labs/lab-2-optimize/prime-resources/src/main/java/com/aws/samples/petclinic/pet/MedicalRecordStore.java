package com.aws.samples.petclinic.pet;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

public class MedicalRecordStore {

    private S3Client s3Client;
    private String bucket;

    public MedicalRecordStore(S3Client s3Client, String bucket) {
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    public MedicalRecord save(MedicalRecord medicalRecord) {
        medicalRecord.setId(UUID.randomUUID().toString());

        s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(bucket)
                    .key("medical-record/" + medicalRecord.getId())
                    .build(),
                RequestBody.fromString(medicalRecord.getRecord()));

        return medicalRecord;
    }

    public void prime() {
        try {
            s3Client.getObject(GetObjectRequest.builder().bucket(bucket).key("medical-record/1").build());
        } catch (Exception e) {
            e.printStackTrace();
            // nothing to do
        }
    }
}
