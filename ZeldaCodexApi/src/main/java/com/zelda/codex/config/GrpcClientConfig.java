package com.zelda.codex.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zelda.codex.grpc.CharacterServiceGrpc;

/**
 * Configuration for gRPC client to connect to Characters service.
 */
@Configuration
public class GrpcClientConfig {

    @Value("${grpc.characters.host:localhost}")
    private String grpcHost;

    @Value("${grpc.characters.port:50051}")
    private int grpcPort;

    @Bean
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder
                .forAddress(grpcHost, grpcPort)
                .usePlaintext()
                .build();
    }

    @Bean
    public CharacterServiceGrpc.CharacterServiceBlockingStub characterServiceBlockingStub(ManagedChannel channel) {
        return CharacterServiceGrpc.newBlockingStub(channel);
    }

    @Bean
    public CharacterServiceGrpc.CharacterServiceStub characterServiceAsyncStub(ManagedChannel channel) {
        return CharacterServiceGrpc.newStub(channel);
    }
}
