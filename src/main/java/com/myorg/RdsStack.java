package com.myorg;

import java.util.List;

import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.rds.*;
import software.constructs.Construct;

public class RdsStack extends Stack {
    public RdsStack(final Construct scope, final String id) {
        super(scope, id);

        // Create a VPC (Amazon RDS must be in a VPC)
        Vpc vpc = Vpc.Builder.create(this, "MyVpc")
                .maxAzs(2) // Multi-AZ deployment
                .build();

        // Create a Security Group for RDS
        SecurityGroup securityGroup = SecurityGroup.Builder.create(this, "MyRdsSecurityGroup")
                .vpc(vpc)
                .allowAllOutbound(true)
                .build();

        securityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(3306), "Allow MySQL access");

        // Store database credentials securely using AWS Secrets Manager
        DatabaseInstance database = DatabaseInstance.Builder.create(this, "MySQLRDS")
                .engine(DatabaseInstanceEngine.mysql(MySqlInstanceEngineProps.builder()
                        .version(MysqlEngineVersion.VER_8_0_36) // Set MySQL version
                        .build()))
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE3, InstanceSize.MICRO)) // Use t2.micro for free
                                                                                             // tier
                .vpc(vpc)
                .vpcSubnets(SubnetSelection.builder()
                        .subnetType(SubnetType.PUBLIC) // Public access (optional, use PRIVATE for production)
                        .build())
                .allocatedStorage(20) // 20GB storage
                .storageType(StorageType.GP2)
                .multiAz(false) // Set to true for high availability
                .credentials(Credentials.fromGeneratedSecret("admin")) // Creates a secret in AWS Secrets Manager
                .port(3306)
                .deletionProtection(false) // Set to true to prevent accidental deletion
                .removalPolicy(RemovalPolicy.DESTROY) // Destroys DB when stack is deleted (for testing)
                .securityGroups(List.of(securityGroup))
                .build();
    }
}
