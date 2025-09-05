"""
GUI styling and utilities module.
Contains modern styling configurations and utility functions.
"""

import tkinter as tk
from tkinter import ttk
from typing import Callable


class ModernStyles:
    """Modern styling configurations for the GUI."""
    
    # Modern color scheme
    COLORS = {
        'primary': '#3498db',
        'secondary': '#2ecc71', 
        'accent': '#e74c3c',
        'warning': '#f39c12',
        'background': '#ecf0f1',
        'surface': '#ffffff',
        'text': '#2c3e50',
        'text_secondary': '#7f8c8d',
        'border': '#bdc3c7',
        'hover': '#34495e'
    }
    
    @classmethod
    def setup_styles(cls):
        """Configure modern visual styles for ttk widgets."""
        style = ttk.Style()
        
        # Use a modern theme
        available_themes = style.theme_names()
        if 'vista' in available_themes:
            style.theme_use('vista')
        elif 'clam' in available_themes:
            style.theme_use('clam')
        
        # Configure modern frame style
        style.configure('Modern.TFrame',
                       background=cls.COLORS['background'],
                       relief='flat',
                       borderwidth=0)
        
        # Configure card-style frame
        style.configure('Card.TFrame',
                       background=cls.COLORS['surface'],
                       relief='solid',
                       borderwidth=1,
                       lightcolor=cls.COLORS['border'],
                       darkcolor=cls.COLORS['border'])
        
        # Configure modern button style
        style.configure('Modern.TButton',
                       background=cls.COLORS['primary'],
                       foreground='white',
                       borderwidth=0,
                       focuscolor='none',
                       padding=[20, 10],
                       font=('Segoe UI', 10, 'bold'))
        
        style.map('Modern.TButton',
                 background=[('active', cls.COLORS['hover']), 
                           ('pressed', cls.COLORS['text'])])
        
        # Configure modern notebook style
        style.configure('Modern.TNotebook',
                       background=cls.COLORS['background'],
                       borderwidth=0,
                       tabmargins=[0, 5, 0, 0])
        
        style.configure('Modern.TNotebook.Tab',
                       background=cls.COLORS['surface'],
                       foreground=cls.COLORS['text'],
                       borderwidth=1,
                       padding=[20, 10],
                       font=('Segoe UI', 11))
        
        style.map('Modern.TNotebook.Tab',
                 background=[('selected', cls.COLORS['primary']), 
                           ('active', cls.COLORS['secondary'])],
                 foreground=[('selected', 'white'), ('active', 'white')])
    
    @classmethod
    def add_button_hover_effects(cls, button: tk.Widget, hover_color: str, normal_color: str):
        """Add hover effects to a button widget."""
        def on_enter(e):
            button.configure(bg=hover_color)
        
        def on_leave(e):
            button.configure(bg=normal_color)
        
        button.bind("<Enter>", on_enter)
        button.bind("<Leave>", on_leave)


