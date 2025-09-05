"""Utility functions for the experiment analyzer."""

import json
import os
from typing import Any, Dict


def load_json_file(filepath: str) -> Dict[str, Any]:
    """
    Load and parse a JSON file.
    
    Args:
        filepath: Path to the JSON file
        
    Returns:
        Parsed JSON data as dictionary
        
    Raises:
        FileNotFoundError: If file doesn't exist
        json.JSONDecodeError: If file contains invalid JSON
    """
    with open(filepath, 'r') as file:
        return json.load(file)


def save_json_file(data: Dict[str, Any], filepath: str):
    """
    Save data to a JSON file.
    
    Args:
        data: Data to save
        filepath: Output file path
    """
    os.makedirs(os.path.dirname(filepath), exist_ok=True)
    
    with open(filepath, 'w') as file:
        json.dump(data, file, indent=2)


def format_number(value: float, precision: int = 2) -> str:
    """
    Format a number for display.
    
    Args:
        value: Number to format
        precision: Decimal places
        
    Returns:
        Formatted number string
    """
    if abs(value) >= 1000000:
        return f"{value/1000000:.{precision}f}M"
    elif abs(value) >= 1000:
        return f"{value/1000:.{precision}f}K"
    else:
        return f"{value:.{precision}f}"


def truncate_text(text: str, max_length: int = 20) -> str:
    """
    Truncate text to specified length.
    
    Args:
        text: Text to truncate
        max_length: Maximum length
        
    Returns:
        Truncated text with ellipsis if needed
    """
    if len(text) <= max_length:
        return text
    return text[:max_length-3] + "..."
