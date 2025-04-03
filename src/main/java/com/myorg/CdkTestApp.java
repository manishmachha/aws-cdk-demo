package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class CdkTestApp {
        public static void main(final String[] args) {
                App app = new App();

                Environment env = Environment.builder()
                                .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                                .region(System.getenv("CDK_DEFAULT_REGION"))
                                .build();

                StackProps props = StackProps.builder().env(env).build();

                // new RdsStack(app, "MySQLRDS-STACK");
                // new S3BucketStack(app, "TEST-S3-BUCKET", env);
                new SpringBootEC2Stack(app, "TEST-SPRINGBOOT-EC2-STACK", props);

                app.synth();
        }
}
