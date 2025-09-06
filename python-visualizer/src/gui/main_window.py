"""
Fixed main window with simple, reliable layout that doesn't squeeze content.
"""

import tkinter as tk
from tkinter import ttk, filedialog, messagebox
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg, NavigationToolbar2Tk
from matplotlib.figure import Figure
import os

from ..data.processor import DataProcessor
from ..charts.generator import ChartGenerator
from .styles import ModernStyles
from .components import TitleBar, Sidebar


class OverviewTab:
    """Overview tab with simple, working layout."""
    
    def __init__(self, parent: tk.Widget, data_processor, chart_generator):
        self.parent = parent
        self.data_processor = data_processor
        self.chart_generator = chart_generator
    
    def create_content(self):
        """Create the overview tab content with simple layout."""
        # Clear any existing content
        for widget in self.parent.winfo_children():
            widget.destroy()
        
        # Create main container with padding
        main_container = tk.Frame(self.parent, bg='white')
        main_container.pack(fill=tk.BOTH, expand=True, padx=15, pady=15)
        
        # Info panel with fixed height
        self.create_info_panel(main_container)
        
        # Chart section
        self.create_chart_section(main_container)
    
    def create_info_panel(self, parent: tk.Widget):
        """Create a properly sized information panel."""
        stats = self.data_processor.get_summary_stats()
        
        # Info section with title
        info_section = tk.Frame(parent, bg='white')
        info_section.pack(fill=tk.X, pady=(0, 20))
        
        # Title
        title_label = tk.Label(info_section, text="📊 Experiment Overview", 
                              font=('Segoe UI', 16, 'bold'), 
                              bg='white', fg='#2c3e50')
        title_label.pack(pady=(0, 15))
        
        # Info cards container with fixed height
        cards_container = tk.Frame(info_section, bg='#f8fafc', relief='solid', bd=1, height=90)
        cards_container.pack(fill=tk.X, pady=(0, 0))
        cards_container.pack_propagate(False)  # Force height
        
        # Cards frame inside container
        cards_frame = tk.Frame(cards_container, bg='#f8fafc')
        cards_frame.pack(fill=tk.BOTH, expand=True, padx=25, pady=15)
        
        # Info cards
        cards_data = [
            ("📊", "Configurations", str(stats.get('total_configurations', 0)), "Test scenarios"),
            ("📏", "Metrics", str(stats.get('total_metrics', 0)), "Measurements"),
            ("📈", "Processing", "Normalized", "Ready for analysis"),
            ("📋", "Reading", "Higher = Better", "Optimized display"),
        ]
        
        for i, (icon, title, value, subtitle) in enumerate(cards_data):
            card = self.create_info_card(cards_frame, icon, title, value, subtitle)
            card.pack(side=tk.LEFT, padx=(0, 20) if i < len(cards_data)-1 else (0, 0))
    
    def create_info_card(self, parent: tk.Widget, icon: str, title: str, value: str, subtitle: str) -> tk.Frame:
        """Create a simple info card."""
        card = tk.Frame(parent, bg='white', relief='solid', bd=1, width=180, height=60)
        card.pack_propagate(False)
        
        # Content
        content = tk.Frame(card, bg='white')
        content.pack(fill=tk.BOTH, expand=True, padx=12, pady=10)
        
        # Header row
        header = tk.Frame(content, bg='white')
        header.pack(fill=tk.X)
        
        icon_label = tk.Label(header, text=icon, font=('Segoe UI Emoji', 16), bg='white')
        icon_label.pack(side=tk.LEFT)
        
        title_label = tk.Label(header, text=title, font=('Segoe UI', 9, 'bold'), 
                              bg='white', fg='#374151')
        title_label.pack(side=tk.LEFT, padx=(8, 0))
        
        # Value
        value_label = tk.Label(content, text=value, font=('Segoe UI', 12, 'bold'), 
                              bg='white', fg='#3b82f6')
        value_label.pack(anchor='w', pady=(3, 0))
        
        # Subtitle
        subtitle_label = tk.Label(content, text=subtitle, font=('Segoe UI', 8), 
                                 bg='white', fg='#6b7280')
        subtitle_label.pack(anchor='w')
        
        return card
    
    def create_chart_section(self, parent: tk.Widget):
        """Create the chart section."""
        # Chart section with title
        chart_section = tk.Frame(parent, bg='white')
        chart_section.pack(fill=tk.BOTH, expand=True, pady=(20, 0))
        
        # Chart title
        chart_title = tk.Label(chart_section, text="🔬 Parallel Coordinates Analysis", 
                              font=('Segoe UI', 14, 'bold'), 
                              bg='white', fg='#2c3e50')
        chart_title.pack(pady=(0, 15))
        
        # Chart container
        chart_container = tk.Frame(chart_section, bg='white')
        chart_container.pack(fill=tk.BOTH, expand=True)
        
        self.create_parallel_coordinates_chart(chart_container)
    
    def create_parallel_coordinates_chart(self, parent: tk.Widget):
        """Create parallel coordinates chart."""
        parallel_data = self.data_processor.get_parallel_coordinates_data()
        
        if parallel_data.empty:
            no_data_label = tk.Label(parent, text="📊\n\nNo data available\nLoad experiment data to view visualization",
                                   font=('Segoe UI', 14), fg='#7f8c8d', bg='white', justify=tk.CENTER)
            no_data_label.pack(expand=True)
            return
        
        # Create figure
        fig = Figure(figsize=(14, 7), dpi=100, facecolor='white')
        ax = fig.add_subplot(111)
        
        # Get metric directions and order
        metric_directions = {}
        metric_order = {}
        
        for metric_config in self.data_processor.data['metricConfigs']:
            metric_name = metric_config['metricName']
            metric_directions[metric_name] = metric_config['direction']
            metric_order[metric_name] = metric_config['order']
        
        # Create the chart
        color_mapping = self.chart_generator.create_parallel_coordinates_chart(
            ax, parallel_data, metric_directions, metric_order
        )
        
        fig.tight_layout(pad=1.0)
        
        # Create canvas
        canvas_widget = FigureCanvasTkAgg(fig, parent)
        canvas_widget.draw()
        
        chart_widget = canvas_widget.get_tk_widget()
        chart_widget.pack(fill=tk.BOTH, expand=True)
        
        # Add navigation toolbar
        toolbar = NavigationToolbar2Tk(canvas_widget, parent)
        toolbar.update()
        
        # Create legend
        self.create_legend(parent, color_mapping)

    def create_legend(self, parent: tk.Widget, color_mapping: list):
        """Create a simple legend."""
        if not color_mapping:
            return
        
        # Legend container
        legend_frame = tk.Frame(parent, bg='white', height=60)
        legend_frame.pack(fill=tk.X, pady=(10, 0))
        legend_frame.pack_propagate(False)
        
        # Title
        title_label = tk.Label(legend_frame, 
                              text=f"🏆 Top Configurations ({len(color_mapping)} total)",
                              font=('Segoe UI', 11, 'bold'), bg='white', fg='#2c3e50')
        title_label.pack(pady=(8, 5))
        
        # Configurations
        configs_container = tk.Frame(legend_frame, bg='white')
        configs_container.pack(fill=tk.X, padx=20)
        
        # Show top 3
        top_configs = [c for c in color_mapping if c['priority'] == 'top']
        for config in top_configs:
            config_frame = tk.Frame(configs_container, bg='white')
            config_frame.pack(side=tk.LEFT, padx=(0, 20))
            
            # Color indicator
            color_canvas = tk.Canvas(config_frame, width=12, height=12, bg='white', highlightthickness=0)
            color_canvas.pack(side=tk.LEFT, padx=(0, 5))
            
            import matplotlib.colors as mcolors
            hex_color = mcolors.to_hex(config['color'])
            color_canvas.create_oval(1, 1, 11, 11, fill=hex_color, outline='#ddd', width=1)
            
            # Label
            label_text = f"{config['emoji']} {config['config']}"
            config_label = tk.Label(config_frame, text=label_text, font=('Segoe UI', 9, 'bold'),
                                   bg='white', fg='#2c3e50')
            config_label.pack(side=tk.LEFT)
        
        # Remaining count
        remaining_count = len(color_mapping) - len(top_configs)
        if remaining_count > 0:
            remaining_label = tk.Label(configs_container, text=f"... +{remaining_count} others",
                                     font=('Segoe UI', 9), bg='white', fg='#7f8c8d')
            remaining_label.pack(side=tk.LEFT, padx=(20, 0))


