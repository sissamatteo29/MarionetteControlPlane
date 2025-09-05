"""
GUI components for the Marionette Experiment Analyzer.
Contains individual UI components and specialized widgets.
"""

import tkinter as tk
from tkinter import ttk
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg, NavigationToolbar2Tk
from matplotlib.figure import Figure
from typing import Dict, Any

from .styles import ModernStyles, InfoCardFactory, LayoutManager


class TitleBar:
    """Modern title bar component."""
    
    def __init__(self, parent: tk.Widget):
        self.frame = ttk.Frame(parent, style='Modern.TFrame')
        self.create_content()
    
    def create_content(self):
        """Create title bar content."""
        # Main title container with increased height
        title_container = tk.Frame(self.frame, bg='white', height=100)
        title_container.pack(fill=tk.X, padx=20, pady=10)
        title_container.pack_propagate(False)
        
        # Icon and title
        icon_label = tk.Label(title_container, text="🔬", 
                             font=('Segoe UI Emoji', 32), bg='white')
        icon_label.pack(side=tk.LEFT, padx=(20, 15), pady=15)
        
        # Title text
        title_frame = tk.Frame(title_container, bg='white')
        title_frame.pack(side=tk.LEFT, fill=tk.Y, pady=15)
        
        main_title = tk.Label(title_frame, text="Marionette Experiment Analyzer", 
                             font=('Segoe UI', 24, 'bold'), 
                             bg='white', fg='#2c3e50')
        main_title.pack(anchor='w')
        
        subtitle = tk.Label(title_frame, text="Advanced A/B Testing Results Analysis", 
                           font=('Segoe UI', 12), 
                           bg='white', fg='#7f8c8d')
        subtitle.pack(anchor='w')
    
    def pack(self, **kwargs):
        """Pack the title bar frame."""
        self.frame.pack(**kwargs)


