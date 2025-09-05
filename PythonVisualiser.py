#!/usr/bin/env python3
"""
Modern GUI for analyzing A/B test experiment results.
Features a contemporary design with proper spacing and improved layouts.
"""

import json
import tkinter as tk
from tkinter import ttk, filedialog, messagebox
import pandas as pd
import matplotlib.pyplot as plt
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg, NavigationToolbar2Tk
from matplotlib.figure import Figure
import seaborn as sns
import numpy as np
from typing import Dict, List, Any
import os

# Set modern style for matplotlib
plt.style.use('seaborn-v0_8-darkgrid')
sns.set_palette("husl")

class ModernExperimentAnalyzer:
    def __init__(self, root):
        self.root = root
        self.root.title("Marionette Experiment Analyzer")
        self.root.geometry("1600x1000")
        self.root.configure(bg='#f0f0f0')
        
        self.data = None
        self.df_rankings = None
        self.df_configs = None
        self.df_metrics = None
        
        self.setup_modern_styles()
        self.create_modern_widgets()
        
    def setup_modern_styles(self):
        """Configure modern visual styles."""
        style = ttk.Style()
        
        # Use a modern theme
        available_themes = style.theme_names()
        if 'vista' in available_themes:
            style.theme_use('vista')
        elif 'clam' in available_themes:
            style.theme_use('clam')
        
        # Define modern color scheme
        colors = {
            'primary': '#2563eb',      # Modern blue
            'secondary': '#64748b',    # Slate gray
            'success': '#10b981',      # Green
            'warning': '#f59e0b',      # Amber
            'danger': '#ef4444',       # Red
            'light': '#f8fafc',        # Light gray
            'dark': '#1e293b',         # Dark slate
            'background': '#ffffff'    # White background
        }
        
        # Configure modern styles
        style.configure('Modern.TFrame', background=colors['background'], relief='flat')
        style.configure('Sidebar.TFrame', background=colors['light'], relief='flat', borderwidth=1)
        style.configure('Title.TLabel', font=('Segoe UI', 24, 'bold'), foreground=colors['dark'], background=colors['background'])
        style.configure('Header.TLabel', font=('Segoe UI', 14, 'bold'), foreground=colors['dark'], background=colors['light'])
        style.configure('Info.TLabel', font=('Segoe UI', 10), foreground=colors['secondary'], background=colors['light'])
        style.configure('Modern.TButton', font=('Segoe UI', 10), padding=(15, 8))
        style.configure('Primary.TButton', font=('Segoe UI', 10, 'bold'), padding=(15, 10))
        
        # Notebook styling
        style.configure('Modern.TNotebook', background=colors['background'], borderwidth=0, tabmargins=[2, 5, 2, 0])
        style.configure('Modern.TNotebook.Tab', 
                       background=colors['light'], 
                       foreground=colors['dark'],
                       padding=[20, 10],
                       font=('Segoe UI', 11))
        style.map('Modern.TNotebook.Tab',
                 background=[('selected', colors['primary']), ('active', colors['secondary'])],
                 foreground=[('selected', 'white'), ('active', 'white')])
        
    def create_modern_widgets(self):
        """Create the modern GUI layout."""
        # Main container
        main_container = ttk.Frame(self.root, style='Modern.TFrame', padding="0")
        main_container.pack(fill=tk.BOTH, expand=True)
        
        # Title bar
        self.create_title_bar(main_container)
        
        # Content area
        content_area = ttk.Frame(main_container, style='Modern.TFrame')
        content_area.pack(fill=tk.BOTH, expand=True, padx=20, pady=(0, 20))
        
        # Create horizontal layout: sidebar + main content
        self.create_modern_layout(content_area)
        
    def create_title_bar(self, parent):
        """Create a clean, modern title bar."""
        # Main title container with gradient-like background effect
        title_container = tk.Frame(parent, bg='#1e293b', height=120)
        title_container.pack(fill=tk.X)
        title_container.pack_propagate(False)
        
        # Inner content frame for proper padding
        content_frame = tk.Frame(title_container, bg='#1e293b')
        content_frame.pack(expand=True, fill=tk.BOTH, padx=40, pady=20)
        
        # Main title
        title_label = tk.Label(
            content_frame,
            text="Marionette Experiment Analyzer",
            font=('Helvetica', 28, 'bold'),
            fg='#ffffff',
            bg='#1e293b'
        )
        title_label.pack(anchor=tk.W)
        
        # Subtitle with accent color
        subtitle_label = tk.Label(
            content_frame,
            text="Intelligent A/B Testing Results Analysis & Visualization",
            font=('Helvetica', 14),
            fg='#60a5fa',
            bg='#1e293b'
        )
        subtitle_label.pack(anchor=tk.W, pady=(8, 0))
        
        # Status indicator (optional)
        status_frame = tk.Frame(content_frame, bg='#1e293b')
        status_frame.pack(anchor=tk.W, pady=(12, 0))
        
        status_dot = tk.Label(
            status_frame,
            text="●",
            font=('Helvetica', 12),
            fg='#10b981',
            bg='#1e293b'
        )
        status_dot.pack(side=tk.LEFT)
        
        status_text = tk.Label(
            status_frame,
            text="Ready for data analysis",
            font=('Helvetica', 11),
            fg='#94a3b8',
            bg='#1e293b'
        )
        status_text.pack(side=tk.LEFT, padx=(8, 0))
        
    def create_modern_layout(self, parent):
        """Create the main layout with sidebar and content area."""
        # Create paned window for resizable layout
        paned = ttk.PanedWindow(parent, orient=tk.HORIZONTAL)
        paned.pack(fill=tk.BOTH, expand=True)
        
        # Left sidebar
        self.create_modern_sidebar(paned)
        
        # Right content area
        self.create_content_area(paned)
        
    def create_modern_sidebar(self, parent):
        """Create a clean, modern sidebar."""
        # Sidebar container with clean background
        sidebar_container = tk.Frame(parent, bg='#f8fafc', width=320, relief='solid', borderwidth=1)
        sidebar_container.pack_propagate(False)
        
        # Main content area with padding
        sidebar_content = tk.Frame(sidebar_container, bg='#f8fafc')
        sidebar_content.pack(fill=tk.BOTH, expand=True, padx=20, pady=20)
        
        # Data loading section
        self.create_data_loading_section(sidebar_content)
        
        # Add some spacing
        tk.Frame(sidebar_content, bg='#f8fafc', height=25).pack(fill=tk.X)
        
        # Summary section
        self.create_summary_section(sidebar_content)
        
        parent.add(sidebar_container, weight=0)
        
    def create_data_loading_section(self, parent):
        """Create the data loading section with modern styling."""
        # Section header
        header_label = tk.Label(
            parent,
            text="Data Management",
            font=('Helvetica', 14, 'bold'),
            fg='#1e293b',
            bg='#f8fafc'
        )
        header_label.pack(anchor=tk.W, pady=(0, 15))
        
        # Load button with modern styling
        self.load_button = tk.Button(
            parent,
            text="Load Experiment Data",
            font=('Helvetica', 11, 'bold'),
            fg='#ffffff',
            bg='#3b82f6',
            activebackground='#2563eb',
            activeforeground='#ffffff',
            relief='flat',
            borderwidth=0,
            padx=20,
            pady=12,
            cursor='hand2',
            command=self.load_data
        )
        self.load_button.pack(fill=tk.X, pady=(0, 15))
        
        # Add hover effects to the button
        self.add_button_hover_effects(self.load_button, '#2563eb', '#3b82f6')
        
        # File status area
        status_frame = tk.Frame(parent, bg='#f8fafc')
        status_frame.pack(fill=tk.X)
        
        # Status icon and text
        self.status_icon = tk.Label(
            status_frame,
            text="○",
            font=('Helvetica', 12),
            fg='#6b7280',
            bg='#f8fafc'
        )
        self.status_icon.pack(side=tk.LEFT)
        
        self.file_label = tk.Label(
            status_frame,
            text="No data loaded",
            font=('Helvetica', 10),
            fg='#6b7280',
            bg='#f8fafc'
        )
        self.file_label.pack(side=tk.LEFT, padx=(8, 0))
    
    def add_button_hover_effects(self, button, hover_color, normal_color):
        """Add hover effects to a button."""
        def on_enter(e):
            button.config(bg=hover_color)
        
        def on_leave(e):
            button.config(bg=normal_color)
        
        button.bind("<Enter>", on_enter)
        button.bind("<Leave>", on_leave)
    
    def create_summary_section(self, parent):
        """Create the data summary section with modern cards."""
        # Section header
        header_label = tk.Label(
            parent,
            text="Data Summary",
            font=('Helvetica', 14, 'bold'),
            fg='#1e293b',
            bg='#f8fafc'
        )
        header_label.pack(anchor=tk.W, pady=(0, 15))
        
        # Container for summary content
        self.summary_container = tk.Frame(parent, bg='#f8fafc')
        self.summary_container.pack(fill=tk.X)
        
        # Default message
        self.no_data_label = tk.Label(
            self.summary_container,
            text="Load data to see summary",
            font=('Helvetica', 10),
            fg='#9ca3af',
            bg='#f8fafc'
        )
        self.no_data_label.pack(anchor=tk.W)
    
    def create_viz_section(self, parent):
        """Create visualization controls with modern buttons."""
        # Section header
        header_label = tk.Label(
            parent,
            text="Visualizations",
            font=('Helvetica', 14, 'bold'),
            fg='#1e293b',
            bg='#f8fafc'
        )
        header_label.pack(anchor=tk.W, pady=(0, 15))
        
        # Container for viz controls
        self.viz_container = tk.Frame(parent, bg='#f8fafc')
        self.viz_container.pack(fill=tk.X)
        
        # Default message
        self.no_viz_label = tk.Label(
            self.viz_container,
            text="Load data to enable visualizations",
            font=('Helvetica', 10),
            fg='#9ca3af',
            bg='#f8fafc'
        )
        self.no_viz_label.pack(anchor=tk.W)
    
    def load_data(self):
        """Load and parse the JSON experiment results file with custom dialog."""
        # Use a more modern file dialog approach
        file_path = filedialog.askopenfilename(
            title="Select Experiment Results File",
            filetypes=[
                ("JSON files", "*.json"),
                ("All files", "*.*")
            ],
            initialdir=os.path.expanduser("~")
        )
        
        if not file_path:
            return
            
        try:
            # Update button to show loading state
            self.load_button.config(
                text="Loading...",
                bg='#6b7280',
                state='disabled'
            )
            self.root.update()
            
            # Load the file
            with open(file_path, 'r') as f:
                self.data = json.load(f)
            
            # Update status
            filename = os.path.basename(file_path)
            self.status_icon.config(text="●", fg='#10b981')
            self.file_label.config(
                text=f"Loaded: {filename}",
                fg='#059669'
            )
            
            # Process the data
            self.process_data()
            
            # Update UI sections
            self.update_modern_summary()
            
            # Generate initial visualizations
            self.create_modern_overview()
            
            # Reset button
            self.load_button.config(
                text="Load Experiment Data",
                bg='#3b82f6',
                state='normal'
            )
            
            # Success notification
            try:
                messagebox.showinfo("Success", f"Successfully loaded {filename}")
            except tk.TclError:
                # Window might be destroyed, ignore messagebox error
                pass
            
        except Exception as e:
            # Reset button on error
            self.load_button.config(
                text="Load Experiment Data",
                bg='#3b82f6',
                state='normal'
            )
            
            # Update status to show error
            self.status_icon.config(text="●", fg='#ef4444')
            self.file_label.config(
                text="Error loading file",
                fg='#dc2626'
            )
            
            try:
                messagebox.showerror("Error", f"Failed to load data:\n{str(e)}")
            except tk.TclError:
                # Window might be destroyed, ignore messagebox error
                pass
    
    def update_modern_summary(self):
        """Update the summary section with modern info cards."""
        # Clear existing content
        for widget in self.summary_container.winfo_children():
            widget.destroy()
        
        if not self.data:
            return
        
        # Create summary cards
        num_configs = len(self.data['ranking'])
        num_metrics = len(self.data['metricConfigs'])
        
        # Configurations card with icon
        self.create_modern_summary_card(
            self.summary_container,
            "⚙️",
            str(num_configs),
            "Configurations",
            "#3b82f6"
        )
        
        # Metrics card with icon
        self.create_modern_summary_card(
            self.summary_container,
            "📊",
            str(num_metrics),
            "Metrics Tracked",
            "#10b981"
        )
        
        # Metrics list with modern styling
        if num_metrics > 0:
            self.create_metrics_list()
    
    def create_modern_summary_card(self, parent, icon, value, title, color):
        """Create a modern summary card with icon."""
        # Card container with subtle shadow effect
        card_frame = tk.Frame(
            parent,
            bg='#ffffff',
            relief='solid',
            borderwidth=1,
            highlightbackground='#e5e7eb',
            highlightthickness=1
        )
        card_frame.pack(fill=tk.X, pady=(0, 12))
        
        # Card content with padding
        content_frame = tk.Frame(card_frame, bg='#ffffff')
        content_frame.pack(fill=tk.X, padx=16, pady=14)
        
        # Header with icon
        header_frame = tk.Frame(content_frame, bg='#ffffff')
        header_frame.pack(fill=tk.X, pady=(0, 8))
        
        # Icon
        icon_label = tk.Label(
            header_frame,
            text=icon,
            font=('Helvetica', 18),
            bg='#ffffff',
            fg=color
        )
        icon_label.pack(side=tk.LEFT)
        
        # Value (large number)
        value_label = tk.Label(
            content_frame,
            text=value,
            font=('Helvetica', 24, 'bold'),
            fg=color,
            bg='#ffffff'
        )
        value_label.pack(anchor=tk.W, pady=(0, 4))
        
        # Title (smaller text)
        title_label = tk.Label(
            content_frame,
            text=title,
            font=('Helvetica', 11),
            fg='#6b7280',
            bg='#ffffff'
        )
        title_label.pack(anchor=tk.W)
    
    def create_metrics_list(self):
        """Create a modern metrics list."""
        # Spacing
        tk.Frame(self.summary_container, bg='#f8fafc', height=20).pack(fill=tk.X)
        
        # Section header
        header_frame = tk.Frame(self.summary_container, bg='#f8fafc')
        header_frame.pack(fill=tk.X, pady=(0, 12))
        
        # Header with icon
        header_icon = tk.Label(
            header_frame,
            text="📈",
            font=('Helvetica', 14),
            fg='#374151',
            bg='#f8fafc'
        )
        header_icon.pack(side=tk.LEFT)
        
        metrics_label = tk.Label(
            header_frame,
            text="Tracked Metrics",
            font=('Helvetica', 12, 'bold'),
            fg='#374151',
            bg='#f8fafc'
        )
        metrics_label.pack(side=tk.LEFT, padx=(8, 0))
        
        # Metrics container with background
        metrics_container = tk.Frame(
            self.summary_container,
            bg='#ffffff',
            relief='solid',
            borderwidth=1,
            highlightbackground='#e5e7eb',
            highlightthickness=1
        )
        metrics_container.pack(fill=tk.X)
        
        # Metrics list
        for i, metric in enumerate(self.data['metricConfigs']):
            direction_color = "#10b981" if metric['direction'] == 'higher' else "#ef4444"
            arrow = "↗️" if metric['direction'] == 'higher' else "↘️"
            
            # Alternating background for better readability
            bg_color = "#f9fafb" if i % 2 == 0 else "#ffffff"
            
            metric_frame = tk.Frame(metrics_container, bg=bg_color)
            metric_frame.pack(fill=tk.X, padx=1, pady=1)
            
            # Content frame with padding
            content_frame = tk.Frame(metric_frame, bg=bg_color)
            content_frame.pack(fill=tk.X, padx=12, pady=8)
            
            # Arrow indicator
            arrow_label = tk.Label(
                content_frame,
                text=arrow,
                font=('Helvetica', 10),
                bg=bg_color
            )
            arrow_label.pack(side=tk.LEFT)
            
            # Metric name
            metric_label = tk.Label(
                content_frame,
                text=metric['metricName'],
                font=('Helvetica', 10),
                fg='#374151',
                bg=bg_color
            )
            metric_label.pack(side=tk.LEFT, padx=(8, 0))
            
            # Direction indicator
            direction_label = tk.Label(
                content_frame,
                text=f"({metric['direction']})",
                font=('Helvetica', 9),
                fg=direction_color,
                bg=bg_color
            )
            direction_label.pack(side=tk.RIGHT)
    
    def create_info_card(self, parent, title, value, color):
        """Create a modern info card."""
        # Card container
        card_frame = tk.Frame(
            parent,
            bg='#ffffff',
            relief='solid',
            borderwidth=1,
            highlightbackground='#e5e7eb',
            highlightthickness=1
        )
        card_frame.pack(fill=tk.X, pady=(0, 10))
        
        # Card content with padding
        content_frame = tk.Frame(card_frame, bg='#ffffff')
        content_frame.pack(fill=tk.X, padx=15, pady=12)
        
        # Value (large number)
        value_label = tk.Label(
            content_frame,
            text=value,
            font=('Helvetica', 20, 'bold'),
            fg=color,
            bg='#ffffff'
        )
        value_label.pack(anchor=tk.W)
        
        # Title (smaller text)
        title_label = tk.Label(
            content_frame,
            text=title,
            font=('Helvetica', 10),
            fg='#6b7280',
            bg='#ffffff'
        )
        title_label.pack(anchor=tk.W)
    
    def create_modern_viz_controls(self):
        """Create modern visualization control buttons."""
        # Clear existing content
        for widget in self.viz_container.winfo_children():
            widget.destroy()
        
        # Visualization buttons
        viz_buttons = [
            ("Rankings Analysis", self.show_modern_rankings),
            ("Metric Trends", self.show_modern_metrics),
            ("Configuration Details", self.show_modern_configs),
            ("Service Comparison", self.show_modern_comparison),
        ]
        
        for text, command in viz_buttons:
            button = tk.Button(
                self.viz_container,
                text=text,
                font=('Helvetica', 10),
                fg='#374151',
                bg='#ffffff',
                activebackground='#f3f4f6',
                activeforeground='#1f2937',
                relief='solid',
                borderwidth=1,
                padx=15,
                pady=8,
                cursor='hand2',
                command=command
            )
            button.pack(fill=tk.X, pady=(0, 8))
            
            # Add hover effects
            self.add_button_hover_effects(button, '#f3f4f6', '#ffffff')
        
    def create_analysis_section(self, parent):
        """Create analysis controls with modern buttons."""
        self.analysis_frame = ttk.LabelFrame(parent, text="  Analysis Tools  ", 
                                            style='Modern.TFrame', padding="15")
        self.analysis_frame.pack(fill=tk.X)
        
    def create_content_area(self, parent):
        """Create the main content area with modern tabs."""
        content_frame = ttk.Frame(parent, style='Modern.TFrame')
        
        # Create modern notebook
        self.notebook = ttk.Notebook(content_frame, style='Modern.TNotebook')
        self.notebook.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)
        
        # Create tabs with proper spacing
        self.create_modern_tabs()
        
        parent.add(content_frame, weight=1)
        
    def create_modern_tabs(self):
        """Create tabs with modern styling and proper spacing."""
        tab_configs = [
            ("📊 Overview", "overview"),
            ("🏆 Rankings", "rankings"), 
            ("⚙️ Configurations", "configs"),
            ("📈 Metrics", "metrics"),
            ("🔍 Comparison", "comparison")
        ]
        
        self.tabs = {}
        for title, key in tab_configs:
            frame = ttk.Frame(self.notebook, style='Modern.TFrame', padding="20")
            self.notebook.add(frame, text=title)
            self.tabs[key] = frame
    
    def process_data(self):
        """Process the loaded JSON data into pandas DataFrames."""
        if not self.data:
            return
        
        # Same data processing logic as before
        rankings_data = []
        configs_data = []
        metrics_data = []
        
        for rank_entry in self.data['ranking']:
            position = rank_entry['position']
            
            # System-level metrics
            for metric in rank_entry['systemResults']:
                rankings_data.append({
                    'position': position,
                    'metric_name': metric['metricName'],
                    'value': metric['value'],
                    'unit': metric['unit'],
                    'level': 'system'
                })
            
            # Configuration details
            for service in rank_entry['systemConfig']:
                service_name = service['serviceName']
                for class_config in service['classConfigs']:
                    class_name = class_config['className']
                    for behavior in class_config['behaviours']:
                        configs_data.append({
                            'position': position,
                            'service_name': service_name,
                            'class_name': class_name,
                            'method_name': behavior['methodName'],
                            'behavior_id': behavior['behaviourId']
                        })
            
            # Service-level metrics
            for service_result in rank_entry['serviceResults']:
                service_name = service_result['serviceName']
                for metric in service_result['results']:
                    metrics_data.append({
                        'position': position,
                        'service_name': service_name,
                        'metric_name': metric['metricName'],
                        'value': metric['value'],
                        'unit': metric['unit'],
                        'level': 'service'
                    })
        
        self.df_rankings = pd.DataFrame(rankings_data)
        self.df_configs = pd.DataFrame(configs_data)
        self.df_metrics = pd.DataFrame(metrics_data)
    

    
    def create_modern_viz_controls(self):
        """Create modern visualization control buttons."""
        for widget in self.viz_container.winfo_children():
            widget.destroy()
        
        buttons = [
            ("📊 Rankings Analysis", self.show_modern_rankings),
            ("📈 Metric Trends", self.show_modern_metrics),
            ("⚙️ Configuration Details", self.show_modern_configs),
            ("🔍 Service Comparison", self.show_modern_comparison),
            ("🎯 Behavior Analysis", self.show_behavior_analysis)
        ]
        
        for text, command in buttons:
            btn = ttk.Button(self.viz_container, text=text, command=command, 
                           style='Modern.TButton')
            btn.pack(fill=tk.X, pady=(0, 8))
    
    def create_modern_overview(self):
        """Create the overview with proper spacing and modern styling."""
        self.clear_tab('overview')
        
        if not self.data:
            self.show_no_data_message(self.tabs['overview'])
            return
        
        # Create scrollable frame
        canvas = tk.Canvas(self.tabs['overview'], bg='white')
        scrollbar = ttk.Scrollbar(self.tabs['overview'], orient="vertical", command=canvas.yview)
        scrollable_frame = ttk.Frame(canvas, style='Modern.TFrame')
        
        scrollable_frame.bind(
            "<Configure>",
            lambda e: canvas.configure(scrollregion=canvas.bbox("all"))
        )
        
        canvas.create_window((0, 0), window=scrollable_frame, anchor="nw")
        canvas.configure(yscrollcommand=scrollbar.set)
        
        # Create figure with single large chart
        fig = Figure(figsize=(14, 8), dpi=80, facecolor='white')
        # No figure title since the tab already has one
        
        # Single subplot for parallel coordinates
        ax = fig.add_subplot(111)
        ax.margins(0.02, 0.02)  # Small margins for better space usage
        
        # Add info panel above the chart
        self.create_overview_info_panel(scrollable_frame)
        
        # Create parallel coordinates plot
        self.create_parallel_coordinates_chart(ax)
        
        # Embed in frame with proper sizing
        chart_frame = ttk.Frame(scrollable_frame, style='Modern.TFrame')
        chart_frame.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)
        
        canvas_widget = FigureCanvasTkAgg(fig, chart_frame)
        canvas_widget.draw()
        
        # Get the widget and configure it to fit properly
        chart_widget = canvas_widget.get_tk_widget()
        chart_widget.pack(fill=tk.BOTH, expand=True)
        
        # Make the canvas widget responsive
        chart_widget.configure(highlightthickness=0)
        
        # Add navigation toolbar
        toolbar = NavigationToolbar2Tk(canvas_widget, chart_frame)
        toolbar.update()
        
        # Pack scrollable components
        canvas.pack(side="left", fill="both", expand=True)
        scrollbar.pack(side="right", fill="y")
    
    def create_overview_info_panel(self, parent_frame):
        """Create a modern information panel above the overview chart."""
        if not self.data:
            return
            
        # Count actual configurations from ranking data
        n_configs = len(self.data['ranking'])
        
        # Count metrics (system metrics only)
        n_metrics = len(self.data['ranking'][0]['systemResults']) if self.data['ranking'] else 0
        
        # Create modern info panel with gradient background
        info_frame = ttk.Frame(parent_frame, style='Modern.TFrame')
        info_frame.pack(fill=tk.X, padx=15, pady=(15, 10))
        
        # Create main info container with modern styling
        info_container = tk.Frame(info_frame, bg='#f8fafc', relief='flat', bd=0)
        info_container.pack(fill=tk.X, pady=10)
        
        # Add subtle border and shadow effect
        border_frame = tk.Frame(info_container, bg='#e2e8f0', height=1)
        border_frame.pack(fill=tk.X, side=tk.BOTTOM)
        
        # Create cards container with modern layout
        cards_container = tk.Frame(info_container, bg='#f8fafc')
        cards_container.pack(fill=tk.X, padx=20, pady=15)
        
        # Configuration count card - Modern glass morphism style
        config_card = self.create_modern_info_card(
            cards_container, 
            icon="📊", 
            title="System Configurations", 
            value=f"{n_configs}",
            subtitle="Test scenarios analyzed",
            color="#3b82f6",
            bg_color="#eff6ff"
        )
        config_card.pack(side=tk.LEFT, padx=(0, 15), fill=tk.Y)
        
        # Metrics info card
        metrics_card = self.create_modern_info_card(
            cards_container,
            icon="📏",
            title="Performance Metrics", 
            value=f"{n_metrics}",
            subtitle="System measurements",
            color="#10b981",
            bg_color="#ecfdf5"
        )
        metrics_card.pack(side=tk.LEFT, padx=(0, 15), fill=tk.Y)
        
        # Processing info card
        processing_card = self.create_modern_info_card(
            cards_container,
            icon="⚡",
            title="Data Processing", 
            value="Normalized",
            subtitle="Ready for comparison",
            color="#f59e0b",
            bg_color="#fffbeb"
        )
        processing_card.pack(side=tk.LEFT, padx=(0, 15), fill=tk.Y)
        
        # Performance direction card
        direction_card = self.create_modern_info_card(
            cards_container,
            icon="📈",
            title="Chart Reading", 
            value="Higher = Better",
            subtitle="Normalized for optimization goals",
            color="#ef4444",
            bg_color="#fef2f2"
        )
        direction_card.pack(side=tk.LEFT, fill=tk.Y)
    
    def create_modern_info_card(self, parent, icon, title, value, subtitle, color, bg_color):
        """Create a modern glassmorphism-style info card."""
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
    
    def create_rankings_chart(self, ax):
        """Create a modern rankings comparison chart."""
        rankings_pivot = self.df_rankings.pivot(index='position', columns='metric_name', values='value')
        
        # Use modern color palette
        colors = ['#3b82f6', '#ef4444', '#10b981', '#f59e0b']
        rankings_pivot.plot(kind='bar', ax=ax, color=colors[:len(rankings_pivot.columns)], 
                           alpha=0.8, width=0.7)
        
        ax.set_title('System Metrics by Configuration Ranking', fontsize=11, fontweight='bold', pad=15)
        ax.set_xlabel('Configuration Position', fontsize=9)
        ax.set_ylabel('Metric Values', fontsize=9)
        ax.legend(bbox_to_anchor=(1.02, 1), loc='upper left', frameon=True, fancybox=True, shadow=True, fontsize=8)
        ax.grid(True, alpha=0.3)
        ax.tick_params(axis='x', rotation=0, labelsize=8)
        ax.tick_params(axis='y', labelsize=8)
    
    def create_trends_chart(self, ax):
        """Create a modern trends chart."""
        for i, metric in enumerate(self.df_rankings['metric_name'].unique()):
            metric_data = self.df_rankings[self.df_rankings['metric_name'] == metric]
            colors = ['#3b82f6', '#ef4444', '#10b981', '#f59e0b']
            ax.plot(metric_data['position'], metric_data['value'], 
                   marker='o', linewidth=3, markersize=8, 
                   color=colors[i % len(colors)], label=metric, alpha=0.8)
        
        ax.set_title('Metric Trends Across Configurations', fontsize=11, fontweight='bold', pad=15)
        ax.set_xlabel('Configuration Position', fontsize=9)
        ax.set_ylabel('Metric Value', fontsize=9)
        ax.legend(frameon=True, fancybox=True, shadow=True, fontsize=8)
        ax.grid(True, alpha=0.3)
        ax.tick_params(axis='both', labelsize=8)
    
    def create_heatmap_chart(self, ax):
        """Create a modern service performance heatmap."""
        service_metrics = self.df_metrics.pivot_table(
            index='service_name', columns='position', values='value', aggfunc='mean')
        
        sns.heatmap(service_metrics, ax=ax, cmap='viridis', annot=True, fmt='.0f',
                   cbar_kws={'label': 'Performance Value'}, square=False, annot_kws={'size': 8})
        ax.set_title('Service Performance Heatmap', fontsize=11, fontweight='bold', pad=15)
        ax.set_xlabel('Configuration Position', fontsize=9)
        ax.set_ylabel('Service', fontsize=9)
        ax.tick_params(axis='both', labelsize=8)
    
    def create_behavior_chart(self, ax):
        """Create a modern behavior distribution chart."""
        behavior_counts = self.df_configs['behavior_id'].value_counts()
        
        # Modern color palette for pie chart
        colors = plt.cm.Set3(np.linspace(0, 1, len(behavior_counts)))
        
        wedges, texts, autotexts = ax.pie(behavior_counts.values, labels=behavior_counts.index, 
                                         autopct='%1.1f%%', colors=colors, startangle=90, textprops={'fontsize': 8})
        
        ax.set_title('Behavior Distribution Across Configurations', fontsize=11, fontweight='bold', pad=15)
        
        # Improve text appearance
        for autotext in autotexts:
            autotext.set_color('white')
            autotext.set_fontweight('bold')
    
    def create_parallel_coordinates_chart(self, ax):
        """Create a modern parallel coordinates plot showing all system configurations."""
        # Set modern style
        plt.style.use('seaborn-v0_8-whitegrid')
        
        # Prepare data for parallel coordinates - use ALL configurations from ranking
        parallel_data = []
        
        # Process ALL ranking entries (not just processed ones)
        for rank_entry in self.data['ranking']:
            row = {'Configuration': f"Config #{rank_entry['position']}"}
            
            # Add ONLY system metrics (remove service/behavior counts)
            for metric in rank_entry['systemResults']:
                row[metric['metricName']] = metric['value']
            
            parallel_data.append(row)
        
        # Convert to DataFrame
        parallel_df = pd.DataFrame(parallel_data)
        
        # Sort metrics according to the order specified in metricConfigs
        metric_order = {}
        for metric_config in self.data['metricConfigs']:
            metric_order[metric_config['metricName']] = metric_config['order']
        
        # Get numeric columns and sort them by their defined order
        all_numeric_cols = [col for col in parallel_df.columns if col != 'Configuration']
        numeric_cols = sorted(all_numeric_cols, key=lambda x: metric_order.get(x, 999))
        
        # Enhanced normalization that highlights differences
        normalized_df = parallel_df.copy()
        
        print(f"Processing {len(parallel_df)} configurations with metrics in order: {numeric_cols}")
        print(f"Metric order mapping: {metric_order}")
        
        # Create direction mapping from metricConfigs
        metric_directions = {}
        for metric_config in self.data['metricConfigs']:
            metric_directions[metric_config['metricName']] = metric_config['direction']
        
        print(f"Metric directions: {metric_directions}")
        
        for col in numeric_cols:
            values = parallel_df[col].values
            min_val = values.min()
            max_val = values.max()
            range_val = max_val - min_val
            direction = metric_directions.get(col, 'higher')  # Default to 'higher' if not found
            
            print(f"Metric '{col}': min={min_val:.6f}, max={max_val:.6f}, range={range_val:.6f}, direction={direction}")
            
            if range_val > 0:  # Only normalize if there's variation
                # Enhanced normalization: center around mean and scale by range
                mean_val = values.mean()
                
                # If the range is very small relative to the mean, use percentage-based scaling
                if range_val / abs(mean_val) < 0.01 if mean_val != 0 else range_val < 0.001:
                    # For very small differences, use percentage deviation from mean
                    normalized_values = (values - mean_val) / range_val
                    # Scale to 0-1 range centered around 0.5
                    if direction == 'lower':
                        # For "lower is better" metrics, invert the normalization
                        normalized_values = 0.5 - (normalized_values * 0.4)  # Invert: lower values get higher positions
                    else:
                        # For "higher is better" metrics, keep normal direction
                        normalized_values = 0.5 + (normalized_values * 0.4)  # Use 80% of the range (0.1 to 0.9)
                else:
                    # Standard min-max normalization for larger differences
                    if direction == 'lower':
                        # For "lower is better" metrics, invert normalization so lower values appear higher
                        normalized_values = 1 - ((values - min_val) / range_val)
                    else:
                        # For "higher is better" metrics, normal min-max normalization
                        normalized_values = (values - min_val) / range_val
                
                normalized_df[col] = normalized_values
            else:
                # All values are the same
                normalized_df[col] = 0.5
        
        # Modern color palette - gradient from best to worst
        n_configs = len(parallel_df)
        
        # Create sophisticated color gradients
        top_colors = plt.cm.plasma(np.linspace(0.1, 0.4, min(3, n_configs)))  # Vibrant colors for top
        mid_colors = plt.cm.viridis(np.linspace(0.3, 0.7, max(0, min(7, n_configs-3))))  # Cool colors for middle
        bottom_colors = plt.cm.cividis(np.linspace(0.5, 0.9, max(0, n_configs-10)))  # Muted colors for bottom
        
        all_colors = np.vstack([top_colors, mid_colors, bottom_colors]) if n_configs > 10 else np.vstack([top_colors, mid_colors])
        
        # Modern background styling
        ax.set_facecolor('#fafafa')  # Very light gray background
        ax.figure.patch.set_facecolor('#ffffff')  # White figure background
        
        # Remove top and right spines for clean look
        ax.spines['top'].set_visible(False)
        ax.spines['right'].set_visible(False)
        ax.spines['left'].set_color('#e0e0e0')
        ax.spines['bottom'].set_color('#e0e0e0')
        
        # Plot all configurations with modern styling
        for i, (_, row) in enumerate(normalized_df.iterrows()):
            values = [row[col] for col in numeric_cols]
            x_positions = range(len(numeric_cols))
            
            # Extract rank number for styling
            rank = int(row['Configuration'].split('#')[1])
            
            # Modern gradient-based styling
            color = all_colors[i] if i < len(all_colors) else all_colors[-1]
            
            if rank <= 3:  # Top 3 configurations - premium styling
                ax.plot(x_positions, values, 'o-', color=color, linewidth=3.5, 
                       markersize=8, alpha=0.95, label=f"Config #{rank}", zorder=10,
                       markeredgewidth=2, markeredgecolor='white',
                       solid_capstyle='round', solid_joinstyle='round')
            elif rank <= 10:  # Top 10 - elevated styling  
                ax.plot(x_positions, values, 'o-', color=color, linewidth=2.5, 
                       markersize=6, alpha=0.8, label=f"Config #{rank}", zorder=5,
                       markeredgewidth=1, markeredgecolor='white',
                       solid_capstyle='round')
            else:  # Remaining configs - subtle styling
                ax.plot(x_positions, values, 'o-', color=color, linewidth=1.8, 
                       markersize=4, alpha=0.6, label=f"Config #{rank}", zorder=1,
                       solid_capstyle='round')
        
        # Modern axis styling
        ax.set_xticks(range(len(numeric_cols)))
        
        # Clean, modern labels with better typography and direction indicators
        clean_labels = []
        for col in numeric_cols:
            direction = metric_directions.get(col, 'higher')
            direction_symbol = "↑" if direction == 'higher' else "↓"
            
            # Shorten long metric names for better display
            if len(col) > 15:
                words = col.split()
                if len(words) > 2:
                    clean_labels.append(f"{words[0]} {direction_symbol}\n{words[1]}")
                else:
                    short_name = col[:12] + "..." if len(col) > 12 else col
                    clean_labels.append(f"{short_name} {direction_symbol}")
            else:
                clean_labels.append(f"{col} {direction_symbol}")
        
        ax.set_xticklabels(clean_labels, fontsize=11, fontweight='500', 
                          color='#2c3e50', rotation=0, ha='center')
        
        # Modern labels
        ax.set_ylabel('Performance Score', fontsize=14, fontweight='600', 
                     color='#2c3e50', labelpad=15)
        
        # No chart title needed - tab already has title
        
        # Modern grid styling - subtle and clean
        ax.grid(True, alpha=0.3, axis='y', linestyle='-', linewidth=0.8, color='#e0e0e0')
        ax.grid(True, alpha=0.15, axis='x', linestyle=':', linewidth=0.5, color='#e0e0e0')
        ax.set_ylim(-0.02, 1.02)
        
        # Modern reference lines with gradient effect
        ax.axhline(y=0.5, color='#95a5a6', linestyle='-', alpha=0.4, linewidth=1.5)
        ax.axhline(y=0.75, color='#27ae60', linestyle=':', alpha=0.3, linewidth=1)
        ax.axhline(y=0.25, color='#e74c3c', linestyle=':', alpha=0.3, linewidth=1)
        
        # Modern legend showing all configurations
        handles, labels = ax.get_legend_handles_labels()
        if handles:
            # Create clean legend entries with medals for top performers
            legend_labels = []
            for label in labels:
                rank = int(label.split('#')[1])
                legend_labels.append(label)
            
            # Show all configurations, use columns for space efficiency
            legend = ax.legend(handles, legend_labels,
                             loc='upper right', fontsize=10, 
                             title=f'All {n_configs} Configurations', 
                             title_fontsize=11,
                             frameon=True, fancybox=True, shadow=False,
                             facecolor='white', edgecolor='#e0e0e0',
                             framealpha=0.95, borderpad=1, 
                             ncol=2 if n_configs > 5 else 1)
            legend.get_title().set_fontweight('600')
            legend.get_title().set_color('#2c3e50')
        
        # Add subtle performance zones with colored backgrounds
        # Excellence zone (top 25%)
        ax.axhspan(0.75, 1.02, alpha=0.08, color='#27ae60', zorder=0)
        ax.text(len(numeric_cols)-0.5, 0.88, 'Excellence\nZone', 
               ha='center', va='center', fontsize=9, fontweight='600',
               color='#27ae60', alpha=0.7)
        
        # Warning zone (bottom 25%)  
        ax.axhspan(-0.02, 0.25, alpha=0.08, color='#e74c3c', zorder=0)
        ax.text(len(numeric_cols)-0.5, 0.12, 'Needs\nImprovement', 
               ha='center', va='center', fontsize=9, fontweight='600',
               color='#e74c3c', alpha=0.7)
        
        # Style tick parameters for modern look
        ax.tick_params(axis='y', labelsize=10, colors='#7f8c8d', 
                      width=0, length=0)  # Remove tick marks
        ax.tick_params(axis='x', labelsize=11, colors='#2c3e50',
                      width=0, length=0, pad=10)  # Remove tick marks, add padding
    
    def show_modern_rankings(self):
        """Display modern rankings analysis."""
        self.notebook.select(self.tabs['rankings'])
        self.clear_tab('rankings')
        
        if not self.data:
            self.show_no_data_message(self.tabs['rankings'])
            return
        
        # Create figure with better spacing
        fig = Figure(figsize=(16, 10), dpi=100, facecolor='white')
        fig.suptitle('Detailed Rankings Analysis', fontsize=18, fontweight='bold', y=0.96)
        
        gs = fig.add_gridspec(2, 1, height_ratios=[1, 1], hspace=0.4,
                             left=0.08, right=0.95, top=0.88, bottom=0.1)
        
        # Top configurations detailed comparison
        ax1 = fig.add_subplot(gs[0])
        self.create_detailed_rankings_chart(ax1)
        
        # Correlation analysis
        ax2 = fig.add_subplot(gs[1])
        self.create_correlation_chart(ax2)
        
        # Embed chart
        self.embed_chart(fig, self.tabs['rankings'])
    
    def create_detailed_rankings_chart(self, ax):
        """Create detailed rankings comparison."""
        top_5 = self.df_rankings[self.df_rankings['position'] <= 5]
        pivot_data = top_5.pivot(index='position', columns='metric_name', values='value')
        
        x = np.arange(len(pivot_data.index))
        width = 0.35
        colors = ['#3b82f6', '#ef4444', '#10b981', '#f59e0b']
        
        metrics = pivot_data.columns
        for i, metric in enumerate(metrics):
            offset = (i - len(metrics)/2) * width / len(metrics)
            ax.bar(x + offset, pivot_data[metric], width/len(metrics), 
                  label=metric, alpha=0.8, color=colors[i % len(colors)])
        
        ax.set_title('Top 5 Configurations - Detailed Comparison', fontsize=14, fontweight='bold', pad=20)
        ax.set_xlabel('Configuration Rank', fontsize=12)
        ax.set_ylabel('Metric Values', fontsize=12)
        ax.set_xticks(x)
        ax.set_xticklabels([f'Rank {i}' for i in pivot_data.index])
        ax.legend(frameon=True, fancybox=True, shadow=True)
        ax.grid(True, alpha=0.3)
    
    def create_correlation_chart(self, ax):
        """Create correlation matrix chart."""
        corr_data = self.df_rankings.pivot(index='position', columns='metric_name', values='value')
        correlation_matrix = corr_data.corr()
        
        sns.heatmap(correlation_matrix, ax=ax, annot=True, cmap='RdBu_r', center=0,
                   square=True, cbar_kws={'label': 'Correlation Coefficient'})
        ax.set_title('Metric Correlation Matrix', fontsize=14, fontweight='bold', pad=20)
    
    def show_modern_metrics(self):
        """Display modern metrics analysis."""
        self.notebook.select(self.tabs['metrics'])
        self.clear_tab('metrics')
        
        if not self.data:
            self.show_no_data_message(self.tabs['metrics'])
            return
        
        # Create figure with proper spacing
        fig = Figure(figsize=(16, 12), dpi=100, facecolor='white')
        fig.suptitle('Comprehensive Metrics Analysis', fontsize=18, fontweight='bold', y=0.96)
        
        unique_metrics = self.df_rankings['metric_name'].unique()
        n_metrics = len(unique_metrics)
        
        # Dynamic subplot arrangement
        rows = (n_metrics + 1) // 2
        gs = fig.add_gridspec(rows, 2, hspace=0.4, wspace=0.3,
                             left=0.08, right=0.95, top=0.88, bottom=0.08)
        
        for i, metric in enumerate(unique_metrics):
            row, col = i // 2, i % 2
            ax = fig.add_subplot(gs[row, col])
            self.create_metric_trend_chart(ax, metric)
        
        self.embed_chart(fig, self.tabs['metrics'])
    
    def create_metric_trend_chart(self, ax, metric):
        """Create individual metric trend chart."""
        # System level trend
        system_data = self.df_rankings[self.df_rankings['metric_name'] == metric]
        ax.plot(system_data['position'], system_data['value'], 
               'o-', linewidth=3, markersize=10, color='#3b82f6', 
               label='System Level', alpha=0.9)
        
        # Service level trends
        service_data = self.df_metrics[self.df_metrics['metric_name'] == metric]
        colors = ['#ef4444', '#10b981', '#f59e0b', '#8b5cf6']
        
        for i, service in enumerate(service_data['service_name'].unique()):
            service_subset = service_data[service_data['service_name'] == service]
            color = colors[i % len(colors)]
            ax.plot(service_subset['position'], service_subset['value'], 
                   'o--', alpha=0.7, color=color, linewidth=2, markersize=6,
                   label=service.replace('-service', ''))
        
        ax.set_title(f'{metric} Performance Analysis', fontsize=12, fontweight='bold', pad=15)
        ax.set_xlabel('Configuration Rank', fontsize=10)
        ax.set_ylabel(f'{metric} Value', fontsize=10)
        ax.legend(fontsize=9, frameon=True, fancybox=True)
        ax.grid(True, alpha=0.3)
    
    def show_modern_configs(self):
        """Display modern configuration analysis."""
        self.notebook.select(self.tabs['configs'])
        self.clear_tab('configs')
        
        if not self.data:
            self.show_no_data_message(self.tabs['configs'])
            return
        
        # Create modern configuration viewer
        self.create_config_viewer(self.tabs['configs'])
    
    def create_config_viewer(self, parent):
        """Create a modern configuration viewer with cards."""
        # Main scrollable frame
        canvas = tk.Canvas(parent, bg='white')
        scrollbar = ttk.Scrollbar(parent, orient="vertical", command=canvas.yview)
        scrollable_frame = ttk.Frame(canvas, style='Modern.TFrame')
        
        scrollable_frame.bind(
            "<Configure>",
            lambda e: canvas.configure(scrollregion=canvas.bbox("all"))
        )
        
        canvas.create_window((0, 0), window=scrollable_frame, anchor="nw")
        canvas.configure(yscrollcommand=scrollbar.set)
        
        # Create configuration cards
        for i, rank_entry in enumerate(self.data['ranking']):
            self.create_config_card(scrollable_frame, rank_entry, i)
        
        canvas.pack(side="left", fill="both", expand=True)
        scrollbar.pack(side="right", fill="y")
    
    def create_config_card(self, parent, rank_entry, index):
        """Create a modern configuration card."""
        position = rank_entry['position']
        
        # Card frame with modern styling
        card_frame = ttk.Frame(parent, style='Modern.TFrame', relief='solid', borderwidth=1)
        card_frame.pack(fill=tk.X, padx=20, pady=10)
        
        # Card content with padding
        content_frame = ttk.Frame(card_frame, style='Modern.TFrame', padding="20")
        content_frame.pack(fill=tk.X)
        
        # Header with rank info
        header_frame = ttk.Frame(content_frame, style='Modern.TFrame')
        header_frame.pack(fill=tk.X, pady=(0, 15))
        
        rank_label = ttk.Label(header_frame, text=f"Configuration Rank #{position}", 
                              font=('Segoe UI', 16, 'bold'))
        rank_label.pack(side=tk.LEFT)
        
        # System metrics summary
        metrics_frame = ttk.LabelFrame(content_frame, text="System Performance", 
                                      style='Modern.TFrame', padding="10")
        metrics_frame.pack(fill=tk.X, pady=(0, 15))
        
        for metric in rank_entry['systemResults']:
            metric_text = f"{metric['metricName']}: {metric['value']:.2f} {metric['unit']}"
            ttk.Label(metrics_frame, text=metric_text, style='Info.TLabel').pack(anchor=tk.W)
        
        # Configuration details
        config_frame = ttk.LabelFrame(content_frame, text="Behavior Configuration", 
                                     style='Modern.TFrame', padding="10")
        config_frame.pack(fill=tk.X)
        
        for service in rank_entry['systemConfig']:
            service_label = ttk.Label(config_frame, text=f"Service: {service['serviceName']}", 
                                     font=('Segoe UI', 12, 'bold'))
            service_label.pack(anchor=tk.W, pady=(10, 5))
            
            for class_config in service['classConfigs']:
                class_name = class_config['className'].split('/')[-1]
                class_label = ttk.Label(config_frame, text=f"  Class: {class_name}", 
                                       font=('Segoe UI', 10, 'bold'))
                class_label.pack(anchor=tk.W, padx=(20, 0))
                
                for behavior in class_config['behaviours']:
                    behavior_text = f"    {behavior['methodName']}: {behavior['behaviourId']}"
                    ttk.Label(config_frame, text=behavior_text, 
                             style='Info.TLabel').pack(anchor=tk.W, padx=(40, 0))
    
    def show_modern_comparison(self):
        """Display modern service comparison analysis."""
        self.notebook.select(self.tabs['comparison'])
        self.clear_tab('comparison')
        
        if not self.data:
            self.show_no_data_message(self.tabs['comparison'])
            return
        
        # Create figure with better spacing
        fig = Figure(figsize=(16, 12), dpi=100, facecolor='white')
        fig.suptitle('Service Performance Comparison', fontsize=18, fontweight='bold', y=0.96)
        
        gs = fig.add_gridspec(2, 2, hspace=0.4, wspace=0.3,
                             left=0.08, right=0.95, top=0.88, bottom=0.08)
        
        # Service performance comparison
        ax1 = fig.add_subplot(gs[0, 0])
        self.create_service_performance_chart(ax1)
        
        # Service ranking distribution
        ax2 = fig.add_subplot(gs[0, 1])
        self.create_service_ranking_chart(ax2)
        
        # Behavior analysis by service
        ax3 = fig.add_subplot(gs[1, 0])
        self.create_service_behavior_chart(ax3)
        
        # Performance variability
        ax4 = fig.add_subplot(gs[1, 1])
        self.create_variability_chart(ax4)
        
        self.embed_chart(fig, self.tabs['comparison'])
    
    def create_service_performance_chart(self, ax):
        """Create service performance comparison chart."""
        service_perf = self.df_metrics.groupby(['service_name', 'metric_name'])['value'].mean().unstack()
        
        # Use modern colors
        colors = ['#3b82f6', '#ef4444', '#10b981', '#f59e0b']
        service_perf.plot(kind='bar', ax=ax, color=colors[:len(service_perf.columns)], alpha=0.8)
        
        ax.set_title('Average Service Performance by Metric', fontsize=12, fontweight='bold', pad=15)
        ax.set_xlabel('Service', fontsize=10)
        ax.set_ylabel('Average Metric Value', fontsize=10)
        ax.legend(bbox_to_anchor=(1.05, 1), loc='upper left', frameon=True, fancybox=True)
        ax.tick_params(axis='x', rotation=45)
        ax.grid(True, alpha=0.3)
    
    def create_service_ranking_chart(self, ax):
        """Create service ranking distribution chart."""
        rank_dist = self.df_metrics.groupby(['service_name', 'position']).size().unstack(fill_value=0)
        sns.heatmap(rank_dist, ax=ax, annot=True, fmt='d', cmap='Blues',
                   cbar_kws={'label': 'Participation Count'})
        ax.set_title('Service Participation by Configuration Rank', fontsize=12, fontweight='bold', pad=15)
        ax.set_xlabel('Configuration Rank', fontsize=10)
        ax.set_ylabel('Service', fontsize=10)
    
    def create_service_behavior_chart(self, ax):
        """Create behavior distribution by service chart."""
        behavior_by_service = self.df_configs.groupby(['service_name', 'behavior_id']).size().unstack(fill_value=0)
        
        colors = plt.cm.Set3(np.linspace(0, 1, len(behavior_by_service.columns)))
        behavior_by_service.plot(kind='bar', stacked=True, ax=ax, color=colors, alpha=0.8)
        
        ax.set_title('Behavior Distribution by Service', fontsize=12, fontweight='bold', pad=15)
        ax.set_xlabel('Service', fontsize=10)
        ax.set_ylabel('Behavior Count', fontsize=10)
        ax.tick_params(axis='x', rotation=45)
        ax.legend(bbox_to_anchor=(1.05, 1), loc='upper left', frameon=True, fancybox=True)
        ax.grid(True, alpha=0.3)
    
    def create_variability_chart(self, ax):
        """Create performance variability chart."""
        variability = self.df_metrics.groupby('service_name')['value'].std()
        
        variability.plot(kind='bar', ax=ax, color='#f59e0b', alpha=0.8)
        ax.set_title('Performance Variability by Service', fontsize=12, fontweight='bold', pad=15)
        ax.set_xlabel('Service', fontsize=10)
        ax.set_ylabel('Standard Deviation', fontsize=10)
        ax.tick_params(axis='x', rotation=45)
        ax.grid(True, alpha=0.3)
    
    def show_behavior_analysis(self):
        """Show detailed behavior analysis in a new window."""
        if not self.data:
            self.show_error("No data loaded for behavior analysis.")
            return
        
        # Create new modern window
        behavior_window = tk.Toplevel(self.root)
        behavior_window.title("Behavior Distribution Analysis")
        behavior_window.geometry("1200x800")
        behavior_window.configure(bg='#f0f0f0')
        
        # Create figure with proper spacing
        fig = Figure(figsize=(14, 10), dpi=100, facecolor='white')
        fig.suptitle('Comprehensive Behavior Analysis', fontsize=16, fontweight='bold', y=0.96)
        
        gs = fig.add_gridspec(2, 2, hspace=0.4, wspace=0.3,
                             left=0.08, right=0.95, top=0.88, bottom=0.1)
        
        # Overall behavior distribution
        ax1 = fig.add_subplot(gs[0, 0])
        behavior_counts = self.df_configs['behavior_id'].value_counts()
        behavior_counts.plot(kind='bar', ax=ax1, color='#3b82f6', alpha=0.8)
        ax1.set_title('Overall Behavior Distribution', fontsize=12, fontweight='bold')
        ax1.tick_params(axis='x', rotation=45)
        ax1.grid(True, alpha=0.3)
        
        # Behavior by service heatmap
        ax2 = fig.add_subplot(gs[0, 1])
        behavior_service = pd.crosstab(self.df_configs['behavior_id'], self.df_configs['service_name'])
        sns.heatmap(behavior_service, ax=ax2, annot=True, fmt='d', cmap='YlOrRd')
        ax2.set_title('Behavior Distribution by Service', fontsize=12, fontweight='bold')
        
        # Performance by behavior
        ax3 = fig.add_subplot(gs[1, 0])
        if 'JVM Heap Memory' in self.df_rankings['metric_name'].values:
            behavior_performance = self.df_configs.merge(
                self.df_rankings[self.df_rankings['metric_name'] == 'JVM Heap Memory'], 
                on='position'
            ).groupby('behavior_id')['value'].mean().sort_values()
            behavior_performance.plot(kind='barh', ax=ax3, color='#10b981', alpha=0.8)
            ax3.set_title('Avg Performance by Behavior (Memory)', fontsize=12, fontweight='bold')
        
        # Behavior ranking correlation
        ax4 = fig.add_subplot(gs[1, 1])
        behavior_ranks = self.df_configs.groupby('behavior_id')['position'].mean().sort_values()
        behavior_ranks.plot(kind='bar', ax=ax4, color='#f59e0b', alpha=0.8)
        ax4.set_title('Average Ranking by Behavior', fontsize=12, fontweight='bold')
        ax4.tick_params(axis='x', rotation=45)
        ax4.grid(True, alpha=0.3)
        
        # Embed in window
        canvas = FigureCanvasTkAgg(fig, behavior_window)
        canvas.draw()
        canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True, padx=20, pady=20)
        
        # Add toolbar
        toolbar = NavigationToolbar2Tk(canvas, behavior_window)
        toolbar.update()
    
    def show_statistics(self):
        """Show statistical analysis in a modern window."""
        if not self.data:
            self.show_error("No data loaded for statistical analysis.")
            return
        
        stats_window = tk.Toplevel(self.root)
        stats_window.title("Statistical Analysis")
        stats_window.geometry("900x700")
        stats_window.configure(bg='#f0f0f0')
        
        # Create modern text viewer
        main_frame = ttk.Frame(stats_window, style='Modern.TFrame', padding="20")
        main_frame.pack(fill=tk.BOTH, expand=True)
        
        # Title
        title_label = ttk.Label(main_frame, text="Statistical Analysis Report", 
                               font=('Segoe UI', 18, 'bold'))
        title_label.pack(pady=(0, 20))
        
        # Text area with scrollbar
        text_frame = ttk.Frame(main_frame, style='Modern.TFrame')
        text_frame.pack(fill=tk.BOTH, expand=True)
        
        scrollbar = ttk.Scrollbar(text_frame)
        scrollbar.pack(side=tk.RIGHT, fill=tk.Y)
        
        text_widget = tk.Text(text_frame, yscrollcommand=scrollbar.set, 
                             font=('Segoe UI', 11), wrap=tk.WORD, 
                             bg='white', relief='solid', borderwidth=1)
        text_widget.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        scrollbar.config(command=text_widget.yview)
        
        # Generate and insert statistics
        stats_text = self.generate_detailed_statistics()
        text_widget.insert(tk.END, stats_text)
        text_widget.config(state=tk.DISABLED)
    
    def generate_detailed_statistics(self):
        """Generate comprehensive statistical analysis."""
        stats = "COMPREHENSIVE STATISTICAL ANALYSIS\n"
        stats += "=" * 50 + "\n\n"
        
        # Dataset overview
        stats += "DATASET OVERVIEW\n"
        stats += "-" * 20 + "\n"
        stats += f"Total Configurations Tested: {len(self.data['ranking'])}\n"
        stats += f"Services Analyzed: {self.df_configs['service_name'].nunique()}\n"
        stats += f"Unique Behaviors: {self.df_configs['behavior_id'].nunique()}\n"
        stats += f"Metrics Tracked: {len(self.data['metricConfigs'])}\n\n"
        
        # Metric statistics
        stats += "METRIC PERFORMANCE STATISTICS\n"
        stats += "-" * 35 + "\n"
        
        for metric in self.df_rankings['metric_name'].unique():
            metric_data = self.df_rankings[self.df_rankings['metric_name'] == metric]['value']
            stats += f"\n{metric}:\n"
            stats += f"  Mean: {metric_data.mean():.4f}\n"
            stats += f"  Median: {metric_data.median():.4f}\n"
            stats += f"  Std Deviation: {metric_data.std():.4f}\n"
            stats += f"  Min: {metric_data.min():.4f}\n"
            stats += f"  Max: {metric_data.max():.4f}\n"
            stats += f"  Range: {metric_data.max() - metric_data.min():.4f}\n"
            
            # Calculate percentiles
            p25 = metric_data.quantile(0.25)
            p75 = metric_data.quantile(0.75)
            stats += f"  25th Percentile: {p25:.4f}\n"
            stats += f"  75th Percentile: {p75:.4f}\n"
            stats += f"  IQR: {p75 - p25:.4f}\n"
        
        # Best configuration analysis
        best_config = self.data['ranking'][0]
        stats += "\n\nBEST PERFORMING CONFIGURATION (Rank #1)\n"
        stats += "-" * 45 + "\n"
        stats += "System Metrics:\n"
        for metric in best_config['systemResults']:
            stats += f"  {metric['metricName']}: {metric['value']:.4f} {metric['unit']}\n"
        
        stats += "\nBehavior Configuration:\n"
        for service in best_config['systemConfig']:
            stats += f"  {service['serviceName']}:\n"
            for class_config in service['classConfigs']:
                for behavior in class_config['behaviours']:
                    stats += f"    {behavior['methodName']}: {behavior['behaviourId']}\n"
        
        # Behavior analysis
        stats += "\n\nBEHAVIOR IMPACT ANALYSIS\n"
        stats += "-" * 28 + "\n"
        behavior_rankings = self.df_configs.groupby('behavior_id')['position'].agg(['mean', 'std', 'count'])
        
        for behavior in behavior_rankings.index:
            stats += f"\n{behavior}:\n"
            stats += f"  Average Rank: {behavior_rankings.loc[behavior, 'mean']:.2f}\n"
            stats += f"  Rank Std Dev: {behavior_rankings.loc[behavior, 'std']:.2f}\n"
            stats += f"  Usage Count: {behavior_rankings.loc[behavior, 'count']}\n"
        
        return stats
    
    def export_summary(self):
        """Export comprehensive analysis summary."""
        if not self.data:
            self.show_warning("No data loaded to export.")
            return
        
        file_path = filedialog.asksaveasfilename(
            title="Export Analysis Summary",
            defaultextension=".txt",
            filetypes=[("Text files", "*.txt"), ("All files", "*.*")]
        )
        
        if file_path:
            try:
                with open(file_path, 'w') as f:
                    f.write(self.generate_detailed_statistics())
                    f.write("\n\n" + "="*50 + "\n")
                    f.write("DETAILED CONFIGURATION BREAKDOWN\n")
                    f.write("="*50 + "\n\n")
                    
                    for rank_entry in self.data['ranking']:
                        position = rank_entry['position']
                        f.write(f"CONFIGURATION RANK #{position}\n")
                        f.write("-" * 30 + "\n")
                        
                        f.write("System Performance:\n")
                        for metric in rank_entry['systemResults']:
                            f.write(f"  {metric['metricName']}: {metric['value']:.4f} {metric['unit']}\n")
                        
                        f.write("\nBehavior Settings:\n")
                        for service in rank_entry['systemConfig']:
                            f.write(f"  Service: {service['serviceName']}\n")
                            for class_config in service['classConfigs']:
                                class_name = class_config['className'].split('/')[-1]
                                f.write(f"    Class: {class_name}\n")
                                for behavior in class_config['behaviours']:
                                    f.write(f"      {behavior['methodName']}: {behavior['behaviourId']}\n")
                        f.write("\n" + "="*50 + "\n\n")
                
                self.show_success(f"Analysis summary exported to {file_path}")
            except Exception as e:
                self.show_error(f"Failed to export summary: {str(e)}")
    
    def generate_report(self):
        """Generate comprehensive PDF report (placeholder with enhanced message)."""
        self.show_info("Advanced Report Generation", 
                      "PDF report generation would include:\n\n"
                      "• Executive summary with key findings\n"
                      "• All visualizations in high resolution\n"
                      "• Statistical analysis tables\n"
                      "• Configuration recommendations\n"
                      "• Performance optimization insights\n\n"
                      "This feature would use libraries like reportlab or weasyprint "
                      "to create professional PDF reports.")
    
    # Utility methods for modern UI
    def clear_tab(self, tab_name):
        """Clear all widgets from a tab."""
        for widget in self.tabs[tab_name].winfo_children():
            widget.destroy()
    
    def show_no_data_message(self, parent):
        """Show a modern 'no data' message."""
        frame = ttk.Frame(parent, style='Modern.TFrame')
        frame.pack(expand=True, fill=tk.BOTH)
        
        ttk.Label(frame, text="No Data Available", 
                 font=('Segoe UI', 18, 'bold'),
                 foreground='#64748b').pack(expand=True)
        ttk.Label(frame, text="Please load experiment data to view this analysis.",
                 font=('Segoe UI', 12),
                 foreground='#94a3b8').pack()
    
    def embed_chart(self, fig, parent):
        """Embed a matplotlib figure in a tkinter parent with toolbar."""
        chart_frame = ttk.Frame(parent, style='Modern.TFrame')
        chart_frame.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)
        
        canvas = FigureCanvasTkAgg(fig, chart_frame)
        canvas.draw()
        canvas.get_tk_widget().pack(fill=tk.BOTH, expand=True)
        
        # Add navigation toolbar
        toolbar = NavigationToolbar2Tk(canvas, chart_frame)
        toolbar.update()
    
    def show_success(self, message):
        """Show a success message."""
        try:
            messagebox.showinfo("Success", message)
        except tk.TclError:
            # Window might be destroyed, ignore messagebox error
            pass
    
    def show_error(self, message):
        """Show an error message."""
        try:
            messagebox.showerror("Error", message)
        except tk.TclError:
            # Window might be destroyed, ignore messagebox error
            pass
    
    def show_warning(self, message):
        """Show a warning message."""
        try:
            messagebox.showwarning("Warning", message)
        except tk.TclError:
            # Window might be destroyed, ignore messagebox error
            pass
    
    def show_info(self, title, message):
        """Show an info message."""
        try:
            messagebox.showinfo(title, message)
        except tk.TclError:
            # Window might be destroyed, ignore messagebox error
            pass

def main():
    """Main function to run the modern application."""
    root = tk.Tk()
    app = ModernExperimentAnalyzer(root)
    root.mainloop()

if __name__ == "__main__":
    main()