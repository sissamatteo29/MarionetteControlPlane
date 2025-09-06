"""
Minimal styling module - only essential styles without over-engineering.
"""

import tkinter as tk
from tkinter import ttk


class ModernStyles:
    """Essential styling configurations."""
    
    @classmethod
    def setup_styles(cls):
        """Configure essential visual styles for ttk widgets."""
        style = ttk.Style()
        
        # Use a clean theme
        available_themes = style.theme_names()
        if 'vista' in available_themes:
            style.theme_use('vista')
        elif 'clam' in available_themes:
            style.theme_use('clam')
        
        # Configure modern frame style
        style.configure('Modern.TFrame',
                       background='#ecf0f1',
                       relief='flat',
                       borderwidth=0)
        
        # Configure modern notebook style
        style.configure('Modern.TNotebook',
                       background='#ecf0f1',
                       borderwidth=0)
        
        style.configure('Modern.TNotebook.Tab',
                       background='#ffffff',
                       foreground='#2c3e50',
                       borderwidth=1,
                       padding=[15, 8],
                       font=('Segoe UI', 10))
        
        style.map('Modern.TNotebook.Tab',
                 background=[('selected', '#3498db'), ('active', '#2ecc71')],
                 foreground=[('selected', 'white'), ('active', 'white')])
    
    @classmethod
    def add_button_hover_effects(cls, button: tk.Widget, hover_color: str, normal_color: str):
        """Add simple hover effects to a button widget."""
        def on_enter(e):
            button.configure(bg=hover_color)
        
        def on_leave(e):
            button.configure(bg=normal_color)
        
        button.bind("<Enter>", on_enter)
        button.bind("<Leave>", on_leave)