class Sidebar:
    """Modern sidebar component with data summary and controls."""
    
    def __init__(self, parent: tk.Widget, main_app):
        self.main_app = main_app
        self.frame = tk.Frame(parent, bg='#2c3e50', width=300)
        self.frame.pack_propagate(False)
        self.create_content()
    
    def create_content(self):
        """Create sidebar content."""
        # Sidebar title
        title_label = tk.Label(self.frame, text="Analysis Control", 
                              font=('Segoe UI', 16, 'bold'), 
                              bg='#2c3e50', fg='white')
        title_label.pack(pady=(20, 30), padx=20)
        
        # Data loading section
        self.create_data_loading_section()
        
        # Data summary section
        self.create_summary_section()
        
        # Metrics list section
        self.create_metrics_section()
    
    def create_data_loading_section(self):
        """Create the data loading section."""
        section_frame = tk.Frame(self.frame, bg='#2c3e50')
        section_frame.pack(fill=tk.X, padx=20, pady=(0, 20))
        
        section_title = tk.Label(section_frame, text="📁 Data Source", 
                                font=('Segoe UI', 12, 'bold'), 
                                bg='#2c3e50', fg='white')
        section_title.pack(anchor='w', pady=(0, 10))
        
        # Load data button
        load_btn = tk.Button(section_frame, text="Load Experiment Data", 
                            command=self.main_app.load_data,
                            bg='#3498db', fg='white', 
                            font=('Segoe UI', 10, 'bold'),
                            relief='flat', bd=0, cursor='hand2',
                            pady=12)
        load_btn.pack(fill=tk.X, pady=(0, 10))
        
        ModernStyles.add_button_hover_effects(load_btn, '#2980b9', '#3498db')
        
        # Status indicator
        self.status_label = tk.Label(section_frame, text="No data loaded", 
                                   font=('Segoe UI', 9), 
                                   bg='#2c3e50', fg='#95a5a6')
        self.status_label.pack(anchor='w')
    
    def create_summary_section(self):
        """Create the data summary section."""
        self.summary_frame = tk.Frame(self.frame, bg='#2c3e50')
        # Don't pack it initially - will be shown when data is loaded
        
        self.summary_title = tk.Label(self.summary_frame, text="📊 Data Summary", 
                                font=('Segoe UI', 12, 'bold'), 
                                bg='#2c3e50', fg='white')
        self.summary_title.pack(anchor='w', pady=(0, 15))
        
        # Initially empty - will be populated when data is loaded
        self.summary_container = tk.Frame(self.summary_frame, bg='#2c3e50')
        self.summary_container.pack(fill=tk.X)
    
    def update_summary(self, stats: Dict[str, Any]):
        """Update the summary section with new statistics."""
        # Clear existing summary
        LayoutManager.clear_frame(self.summary_container)
        
        if not stats:
            return
        
        # Show the summary section now that we have data
        self.summary_frame.pack(fill=tk.X, padx=20, pady=(20, 0))
        
        # Update status
        self.status_label.configure(text="Data loaded successfully", fg='#2ecc71')
        
        # Create summary cards (removed Best Config)
        summary_items = [
            ("📋", stats.get('total_configurations', 0), "Configurations"),
            ("📏", stats.get('total_metrics', 0), "Metrics"),
        ]
        
        for icon, value, title in summary_items:
            card = self.create_summary_card(icon, value, title)
            card.pack(fill=tk.X, pady=(0, 10))
    
    def create_summary_card(self, icon: str, value: Any, title: str) -> tk.Frame:
        """Create a summary card for the sidebar."""
        card_frame = tk.Frame(self.summary_container, bg='#34495e', relief='flat', bd=0)
        card_frame.configure(highlightbackground="#4a5f7a", highlightthickness=1)
        
        # Content container
        content = tk.Frame(card_frame, bg='#34495e')
        content.pack(fill=tk.BOTH, expand=True, padx=12, pady=10)
        
        # Icon and value row
        top_row = tk.Frame(content, bg='#34495e')
        top_row.pack(fill=tk.X)
        
        icon_label = tk.Label(top_row, text=icon, font=('Segoe UI Emoji', 16), 
                             bg='#34495e', fg='white')
        icon_label.pack(side=tk.LEFT)
        
        value_label = tk.Label(top_row, text=str(value), 
                              font=('Segoe UI', 14, 'bold'), 
                              bg='#34495e', fg='#3498db')
        value_label.pack(side=tk.RIGHT)
        
        # Title
        title_label = tk.Label(content, text=title, 
                              font=('Segoe UI', 9), 
                              bg='#34495e', fg='#bdc3c7')
        title_label.pack(anchor='w', pady=(4, 0))
        
        return card_frame
    
    def create_metrics_section(self):
        """Create the metrics list section."""
        self.metrics_frame = tk.Frame(self.frame, bg='#2c3e50')
        # Don't pack it initially - will be shown when data is loaded
        
        self.metrics_title = tk.Label(self.metrics_frame, text="📏 Performance Metrics", 
                                font=('Segoe UI', 12, 'bold'), 
                                bg='#2c3e50', fg='white')
        self.metrics_title.pack(anchor='w', pady=(0, 15))
        
        # Initially empty - will be populated when data is loaded
        self.metrics_container = tk.Frame(self.metrics_frame, bg='#2c3e50')
        self.metrics_container.pack(fill=tk.X)
    
    def update_metrics(self, metrics_data: list):
        """Update the metrics section with metric configurations."""
        # Clear existing metrics
        LayoutManager.clear_frame(self.metrics_container)
        
        if not metrics_data:
            no_metrics_label = tk.Label(self.metrics_container, 
                                       text="No metrics available", 
                                       font=('Segoe UI', 9), 
                                       bg='#2c3e50', fg='#95a5a6')
            no_metrics_label.pack(anchor='w', pady=5)
            return
        
        # Show the metrics section now that we have data
        self.metrics_frame.pack(fill=tk.X, padx=20, pady=(20, 0))
        
        # Sort metrics by order
        sorted_metrics = sorted(metrics_data, key=lambda x: x.get('order', 999))
        
        for metric in sorted_metrics:
            metric_card = self.create_metric_card(metric)
            metric_card.pack(fill=tk.X, pady=(0, 8))
    
    def create_metric_card(self, metric: dict) -> tk.Frame:
        """Create a metric information card."""
        card_frame = tk.Frame(self.metrics_container, bg='#34495e', relief='flat', bd=0)
        card_frame.configure(highlightbackground="#4a5f7a", highlightthickness=1)
        
        # Content container
        content = tk.Frame(card_frame, bg='#34495e')
        content.pack(fill=tk.BOTH, expand=True, padx=10, pady=8)
        
        # Header row with order and direction
        header_row = tk.Frame(content, bg='#34495e')
        header_row.pack(fill=tk.X)
        
        # Order badge
        order_label = tk.Label(header_row, text=f"#{metric.get('order', '?')}", 
                              font=('Segoe UI', 8, 'bold'), 
                              bg='#3498db', fg='white',
                              relief='flat', bd=0, padx=6, pady=2)
        order_label.pack(side=tk.LEFT)
        
        # Direction indicator
        direction = metric.get('direction', 'higher')
        direction_symbol = "↑" if direction == 'higher' else "↓"
        direction_color = "#2ecc71" if direction == 'higher' else "#e74c3c"
        
        direction_label = tk.Label(header_row, text=direction_symbol, 
                                  font=('Segoe UI', 12, 'bold'), 
                                  bg='#34495e', fg=direction_color)
        direction_label.pack(side=tk.RIGHT)
        
        # Metric name
        name_label = tk.Label(content, text=metric.get('metricName', 'Unknown'), 
                             font=('Segoe UI', 10, 'bold'), 
                             bg='#34495e', fg='white')
        name_label.pack(anchor='w', pady=(4, 2))
        
        # Details row
        details_row = tk.Frame(content, bg='#34495e')
        details_row.pack(fill=tk.X)
        
        # Unit
        unit = metric.get('unit', 'N/A')
        unit_label = tk.Label(details_row, text=f"Unit: {unit}", 
                             font=('Segoe UI', 8), 
                             bg='#34495e', fg='#bdc3c7')
        unit_label.pack(side=tk.LEFT)
        
        # Direction text
        direction_text = "Higher is better" if direction == 'higher' else "Lower is better"
        direction_desc = tk.Label(details_row, text=direction_text, 
                                 font=('Segoe UI', 8), 
                                 bg='#34495e', fg=direction_color)
        direction_desc.pack(side=tk.RIGHT)
        
        return card_frame
    
    def reset_sidebar(self):
        """Reset sidebar to initial state with no data."""
        # Hide data sections
        self.summary_frame.pack_forget()
        self.metrics_frame.pack_forget()
        
        # Reset status
        self.status_label.configure(text="No data loaded", fg='#95a5a6')
        
        # Clear containers
        LayoutManager.clear_frame(self.summary_container)
        LayoutManager.clear_frame(self.metrics_container)
    
    def pack(self, **kwargs):
        """Pack the sidebar frame."""
        self.frame.pack(**kwargs)


