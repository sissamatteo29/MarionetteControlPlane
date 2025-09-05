#!/bin/bash
# Startup script for the Marionette Experiment Analyzer

echo "🔬 Starting Marionette Experiment Analyzer..."
echo "📁 Project: python-visualizer"
echo ""

# Check if virtual environment is activated
if [[ "$VIRTUAL_ENV" == "" ]]; then
    echo "⚠️  Virtual environment not detected."
    echo "💡 Activating virtual environment..."
    source ~/python-user-venv/bin/activate
    if [[ $? -eq 0 ]]; then
        echo "✅ Virtual environment activated"
    else
        echo "❌ Failed to activate virtual environment"
        echo "Please ensure ~/python-user-venv exists or activate manually"
        exit 1
    fi
else
    echo "✅ Virtual environment already active: $VIRTUAL_ENV"
fi

echo ""
echo "🚀 Launching application..."
python main.py
