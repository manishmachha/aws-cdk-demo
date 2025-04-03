package com.myorg;

import software.amazon.awscdk.services.ec2.*;
import software.constructs.Construct;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

public class SpringBootEC2Stack extends Stack {
        public SpringBootEC2Stack(final Construct scope, final String id, final StackProps props) {
                super(scope, id, props);

                // Define VPC (use default or create new)
                Vpc vpc = Vpc.Builder.create(this, "MyVpc").maxAzs(2).build();

                // Define Security Group
                SecurityGroup securityGroup = SecurityGroup.Builder.create(this, "SecurityGroup")
                                .vpc(vpc)
                                .allowAllOutbound(true)
                                .build();

                // Allow SSH & HTTP
                securityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(22), "Allow SSH access");
                securityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(9090), "Allow HTTP access");

                // Assume you have an existing key pair named "restapis"
                IKeyPair keyPair = KeyPair.fromKeyPairName(this, "key-0f33f15f006c83685", "restapis");

                // Define EC2 Instance
                Instance ec2Instance = Instance.Builder.create(this, "SpringBootInstance")
                                .instanceType(InstanceType.of(InstanceClass.BURSTABLE3, InstanceSize.MICRO)) // t3.micro
                                .machineImage(MachineImage.latestAmazonLinux2())
                                .vpc(vpc)
                                .vpcSubnets(SubnetSelection.builder()
                                                .subnetType(SubnetType.PUBLIC) // Ensure a public subnet
                                                .build())

                                .securityGroup(securityGroup)
                                .keyPair(keyPair) // Replace with your key pair name
                                .build();

                // User Data to Install Java and Run Spring Boot
                ec2Instance.addUserData(
                                "#!/bin/bash",
                                "sudo yum update -y",
                                "wget https://corretto.aws/downloads/latest/amazon-corretto-21-x64-linux-jdk.rpm",
                                "sudo yum localinstall amazon-corretto-21-x64-linux-jdk.rpm -y",
                                "wget https://test-restapis-bucket-1743665859377.s3.amazonaws.com/myapp.jar -O /home/ec2-user/app.jar",
                                "java -jar /home/ec2-user/app.jar > /home/ec2-user/app.log 2>&1 &");

                // Output Public IP
                CfnOutput.Builder.create(this, "InstancePublicIp")
                                .value(ec2Instance.getInstancePublicIp())
                                .description("Public IP of the EC2 instance")
                                .build();
        }
}