class DataSummarySection:
    """Data summary section component."""
    
    def __init__(self, parent: tk.Widget):
        self.frame = ttk.Frame(parent, style='Modern.TFrame')
        self.create_content()
    
    def create_content(self):
        """Create data summary content."""
        # Will be implemented as needed
        pass
    
    def pack(self, **kwargs):
        """Pack the section frame."""
        self.frame.pack(**kwargs)


class VisualizationSection:
    """Visualization controls section."""
    
    def __init__(self, parent: tk.Widget):
        self.frame = ttk.Frame(parent, style='Modern.TFrame')
        self.create_content()
    
    def create_content(self):
        """Create visualization controls."""
        # Will be implemented as needed
        pass
    
    def pack(self, **kwargs):
        """Pack the section frame."""
        self.frame.pack(**kwargs)


class OverviewTab:
    """Overview tab component with parallel coordinates visualization."""
    
    def __init__(self, parent: tk.Widget, data_processor, chart_generator):
        self.parent = parent
        self.data_processor = data_processor
        self.chart_generator = chart_generator
    
    def create_content(self):
        """Create the overview tab content."""
        # Create scrollable frame
        canvas, scrollbar, scrollable_frame = LayoutManager.create_scrollable_frame(self.parent)
        
        # Add info panel above the chart
        self.create_info_panel(scrollable_frame)
        
        # Create the main chart
        self.create_parallel_coordinates_chart(scrollable_frame)
        
        # Pack scrollable components
        canvas.pack(side="left", fill="both", expand=True)
        scrollbar.pack(side="right", fill="y")
    
    def create_info_panel(self, parent: tk.Widget):
        """Create the information panel above the chart."""
        stats = self.data_processor.get_summary_stats()
        
        # Create modern info panel with gradient background
        info_frame = ttk.Frame(parent, style='Modern.TFrame')
        info_frame.pack(fill=tk.X, padx=15, pady=(15, 10))
        
        # Create main info container with modern styling
        info_container = tk.Frame(info_frame, bg='#f8fafc', relief='flat', bd=0)
        info_container.pack(fill=tk.X, pady=10)
        
        # Add subtle border
        border_frame = tk.Frame(info_container, bg='#e2e8f0', height=1)
        border_frame.pack(fill=tk.X, side=tk.BOTTOM)
        
        # Create cards container
        cards_container = tk.Frame(info_container, bg='#f8fafc')
        cards_container.pack(fill=tk.X, padx=20, pady=15)
        
        # Create info cards
        cards_data = [
            ("📊", "System Configurations", str(stats.get('total_configurations', 0)), 
             "Test scenarios analyzed", "#3b82f6", "#eff6ff"),
            ("📏", "Performance Metrics", str(stats.get('total_metrics', 0)), 
             "System measurements", "#10b981", "#ecfdf5"),
            ("⚡", "Data Processing", "Normalized", 
             "Ready for comparison", "#f59e0b", "#fffbeb"),
            ("📈", "Chart Reading", "Higher = Better", 
             "Normalized for optimization goals", "#ef4444", "#fef2f2")
        ]
        
        for icon, title, value, subtitle, color, bg_color in cards_data:
            card = InfoCardFactory.create_modern_info_card(
                cards_container, icon, title, value, subtitle, color, bg_color
            )
            card.pack(side=tk.LEFT, padx=(0, 15), fill=tk.Y)
    
    def create_parallel_coordinates_chart(self, parent: tk.Widget):
        """Create the parallel coordinates chart."""
        # Get data for visualization
        parallel_data = self.data_processor.get_parallel_coordinates_data()
        
        if parallel_data.empty:
            return
        
        # Create clean chart without legend, then add custom legend below
        fig = Figure(figsize=(14, 8), dpi=80, facecolor='white')
        ax = fig.add_subplot(111)
        ax.margins(0.02, 0.02)
        
        # Get metric directions and order
        metric_directions = {}
        metric_order = {}
        
        for metric_config in self.data_processor.data['metricConfigs']:
            metric_name = metric_config['metricName']
            metric_directions[metric_name] = metric_config['direction']
            metric_order[metric_name] = metric_config['order']
        
        # Create the chart and get color mapping for legend
        color_mapping = self.chart_generator.create_parallel_coordinates_chart(
            ax, parallel_data, metric_directions, metric_order
        )
        
        # Simple tight layout for clean chart
        fig.tight_layout()
        
        # Embed chart
        chart_frame = ttk.Frame(parent, style='Modern.TFrame')
        chart_frame.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)
        
        canvas_widget = FigureCanvasTkAgg(fig, chart_frame)
        canvas_widget.draw()
        
        chart_widget = canvas_widget.get_tk_widget()
        chart_widget.pack(fill=tk.BOTH, expand=True)
        chart_widget.configure(highlightthickness=0)
        
        # Add navigation toolbar
        toolbar = NavigationToolbar2Tk(canvas_widget, chart_frame)
        toolbar.update()
        
        # Create custom legend below the chart
        self.create_custom_legend(parent, color_mapping)

    def create_custom_legend(self, parent: tk.Widget, color_mapping: list):
        """Create a custom legend widget outside the matplotlib chart."""
        if not color_mapping:
            return
        
        # Create legend container
        legend_frame = ttk.Frame(parent, style='Modern.TFrame')
        legend_frame.pack(fill=tk.X, padx=15, pady=(5, 15))
        
        # Legend header
        header_frame = tk.Frame(legend_frame, bg='white')
        header_frame.pack(fill=tk.X, pady=(0, 10))
        
        # Title and subtitle
        title_label = tk.Label(header_frame, 
                              text=f"📊 System Configurations ({len(color_mapping)} total)",
                              font=('Segoe UI', 14, 'bold'),
                              bg='white', fg='#2c3e50')
        title_label.pack(anchor='w')
        
        subtitle_label = tk.Label(header_frame,
                                 text="Color-coded ranking from best (top) to lowest performing configurations",
                                 font=('Segoe UI', 10),
                                 bg='white', fg='#7f8c8d')
        subtitle_label.pack(anchor='w', pady=(2, 0))
        
        # Create sections for different priority groups
        self._create_legend_section(legend_frame, color_mapping, 'top', '🏆 Top Performers', '#e8f5e8')
        self._create_legend_section(legend_frame, color_mapping, 'high', '⭐ High Performers', '#f0f8ff')
        self._create_legend_section(legend_frame, color_mapping, 'normal', '📈 Other Configurations', '#fafafa')
        
    def _create_legend_section(self, parent: tk.Widget, color_mapping: list, 
                              priority: str, section_title: str, bg_color: str):
        """Create a section of the legend for a specific priority group."""
        # Filter configurations by priority
        section_configs = [config for config in color_mapping if config['priority'] == priority]
        
        if not section_configs:
            return
        
        # Section container
        section_frame = tk.Frame(parent, bg='white', relief='solid', bd=1)
        section_frame.pack(fill=tk.X, pady=(0, 8))
        
        # Section header
        header_bg = tk.Frame(section_frame, bg=bg_color, height=35)
        header_bg.pack(fill=tk.X)
        header_bg.pack_propagate(False)
        
        section_label = tk.Label(header_bg, text=section_title,
                                font=('Segoe UI', 11, 'bold'),
                                bg=bg_color, fg='#2c3e50')
        section_label.pack(pady=8)
        
        # Configurations container
        configs_frame = tk.Frame(section_frame, bg='white')
        configs_frame.pack(fill=tk.X, padx=10, pady=10)
        
        # Determine layout based on number of configurations
        num_configs = len(section_configs)
        max_cols = min(4, num_configs) if priority == 'top' else min(6, num_configs)
        
        for i, config in enumerate(section_configs):
            row = i // max_cols
            col = i % max_cols
            
            # Create config item
            config_frame = tk.Frame(configs_frame, bg='white')
            config_frame.grid(row=row, column=col, padx=5, pady=3, sticky='w')
            
            # Color indicator
            color_canvas = tk.Canvas(config_frame, width=16, height=16, 
                                   bg='white', highlightthickness=0)
            color_canvas.pack(side=tk.LEFT, padx=(0, 8))
            
            # Convert matplotlib color to hex
            import matplotlib.colors as mcolors
            hex_color = mcolors.to_hex(config['color'])
            
            # Draw color circle
            color_canvas.create_oval(2, 2, 14, 14, fill=hex_color, outline='#ddd', width=1)
            
            # Configuration label
            emoji = config.get('emoji', '')
            label_text = f"{emoji} {config['config']}" if emoji else config['config']
            
            config_label = tk.Label(config_frame, text=label_text,
                                   font=('Segoe UI', 9, 'bold' if priority == 'top' else 'normal'),
                                   bg='white', 
                                   fg='#2c3e50' if priority == 'top' else '#34495e')
            config_label.pack(side=tk.LEFT)
        
        # Configure grid weights for even spacing
        for col in range(max_cols):
            configs_frame.grid_columnconfigure(col, weight=1)
