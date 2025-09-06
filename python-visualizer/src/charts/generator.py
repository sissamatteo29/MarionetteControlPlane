"""
Simplified chart generation module with better sizing and reduced complexity.
Focuses on essential functionality while maintaining visual quality.
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
    """Simplified chart generator with essential functionality."""
    
    def __init__(self):
        pass
    
    def create_parallel_coordinates_chart(self, ax, data: pd.DataFrame, 
                                        metric_directions: Dict[str, str],
                                        metric_order: Dict[str, int]):
        """
        Create a parallel coordinates plot optimized for full-width display.
        
        Args:
            ax: Matplotlib axis object
            data: DataFrame with configuration data
            metric_directions: Mapping of metric names to optimization directions
            metric_order: Mapping of metric names to display order
            
        Returns:
            List[Dict]: Configuration color mapping information
        """
        if data.empty:
            ax.text(0.5, 0.5, 'No data available', ha='center', va='center',
                   transform=ax.transAxes, fontsize=16)
            return []
        
        # Sort metrics according to their defined order
        all_numeric_cols = [col for col in data.columns if col != 'Configuration']
        numeric_cols = sorted(all_numeric_cols, key=lambda x: metric_order.get(x, 999))
        
        # Normalize data with direction awareness
        normalized_df = self._normalize_data_with_direction(data, numeric_cols, metric_directions)
        
        # Create color palette - simplified approach
        n_configs = len(data)
        colors = plt.cm.viridis(np.linspace(0.1, 0.9, n_configs))
        
        # Store color mapping for legend
        color_mapping = []
        
        # Clean chart styling
        ax.set_facecolor('#fafafa')
        ax.grid(True, alpha=0.3, linewidth=0.8, color='#cccccc')
        ax.set_axisbelow(True)
        
        # Remove unnecessary spines
        for spine in ax.spines.values():
            spine.set_visible(False)
        
        # Plot each configuration
        for i, (idx, row) in enumerate(normalized_df.iterrows()):
            rank = int(data.iloc[i]['Configuration'].split('#')[1])
            values = [row[col] for col in numeric_cols]
            x_positions = range(len(numeric_cols))
            
            color = colors[i]
            
            # Style based on ranking
            if rank <= 3:  # Top 3
                linewidth = 3.5
                markersize = 8
                alpha = 0.9
                zorder = 10
                emoji = ["🥇","🥈","🥉"][rank-1]
                priority = 'top'
            elif rank <= 10:  # Top 10
                linewidth = 2.5
                markersize = 6
                alpha = 0.8
                zorder = 5
                emoji = '⭐'
                priority = 'high'
            else:  # Others
                linewidth = 1.8
                markersize = 4
                alpha = 0.6
                zorder = 1
                emoji = ''
                priority = 'normal'
            
            ax.plot(x_positions, values, 'o-', color=color, 
                   linewidth=linewidth, markersize=markersize, alpha=alpha, 
                   zorder=zorder, markeredgewidth=1, markeredgecolor='white')
            
            color_mapping.append({
                'config': f"Config #{rank}",
                'color': color,
                'emoji': emoji,
                'rank': rank,
                'priority': priority
            })
        
        # Configure axes
        ax.set_xticks(range(len(numeric_cols)))
        
        # Create clean metric labels
        clean_labels = []
        for col in numeric_cols:
            direction = metric_directions.get(col, 'higher')
            direction_symbol = "↑" if direction == 'higher' else "↓"
            
            # Simplify long names
            if len(col) > 15:
                short_name = col[:12] + "..."
                clean_labels.append(f"{short_name} {direction_symbol}")
            else:
                clean_labels.append(f"{col} {direction_symbol}")
        
        ax.set_xticklabels(clean_labels, fontsize=11, fontweight='500', 
                          color='#2c3e50', rotation=0, ha='center')
        
        ax.set_ylabel('Performance Score', fontsize=14, fontweight='600', 
                     color='#2c3e50')
        
        # Style ticks
        ax.tick_params(axis='y', labelsize=10, colors='#7f8c8d', 
                      width=0, length=0)
        ax.tick_params(axis='x', labelsize=11, colors='#2c3e50',
                      width=0, length=0, pad=10)
        
        # Add performance zones
        ax.axhspan(0.75, 1.02, alpha=0.08, color='#27ae60', zorder=0)
        ax.axhspan(-0.02, 0.25, alpha=0.08, color='#e74c3c', zorder=0)
        
        return color_mapping
    
    def _normalize_data_with_direction(self, data: pd.DataFrame, 
                                     numeric_cols: List[str],
                                     metric_directions: Dict[str, str]) -> pd.DataFrame:
        """Normalize data respecting optimization direction for each metric."""
        normalized_df = data.copy()
        
        for col in numeric_cols:
            values = data[col].values
            min_val = values.min()
            max_val = values.max()
            range_val = max_val - min_val
            direction = metric_directions.get(col, 'higher')
            
            if range_val > 0:
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
    
    def create_rankings_chart(self, ax, rankings_data: pd.DataFrame):
        """Create a simplified rankings comparison chart."""
        if rankings_data.empty:
            ax.text(0.5, 0.5, 'No ranking data available', ha='center', va='center',
                   transform=ax.transAxes, fontsize=16)
            return
        
        rankings_pivot = rankings_data.pivot(index='position', columns='metric_name', values='value')
        
        # Use clean color palette
        colors = ['#3b82f6', '#ef4444', '#10b981', '#f59e0b']
        rankings_pivot.plot(kind='bar', ax=ax, color=colors[:len(rankings_pivot.columns)], 
                           alpha=0.8, width=0.7)
        
        ax.set_title('System Metrics by Configuration Ranking', fontsize=14, fontweight='bold', pad=20)
        ax.set_xlabel('Configuration Position', fontsize=12)
        ax.set_ylabel('Metric Values', fontsize=12)
        ax.legend(bbox_to_anchor=(1.02, 1), loc='upper left', frameon=True)
        ax.grid(True, alpha=0.3)
        ax.tick_params(axis='x', rotation=0)
        
        # Clean up spines
        for spine in ax.spines.values():
            spine.set_color('#e0e0e0')
            spine.set_linewidth(0.5)