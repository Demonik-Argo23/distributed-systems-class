"""
Test client for Zelda Characters gRPC Service
This client demonstrates all three types of RPC methods:
- Unary
- Client Streaming
- Server Streaming
"""

import grpc
import characters_pb2
import characters_pb2_grpc
from typing import Iterator


def run_unary_examples(stub):
    """Test Unary RPC methods."""
    print("\n" + "="*60)
    print("UNARY RPC EXAMPLES")
    print("="*60)
    
    # 1. Create a character
    print("\n[1] Creating a new character (CreateCharacter)...")
    try:
        response = stub.CreateCharacter(characters_pb2.CreateCharacterRequest(
            name="Ganondorf",
            email="ganondorf@evil.com",
            game="Ocarina of Time",
            race="Gerudo",
            health=500,
            stamina=300,
            attack=200,
            defense=150,
            weapons=["25", "26"]
        ))
        print(f"✅ Character created: {response.name} (ID: {response.id})")
        character_id = response.id
    except grpc.RpcError as e:
        print(f"❌ Error: {e.code()} - {e.details()}")
        return
    
    # 2. Get the character
    print(f"\n[2] Getting character by ID (GetCharacter)...")
    try:
        response = stub.GetCharacter(characters_pb2.GetCharacterRequest(id=character_id))
        print(f"✅ Found character: {response.name}")
        print(f"   Email: {response.email}")
        print(f"   Game: {response.game}")
        print(f"   Stats: HP={response.health}, ATK={response.attack}, DEF={response.defense}")
    except grpc.RpcError as e:
        print(f"❌ Error: {e.code()} - {e.details()}")
    
    # 3. Update the character
    print(f"\n[3] Updating character (UpdateCharacter)...")
    try:
        response = stub.UpdateCharacter(characters_pb2.UpdateCharacterRequest(
            id=character_id,
            attack=250,  # Increase attack
            defense=180  # Increase defense
        ))
        print(f"✅ Character updated: {response.name}")
        print(f"   New stats: ATK={response.attack}, DEF={response.defense}")
    except grpc.RpcError as e:
        print(f"❌ Error: {e.code()} - {e.details()}")
    
    # 4. List characters by weapon
    print(f"\n[4] Finding characters who use weapon ID '1' (ListCharactersByWeapon)...")
    try:
        response = stub.ListCharactersByWeapon(
            characters_pb2.WeaponFilterRequest(weapon_id="1")
        )
        print(f"✅ Found {len(response.characters)} character(s):")
        for char in response.characters:
            print(f"   - {char.name} ({char.email})")
    except grpc.RpcError as e:
        print(f"❌ Error: {e.code()} - {e.details()}")
    
    # 5. Delete the character
    print(f"\n[5] Deleting character (DeleteCharacter)...")
    try:
        response = stub.DeleteCharacter(characters_pb2.DeleteCharacterRequest(id=character_id))
        if response.success:
            print(f"✅ {response.message}")
        else:
            print(f"❌ {response.message}")
    except grpc.RpcError as e:
        print(f"❌ Error: {e.code()} - {e.details()}")


def generate_batch_characters() -> Iterator[characters_pb2.CreateCharacterRequest]:
    """Generate multiple character requests for batch creation."""
    characters = [
        {
            "name": "Sheik",
            "email": "sheik@hyrule.com",
            "game": "Ocarina of Time",
            "race": "Hylian",
            "health": 110,
            "stamina": 130,
            "attack": 85,
            "defense": 75,
            "weapons": ["7", "11"]
        },
        {
            "name": "Impa",
            "email": "impa@sheikah.com",
            "game": "Ocarina of Time",
            "race": "Sheikah",
            "health": 140,
            "stamina": 115,
            "attack": 95,
            "defense": 100,
            "weapons": ["14"]
        },
        {
            "name": "Riju",
            "email": "riju@gerudo.com",
            "game": "Breath of the Wild",
            "race": "Gerudo",
            "health": 100,
            "stamina": 90,
            "attack": 80,
            "defense": 70,
            "weapons": ["19"]
        }
    ]
    
    for char in characters:
        yield characters_pb2.CreateCharacterRequest(**char)


def run_client_streaming_example(stub):
    """Test Client Streaming RPC method."""
    print("\n" + "="*60)
    print("CLIENT STREAMING RPC EXAMPLE")
    print("="*60)
    
    print("\n[1] Creating multiple characters in batch (CreateCharactersBatch)...")
    print("    Sending stream of 3 characters...")
    
    try:
        response = stub.CreateCharactersBatch(generate_batch_characters())
        print(f"\n✅ Batch creation completed!")
        print(f"   Created: {response.created_count} character(s)")
        print(f"   IDs: {', '.join(response.created_ids)}")
        if response.errors:
            print(f"   Errors: {len(response.errors)}")
            for error in response.errors:
                print(f"     - {error}")
    except grpc.RpcError as e:
        print(f"❌ Error: {e.code()} - {e.details()}")


