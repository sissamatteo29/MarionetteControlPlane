import React, { useState, useEffect, useRef } from 'react';
import { BarChart3, Zap, Activity, AlertTriangle, TrendingUp, TrendingDown, Minus } from 'lucide-react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, AreaChart, Area } from 'recharts';

// API base URL - dynamically detect the current host for Minikube compatibility
const API_BASE_URL = (() => {
  const { protocol, hostname, port } = window.location;
  
  if (hostname === 'localhost' && port === '3000') {
    return 'http://localhost:8080/api';
  }
  
  return `${protocol}//${hostname}${port ? ':' + port : ''}/api`;
})();

// Safe number formatting utility
const safeFormat = (value, decimals = 2, fallback = 'N/A') => {
  if (value === null || value === undefined || isNaN(value)) {
    return fallback;
  }
  return Number(value).toFixed(decimals);
};

// Safe percentage formatting
const safeFormatPercentage = (value, fallback = 'N/A') => {
  if (value === null || value === undefined || isNaN(value)) {
    return fallback;
  }
  return `${(Number(value) * 100).toFixed(1)}%`;
};

// Format bytes to human readable format
const formatBytes = (bytes) => {
  if (bytes === null || bytes === undefined || isNaN(bytes)) return 'N/A';
  
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  if (bytes === 0) return '0 B';
  
  const i = Math.floor(Math.log(bytes) / Math.log(1024));
  return `${(bytes / Math.pow(1024, i)).toFixed(1)} ${sizes[i]}`;
};

// Format milliseconds
const formatMilliseconds = (ms) => {
  if (ms === null || ms === undefined || isNaN(ms)) return 'N/A';
  
  if (ms < 1) return `${(ms * 1000).toFixed(0)}μs`;
  if (ms < 1000) return `${ms.toFixed(1)}ms`;
  return `${(ms / 1000).toFixed(2)}s`;
};

export const MetricsCard = ({ title, value, unit = '', change, icon: Icon, color = "blue", formatType = "number" }) => {
  const formatValue = () => {
    switch (formatType) {
      case 'percentage':
        return safeFormatPercentage(value);
      case 'bytes':
        return formatBytes(value);
      case 'milliseconds':
        return formatMilliseconds(value);
      case 'time':
        return value !== null && value !== undefined ? formatMilliseconds(value * 1000) : 'N/A';
      default:
        return value !== null && value !== undefined ? `${safeFormat(value)}${unit}` : 'N/A';
    }
  };

  const getTrendIcon = () => {
    if (change === null || change === undefined || isNaN(change)) return <Minus size={12} />;
    if (change > 0) return <TrendingUp size={12} />;
    if (change < 0) return <TrendingDown size={12} />;
    return <Minus size={12} />;
  };

  return (
    <div className={`metrics-card ${color}`}>
      <div className="metrics-card-header">
        <div className="metrics-icon">
          <Icon size={20} />
        </div>
        <div className="metrics-title">{title}</div>
      </div>
      <div className="metrics-value">
        {formatValue()}
      </div>
      {change !== null && change !== undefined && !isNaN(change) && (
        <div className={`metrics-change ${change >= 0 ? 'positive' : 'negative'}`}>
          {getTrendIcon()}
          {change >= 0 ? '+' : ''}{safeFormat(change, 1)}%
        </div>
      )}
    </div>
  );
};

