package com.ash.cloud;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.ArrayList;
import java.util.List;

public class AwsCloudProvider implements CloudProvider {
    
    private final Ec2Client ec2Client;
    
    public AwsCloudProvider() {
        this.ec2Client = Ec2Client.builder()
                .region(Region.of(CloudConfig.AwsConfig.REGION))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Override
    public List<Instance> listInstances() throws Exception {
        List<Instance> instances = new ArrayList<>();
        
        try {
            DescribeInstancesResponse response = ec2Client.describeInstances();
            
            for (Reservation reservation : response.reservations()) {
                for (software.amazon.awssdk.services.ec2.model.Instance awsInstance : reservation.instances()) {
                    instances.add(new Instance(
                        awsInstance.instanceId(),
                        awsInstance.instanceTypeAsString(),
                        awsInstance.state().nameAsString(),
                        "AWS"
                    ));
                }
            }
        } catch (Exception e) {
            throw new Exception("AWS: Failed to list instances - " + e.getMessage());
        }
        
        return instances;
    }

    @Override
    public void deleteInstance(String instanceId) throws Exception {
        try {
            TerminateInstancesRequest request = TerminateInstancesRequest.builder()
                    .instanceIds(instanceId)
                    .build();
            ec2Client.terminateInstances(request);
        } catch (Exception e) {
            throw new Exception("AWS: Failed to delete instance " + instanceId + " - " + e.getMessage());
        }
    }

    @Override
    public String getProviderName() {
        return "AWS";
    }
}