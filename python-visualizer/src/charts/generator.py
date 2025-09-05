"""
Chart generation module for experiment visualization.
Contains all chart creation and styling logic.
"""

import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from matplotlib.figure import Figure
from typing import Dict, List, Any
import seaborn as sns

# Set modern style for matplotlib
plt.style.use('seaborn-v0_8-whitegrid')
sns.set_palette("husl")


class ChartGenerator:
    """Handles creation of all chart types for experiment visualization."""
    
    def __init__(self):
        pass
    
    def create_parallel_coordinates_chart(self, ax, data: pd.DataFrame, 
                                        metric_directions: Dict[str, str],
                                        metric_order: Dict[str, int]):
        """
        Create a modern parallel coordinates plot showing all system configurations.
        
        Args:
            ax: Matplotlib axis object
            data: DataFrame with configuration data
            metric_directions: Mapping of metric names to optimization directions
            metric_order: Mapping of metric names to display order
            
        Returns:
            List[Dict]: Configuration color mapping information for custom legend
        """
        if data.empty:
            ax.text(0.5, 0.5, 'No data available', ha='center', va='center',
                   transform=ax.transAxes, fontsize=16)
            return []
        
        # Set modern style
        plt.style.use('seaborn-v0_8-whitegrid')
        
        # Sort metrics according to their defined order
        all_numeric_cols = [col for col in data.columns if col != 'Configuration']
        numeric_cols = sorted(all_numeric_cols, key=lambda x: metric_order.get(x, 999))
        
        # Enhanced normalization that respects optimization direction
        normalized_df = self._normalize_data_with_direction(data, numeric_cols, metric_directions)
        
        # Modern color palette - gradient from best to worst
        n_configs = len(data)
        
        # Create sophisticated color gradients
        top_colors = plt.cm.plasma(np.linspace(0.1, 0.4, min(3, n_configs)))
        mid_colors = plt.cm.viridis(np.linspace(0.3, 0.7, max(0, min(7, n_configs-3))))
        bottom_colors = plt.cm.cividis(np.linspace(0.5, 0.9, max(0, n_configs-10)))
        
        all_colors = np.vstack([top_colors, mid_colors, bottom_colors]) if n_configs > 10 else np.vstack([top_colors, mid_colors])
        
        # Store color mapping for custom legend
        color_mapping = []
        
        # Modern background styling
        ax.set_facecolor('#fafafa')
        ax.figure.patch.set_facecolor('#ffffff')
        
        # Remove spines for clean look
        ax.spines['top'].set_visible(False)
        ax.spines['right'].set_visible(False)
        ax.spines['bottom'].set_color('#e0e0e0')
        ax.spines['left'].set_color('#e0e0e0')
        
        # Modern grid styling
        ax.grid(True, alpha=0.3, linewidth=0.8, color='#cccccc')
        ax.set_axisbelow(True)
        
        # Plot each configuration with premium styling
        for i, (idx, row) in enumerate(normalized_df.iterrows()):
            rank = data.iloc[i]['Configuration'].split('#')[1]
            rank = int(rank)
            
            values = [row[col] for col in numeric_cols]
            x_positions = range(len(numeric_cols))
            
            color = all_colors[min(i, len(all_colors)-1)]
            
            # Different styling based on ranking
            if rank <= 3:  # Top 3 - premium styling with emphasis
                ax.plot(x_positions, values, 'o-', color=color, linewidth=3.5, 
                       markersize=8, alpha=0.9, 
                       zorder=10, markeredgewidth=2, markeredgecolor='white',
                       solid_capstyle='round')
                
                # Store color mapping for custom legend
                emoji = ["🥇","🥈","🥉"][rank-1]
                color_mapping.append({
                    'config': f"Config #{rank}",
                    'color': color,
                    'emoji': emoji,
                    'rank': rank,
                    'priority': 'top'
                })
                
            elif rank <= 10:  # Top 10 - elevated styling  
                ax.plot(x_positions, values, 'o-', color=color, linewidth=2.5, 
                       markersize=6, alpha=0.8, zorder=5,
                       markeredgewidth=1, markeredgecolor='white',
                       solid_capstyle='round')
                
                color_mapping.append({
                    'config': f"Config #{rank}",
                    'color': color,
                    'emoji': '⭐',
                    'rank': rank,
                    'priority': 'high'
                })
                
            else:  # Remaining configs - subtle styling
                ax.plot(x_positions, values, 'o-', color=color, linewidth=1.8, 
                       markersize=4, alpha=0.6, zorder=1,
                       solid_capstyle='round')
                
                color_mapping.append({
                    'config': f"Config #{rank}",
                    'color': color,
                    'emoji': '',
                    'rank': rank,
                    'priority': 'normal'
                })
        
        # Modern axis styling
        ax.set_xticks(range(len(numeric_cols)))
        
        # Enhanced labels with direction indicators
        clean_labels = self._create_metric_labels(numeric_cols, metric_directions)
        ax.set_xticklabels(clean_labels, fontsize=11, fontweight='500', 
                          color='#2c3e50', rotation=0, ha='center')
        
        # Modern labels
        ax.set_ylabel('Performance Score', fontsize=14, fontweight='600', 
                     color='#2c3e50', labelpad=15)
        
        # NO matplotlib legend - we'll create a custom one
        
        # Add performance zones with colored backgrounds
        self._add_performance_zones(ax, len(numeric_cols))
        
        # Style tick parameters for modern look
        ax.tick_params(axis='y', labelsize=10, colors='#7f8c8d', 
                      width=0, length=0)
        ax.tick_params(axis='x', labelsize=11, colors='#2c3e50',
                      width=0, length=0, pad=10)
        
        # Return color mapping for custom legend
        return color_mapping
    
    def _normalize_data_with_direction(self, data: pd.DataFrame, 
                                     numeric_cols: List[str],
                                     metric_directions: Dict[str, str]) -> pd.DataFrame:
        """
        Normalize data respecting optimization direction for each metric.
        
        Args:
            data: Raw data DataFrame
            numeric_cols: List of numeric column names
            metric_directions: Mapping of metric names to optimization directions
            
        Returns:
            Normalized DataFrame where higher values always mean better performance
        """
        normalized_df = data.copy()
        
        print(f"Processing {len(data)} configurations with metrics in order: {numeric_cols}")
        print(f"Metric directions: {metric_directions}")
        
        for col in numeric_cols:
            values = data[col].values
            min_val = values.min()
            max_val = values.max()
            range_val = max_val - min_val
            direction = metric_directions.get(col, 'higher')
            
            print(f"Metric '{col}': min={min_val:.6f}, max={max_val:.6f}, range={range_val:.6f}, direction={direction}")
            
            if range_val > 0:
                mean_val = values.mean()
                
                # Handle small differences with percentage-based scaling
                if range_val / abs(mean_val) < 0.01 if mean_val != 0 else range_val < 0.001:
                    normalized_values = (values - mean_val) / range_val
                    if direction == 'lower':
                        normalized_values = 0.5 - (normalized_values * 0.4)
                    else:
                        normalized_values = 0.5 + (normalized_values * 0.4)
                else:
                    # Standard normalization with direction awareness
                    if direction == 'lower':
                        # For "lower is better", invert so lower values appear higher
                        normalized_values = 1 - ((values - min_val) / range_val)
                    else:
                        # For "higher is better", normal normalization
                        normalized_values = (values - min_val) / range_val
                
                normalized_df[col] = normalized_values
            else:
                # All values are the same
                normalized_df[col] = 0.5
        
        return normalized_df
    
    def _create_metric_labels(self, numeric_cols: List[str], 
                            metric_directions: Dict[str, str]) -> List[str]:
        """Create clean metric labels with direction indicators."""
        clean_labels = []
        
        for col in numeric_cols:
            direction = metric_directions.get(col, 'higher')
            direction_symbol = "↑" if direction == 'higher' else "↓"
            
            # Shorten long metric names for better display
            if len(col) > 15:
                words = col.split()
                if len(words) > 2:
                    clean_labels.append(f"{words[0]} {direction_symbol}\\n{words[1]}")
                else:
                    short_name = col[:12] + "..." if len(col) > 12 else col
                    clean_labels.append(f"{short_name} {direction_symbol}")
            else:
                clean_labels.append(f"{col} {direction_symbol}")
        
        return clean_labels
    
    def _add_performance_zones(self, ax, num_metrics: int):
        """Add subtle performance zones with colored backgrounds."""
        # Excellence zone (top 25%)
        ax.axhspan(0.75, 1.02, alpha=0.08, color='#27ae60', zorder=0)
        ax.text(num_metrics-0.5, 0.88, 'Excellence\\nZone', 
               ha='center', va='center', fontsize=9, fontweight='600',
               color='#27ae60', alpha=0.7)
        
        # Warning zone (bottom 25%)  
        ax.axhspan(-0.02, 0.25, alpha=0.08, color='#e74c3c', zorder=0)
        ax.text(num_metrics-0.5, 0.12, 'Needs\\nImprovement', 
               ha='center', va='center', fontsize=9, fontweight='600',
               color='#e74c3c', alpha=0.7)
    
    def create_rankings_chart(self, ax, rankings_data: pd.DataFrame):
        """Create a modern rankings comparison chart."""
        if rankings_data.empty:
            ax.text(0.5, 0.5, 'No ranking data available', ha='center', va='center',
                   transform=ax.transAxes, fontsize=16)
            return
        
        rankings_pivot = rankings_data.pivot(index='position', columns='metric_name', values='value')
        
        # Use modern color palette
        colors = ['#3b82f6', '#ef4444', '#10b981', '#f59e0b']
        rankings_pivot.plot(kind='bar', ax=ax, color=colors[:len(rankings_pivot.columns)], 
                           alpha=0.8, width=0.7)
        
        ax.set_title('System Metrics by Configuration Ranking', fontsize=11, fontweight='bold', pad=15)
        ax.set_xlabel('Configuration Position', fontsize=9)
        ax.set_ylabel('Metric Values', fontsize=9)
        ax.legend(bbox_to_anchor=(1.02, 1), loc='upper left', frameon=True, 
                 fancybox=True, shadow=True, fontsize=8)
        ax.grid(True, alpha=0.3)
        ax.tick_params(axis='x', rotation=0, labelsize=8)
        ax.tick_params(axis='y', labelsize=8)
