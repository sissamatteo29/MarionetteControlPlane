"""
Properly fixed GUI components with forced title heights.
"""

import tkinter as tk
from tkinter import ttk
from typing import Dict, Any

from .styles import ModernStyles


class TitleBar:
    """Title bar component with FORCED proper height."""
    
    def __init__(self, parent: tk.Widget):
        self.frame = ttk.Frame(parent, style='Modern.TFrame')
        self.create_content()
    
    def create_content(self):
        """Create title bar content with FORCED increased height."""
        # Content container that fills the parent (which has forced height)
        content_container = tk.Frame(self.frame, bg='white')
        content_container.pack(fill=tk.BOTH, expand=True, padx=25, pady=20)
        
        # Icon and title layout
        layout_frame = tk.Frame(content_container, bg='white')
        layout_frame.pack(fill=tk.BOTH, expand=True)
        
        # Icon with larger size
        icon_label = tk.Label(layout_frame, text="🔬", 
                             font=('Segoe UI Emoji', 36), bg='white')
        icon_label.pack(side=tk.LEFT, padx=(0, 20), pady=10)
        
        # Title text container
        title_frame = tk.Frame(layout_frame, bg='white')
        title_frame.pack(side=tk.LEFT, fill=tk.Y, pady=10)
        
        # Main title with larger font
        main_title = tk.Label(title_frame, text="Marionette Experiment Analyzer", 
                             font=('Segoe UI', 24, 'bold'), 
                             bg='white', fg='#2c3e50')
        main_title.pack(anchor='w', pady=(5, 0))
        
        # Subtitle with proper spacing
        subtitle = tk.Label(title_frame, text="Advanced A/B Testing Results Analysis", 
                           font=('Segoe UI', 13), 
                           bg='white', fg='#7f8c8d')
        subtitle.pack(anchor='w', pady=(8, 5))
    
    def pack(self, **kwargs):
        """Pack the title bar frame."""
        self.frame.pack(**kwargs)


