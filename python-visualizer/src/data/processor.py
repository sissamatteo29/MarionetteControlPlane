"""
Data processing and management module for experiment results.
Handles loading, parsing, and transformation of experiment data.
"""

import json
import pandas as pd
from typing import Dict, List, Any, Optional


class DataProcessor:
    """Handles data loading and processing for experiment results."""
    
    def __init__(self):
        self.data = None
        self.df_rankings = None
        self.df_configs = None
        self.df_metrics = None
        
    def load_data_from_file(self, filepath: str) -> bool:
        """
        Load experiment data from JSON file.
        
        Args:
            filepath: Path to the JSON file containing experiment results
            
        Returns:
            bool: True if data loaded successfully, False otherwise
        """
        try:
            with open(filepath, 'r') as file:
                self.data = json.load(file)
                
            if not self._validate_data():
                return False
                
            self._process_data()
            return True
            
        except (FileNotFoundError, json.JSONDecodeError, KeyError) as e:
            print(f"Error loading data: {e}")
            return False
    
    def _validate_data(self) -> bool:
        """Validate that required data fields are present."""
        if not self.data:
            return False
            
        required_fields = ['metricConfigs', 'ranking']
        return all(field in self.data for field in required_fields)
    
    def _process_data(self):
        """Process raw data into structured DataFrames."""
        self._create_rankings_dataframe()
        self._create_configs_dataframe()
        self._create_metrics_dataframe()
    
    def _create_rankings_dataframe(self):
        """Create DataFrame for ranking data."""
        rankings_data = []
        
        for rank_entry in self.data['ranking']:
            position = rank_entry['position']
            
            # Process system metrics
            for metric in rank_entry['systemResults']:
                rankings_data.append({
                    'position': position,
                    'metric_name': metric['metricName'],
                    'value': metric['value'],
                    'type': 'system'
                })
                
        self.df_rankings = pd.DataFrame(rankings_data)
    
    def _create_configs_dataframe(self):
        """Create DataFrame for configuration data."""
        config_data = []
        
        for rank_entry in self.data['ranking']:
            position = rank_entry['position']
            
            # Count services and behaviors
            total_services = len(rank_entry['systemConfig'])
            total_behaviors = sum(
                len(service['classConfigs']) 
                for service in rank_entry['systemConfig']
            )
            
            config_data.append({
                'position': position,
                'total_services': total_services,
                'total_behaviors': total_behaviors
            })
            
        self.df_configs = pd.DataFrame(config_data)
    
    def _create_metrics_dataframe(self):
        """Create DataFrame for metrics configuration."""
        metrics_data = []
        
        for metric_config in self.data['metricConfigs']:
            metrics_data.append({
                'metric_name': metric_config['metricName'],
                'order': metric_config['order'],
                'unit': metric_config['unit'],
                'direction': metric_config['direction']
            })
            
        self.df_metrics = pd.DataFrame(metrics_data)
    
    def get_metric_direction(self, metric_name: str) -> str:
        """Get the optimization direction for a specific metric."""
        if self.df_metrics is None:
            return 'higher'
            
        metric_row = self.df_metrics[self.df_metrics['metric_name'] == metric_name]
        if metric_row.empty:
            return 'higher'
            
        return metric_row.iloc[0]['direction']
    
    def get_metric_order(self, metric_name: str) -> int:
        """Get the display order for a specific metric."""
        if self.df_metrics is None:
            return 999
            
        metric_row = self.df_metrics[self.df_metrics['metric_name'] == metric_name]
        if metric_row.empty:
            return 999
            
        return metric_row.iloc[0]['order']
    
    def get_parallel_coordinates_data(self) -> pd.DataFrame:
        """
        Prepare data for parallel coordinates visualization.
        
        Returns:
            DataFrame with configurations and their metric values
        """
        if not self.data:
            return pd.DataFrame()
            
        parallel_data = []
        
        for rank_entry in self.data['ranking']:
            row = {'Configuration': f"Config #{rank_entry['position']}"}
            
            # Add system metrics only
            for metric in rank_entry['systemResults']:
                row[metric['metricName']] = metric['value']
            
            parallel_data.append(row)
        
        return pd.DataFrame(parallel_data)
    
    def get_ordered_metrics(self) -> List[str]:
        """Get metric names sorted by their defined order."""
        if self.df_metrics is None:
            return []
            
        sorted_metrics = self.df_metrics.sort_values('order')
        return sorted_metrics['metric_name'].tolist()
    
    def get_summary_stats(self) -> Dict[str, Any]:
        """Get summary statistics for the experiment."""
        if not self.data:
            return {}
            
        return {
            'total_configurations': len(self.data['ranking']),
            'total_metrics': len(self.data['metricConfigs']),
            'best_config': self.data['ranking'][0]['position'] if self.data['ranking'] else None,
            'worst_config': self.data['ranking'][-1]['position'] if self.data['ranking'] else None
        }
