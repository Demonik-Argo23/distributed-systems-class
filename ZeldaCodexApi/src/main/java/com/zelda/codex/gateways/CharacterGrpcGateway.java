package com.zelda.codex.gateways;

import com.zelda.codex.dtos.CharacterDTO;
import com.zelda.codex.grpc.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Gateway service for communicating with the gRPC Characters service.
 * This service acts as a client to the Python gRPC server.
 */
@Service
public class CharacterGrpcGateway {

    private static final Logger logger = LoggerFactory.getLogger(CharacterGrpcGateway.class);

    private final CharacterServiceGrpc.CharacterServiceBlockingStub blockingStub;
    private final CharacterServiceGrpc.CharacterServiceStub asyncStub;

    public CharacterGrpcGateway(
            CharacterServiceGrpc.CharacterServiceBlockingStub blockingStub,
            CharacterServiceGrpc.CharacterServiceStub asyncStub) {
        this.blockingStub = blockingStub;
        this.asyncStub = asyncStub;
    }

    /**
     * UNARY RPC: Get a character by ID.
     */
    public CharacterDTO getCharacterById(String id) {
        logger.info("Calling gRPC GetCharacter for id: {}", id);
        
        try {
            GetCharacterRequest request = GetCharacterRequest.newBuilder()
                    .setId(id)
                    .build();
            
            CharacterResponse response = blockingStub.getCharacter(request);
            return mapToDTO(response);
            
        } catch (StatusRuntimeException e) {
            logger.error("gRPC error getting character: {} - {}", e.getStatus().getCode(), e.getStatus().getDescription());
            throw new RuntimeException("Error getting character: " + e.getStatus().getDescription(), e);
        }
    }

    /**
     * UNARY RPC: Create a character.
     */
    public CharacterDTO createCharacter(CharacterDTO dto) {
        logger.info("Calling gRPC CreateCharacter for: {}", dto.getName());
        
        try {
            CreateCharacterRequest.Builder requestBuilder = CreateCharacterRequest.newBuilder()
                    .setName(dto.getName())
                    .setEmail(dto.getEmail())
                    .setGame(dto.getGame())
                    .setHealth(dto.getHealth())
                    .setStamina(dto.getStamina())
                    .setAttack(dto.getAttack())
                    .setDefense(dto.getDefense());
            
            if (dto.getRace() != null) {
                requestBuilder.setRace(dto.getRace());
            }
            
            if (dto.getWeapons() != null) {
                requestBuilder.addAllWeapons(dto.getWeapons());
            }
            
            CharacterResponse response = blockingStub.createCharacter(requestBuilder.build());
            return mapToDTO(response);
            
        } catch (StatusRuntimeException e) {
            logger.error("gRPC error creating character: {} - {}", e.getStatus().getCode(), e.getStatus().getDescription());
            
            if (e.getStatus().getCode() == Status.Code.ALREADY_EXISTS) {
                throw new RuntimeException("Character with this email already exists", e);
            } else if (e.getStatus().getCode() == Status.Code.INVALID_ARGUMENT) {
                throw new IllegalArgumentException("Invalid character data: " + e.getStatus().getDescription(), e);
            }
            
            throw new RuntimeException("Error creating character: " + e.getStatus().getDescription(), e);
        }
    }

    /**
     * CLIENT STREAMING RPC: Create multiple characters in batch.
     */
    public BatchResult createCharactersBatch(List<CharacterDTO> characters) {
        logger.info("Calling gRPC CreateCharactersBatch with {} characters", characters.size());
        
        final CountDownLatch latch = new CountDownLatch(1);
        final BatchResult result = new BatchResult();
        
        StreamObserver<BatchResponse> responseObserver = new StreamObserver<BatchResponse>() {
            @Override
            public void onNext(BatchResponse response) {
                result.createdCount = response.getCreatedCount();
                result.createdIds = new ArrayList<>(response.getCreatedIdsList());
                result.errors = new ArrayList<>(response.getErrorsList());
            }

            @Override
            public void onError(Throwable t) {
                logger.error("Error in batch creation: {}", t.getMessage());
                result.error = t.getMessage();
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                logger.info("Batch creation completed: {} created", result.createdCount);
                latch.countDown();
            }
        };

        StreamObserver<CreateCharacterRequest> requestObserver = asyncStub.createCharactersBatch(responseObserver);

        try {
            for (CharacterDTO dto : characters) {
                CreateCharacterRequest.Builder requestBuilder = CreateCharacterRequest.newBuilder()
                        .setName(dto.getName())
                        .setEmail(dto.getEmail())
                        .setGame(dto.getGame())
                        .setHealth(dto.getHealth())
                        .setStamina(dto.getStamina())
                        .setAttack(dto.getAttack())
                        .setDefense(dto.getDefense());
                
                if (dto.getRace() != null) {
                    requestBuilder.setRace(dto.getRace());
                }
                
                if (dto.getWeapons() != null) {
                    requestBuilder.addAllWeapons(dto.getWeapons());
                }
                
                requestObserver.onNext(requestBuilder.build());
            }
            
            requestObserver.onCompleted();
            
            // Wait for response
            if (!latch.await(10, TimeUnit.SECONDS)) {
                throw new RuntimeException("Batch creation timed out");
            }
            
            return result;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Batch creation interrupted", e);
        }
    }

