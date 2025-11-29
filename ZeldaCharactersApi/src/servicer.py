import grpc
from concurrent import futures
import logging
from datetime import datetime
from bson import ObjectId
from pymongo.errors import DuplicateKeyError

import characters_pb2
import characters_pb2_grpc
from database import Database
from validators import (
    validate_create_character_request,
    validate_update_character_request,
    validate_required_field
)

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class CharacterServicer(characters_pb2_grpc.CharacterServiceServicer):
    def __init__(self):
        self.db = Database()
        self.collection = self.db.get_collection()
    
    def _character_to_response(self, character):
        try:
            created_at = character.get('created_at') or character.get('createdAt')
            updated_at = character.get('updated_at') or character.get('updatedAt')
            
            return characters_pb2.CharacterResponse(
                id=str(character['_id']),
                name=character.get('name', ''),
                email=character.get('email', ''),
                game=character.get('game', ''),
                race=character.get('race', ''),
                health=character.get('health', 0),
                stamina=character.get('stamina', 0),
                attack=character.get('attack', 0),
                defense=character.get('defense', 0),
                weapons=character.get('weapons', []),
                created_at=created_at.isoformat() if created_at else datetime.utcnow().isoformat(),
                updated_at=updated_at.isoformat() if updated_at else datetime.utcnow().isoformat()
            )
        except Exception as e:
            logger.error(f"Error converting character to response: {str(e)}, character: {character}")
            raise
    
    def GetCharacter(self, request, context):
        logger.info(f"GetCharacter called with id: {request.id}")
        
        if not request.id or not request.id.strip():
            context.set_code(grpc.StatusCode.INVALID_ARGUMENT)
            context.set_details("Character ID is required")
            return characters_pb2.CharacterResponse()
        
        try:
            if ObjectId.is_valid(request.id):
                character = self.collection.find_one({'_id': ObjectId(request.id)})
            else:
                character = self.collection.find_one({'_id': request.id})
            
            if not character:
                context.set_code(grpc.StatusCode.NOT_FOUND)
                context.set_details(f"Character with ID {request.id} not found")
                return characters_pb2.CharacterResponse()
            
            return self._character_to_response(character)
        
        except Exception as e:
            logger.error(f"Error getting character: {str(e)}", exc_info=True)
            context.set_code(grpc.StatusCode.INTERNAL)
            context.set_details(f"Internal error: {str(e)}")
            return characters_pb2.CharacterResponse()
    
    def CreateCharacter(self, request, context):
        logger.info(f"CreateCharacter called for: {request.name}")
        
        valid, error = validate_create_character_request(request)
        if not valid:
            context.set_code(grpc.StatusCode.INVALID_ARGUMENT)
            context.set_details(error)
            return characters_pb2.CharacterResponse()
        
        character_doc = {
            'name': request.name.strip(),
            'email': request.email.strip().lower(),
            'game': request.game.strip(),
            'race': request.race.strip() if request.race else '',
            'health': request.health,
            'stamina': request.stamina,
            'attack': request.attack,
            'defense': request.defense,
            'weapons': list(request.weapons),
            'created_at': datetime.utcnow(),
            'updated_at': datetime.utcnow()
        }
        
        try:
            result = self.collection.insert_one(character_doc)
            character_doc['_id'] = result.inserted_id
            logger.info(f"Character created with ID: {result.inserted_id}")
            return self._character_to_response(character_doc)
        
        except DuplicateKeyError:
            context.set_code(grpc.StatusCode.ALREADY_EXISTS)
            context.set_details(f"Character with email {request.email} already exists")
            return characters_pb2.CharacterResponse()
        
        except Exception as e:
            logger.error(f"Error creating character: {str(e)}")
            context.set_code(grpc.StatusCode.INTERNAL)
            context.set_details(f"Internal error: {str(e)}")
            return characters_pb2.CharacterResponse()
    
    def CreateCharactersBatch(self, request_iterator, context):
        logger.info("CreateCharactersBatch called")
        
        created_count = 0
        created_ids = []
        errors = []
        
        for request in request_iterator:
            valid, error = validate_create_character_request(request)
            if not valid:
                errors.append(f"{request.name or 'Unknown'}: {error}")
                continue
            
            character_doc = {
                'name': request.name.strip(),
                'email': request.email.strip().lower(),
                'game': request.game.strip(),
                'race': request.race.strip() if request.race else '',
                'health': request.health,
                'stamina': request.stamina,
                'attack': request.attack,
                'defense': request.defense,
                'weapons': list(request.weapons),
                'created_at': datetime.utcnow(),
                'updated_at': datetime.utcnow()
            }
            
            try:
                result = self.collection.insert_one(character_doc)
                created_count += 1
                created_ids.append(str(result.inserted_id))
                logger.info(f"Character created in batch: {request.name} ({result.inserted_id})")
            
            except DuplicateKeyError:
                errors.append(f"{request.name}: Email {request.email} already exists")
            
            except Exception as e:
                errors.append(f"{request.name}: {str(e)}")
        
        logger.info(f"Batch creation completed: {created_count} created, {len(errors)} errors")
        
        return characters_pb2.BatchResponse(
            created_count=created_count,
            created_ids=created_ids,
            errors=errors
        )
    
    def ListCharactersByGame(self, request, context):
        logger.info(f"ListCharactersByGame called for game: {request.game}")
        
        valid, error = validate_required_field(request.game, "Game")
        if not valid:
            context.set_code(grpc.StatusCode.INVALID_ARGUMENT)
            context.set_details(error)
            return
        
        limit = request.limit if request.limit > 0 else 100
        if limit > 1000:
            limit = 1000
        
        try:
            cursor = self.collection.find({'game': request.game}).limit(limit)
            
            count = 0
            for character in cursor:
                yield self._character_to_response(character)
                count += 1
            
            logger.info(f"Streamed {count} characters for game: {request.game}")
        
        except Exception as e:
            logger.error(f"Error listing characters: {str(e)}")
            context.set_code(grpc.StatusCode.INTERNAL)
            context.set_details(f"Internal error: {str(e)}")
    
    def UpdateCharacter(self, request, context):
        logger.info(f"UpdateCharacter called for id: {request.id}")
        
        valid, error = validate_update_character_request(request)
        if not valid:
            context.set_code(grpc.StatusCode.INVALID_ARGUMENT)
            context.set_details(error)
            return characters_pb2.CharacterResponse()
        
        try:
            if ObjectId.is_valid(request.id):
                existing = self.collection.find_one({'_id': ObjectId(request.id)})
                query_id = ObjectId(request.id)
            else:
                existing = self.collection.find_one({'_id': request.id})
                query_id = request.id
            
            if not existing:
                context.set_code(grpc.StatusCode.NOT_FOUND)
                context.set_details(f"Character with ID {request.id} not found")
                return characters_pb2.CharacterResponse()
        except Exception as e:
            context.set_code(grpc.StatusCode.INVALID_ARGUMENT)
            context.set_details(f"Invalid character ID: {str(e)}")
            return characters_pb2.CharacterResponse()
        
        update_doc = {'updated_at': datetime.utcnow()}
        
        if request.name:
            update_doc['name'] = request.name.strip()
        if request.email:
            update_doc['email'] = request.email.strip().lower()
        if request.game:
            update_doc['game'] = request.game.strip()
        if request.race:
            update_doc['race'] = request.race.strip()
        if request.health > 0:
            update_doc['health'] = request.health
        if request.stamina > 0:
            update_doc['stamina'] = request.stamina
        if request.attack > 0:
            update_doc['attack'] = request.attack
        if request.defense > 0:
            update_doc['defense'] = request.defense
        if request.weapons:
            update_doc['weapons'] = list(request.weapons)
        
        try:
            self.collection.update_one(
                {'_id': query_id},
                {'$set': update_doc}
            )
            
            updated_character = self.collection.find_one({'_id': query_id})
            logger.info(f"Character updated: {request.id}")
            return self._character_to_response(updated_character)
        
        except DuplicateKeyError:
            context.set_code(grpc.StatusCode.ALREADY_EXISTS)
            context.set_details(f"Email {request.email} is already in use")
            return characters_pb2.CharacterResponse()
        
        except Exception as e:
            logger.error(f"Error updating character: {str(e)}")
            context.set_code(grpc.StatusCode.INTERNAL)
            context.set_details(f"Internal error: {str(e)}")
            return characters_pb2.CharacterResponse()
    
    def DeleteCharacter(self, request, context):
        logger.info(f"DeleteCharacter called for id: {request.id}")
        
        if not request.id or not request.id.strip():
            context.set_code(grpc.StatusCode.INVALID_ARGUMENT)
            context.set_details("Character ID is required")
            return characters_pb2.DeleteResponse(success=False, message="Character ID is required")
        
        try:
            if ObjectId.is_valid(request.id):
                result = self.collection.delete_one({'_id': ObjectId(request.id)})
            else:
                result = self.collection.delete_one({'_id': request.id})
            
            if result.deleted_count == 0:
                context.set_code(grpc.StatusCode.NOT_FOUND)
                context.set_details(f"Character with ID {request.id} not found")
                return characters_pb2.DeleteResponse(
                    success=False,
                    message=f"Character with ID {request.id} not found"
                )
            
            logger.info(f"Character deleted: {request.id}")
            return characters_pb2.DeleteResponse(
                success=True,
                message=f"Character {request.id} deleted successfully"
            )
        
        except Exception as e:
            logger.error(f"Error deleting character: {str(e)}")
            context.set_code(grpc.StatusCode.INTERNAL)
            context.set_details(f"Internal error: {str(e)}")
            return characters_pb2.DeleteResponse(success=False, message=str(e))
    
    def ListCharactersByWeapon(self, request, context):
        logger.info(f"ListCharactersByWeapon called for weapon_id: {request.weapon_id}")
        
        if not request.weapon_id or not request.weapon_id.strip():
            context.set_code(grpc.StatusCode.INVALID_ARGUMENT)
            context.set_details("Weapon ID is required")
            return characters_pb2.CharacterListResponse()
        
        try:
            cursor = self.collection.find({'weapons': request.weapon_id})
            
            characters = [self._character_to_response(char) for char in cursor]
            logger.info(f"Found {len(characters)} characters with weapon {request.weapon_id}")
            
            return characters_pb2.CharacterListResponse(characters=characters)
        
        except Exception as e:
            logger.error(f"Error listing characters by weapon: {str(e)}")
            context.set_code(grpc.StatusCode.INTERNAL)
            context.set_details(f"Internal error: {str(e)}")
            return characters_pb2.CharacterListResponse()