export const MetricsChart = ({ data, title, yAxisLabel, color = "#3b82f6", formatType = "number" }) => {
  if (!data || data.length === 0) {
    return (
      <div className="metrics-chart">
        <h4>{title}</h4>
        <div className="no-data">
          <p>No data available</p>
        </div>
      </div>
    );
  }

  const formatTooltipValue = (value, name) => {
    if (value === null || value === undefined || isNaN(value)) return 'N/A';
    
    switch (formatType) {
      case 'time':
        return formatMilliseconds(value * 1000);
      case 'rate':
        return `${safeFormat(value)} req/s`;
      case 'percentage':
        return safeFormatPercentage(value);
      case 'bytes':
        return formatBytes(value);
      default:
        return safeFormat(value);
    }
  };

  const formatXAxisTick = (tickItem) => {
    return new Date(tickItem).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  const formatYAxisTick = (value) => {
    switch (formatType) {
      case 'bytes':
        return formatBytes(value);
      case 'percentage':
        return `${(value * 100).toFixed(0)}%`;
      default:
        return safeFormat(value, 1);
    }
  };

  return (
    <div className="metrics-chart">
      <h4>{title}</h4>
      <div style={{ width: '100%', height: 200 }}>
        <ResponsiveContainer>
          <AreaChart data={data}>
            <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
            <XAxis 
              dataKey="timestamp" 
              tickFormatter={formatXAxisTick}
              stroke="#9CA3AF"
              fontSize={12}
            />
            <YAxis 
              stroke="#9CA3AF"
              tickFormatter={formatYAxisTick}
              fontSize={12}
              width={60}
            />
            <Tooltip 
              formatter={formatTooltipValue}
              labelFormatter={(label) => new Date(label).toLocaleString()}
              contentStyle={{ 
                backgroundColor: '#1F2937', 
                border: '1px solid #374151',
                borderRadius: '6px',
                fontSize: '12px'
              }}
            />
            <Area 
              type="monotone" 
              dataKey="value" 
              stroke={color} 
              fill={color}
              fillOpacity={0.2}
              strokeWidth={2}
            />
          </AreaChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};

export const ServiceMetricsPanel = ({ serviceName, onClose }) => {
  const [metrics, setMetrics] = useState(null);
  const [liveMetrics, setLiveMetrics] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [timeRange, setTimeRange] = useState(15); // minutes
  const intervalRef = useRef(null);

  const fetchMetrics = async () => {
    try {
      console.log(`Fetching metrics for ${serviceName} with ${timeRange} minutes range`);
      const response = await fetch(`${API_BASE_URL}/metrics/${serviceName}?minutes=${timeRange}`);
      if (!response.ok) throw new Error(`Failed to fetch metrics: ${response.status}`);
      const rawData = await response.json();
      console.log('Raw metrics response:', rawData);
      console.log('Metrics object:', rawData.metrics);
      console.log('Metrics keys:', Object.keys(rawData.metrics || {}));
      
      // Your response has this structure: { serviceName, metrics: {...}, configurations: {...}, ... }
      // The actual time series data should be in rawData.metrics
      if (rawData.metrics) {
        setMetrics(rawData.metrics);
        console.log('Set metrics to:', rawData.metrics);
      } else {
        console.log('No metrics object found in response');
        setMetrics({});
      }
      
      setError(null);
    } catch (err) {
      setError(err.message);
      console.error('Failed to load metrics:', err);
    }
  };

  const fetchLiveMetrics = async () => {
    try {
      console.log(`Fetching live metrics for ${serviceName}`);
      const response = await fetch(`${API_BASE_URL}/metrics/${serviceName}/live`);
      if (!response.ok) throw new Error(`Failed to fetch live metrics: ${response.status}`);
      const data = await response.json();
      console.log('Live metrics response:', data);
      console.log('Live metrics object:', data.metrics);
      
      // Extract live metrics from the nested structure
      if (data.metrics) {
        setLiveMetrics(data.metrics);
        console.log('Set live metrics to:', data.metrics);
      } else {
        console.log('No metrics object found in live response');
        setLiveMetrics({});
      }
    } catch (err) {
      console.error('Failed to load live metrics:', err);
    }
  };

  useEffect(() => {
    const loadInitialData = async () => {
      setLoading(true);
      await Promise.all([fetchMetrics(), fetchLiveMetrics()]);
      setLoading(false);
    };

    loadInitialData();

    // Set up periodic updates for live metrics
    intervalRef.current = setInterval(fetchLiveMetrics, 30000); // Update every 30 seconds

    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    };
  }, [serviceName, timeRange]);

  // Transform time series data from your backend format
  const transformMetricsData = (timeSeriesData) => {
    if (!timeSeriesData || timeSeriesData.length === 0) return [];
    
    console.log('Transforming data:', timeSeriesData);
    
    // Handle both array of time series and single time series
    const seriesArray = Array.isArray(timeSeriesData) ? timeSeriesData : [timeSeriesData];
    
    const allDataPoints = [];
    seriesArray.forEach(series => {
      if (series && series.dataPoints && Array.isArray(series.dataPoints)) {
        series.dataPoints.forEach(point => {
          if (point && point.timestamp && point.value !== null && point.value !== undefined) {
            allDataPoints.push({
              timestamp: point.timestamp, // Your timestamps are already in milliseconds
              value: Number(point.value)
            });
          }
        });
      }
    });
    
    // Sort by timestamp and return
    return allDataPoints.sort((a, b) => a.timestamp - b.timestamp);
  };

  // Process the raw metrics response from your backend
  const processMetricsResponse = (rawMetrics) => {
    if (!Array.isArray(rawMetrics) || rawMetrics.length === 0) return {};
    
    console.log('Processing raw metrics:', rawMetrics);
    
    // Since your backend returns an array of time series without specific metric type identifiers,
    // we'll need to infer or assign them based on the data patterns
    const processedMetrics = {};
    
    rawMetrics.forEach((series, index) => {
      if (series && series.dataPoints && Array.isArray(series.dataPoints)) {
        // Try to infer metric type based on values or assign generic names
        let metricKey;
        const hasNonZeroValues = series.dataPoints.some(point => point.value !== 0);
        const avgValue = series.dataPoints.reduce((sum, point) => sum + point.value, 0) / series.dataPoints.length;
        
        // Simple heuristic to assign metric types based on value patterns
        if (avgValue === 0 && !hasNonZeroValues) {
          metricKey = `metric_zero_${index}`;
        } else if (avgValue < 0.1) {
          metricKey = `metric_small_${index}`;
        } else if (avgValue > 1000000) {
          metricKey = `memory_metric_${index}`;
        } else {
          metricKey = `metric_${index}`;
        }
        
        processedMetrics[metricKey] = [series];
      }
    });
    
    return processedMetrics;
  };

  // Extract current values from live metrics
  const getCurrentValue = (metricName) => {
    if (!liveMetrics || !liveMetrics[metricName]) return null;
    
    const metric = liveMetrics[metricName];
    if (Array.isArray(metric) && metric.length > 0) {
      return metric[0].value;
    }
    if (typeof metric === 'object' && metric.value !== undefined) {
      return metric.value;
    }
    if (typeof metric === 'number') {
      return metric;
    }
    return null;
  };

  if (loading) {
    return (
      <div className="metrics-panel">
        <div className="metrics-header">
          <h3>
            <BarChart3 size={20} />
            Metrics for {serviceName}
          </h3>
          <button onClick={onClose} className="close-button">×</button>
        </div>
        <div className="loading-metrics">
          <div className="spinner"></div>
          <p>Loading metrics...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="metrics-panel">
        <div className="metrics-header">
          <h3>
            <BarChart3 size={20} />
            Metrics for {serviceName}
          </h3>
          <button onClick={onClose} className="close-button">×</button>
        </div>
        <div className="error-metrics">
          <AlertTriangle size={24} />
          <p>Failed to load metrics: {error}</p>
          <button onClick={fetchMetrics} className="retry-button">Retry</button>
        </div>
      </div>
    );
  }

  return (
    <div className="metrics-panel">
      <div className="metrics-header">
        <h3>
          <BarChart3 size={20} />
          Metrics for {serviceName}
        </h3>
        <div className="metrics-controls">
          <select 
            value={timeRange} 
            onChange={(e) => setTimeRange(parseInt(e.target.value))}
            className="time-range-select"
          >
            <option value={5}>Last 5 minutes</option>
            <option value={15}>Last 15 minutes</option>
            <option value={30}>Last 30 minutes</option>
            <option value={60}>Last 1 hour</option>
          </select>
          <button onClick={onClose} className="close-button">×</button>
        </div>
      </div>

      {/* Live Metrics Cards */}
      <div className="live-metrics">
        <MetricsCard
          title="Response Time"
          value={getCurrentValue('responseTime')}
          icon={Zap}
          color="blue"
          formatType="time"
        />
        <MetricsCard
          title="Request Rate"
          value={getCurrentValue('requestRate')}
          unit=" req/s"
          icon={Activity}
          color="green"
        />
        <MetricsCard
          title="Memory Usage"
          value={getCurrentValue('jvm_memory')}
          icon={Activity}
          color="purple"
          formatType="bytes"
        />
        <MetricsCard
          title="Error Rate"
          value={getCurrentValue('errorRate')}
          icon={AlertTriangle}
          color={(() => {
            const errorRate = getCurrentValue('errorRate');
            if (errorRate === null || errorRate === undefined) return "gray";
            return errorRate > 0.01 ? "red" : "green";
          })()}
          formatType="percentage"
        />
      </div>

      {/* Historical Charts */}
      <div className="metrics-charts">
        {metrics && Object.keys(metrics).length > 0 ? (
          Object.entries(metrics).map(([metricKey, metricData]) => {
            console.log(`Processing metric: ${metricKey}`, metricData);
            
            // Handle different possible data structures
            let chartData = [];
            
            if (Array.isArray(metricData)) {
              // If it's an array of time series objects
              chartData = transformMetricsData(metricData);
            } else if (metricData && typeof metricData === 'object' && metricData.dataPoints) {
              // If it's a single time series object
              chartData = transformMetricsData([metricData]);
            } else if (metricData && typeof metricData === 'object' && Object.keys(metricData).length > 0) {
              // If it's an object with nested time series
              const firstKey = Object.keys(metricData)[0];
              if (Array.isArray(metricData[firstKey])) {
                chartData = transformMetricsData(metricData[firstKey]);
              }
            }
            
            console.log(`Chart data for ${metricKey}:`, chartData);
            
            if (chartData.length === 0) {
              console.log(`No chart data for ${metricKey}, skipping`);
              return null;
            }
            
            // Determine chart properties based on metric key and data
            let title, color, formatType;
            const avgValue = chartData.reduce((sum, point) => sum + point.value, 0) / chartData.length;
            
            // Better metric type detection based on key names and values
            if (metricKey.toLowerCase().includes('memory') || metricKey.toLowerCase().includes('jvm_memory')) {
              title = 'Memory Usage';
              color = '#8B5CF6';
              formatType = 'bytes';
            } else if (metricKey.toLowerCase().includes('request') || metricKey.toLowerCase().includes('http')) {
              title = 'Request Rate';
              color = '#10B981';
              formatType = 'rate';
            } else if (metricKey.toLowerCase().includes('response') || metricKey.toLowerCase().includes('duration')) {
              title = 'Response Time';
              color = '#3B82F6';
              formatType = 'time';
            } else if (metricKey.toLowerCase().includes('error')) {
              title = 'Error Rate';
              color = '#EF4444';
              formatType = 'percentage';
            } else if (metricKey.toLowerCase().includes('cpu')) {
              title = 'CPU Usage';
              color = '#F59E0B';
              formatType = 'percentage';
            } else {
              // Generic naming based on metric key
              title = metricKey.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
              color = '#6366F1';
              formatType = avgValue > 1000000 ? 'bytes' : avgValue < 1 ? 'percentage' : 'number';
            }
            
            return (
              <MetricsChart
                key={metricKey}
                data={chartData}
                title={title}
                yAxisLabel={title}
                color={color}
                formatType={formatType}
              />
            );
          })
        ) : null}
      </div>

      {/* Show message if no charts are available */}
      {(!metrics || Object.keys(metrics).length === 0) && !loading && (
        <div className="no-metrics">
          <AlertTriangle size={48} />
          <h3>No Historical Data Available</h3>
          <p>No time-series metrics found for this service in the specified time range.</p>
          <p><strong>Debug Info:</strong></p>
          <p>Metrics object: {JSON.stringify(metrics, null, 2)}</p>
        </div>
      )}
    </div>
  );
};