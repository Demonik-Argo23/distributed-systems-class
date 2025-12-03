import os
from pymongo import MongoClient
from dotenv import load_dotenv

load_dotenv()

class Database:
    _instance = None
    
    def __new__(cls):
        if cls._instance is None:
            cls._instance = super(Database, cls).__new__(cls)
            cls._instance._initialize()
        return cls._instance
    
    def _initialize(self):
        mongo_uri = os.getenv('MONGO_URI', 'mongodb://localhost:27017/')
        mongo_db = os.getenv('MONGO_DB', 'zelda_characters')
        
        self.client = MongoClient(mongo_uri)
        self.db = self.client[mongo_db]
        self.characters = self.db.characters
        
        # Ensure indexes
        self.characters.create_index('email', unique=True)
        self.characters.create_index('game')
        self.characters.create_index('weapons')
    
    def get_collection(self):
        return self.characters
    
    def close(self):
        self.client.close()
