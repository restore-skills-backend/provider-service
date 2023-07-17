package com.restore.providerservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Service
public class CloudFileService {

    private final S3Client s3Client;
    @Value("${aws.s3.bucket-name}")
    private String bucketname;
    @Autowired
    public CloudFileService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadProfileImage(MultipartFile profileImage, Long providerId) throws IOException {
        String imageName = profileImage.getName();
        String key = generateUniqueKeyForProviderProfileImage(providerId);
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketname)
                .key(key)
                .build();
        try {
            PutObjectResponse response = s3Client.putObject(request, RequestBody.fromBytes(profileImage.getBytes()));
            return key;
        }catch (SdkClientException ex){
            ex.printStackTrace();
            throw new IOException("Failed to upload profile image to S3.", ex);
        }
    }

    public void updateObject(String key, MultipartFile newProfileImage) throws IOException {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketname)
                    .key(key)
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(newProfileImage.getBytes()));
        } catch (S3Exception e) {
            throw new IOException("Error updating object in S3: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new IOException("Error reading object data: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new IOException("An error occurred: " + e.getMessage(), e);
        }
    }

    public String getObjectAsBase64(String key) throws IOException {
        try{
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketname)
                    .key(key)
                    .build();
            ResponseInputStream<GetObjectResponse> responseBytes = s3Client.getObject(request);
            byte[] objectBytes = responseBytes.readAllBytes();
            String base64Object = Base64.getEncoder().encodeToString(objectBytes);
            return base64Object;
        } catch (S3Exception e) {
            throw new IOException("Error retrieving object from S3: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new IOException("Error reading object data: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new IOException("An error occurred: " + e.getMessage(), e);
        }
    }

    public void deleteObject(String key) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketname)
                    .key(key)
                    .build();

            DeleteObjectResponse response = s3Client.deleteObject(request);
        } catch (S3Exception e) {
            throw new RuntimeException("Error deleting object from S3: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }

    public String generateUniqueKeyForProviderProfileImage(Long providerId){
        return "providers/"+ providerId +UUID.randomUUID().toString();
    }
}