    /**
     * SERVER STREAMING RPC: List characters by game.
     */
    public List<CharacterDTO> listCharactersByGame(String game, int limit) {
        logger.info("Calling gRPC ListCharactersByGame for game: {}", game);
        
        try {
            ListCharactersRequest request = ListCharactersRequest.newBuilder()
                    .setGame(game)
                    .setLimit(limit)
                    .build();
            
            Iterator<CharacterResponse> responses = blockingStub.listCharactersByGame(request);
            
            List<CharacterDTO> characters = new ArrayList<>();
            while (responses.hasNext()) {
                characters.add(mapToDTO(responses.next()));
            }
            
            logger.info("Received {} characters from stream", characters.size());
            return characters;
            
        } catch (StatusRuntimeException e) {
            logger.error("gRPC error listing characters: {} - {}", e.getStatus().getCode(), e.getStatus().getDescription());
            throw new RuntimeException("Error listing characters: " + e.getStatus().getDescription(), e);
        }
    }

    /**
     * UNARY RPC: Update a character.
     */
    public CharacterDTO updateCharacter(String id, CharacterDTO dto) {
        logger.info("Calling gRPC UpdateCharacter for id: {}", id);
        
        try {
            UpdateCharacterRequest.Builder requestBuilder = UpdateCharacterRequest.newBuilder()
                    .setId(id);
            
            if (dto.getName() != null) {
                requestBuilder.setName(dto.getName());
            }
            if (dto.getEmail() != null) {
                requestBuilder.setEmail(dto.getEmail());
            }
            if (dto.getGame() != null) {
                requestBuilder.setGame(dto.getGame());
            }
            if (dto.getRace() != null) {
                requestBuilder.setRace(dto.getRace());
            }
            if (dto.getHealth() != null) {
                requestBuilder.setHealth(dto.getHealth());
            }
            if (dto.getStamina() != null) {
                requestBuilder.setStamina(dto.getStamina());
            }
            if (dto.getAttack() != null) {
                requestBuilder.setAttack(dto.getAttack());
            }
            if (dto.getDefense() != null) {
                requestBuilder.setDefense(dto.getDefense());
            }
            if (dto.getWeapons() != null) {
                requestBuilder.addAllWeapons(dto.getWeapons());
            }
            
            CharacterResponse response = blockingStub.updateCharacter(requestBuilder.build());
            return mapToDTO(response);
            
        } catch (StatusRuntimeException e) {
            logger.error("gRPC error updating character: {} - {}", e.getStatus().getCode(), e.getStatus().getDescription());
            
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new RuntimeException("Character not found", e);
            }
            
            throw new RuntimeException("Error updating character: " + e.getStatus().getDescription(), e);
        }
    }

    /**
     * UNARY RPC: Delete a character.
     */
    public boolean deleteCharacter(String id) {
        logger.info("Calling gRPC DeleteCharacter for id: {}", id);
        
        try {
            DeleteCharacterRequest request = DeleteCharacterRequest.newBuilder()
                    .setId(id)
                    .build();
            
            DeleteResponse response = blockingStub.deleteCharacter(request);
            return response.getSuccess();
            
        } catch (StatusRuntimeException e) {
            logger.error("gRPC error deleting character: {} - {}", e.getStatus().getCode(), e.getStatus().getDescription());
            
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new RuntimeException("Character not found", e);
            }
            
            throw new RuntimeException("Error deleting character: " + e.getStatus().getDescription(), e);
        }
    }

    /**
     * UNARY RPC: List characters by weapon.
     */
    public List<CharacterDTO> listCharactersByWeapon(String weaponId) {
        logger.info("Calling gRPC ListCharactersByWeapon for weapon: {}", weaponId);
        
        try {
            WeaponFilterRequest request = WeaponFilterRequest.newBuilder()
                    .setWeaponId(weaponId)
                    .build();
            
            CharacterListResponse response = blockingStub.listCharactersByWeapon(request);
            
            List<CharacterDTO> characters = new ArrayList<>();
            for (CharacterResponse charResponse : response.getCharactersList()) {
                characters.add(mapToDTO(charResponse));
            }
            
            logger.info("Found {} characters with weapon {}", characters.size(), weaponId);
            return characters;
            
        } catch (StatusRuntimeException e) {
            logger.error("gRPC error listing characters by weapon: {} - {}", e.getStatus().getCode(), e.getStatus().getDescription());
            throw new RuntimeException("Error listing characters by weapon: " + e.getStatus().getDescription(), e);
        }
    }

    /**
     * Map gRPC CharacterResponse to DTO.
     */
    private CharacterDTO mapToDTO(CharacterResponse response) {
        CharacterDTO dto = new CharacterDTO();
        dto.setId(response.getId());
        dto.setName(response.getName());
        dto.setEmail(response.getEmail());
        dto.setGame(response.getGame());
        dto.setRace(response.getRace());
        dto.setHealth(response.getHealth());
        dto.setStamina(response.getStamina());
        dto.setAttack(response.getAttack());
        dto.setDefense(response.getDefense());
        dto.setWeapons(new ArrayList<>(response.getWeaponsList()));
        dto.setCreatedAt(response.getCreatedAt());
        dto.setUpdatedAt(response.getUpdatedAt());
        return dto;
    }

    /**
     * Result class for batch operations.
     */
    public static class BatchResult {
        public int createdCount;
        public List<String> createdIds = new ArrayList<>();
        public List<String> errors = new ArrayList<>();
        public String error;
    }
}