class Sidebar:
    """Sidebar component with proper section title positioning."""
    
    def __init__(self, parent: tk.Widget, main_app):
        self.main_app = main_app
        self.frame = tk.Frame(parent, bg='#2c3e50', width=280)
        self.frame.pack_propagate(False)
        self.create_content()
    
    def create_content(self):
        """Create sidebar content with properly positioned titles."""
        # Sidebar main title
        title_label = tk.Label(self.frame, text="Analysis Control", 
                              font=('Segoe UI', 15, 'bold'), 
                              bg='#2c3e50', fg='white')
        title_label.pack(pady=(20, 25), padx=20)
        
        # Data loading section
        self.create_data_loading_section()
        
        # Data summary section (initially hidden)
        self.summary_frame = tk.Frame(self.frame, bg='#2c3e50')
        self.summary_title = None
        self.summary_container = tk.Frame(self.summary_frame, bg='#2c3e50')
        self.summary_container.pack(fill=tk.X, pady=(10, 0))
        
        # Metrics section (initially hidden)
        self.metrics_frame = tk.Frame(self.frame, bg='#2c3e50')
        self.metrics_title = None
        self.metrics_container = tk.Frame(self.metrics_frame, bg='#2c3e50')
        self.metrics_container.pack(fill=tk.X, pady=(10, 0))
    
    def create_data_loading_section(self):
        """Create the data loading section."""
        # Section title ABOVE the content
        section_title = tk.Label(self.frame, text="📁 Data Source", 
                                font=('Segoe UI', 11, 'bold'), 
                                bg='#2c3e50', fg='white')
        section_title.pack(anchor='w', padx=20, pady=(15, 10))
        
        # Section content
        section_frame = tk.Frame(self.frame, bg='#2c3e50')
        section_frame.pack(fill=tk.X, padx=20, pady=(0, 20))
        
        # Load data button
        load_btn = tk.Button(section_frame, text="Load Experiment Data", 
                            command=self.main_app.load_data,
                            bg='#3498db', fg='white', 
                            font=('Segoe UI', 10, 'bold'),
                            relief='flat', bd=0, cursor='hand2',
                            pady=10)
        load_btn.pack(fill=tk.X, pady=(0, 10))
        
        ModernStyles.add_button_hover_effects(load_btn, '#2980b9', '#3498db')
        
        # Status indicator
        self.status_label = tk.Label(section_frame, text="No data loaded", 
                                   font=('Segoe UI', 9), 
                                   bg='#2c3e50', fg='#95a5a6')
        self.status_label.pack(anchor='w')
    
    def update_summary(self, stats: Dict[str, Any]):
        """Update the summary section with new statistics."""
        # Clear existing summary
        for widget in self.summary_container.winfo_children():
            widget.destroy()
        
        if not stats:
            return
        
        # Show the summary section with title ABOVE
        if not self.summary_frame.winfo_ismapped():
            # Add section title FIRST
            self.summary_title = tk.Label(self.frame, text="📊 Data Summary", 
                                         font=('Segoe UI', 11, 'bold'), 
                                         bg='#2c3e50', fg='white')
            self.summary_title.pack(anchor='w', padx=20, pady=(15, 0))
            
            # Then pack the frame
            self.summary_frame.pack(fill=tk.X, padx=20, pady=(0, 0))
        
        # Update status
        self.status_label.configure(text="Data loaded successfully", fg='#2ecc71')
        
        # Create simple summary cards
        summary_items = [
            ("📋", stats.get('total_configurations', 0), "Configurations"),
            ("📏", stats.get('total_metrics', 0), "Metrics"),
        ]
        
        for icon, value, title in summary_items:
            card = self.create_summary_card(icon, value, title)
            card.pack(fill=tk.X, pady=(0, 8))
    
    def create_summary_card(self, icon: str, value: Any, title: str) -> tk.Frame:
        """Create a simple summary card."""
        card_frame = tk.Frame(self.summary_container, bg='#34495e', relief='flat', bd=1)
        
        # Content container
        content = tk.Frame(card_frame, bg='#34495e')
        content.pack(fill=tk.BOTH, expand=True, padx=10, pady=8)
        
        # Icon and value row
        top_row = tk.Frame(content, bg='#34495e')
        top_row.pack(fill=tk.X)
        
        icon_label = tk.Label(top_row, text=icon, font=('Segoe UI Emoji', 14), 
                             bg='#34495e', fg='white')
        icon_label.pack(side=tk.LEFT)
        
        value_label = tk.Label(top_row, text=str(value), 
                              font=('Segoe UI', 12, 'bold'), 
                              bg='#34495e', fg='#3498db')
        value_label.pack(side=tk.RIGHT)
        
        # Title
        title_label = tk.Label(content, text=title, 
                              font=('Segoe UI', 9), 
                              bg='#34495e', fg='#bdc3c7')
        title_label.pack(anchor='w', pady=(2, 0))
        
        return card_frame
    
    def update_metrics(self, metrics_data: list):
        """Update the metrics section with metric configurations."""
        # Clear existing metrics
        for widget in self.metrics_container.winfo_children():
            widget.destroy()
        
        if not metrics_data:
            return
        
        # Show the metrics section with title ABOVE
        if not self.metrics_frame.winfo_ismapped():
            # Add section title FIRST
            self.metrics_title = tk.Label(self.frame, text="📏 Performance Metrics", 
                                         font=('Segoe UI', 11, 'bold'), 
                                         bg='#2c3e50', fg='white')
            self.metrics_title.pack(anchor='w', padx=20, pady=(15, 0))
            
            # Then pack the frame
            self.metrics_frame.pack(fill=tk.X, padx=20, pady=(0, 0))
        
        # Sort metrics by order
        sorted_metrics = sorted(metrics_data, key=lambda x: x.get('order', 999))
        
        for metric in sorted_metrics[:5]:  # Show only first 5 to save space
            metric_card = self.create_metric_card(metric)
            metric_card.pack(fill=tk.X, pady=(0, 6))
    
    def create_metric_card(self, metric: dict) -> tk.Frame:
        """Create a simple metric information card."""
        card_frame = tk.Frame(self.metrics_container, bg='#34495e', relief='flat', bd=1)
        
        # Content container
        content = tk.Frame(card_frame, bg='#34495e')
        content.pack(fill=tk.BOTH, expand=True, padx=8, pady=6)
        
        # Header row
        header_row = tk.Frame(content, bg='#34495e')
        header_row.pack(fill=tk.X)
        
        # Order badge
        order_label = tk.Label(header_row, text=f"#{metric.get('order', '?')}", 
                              font=('Segoe UI', 8, 'bold'), 
                              bg='#3498db', fg='white',
                              relief='flat', bd=0, padx=4, pady=1)
        order_label.pack(side=tk.LEFT)
        
        # Direction indicator
        direction = metric.get('direction', 'higher')
        direction_symbol = "↑" if direction == 'higher' else "↓"
        direction_color = "#2ecc71" if direction == 'higher' else "#e74c3c"
        
        direction_label = tk.Label(header_row, text=direction_symbol, 
                                  font=('Segoe UI', 10, 'bold'), 
                                  bg='#34495e', fg=direction_color)
        direction_label.pack(side=tk.RIGHT)
        
        # Metric name
        name_label = tk.Label(content, text=metric.get('metricName', 'Unknown'), 
                             font=('Segoe UI', 9, 'bold'), 
                             bg='#34495e', fg='white')
        name_label.pack(anchor='w', pady=(3, 0))
        
        return card_frame
    
    def pack(self, **kwargs):
        """Pack the sidebar frame."""
        self.frame.pack(**kwargs)