class ModernExperimentAnalyzer:
    """Main application class with simple, reliable layout."""
    
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
        main_container = ttk.Frame(self.root, style='Modern.TFrame')
        main_container.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)
        
        # Title bar with FORCED height
        title_container = tk.Frame(main_container, bg='white', height=110)
        title_container.pack(fill=tk.X, pady=(0, 15))
        title_container.pack_propagate(False)  # FORCE the height
        
        self.title_bar = TitleBar(title_container)
        self.title_bar.pack(fill=tk.BOTH, expand=True)
        
        # Content area with sidebar and main content
        content_frame = ttk.Frame(main_container, style='Modern.TFrame')
        content_frame.pack(fill=tk.BOTH, expand=True)
        
        # Sidebar (fixed width)
        self.sidebar = Sidebar(content_frame, self)
        self.sidebar.pack(side=tk.LEFT, fill=tk.Y, padx=(0, 15))
        
        # Main content area (expandable) - THIS IS KEY
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
            frame = ttk.Frame(self.notebook, style='Modern.TFrame')
            self.notebook.add(frame, text=tab_name)
            self.tabs[tab_id] = frame
        
        # Initialize default view
        self.show_no_data_message_all_tabs()
    
    def load_data(self):
        """Load experiment data from file."""
        # Try default file first
        project_dir = os.path.dirname(os.path.dirname(os.path.dirname(__file__)))
        default_file = os.path.join(project_dir, 'results.json')
        
        if os.path.exists(default_file):
            if self.data_processor.load_data_from_file(default_file):
                self.on_data_loaded()
                return
        
        # Open file dialog
        filepath = filedialog.askopenfilename(
            title="Select Experiment Results File",
            filetypes=[("JSON files", "*.json"), ("All files", "*.*")],
            initialdir=project_dir
        )
        
        if filepath:
            if self.data_processor.load_data_from_file(filepath):
                self.on_data_loaded()
            else:
                messagebox.showerror("Error", "Failed to load data. Please check the file format.")
    
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
        
        if self.data_processor.data and 'metricConfigs' in self.data_processor.data:
            self.sidebar.update_metrics(self.data_processor.data['metricConfigs'])
    
    def create_overview(self):
        """Create the overview tab content."""
        self.clear_tab('overview')
        
        if not self.data_processor.data:
            self.show_no_data_message(self.tabs['overview'])
            return
        
        # Create simple overview tab
        overview_tab = OverviewTab(self.tabs['overview'], self.data_processor, self.chart_generator)
        overview_tab.create_content()
    
    def create_rankings(self):
        """Create the rankings tab content."""
        self.clear_tab('rankings')
        
        if not self.data_processor.data:
            self.show_no_data_message(self.tabs['rankings'])
            return
        
        # Create rankings visualization
        fig = Figure(figsize=(12, 8), dpi=100, facecolor='white')
        ax = fig.add_subplot(111)
        
        self.chart_generator.create_rankings_chart(ax, self.data_processor.df_rankings)
        fig.tight_layout()
        
        # Embed chart
        canvas_widget = FigureCanvasTkAgg(fig, self.tabs['rankings'])
        canvas_widget.draw()
        canvas_widget.get_tk_widget().pack(fill=tk.BOTH, expand=True, padx=10, pady=10)
        
        # Add navigation toolbar
        toolbar = NavigationToolbar2Tk(canvas_widget, self.tabs['rankings'])
        toolbar.update()
    
    def create_trends(self):
        """Create the trends tab content."""
        self.clear_tab('trends')
        
        if not self.data_processor.data:
            self.show_no_data_message(self.tabs['trends'])
            return
        
        # Placeholder for trends analysis
        label = tk.Label(self.tabs['trends'], 
                        text="Trends Analysis\n(Feature coming soon)", 
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
            for widget in self.tabs[tab_id].winfo_children():
                widget.destroy()
    
    def show_no_data_message(self, parent: tk.Widget):
        """Show 'no data' message in a tab."""
        message_frame = tk.Frame(parent, bg='white')
        message_frame.pack(fill=tk.BOTH, expand=True)
        
        icon_label = tk.Label(message_frame, text="📊", 
                             font=('Segoe UI Emoji', 48), 
                             bg='white', fg='#bdc3c7')
        icon_label.pack(pady=(100, 20))
        
        message_label = tk.Label(message_frame, 
                               text="No data loaded\nClick 'Load Data' to begin analysis", 
                               font=('Segoe UI', 16), 
                               bg='white', fg='#7f8c8d',
                               justify=tk.CENTER)
        message_label.pack()
    
    def show_no_data_message_all_tabs(self):
        """Show no data message in all tabs."""
        for tab_id in self.tabs:
            self.show_no_data_message(self.tabs[tab_id])