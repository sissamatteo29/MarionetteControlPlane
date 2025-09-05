"""
Main window and application logic for the Marionette Experiment Analyzer.
Handles the primary GUI layout and coordination between components.
"""

import tkinter as tk
from tkinter import ttk, filedialog, messagebox
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg, NavigationToolbar2Tk
from matplotlib.figure import Figure
import os

from ..data.processor import DataProcessor
from ..charts.generator import ChartGenerator
from .styles import ModernStyles, InfoCardFactory, LayoutManager
from .components import (
    TitleBar, Sidebar, DataSummarySection, 
    VisualizationSection, OverviewTab
)


class ModernExperimentAnalyzer:
    """Main application class for the experiment analyzer."""
    
    def __init__(self, root: tk.Tk):
        self.root = root
        self.root.title("Marionette Experiment Analyzer")
        self.root.geometry("1600x1000")
        self.root.configure(bg='#f0f0f0')
        
        # Initialize core components
        self.data_processor = DataProcessor()
        self.chart_generator = ChartGenerator()
        
        # GUI state
        self.tabs = {}
        self.notebook = None
        
        # Initialize GUI
        self.setup_gui()
        
    def setup_gui(self):
        """Initialize the complete GUI."""
        ModernStyles.setup_styles()
        self.create_main_layout()
        
    def create_main_layout(self):
        """Create the main application layout."""
        # Main container
        main_container = ttk.Frame(self.root, style='Modern.TFrame', padding="0")
        main_container.pack(fill=tk.BOTH, expand=True)
        
        # Title bar
        self.title_bar = TitleBar(main_container)
        self.title_bar.pack(fill=tk.X, pady=(0, 10))
        
        # Content area with sidebar and main content
        content_frame = ttk.Frame(main_container, style='Modern.TFrame')
        content_frame.pack(fill=tk.BOTH, expand=True, padx=20, pady=(0, 20))
        
        # Sidebar
        self.sidebar = Sidebar(content_frame, self)
        self.sidebar.pack(side=tk.LEFT, fill=tk.Y, padx=(0, 20))
        
        # Main content area
        self.main_content = ttk.Frame(content_frame, style='Modern.TFrame')
        self.main_content.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        
        # Create tabbed interface
        self.create_tabs()
        
    def create_tabs(self):
        """Create the main tabbed interface."""
        self.notebook = ttk.Notebook(self.main_content, style='Modern.TNotebook')
        self.notebook.pack(fill=tk.BOTH, expand=True)
        
        # Define tabs
        tab_configs = [
            ("📊 Overview", "overview"),
            ("🏆 Rankings", "rankings"),
            ("📈 Trends", "trends"),
            ("📋 Raw Data", "raw_data")
        ]
        
        # Create tab frames
        for tab_name, tab_id in tab_configs:
            frame = ttk.Frame(self.notebook, style='Modern.TFrame', padding="10")
            self.notebook.add(frame, text=tab_name)
            self.tabs[tab_id] = frame
        
        # Initialize default view
        self.show_no_data_message_all_tabs()
    
    def load_data(self):
        """Load experiment data from file."""
        # Try default file first (results.json in the same directory as main.py)
        project_dir = os.path.dirname(os.path.dirname(os.path.dirname(__file__)))  # Go up from src/gui/
        default_file = os.path.join(project_dir, 'results.json')
        
        print(f"Looking for default file at: {default_file}")
        
        if os.path.exists(default_file):
            print("Default file found, loading...")
            if self.data_processor.load_data_from_file(default_file):
                self.on_data_loaded()
                return
            else:
                print("Failed to load default file")
        else:
            print("Default file not found")
        
        # If default doesn't exist or fails to load, open file dialog
        print("Opening file dialog...")
        filepath = filedialog.askopenfilename(
            title="Select Experiment Results File",
            filetypes=[("JSON files", "*.json"), ("All files", "*.*")],
            initialdir=project_dir
        )
        
        if filepath:
            print(f"User selected file: {filepath}")
            if self.data_processor.load_data_from_file(filepath):
                self.on_data_loaded()
            else:
                messagebox.showerror("Error", 
                                   "Failed to load data. Please check the file format.")
        else:
            print("No file selected")
    
    def on_data_loaded(self):
        """Handle successful data loading."""
        messagebox.showinfo("Success", "Data loaded successfully!")
        self.update_all_views()
    
    def update_all_views(self):
        """Update all views with new data."""
        self.update_sidebar()
        self.create_overview()
        self.create_rankings()
        self.create_trends()
        self.create_raw_data()
    
    def update_sidebar(self):
        """Update sidebar with data summary."""
        stats = self.data_processor.get_summary_stats()
        self.sidebar.update_summary(stats)
        
        # Update metrics list
        if self.data_processor.data and 'metricConfigs' in self.data_processor.data:
            self.sidebar.update_metrics(self.data_processor.data['metricConfigs'])
    
    def create_overview(self):
        """Create the overview tab content."""
        self.clear_tab('overview')
        
        if not self.data_processor.data:
            self.show_no_data_message(self.tabs['overview'])
            return
        
        # Create overview tab
        overview_tab = OverviewTab(self.tabs['overview'], self.data_processor, self.chart_generator)
        overview_tab.create_content()
    
    def create_rankings(self):
        """Create the rankings tab content."""
        self.clear_tab('rankings')
        
        if not self.data_processor.data:
            self.show_no_data_message(self.tabs['rankings'])
            return
        
        # Create rankings visualization
        fig = Figure(figsize=(16, 10), dpi=100, facecolor='white')
        ax = fig.add_subplot(111)
        
        self.chart_generator.create_rankings_chart(ax, self.data_processor.df_rankings)
        
        # Embed chart
        chart_frame = ttk.Frame(self.tabs['rankings'], style='Modern.TFrame')
        chart_frame.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)
        
        canvas_widget = FigureCanvasTkAgg(fig, chart_frame)
        canvas_widget.draw()
        canvas_widget.get_tk_widget().pack(fill=tk.BOTH, expand=True)
        
        # Add navigation toolbar
        toolbar = NavigationToolbar2Tk(canvas_widget, chart_frame)
        toolbar.update()
    
    def create_trends(self):
        """Create the trends tab content."""
        self.clear_tab('trends')
        
        if not self.data_processor.data:
            self.show_no_data_message(self.tabs['trends'])
            return
        
        # Placeholder for trends analysis
        label = tk.Label(self.tabs['trends'], 
                        text="Trends Analysis\\n(Feature coming soon)", 
                        font=('Segoe UI', 16), 
                        fg='#7f8c8d', bg='white')
        label.pack(expand=True)
    
    def create_raw_data(self):
        """Create the raw data tab content."""
        self.clear_tab('raw_data')
        
        if not self.data_processor.data:
            self.show_no_data_message(self.tabs['raw_data'])
            return
        
        # Create scrollable text widget for raw data
        text_frame = tk.Frame(self.tabs['raw_data'], bg='white')
        text_frame.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)
        
        # Scrollable text
        text_widget = tk.Text(text_frame, wrap=tk.WORD, font=('Consolas', 10))
        scrollbar = ttk.Scrollbar(text_frame, orient=tk.VERTICAL, command=text_widget.yview)
        text_widget.configure(yscrollcommand=scrollbar.set)
        
        # Insert data summary
        import json
        data_summary = json.dumps(self.data_processor.data, indent=2)
        text_widget.insert(tk.END, data_summary)
        text_widget.configure(state=tk.DISABLED)
        
        # Pack widgets
        text_widget.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        scrollbar.pack(side=tk.RIGHT, fill=tk.Y)
    
    def clear_tab(self, tab_id: str):
        """Clear content from a specific tab."""
        if tab_id in self.tabs:
            LayoutManager.clear_frame(self.tabs[tab_id])
    
    def show_no_data_message(self, parent: tk.Widget):
        """Show 'no data' message in a tab."""
        message_frame = tk.Frame(parent, bg='white')
        message_frame.pack(fill=tk.BOTH, expand=True)
        
        icon_label = tk.Label(message_frame, text="📊", 
                             font=('Segoe UI Emoji', 48), 
                             bg='white', fg='#bdc3c7')
        icon_label.pack(pady=(100, 20))
        
        message_label = tk.Label(message_frame, 
                               text="No data loaded\\nClick 'Load Data' to begin analysis", 
                               font=('Segoe UI', 16), 
                               bg='white', fg='#7f8c8d',
                               justify=tk.CENTER)
        message_label.pack()
    
    def show_no_data_message_all_tabs(self):
        """Show no data message in all tabs."""
        for tab_id in self.tabs:
            self.show_no_data_message(self.tabs[tab_id])
