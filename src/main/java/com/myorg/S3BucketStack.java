package com.myorg;

import java.nio.file.Paths;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.s3.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.constructs.Construct;

public class S3BucketStack extends Stack {
        public S3BucketStack(final Construct scope, final String id, final Environment env) {
                super(scope, id, StackProps.builder().env(env).build());

                // Create an S3 bucket
                Bucket bucket = Bucket.Builder.create(this, "TestBucket" + System
                                .currentTimeMillis())
                                .bucketName("test-restapis-bucket-" + System
                                                .currentTimeMillis()) // Change bucket name
                                .publicReadAccess(true) // Public bucket
                                .publicReadAccess(true) // Allow public read access
                                .blockPublicAccess(BlockPublicAccess.Builder.create()
                                                .blockPublicAcls(false)
                                                .blockPublicPolicy(false)
                                                .restrictPublicBuckets(false)
                                                .ignorePublicAcls(false)
                                                .build()) // Explicitly disable public access restrictions
                                .removalPolicy(RemovalPolicy.DESTROY) // Change this if needed
                                .build();

                // Output the bucket name
                CfnOutput.Builder.create(this, "TestBucketOutput")
                                .value(bucket.getBucketName())
                                .exportName("test-restapis-bucket-export")
                                .build();

                uploadFileToS3(bucket.getBucketName(),
                                "C:\\Users\\manis\\OneDrive\\Desktop\\cdk-test\\target\\myapp.jar",
                                "myapp");
        }

        // Corrected upload method
        private void uploadFileToS3(String bucketName, String filePath, String keyName) {
                try (S3Client s3 = S3Client.builder().region(Region.US_EAST_1).build()) {
                        s3.putObject(PutObjectRequest.builder()
                                        .bucket(bucketName)
                                        .key(keyName)
                                        .build(),
                                        Paths.get(filePath));
                        System.out.println("File uploaded to S3: " + keyName);
                } catch (Exception e) {
                        System.err.println("Error uploading file: " + e.getMessage());
                }
        }

}
