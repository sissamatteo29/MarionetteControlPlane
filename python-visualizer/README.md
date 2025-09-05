# Marionette Experiment Analyzer

A modern GUI application for analyzing A/B test experiment results with advanced visualization capabilities.

## Features

- **Modern UI Design**: Clean, contemporary interface with professional styling
- **Parallel Coordinates Visualization**: Advanced chart showing all system configurations
- **Direction-Aware Normalization**: Respects metric optimization goals (higher/lower is better)
- **Metric Ordering**: Displays metrics according to importance defined in configuration
- **Interactive Analysis**: Multiple tabs for different analysis perspectives
- **Data Summary**: Real-time statistics and configuration overview

## Project Structure

```
python-visualizer/
├── main.py                    # Application entry point
├── requirements.txt           # Python dependencies
├── README.md                 # This file
└── src/
    ├── __init__.py
    ├── data/                 # Data processing modules
    │   ├── __init__.py
    │   └── processor.py      # Data loading and transformation
    ├── charts/               # Chart generation modules
    │   ├── __init__.py
    │   └── generator.py      # Chart creation and styling
    ├── gui/                  # GUI components
    │   ├── __init__.py
    │   ├── main_window.py    # Main application window
    │   ├── components.py     # UI components
    │   └── styles.py         # Styling and theming
    └── utils/                # Utility functions
        ├── __init__.py
        └── helpers.py        # Helper functions
```

## Installation

1. **Install Python dependencies**:
   ```bash
   pip install -r requirements.txt
   ```

2. **Activate your virtual environment** (if using one):
   ```bash
   source ~/python-user-venv/bin/activate
   ```

## Usage

1. **Run the application**:
   ```bash
   python main.py
   ```

2. **Load experiment data**:
   - Click "Load Experiment Data" in the sidebar
   - Select your JSON results file
   - The application will automatically process and visualize the data

3. **Analyze results**:
   - **Overview tab**: Parallel coordinates plot comparing all configurations
   - **Rankings tab**: Detailed ranking analysis
   - **Trends tab**: Trend analysis (coming soon)
   - **Raw Data tab**: View raw experiment data

## Data Format

The application expects JSON data with the following structure:

```json
{
  "metricConfigs": [
    {
      "metricName": "JVM Heap Memory",
      "order": 1,
      "unit": "bytes", 
      "direction": "lower"
    }
  ],
  "ranking": [
    {
      "position": 1,
      "systemResults": [
        {
          "metricName": "JVM Heap Memory",
          "value": 12345.67
        }
      ],
      "systemConfig": [...]
    }
  ]
}
```

## Key Features

### Direction-Aware Normalization
- Metrics with `"direction": "lower"` are inverted so lower values appear higher on charts
- Metrics with `"direction": "higher"` use standard normalization
- All charts consistently show "higher = better performance"

### Metric Ordering
- Metrics are displayed according to their `"order"` parameter
- Most important metrics appear first (leftmost in parallel coordinates)

### Modern Design
- Contemporary glassmorphism-style cards
- Professional color schemes and typography
- Responsive layout with scrollable content
- Interactive hover effects and modern styling

## Module Overview

- **`data.processor`**: Handles data loading, validation, and transformation
- **`charts.generator`**: Creates all visualizations with modern styling
- **`gui.main_window`**: Main application logic and window management  
- **`gui.components`**: Individual UI components (sidebar, tabs, etc.)
- **`gui.styles`**: Styling utilities and theme management
- **`utils.helpers`**: Common utility functions

## Contributing

This is a modular design that makes it easy to:
- Add new chart types in `charts/generator.py`
- Create new GUI components in `gui/components.py`
- Extend data processing in `data/processor.py`
- Add utility functions in `utils/helpers.py`

## Dependencies

- **pandas**: Data manipulation and analysis
- **matplotlib**: Plotting and visualization
- **seaborn**: Statistical visualization enhancement
- **numpy**: Numerical computations
- **tkinter**: GUI framework (included with Python)