class InfoCardFactory:
    """Factory for creating modern info cards."""
    
    @staticmethod
    def create_modern_info_card(parent: tk.Widget, icon: str, title: str, 
                              value: str, subtitle: str, color: str, bg_color: str) -> tk.Frame:
        """
        Create a modern glassmorphism-style info card.
        
        Args:
            parent: Parent widget
            icon: Emoji icon for the card
            title: Card title
            value: Main value to display
            subtitle: Subtitle text
            color: Primary color for the card
            bg_color: Background color for the card
            
        Returns:
            Configured card frame
        """
        # Main card container with rounded corners effect
        card = tk.Frame(parent, bg=bg_color, relief='flat', bd=0)
        card.configure(highlightbackground="#e2e8f0", highlightthickness=1)
        
        # Inner content frame
        content_frame = tk.Frame(card, bg=bg_color)
        content_frame.pack(fill=tk.BOTH, expand=True, padx=20, pady=15)
        
        # Icon with modern styling
        icon_label = tk.Label(content_frame, text=icon, font=('Segoe UI Emoji', 20), 
                             bg=bg_color, fg=color)
        icon_label.pack(anchor='w', pady=(0, 8))
        
        # Title with modern typography
        title_label = tk.Label(content_frame, text=title, 
                              font=('Segoe UI', 9, 'bold'), 
                              bg=bg_color, fg="#374151")
        title_label.pack(anchor='w')
        
        # Value with emphasis
        value_label = tk.Label(content_frame, text=value,
                              font=('Segoe UI', 16, 'bold'), 
                              bg=bg_color, fg=color)
        value_label.pack(anchor='w', pady=(2, 4))
        
        # Subtitle with muted styling
        subtitle_label = tk.Label(content_frame, text=subtitle,
                                 font=('Segoe UI', 8), 
                                 bg=bg_color, fg="#6b7280")
        subtitle_label.pack(anchor='w')
        
        return card
    
    @staticmethod
    def create_summary_card(parent: tk.Widget, icon: str, value: str, 
                          title: str, color: str) -> tk.Frame:
        """Create a summary statistics card."""
        card_frame = tk.Frame(parent, bg='white', relief='solid', borderwidth=1)
        card_frame.configure(highlightbackground="#e8e8e8", highlightthickness=1)
        
        # Content container
        content = tk.Frame(card_frame, bg='white')
        content.pack(fill=tk.BOTH, expand=True, padx=15, pady=12)
        
        # Icon
        icon_label = tk.Label(content, text=icon, font=('Segoe UI Emoji', 24), 
                             bg='white', fg=color)
        icon_label.pack(pady=(0, 8))
        
        # Value
        value_label = tk.Label(content, text=str(value), 
                              font=('Segoe UI', 20, 'bold'), 
                              bg='white', fg=color)
        value_label.pack()
        
        # Title
        title_label = tk.Label(content, text=title, 
                              font=('Segoe UI', 10), 
                              bg='white', fg='#666666')
        title_label.pack(pady=(4, 0))
        
        return card_frame


class LayoutManager:
    """Utility class for managing layout operations."""
    
    @staticmethod
    def clear_frame(frame: tk.Widget):
        """Clear all widgets from a frame."""
        for widget in frame.winfo_children():
            widget.destroy()
    
    @staticmethod
    def create_scrollable_frame(parent: tk.Widget) -> tuple:
        """
        Create a scrollable frame setup with mouse wheel support.
        
        Returns:
            Tuple of (canvas, scrollbar, scrollable_frame)
        """
        canvas = tk.Canvas(parent, bg='white')
        scrollbar = ttk.Scrollbar(parent, orient="vertical", command=canvas.yview)
        scrollable_frame = ttk.Frame(canvas, style='Modern.TFrame')
        
        scrollable_frame.bind(
            "<Configure>",
            lambda e: canvas.configure(scrollregion=canvas.bbox("all"))
        )
        
        canvas.create_window((0, 0), window=scrollable_frame, anchor="nw")
        canvas.configure(yscrollcommand=scrollbar.set)
        
        # Add mouse wheel scrolling support
        def _on_mousewheel(event):
            canvas.yview_scroll(int(-1*(event.delta/120)), "units")
        
        def _bind_to_mousewheel(event):
            canvas.bind_all("<MouseWheel>", _on_mousewheel)
        
        def _unbind_from_mousewheel(event):
            canvas.unbind_all("<MouseWheel>")
        
        # Bind mouse wheel events when mouse enters/leaves the canvas
        canvas.bind('<Enter>', _bind_to_mousewheel)
        canvas.bind('<Leave>', _unbind_from_mousewheel)
        
        # For Linux systems, also bind Button-4 and Button-5 (scroll wheel)
        def _on_mousewheel_linux(event):
            if event.num == 4:
                canvas.yview_scroll(-1, "units")
            elif event.num == 5:
                canvas.yview_scroll(1, "units")
        
        def _bind_to_mousewheel_linux(event):
            canvas.bind_all("<Button-4>", _on_mousewheel_linux)
            canvas.bind_all("<Button-5>", _on_mousewheel_linux)
        
        def _unbind_from_mousewheel_linux(event):
            canvas.unbind_all("<Button-4>")
            canvas.unbind_all("<Button-5>")
        
        # Bind Linux scroll events
        canvas.bind('<Enter>', lambda e: [_bind_to_mousewheel(e), _bind_to_mousewheel_linux(e)])
        canvas.bind('<Leave>', lambda e: [_unbind_from_mousewheel(e), _unbind_from_mousewheel_linux(e)])
        
        return canvas, scrollbar, scrollable_frame
