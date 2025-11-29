import re
from typing import Tuple

def validate_required_field(value: str, field_name: str, min_length: int = 1) -> Tuple[bool, str]:
    """Validate that a required field is not empty and meets minimum length."""
    if not value or not value.strip():
        return False, f"{field_name} is required"
    if len(value.strip()) < min_length:
        return False, f"{field_name} must be at least {min_length} characters"
    return True, ""

def validate_email(email: str) -> Tuple[bool, str]:
    """Validate email format."""
    if not email or not email.strip():
        return False, "Email is required"
    
    # RFC 5322 simplified regex
    pattern = r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$'
    if not re.match(pattern, email):
        return False, "Invalid email format"
    return True, ""

def validate_numeric_range(value: int, field_name: str, min_val: int = 1, max_val: int = 999) -> Tuple[bool, str]:
    """Validate numeric field is within range."""
    if value < min_val or value > max_val:
        return False, f"{field_name} must be between {min_val} and {max_val}"
    return True, ""

def validate_create_character_request(request) -> Tuple[bool, str]:
    """Validate all fields for character creation."""
    
    # Validate name
    valid, error = validate_required_field(request.name, "Name", min_length=2)
    if not valid:
        return False, error
    
    # Validate email
    valid, error = validate_email(request.email)
    if not valid:
        return False, error
    
    # Validate game
    valid, error = validate_required_field(request.game, "Game")
    if not valid:
        return False, error
    
    # Validate numeric fields
    fields = [
        (request.health, "Health"),
        (request.stamina, "Stamina"),
        (request.attack, "Attack"),
        (request.defense, "Defense")
    ]
    
    for value, name in fields:
        valid, error = validate_numeric_range(value, name)
        if not valid:
            return False, error
    
    return True, ""

def validate_update_character_request(request) -> Tuple[bool, str]:
    """Validate fields for character update (only non-empty fields)."""
    
    # Validate ID is required
    if not request.id or not request.id.strip():
        return False, "Character ID is required"
    
    # Validate name if provided
    if request.name:
        valid, error = validate_required_field(request.name, "Name", min_length=2)
        if not valid:
            return False, error
    
    # Validate email if provided
    if request.email:
        valid, error = validate_email(request.email)
        if not valid:
            return False, error
    
    # Validate game if provided
    if request.game:
        valid, error = validate_required_field(request.game, "Game")
        if not valid:
            return False, error
    
    # Validate numeric fields if provided (> 0 means it was set)
    numeric_fields = [
        (request.health, "Health"),
        (request.stamina, "Stamina"),
        (request.attack, "Attack"),
        (request.defense, "Defense")
    ]
    
    for value, name in numeric_fields:
        if value > 0:  # Only validate if value was provided
            valid, error = validate_numeric_range(value, name)
            if not valid:
                return False, error
    
    return True, ""