def run_server_streaming_example(stub):
    """Test Server Streaming RPC method."""
    print("\n" + "="*60)
    print("SERVER STREAMING RPC EXAMPLE")
    print("="*60)
    
    print("\n[1] Listing all characters from 'Breath of the Wild' (ListCharactersByGame)...")
    print("    Receiving stream of characters...\n")
    
    try:
        count = 0
        for character in stub.ListCharactersByGame(
            characters_pb2.ListCharactersRequest(
                game="Breath of the Wild",
                limit=50
            )
        ):
            count += 1
            print(f"   [{count}] {character.name} ({character.race})")
            print(f"       Email: {character.email}")
            print(f"       Stats: HP={character.health}, STM={character.stamina}, "
                  f"ATK={character.attack}, DEF={character.defense}")
            print(f"       Weapons: {', '.join(character.weapons) if character.weapons else 'None'}")
            print()
        
        print(f"✅ Received {count} character(s) from stream")
    
    except grpc.RpcError as e:
        print(f"❌ Error: {e.code()} - {e.details()}")


def run_validation_examples(stub):
    """Test validation error handling."""
    print("\n" + "="*60)
    print("VALIDATION EXAMPLES")
    print("="*60)
    
    # Test 1: Missing required field
    print("\n[1] Testing missing required field...")
    try:
        stub.CreateCharacter(characters_pb2.CreateCharacterRequest(
            name="",  # Empty name
            email="test@test.com",
            game="Test Game",
            health=100,
            stamina=100,
            attack=50,
            defense=50
        ))
    except grpc.RpcError as e:
        print(f"✅ Validation caught: {e.code()} - {e.details()}")
    
    # Test 2: Invalid email format
    print("\n[2] Testing invalid email format...")
    try:
        stub.CreateCharacter(characters_pb2.CreateCharacterRequest(
            name="Test Character",
            email="invalid-email",  # Invalid format
            game="Test Game",
            health=100,
            stamina=100,
            attack=50,
            defense=50
        ))
    except grpc.RpcError as e:
        print(f"✅ Validation caught: {e.code()} - {e.details()}")
    
    # Test 3: Numeric out of range
    print("\n[3] Testing numeric value out of range...")
    try:
        stub.CreateCharacter(characters_pb2.CreateCharacterRequest(
            name="Test Character",
            email="test2@test.com",
            game="Test Game",
            health=9999,  # Over max (999)
            stamina=100,
            attack=50,
            defense=50
        ))
    except grpc.RpcError as e:
        print(f"✅ Validation caught: {e.code()} - {e.details()}")
    
    # Test 4: Duplicate email (ALREADY_EXISTS)
    print("\n[4] Testing duplicate email...")
    try:
        # First, create a character
        stub.CreateCharacter(characters_pb2.CreateCharacterRequest(
            name="Original",
            email="duplicate@test.com",
            game="Test Game",
            health=100,
            stamina=100,
            attack=50,
            defense=50
        ))
        # Try to create another with same email
        stub.CreateCharacter(characters_pb2.CreateCharacterRequest(
            name="Duplicate",
            email="duplicate@test.com",  # Same email
            game="Test Game",
            health=100,
            stamina=100,
            attack=50,
            defense=50
        ))
    except grpc.RpcError as e:
        print(f"✅ Validation caught: {e.code()} - {e.details()}")
    
    # Test 5: Not found
    print("\n[5] Testing NOT_FOUND error...")
    try:
        stub.GetCharacter(characters_pb2.GetCharacterRequest(
            id="507f1f77bcf86cd799439011"  # Valid format but doesn't exist
        ))
    except grpc.RpcError as e:
        print(f"✅ Validation caught: {e.code()} - {e.details()}")


def main():
    # Connect to gRPC server
    channel = grpc.insecure_channel('localhost:50051')
    stub = characters_pb2_grpc.CharacterServiceStub(channel)
    
    print("="*60)
    print("ZELDA CHARACTERS gRPC CLIENT TEST")
    print("="*60)
    print("Connecting to localhost:50051...")
    
    try:
        # Run all examples
        run_unary_examples(stub)
        run_client_streaming_example(stub)
        run_server_streaming_example(stub)
        run_validation_examples(stub)
        
        print("\n" + "="*60)
        print("ALL TESTS COMPLETED")
        print("="*60)
    
    except Exception as e:
        print(f"\n❌ Unexpected error: {str(e)}")
    
    finally:
        channel.close()


if __name__ == '__main__':
    main()
