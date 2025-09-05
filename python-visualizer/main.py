#!/usr/bin/env python3
"""
Main entry point for the Marionette Experiment Analyzer.
Modern GUI for analyzing A/B test experiment results.
"""

import tkinter as tk
from src.gui.main_window import ModernExperimentAnalyzer


def main():
    """Initialize and run the application."""
    root = tk.Tk()
    app = ModernExperimentAnalyzer(root)
    root.mainloop()


if __name__ == "__main__":
    main()
