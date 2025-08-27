import React, { useState, useEffect, useRef } from 'react';
import { ChevronRight, ChevronDown, Settings, Activity, Layers, Code, BarChart3, Zap, AlertTriangle, Search, RotateCcw } from 'lucide-react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, AreaChart, Area } from 'recharts';
import './App.css';

// API base URL - dynamically detect the current host for Minikube compatibility
const API_BASE_URL = (() => {
  const { protocol, hostname, port } = window.location;
  
  if (hostname === 'localhost' && port === '3000') {
    return 'http://localhost:8080/api';
  }
  
  return `${protocol}//${hostname}${port ? ':' + port : ''}/api`;
})();

const MetricsCard = ({ title, value, unit, change, icon: Icon, color = "blue" }) => {
  return (
    <div className={`metrics-card ${color}`}>
      <div className="metrics-card-header">
        <div className="metrics-icon">
          <Icon size={20} />
        </div>
        <div className="metrics-title">{title}</div>
      </div>
      <div className="metrics-value">
        {value !== null ? `${value.toFixed(2)}${unit}` : 'N/A'}
      </div>
      {change && (
        <div className={`metrics-change ${change >= 0 ? 'positive' : 'negative'}`}>
          {change >= 0 ? '+' : ''}{change.toFixed(1)}%
        </div>
      )}
    </div>
  );
};

const MetricsChart = ({ data, title, yAxisLabel, color = "#8884d8" }) => {
  const formatTooltipValue = (value, name) => {
    if (name.toLowerCase().includes('time') || name.toLowerCase().includes('latency')) {
      return `${(value * 1000).toFixed(2)}ms`;
    }
    if (name.toLowerCase().includes('rate') && !name.toLowerCase().includes('error')) {
      return `${value.toFixed(2)} req/s`;
    }
    if (name.toLowerCase().includes('error')) {
      return `${(value * 100).toFixed(2)}%`;
    }
    return value.toFixed(2);
  };

  const formatXAxisTick = (tickItem) => {
    return new Date(tickItem).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
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
            />
            <YAxis 
              stroke="#9CA3AF"
              label={{ value: yAxisLabel, angle: -90, position: 'insideLeft' }}
            />
            <Tooltip 
              formatter={formatTooltipValue}
              labelFormatter={(label) => new Date(label).toLocaleString()}
              contentStyle={{ 
                backgroundColor: '#1F2937', 
                border: '1px solid #374151',
                borderRadius: '6px'
              }}
            />
            <Area 
              type="monotone" 
              dataKey="value" 
              stroke={color} 
              fill={color}
              fillOpacity={0.3}
              strokeWidth={2}
            />
          </AreaChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};

const ServiceMetricsPanel = ({ serviceName, onClose }) => {
  const [metrics, setMetrics] = useState(null);
  const [liveMetrics, setLiveMetrics] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [timeRange, setTimeRange] = useState(15); // minutes
  const intervalRef = useRef(null);

  const fetchMetrics = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/metrics/${serviceName}?minutes=${timeRange}`);
      if (!response.ok) throw new Error('Failed to fetch metrics');
      const data = await response.json();
      setMetrics(data);
    } catch (err) {
      setError(err.message);
      console.error('Failed to load metrics:', err);
    }
  };

  const fetchLiveMetrics = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/metrics/${serviceName}/live`);
      if (!response.ok) throw new Error('Failed to fetch live metrics');
      const data = await response.json();
      setLiveMetrics(data);
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

  const transformMetricsData = (timeSeriesArray) => {
    if (!timeSeriesArray || timeSeriesArray.length === 0) return [];
    
    // Combine all time series into a single dataset
    const allDataPoints = [];
    timeSeriesArray.forEach(series => {
      series.dataPoints.forEach(point => {
        allDataPoints.push({
          timestamp: point.timestamp,
          value: point.value
        });
      });
    });
    
    // Sort by timestamp and return
    return allDataPoints.sort((a, b) => a.timestamp - b.timestamp);
  };

  if (loading) {
    return (
      <div className="metrics-panel">
        <div className="metrics-header">
          <h3>Metrics for {serviceName}</h3>
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
          <h3>Metrics for {serviceName}</h3>
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
          <BarChart3 size={24} />
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
          value={liveMetrics.responseTime ? liveMetrics.responseTime * 1000 : null}
          unit="ms"
          icon={Zap}
          color="blue"
        />
        <MetricsCard
          title="Request Rate"
          value={liveMetrics.requestRate}
          unit=" req/s"
          icon={Activity}
          color="green"
        />
        <MetricsCard
          title="Error Rate"
          value={liveMetrics.errorRate ? liveMetrics.errorRate * 100 : null}
          unit="%"
          icon={AlertTriangle}
          color={liveMetrics.errorRate > 0.01 ? "red" : "green"}
        />
      </div>

      {/* Historical Charts */}
      <div className="metrics-charts">
        {metrics?.responseTime && (
          <MetricsChart
            data={transformMetricsData(metrics.responseTime)}
            title="Response Time (95th percentile)"
            yAxisLabel="Time (s)"
            color="#3B82F6"
          />
        )}
        
        {metrics?.requestRate && (
          <MetricsChart
            data={transformMetricsData(metrics.requestRate)}
            title="Request Rate"
            yAxisLabel="Requests/sec"
            color="#10B981"
          />
        )}
        
        {metrics?.errorRate && (
          <MetricsChart
            data={transformMetricsData(metrics.errorRate)}
            title="Error Rate"
            yAxisLabel="Error %"
            color="#EF4444"
          />
        )}
        
        {metrics?.cpuUsage && (
          <MetricsChart
            data={transformMetricsData(metrics.cpuUsage)}
            title="CPU Usage"
            yAxisLabel="CPU %"
            color="#F59E0B"
          />
        )}
        
        {metrics?.memoryUsage && (
          <MetricsChart
            data={transformMetricsData(metrics.memoryUsage)}
            title="Memory Usage"
            yAxisLabel="Memory (bytes)"
            color="#8B5CF6"
          />
        )}
      </div>
    </div>
  );
};

const ServiceOverviewCard = ({ serviceName, serviceConfig, onServiceClick }) => {
  const classCount = Object.keys(serviceConfig.classes).length;
  const methodCount = Object.values(serviceConfig.classes).reduce(
    (total, classConfig) => total + Object.keys(classConfig.methods).length, 0
  );
  
  // Count methods by behavior status
  const behaviorStats = Object.values(serviceConfig.classes).reduce((stats, classConfig) => {
    Object.values(classConfig.methods).forEach(method => {
      if (method.currentBehaviourId === method.defaultBehaviourId) {
        stats.normal++;
      } else {
        stats.modified++;
      }
    });
    return stats;
  }, { normal: 0, modified: 0 });

  return (
    <div 
      className="service-card"
      onClick={() => onServiceClick(serviceName)}
    >
      <div className="service-header">
        <div className="service-info">
          <div className="service-icon">
            <Activity size={24} />
          </div>
          <div>
            <h3>{serviceName}</h3>
            <p>Microservice Configuration</p>
          </div>
        </div>
        <ChevronRight size={20} />
      </div>
      
      <div className="service-stats">
        <div className="stat">
          <div className="stat-number">{classCount}</div>
          <div className="stat-label">
            <Layers size={12} />
            Classes
          </div>
        </div>
        <div className="stat">
          <div className="stat-number">{methodCount}</div>
          <div className="stat-label">
            <Code size={12} />
            Methods
          </div>
        </div>
        <div className="stat">
          <div className="stat-number modified">{behaviorStats.modified}</div>
          <div className="stat-label">
            <Settings size={12} />
            Modified
          </div>
        </div>
      </div>
    </div>
  );
};

const BehaviorBadge = ({ behavior, isDefault, isCurrent }) => {
  return (
    <span className={`badge ${behavior.toLowerCase()} ${isCurrent ? 'current' : ''}`}>
      {behavior}
      {isDefault && <span className="default-indicator">(default)</span>}
    </span>
  );
};

const MethodConfigRow = ({ method, onBehaviorChange }) => {
  const [isExpanded, setIsExpanded] = useState(false);

  return (
    <div className="method-config">
      <div 
        className="method-header"
        onClick={() => setIsExpanded(!isExpanded)}
      >
        <div className="method-info">
          {isExpanded ? <ChevronDown size={16} /> : <ChevronRight size={16} />}
          <Code size={16} />
          <span>{method.methodName}</span>
        </div>
        <BehaviorBadge 
          behavior={method.currentBehaviourId} 
          isCurrent={true}
        />
      </div>
      
      {isExpanded && (
        <div className="method-details">
          <div className="behavior-selector">
            <label>Current Behavior</label>
            <select 
              value={method.currentBehaviourId}
              onChange={(e) => onBehaviorChange(method.methodName, e.target.value)}
            >
              {method.availableBehaviourIds.map(behaviorId => (
                <option key={behaviorId} value={behaviorId}>
                  {behaviorId} {behaviorId === method.defaultBehaviourId ? '(default)' : ''}
                </option>
              ))}
            </select>
          </div>
          
          <div className="available-behaviors">
            <span className="label">Available Behaviors:</span>
            <div className="behavior-list">
              {method.availableBehaviourIds.map(behaviorId => (
                <BehaviorBadge
                  key={behaviorId}
                  behavior={behaviorId}
                  isDefault={behaviorId === method.defaultBehaviourId}
                  isCurrent={behaviorId === method.currentBehaviourId}
                />
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

const ClassConfigSection = ({ classConfig, onBehaviorChange }) => {
  const [isExpanded, setIsExpanded] = useState(true);
  const methodCount = Object.keys(classConfig.methods).length;

  return (
    <div className="class-config">
      <div 
        className="class-header"
        onClick={() => setIsExpanded(!isExpanded)}
      >
        <div className="class-info">
          {isExpanded ? <ChevronDown size={20} /> : <ChevronRight size={20} />}
          <Layers size={20} />
          <div>
            <h4>{classConfig.className}</h4>
            <p>{methodCount} method{methodCount !== 1 ? 's' : ''}</p>
          </div>
        </div>
      </div>
      
      {isExpanded && (
        <div className="class-methods">
          {Object.values(classConfig.methods).map(method => (
            <MethodConfigRow
              key={method.methodName}
              method={method}
              onBehaviorChange={(methodName, newBehavior) => 
                onBehaviorChange(classConfig.className, methodName, newBehavior)
              }
            />
          ))}
        </div>
      )}
    </div>
  );
};

const ServiceDetailView = ({ serviceName, serviceConfig, onBack, onReset, onBehaviorChange }) => {
  const [showMetrics, setShowMetrics] = useState(false);
  const classCount = Object.keys(serviceConfig.classes).length;
  const methodCount = Object.values(serviceConfig.classes).reduce(
    (total, classConfig) => total + Object.keys(classConfig.methods).length, 0
  );

  return (
    <div className="service-detail">
      <div className="service-detail-header">
        <div className="header-content">
          <div className="navigation">
            <button onClick={onBack} className="back-button">
              ← Back to Services
            </button>
            <div className="service-title">
              <div className="service-icon">
                <Activity size={24} />
              </div>
              <div>
                <h1>{serviceName}</h1>
                <p>{classCount} classes, {methodCount} methods</p>
              </div>
            </div>
          </div>
          <div className="service-actions">
            <button 
              onClick={onReset}
              className="reset-button"
              title="Reset to template configuration"
            >
              <RotateCcw size={20} />
              Reset to Template
            </button>
            <button 
              onClick={() => setShowMetrics(!showMetrics)}
              className={`metrics-toggle ${showMetrics ? 'active' : ''}`}
            >
              <BarChart3 size={20} />
              {showMetrics ? 'Hide Metrics' : 'Show Metrics'}
            </button>
          </div>
        </div>
      </div>

      <div className="service-detail-content">
        <div className="service-detail-layout">
          <div className="service-config-section">
            <div className="classes-list">
              {Object.values(serviceConfig.classes).map(classConfig => (
                <ClassConfigSection
                  key={classConfig.className}
                  classConfig={classConfig}
                  onBehaviorChange={onBehaviorChange}
                />
              ))}
            </div>
          </div>
          
          {showMetrics && (
            <div className="service-metrics-section">
              <ServiceMetricsPanel 
                serviceName={serviceName}
                onClose={() => setShowMetrics(false)}
              />
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

// API functions to communicate with your Java backend
const fetchAllServices = async (refresh = false) => {
  const url = refresh ? `${API_BASE_URL}/services?refresh=true` : `${API_BASE_URL}/services`;
  const response = await fetch(url);
  if (!response.ok) throw new Error('Failed to fetch services');
  return response.json();
};

const triggerServiceDiscovery = async (fullRefresh = false) => {
  const response = await fetch(`${API_BASE_URL}/services/discover?fullRefresh=${fullRefresh}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    }
  });
  if (!response.ok) throw new Error('Failed to trigger discovery');
  return response.json();
};

const resetServiceToTemplate = async (serviceName) => {
  const response = await fetch(`${API_BASE_URL}/services/${serviceName}/reset`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    }
  });
  if (!response.ok) throw new Error('Failed to reset service');
  return response.text();
};

const updateMethodBehavior = async (serviceName, className, methodName, newBehavior) => {
  const response = await fetch(
    `${API_BASE_URL}/services/${serviceName}/changeBehaviour?className=${encodeURIComponent(className)}&methodName=${encodeURIComponent(methodName)}&behaviourId=${encodeURIComponent(newBehavior)}`,
    {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      }
    }
  );
  
  if (!response.ok) throw new Error('Failed to update behavior');
  return response.text();
};

const MarionetteControlPanel = () => {
  const [servicesData, setServicesData] = useState(null);
  const [selectedService, setSelectedService] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isDiscovering, setIsDiscovering] = useState(false);

  // Load data from your Java API when component mounts
  useEffect(() => {
    const loadServices = async () => {
      try {
        setLoading(true);
        const data = await fetchAllServices();
        setServicesData(data);
      } catch (err) {
        setError(err.message);
        console.error('Failed to load services:', err);
      } finally {
        setLoading(false);
      }
    };

    loadServices();
  }, []);

  const handleRefreshServices = async (fullRefresh = false) => {
    try {
      setIsDiscovering(true);
      
      if (fullRefresh) {
        // Trigger full discovery
        await triggerServiceDiscovery(true);
        // Wait a bit for discovery to complete, then refresh
        setTimeout(async () => {
          const data = await fetchAllServices(true);
          setServicesData(data);
          setIsDiscovering(false);
        }, 3000);
      } else {
        // Quick refresh
        const data = await fetchAllServices(true);
        setServicesData(data);
        setIsDiscovering(false);
      }
    } catch (err) {
      setError(err.message);
      setIsDiscovering(false);
    }
  };

  const handleResetService = async (serviceName) => {
    try {
      await resetServiceToTemplate(serviceName);
      // Refresh the data to show the reset
      const data = await fetchAllServices(true);
      setServicesData(data);
      alert(`Successfully reset ${serviceName} to template configuration`);
    } catch (err) {
      alert(`Failed to reset service: ${err.message}`);
    }
  };

  const handleServiceClick = (serviceName) => {
    setSelectedService(serviceName);
  };

  const handleBack = () => {
    setSelectedService(null);
  };

  const handleBehaviorChange = async (serviceName, className, methodName, newBehavior) => {
    try {
      // Optimistically update the UI
      setServicesData(prev => ({
        ...prev,
        services: {
          ...prev.services,
          [serviceName]: {
            ...prev.services[serviceName],
            classes: {
              ...prev.services[serviceName].classes,
              [className]: {
                ...prev.services[serviceName].classes[className],
                methods: {
                  ...prev.services[serviceName].classes[className].methods,
                  [methodName]: {
                    ...prev.services[serviceName].classes[className].methods[methodName],
                    currentBehaviourId: newBehavior
                  }
                }
              }
            }
          }
        }
      }));

      // Send the change to your Java backend
      await updateMethodBehavior(serviceName, className, methodName, newBehavior);
      
      console.log(`Successfully updated ${serviceName}.${className}.${methodName} to ${newBehavior}`);
    } catch (err) {
      console.error('Failed to update behavior:', err);
      // You might want to revert the UI change here or show an error message
      alert('Failed to update behavior: ' + err.message);
      // Refresh to get the correct state
      const data = await fetchAllServices();
      setServicesData(data);
    }
  };

  const formatTimestamp = (timestamp) => {
    if (!timestamp) return 'Never';
    return new Date(timestamp).toLocaleString();
  };

  if (loading) {
    return (
      <div className="loading">
        <div className="loading-content">
          <div className="spinner"></div>
          <p>Loading services...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="error">
        <div className="error-content">
          <div className="error-message">Error loading services</div>
          <p>{error}</p>
          <button onClick={() => handleRefreshServices(false)} className="retry-button">
            Retry
          </button>
        </div>
      </div>
    );
  }

  if (selectedService && servicesData?.services) {
    return (
      <ServiceDetailView
        serviceName={selectedService}
        serviceConfig={servicesData.services[selectedService]}
        onBack={handleBack}
        onReset={() => handleResetService(selectedService)}
        onBehaviorChange={(className, methodName, newBehavior) =>
          handleBehaviorChange(selectedService, className, methodName, newBehavior)
        }
      />
    );
  }

  return (
    <div className="app">
      <div className="app-header">
        <div className="header-content">
          <div className="app-title">
            <Settings size={32} />
            <div>
              <h1>Marionette Control Panel</h1>
              <p>Manage microservice behavior configurations with real-time metrics</p>
            </div>
          </div>
          <div className="app-actions">
            <button 
              onClick={() => handleRefreshServices(false)}
              className="refresh-button"
              disabled={isDiscovering}
            >
              <Activity size={20} />
              {isDiscovering ? 'Refreshing...' : 'Refresh'}
            </button>
            <button 
              onClick={() => handleRefreshServices(true)}
              className="discover-button"
              disabled={isDiscovering}
            >
              <Search size={20} />
              {isDiscovering ? 'Discovering...' : 'Full Discovery'}
            </button>
          </div>
        </div>
      </div>

      <div className="app-content">
        <div className="services-section">
          <div className="services-header">
            <div>
              <h2>Services Overview</h2>
              <p>Click on a service to configure its behavior settings and view metrics</p>
            </div>
            <div className="discovery-status">
              <div className="status-item">
                <span className="label">Total Services:</span>
                <span className="value">{servicesData?.totalServices || 0}</span>
              </div>
              <div className="status-item">
                <span className="label">Unavailable:</span>
                <span className={`value ${servicesData?.unavailableServices > 0 ? 'warning' : ''}`}>
                  {servicesData?.unavailableServices || 0}
                </span>
              </div>
              <div className="status-item">
                <span className="label">Last Discovery:</span>
                <span className="value">{formatTimestamp(servicesData?.lastDiscovery)}</span>
              </div>
            </div>
          </div>
        </div>

        <div className="services-grid">
          {servicesData?.services && Object.entries(servicesData.services).map(([serviceName, serviceConfig]) => (
            <ServiceOverviewCard
              key={serviceName}
              serviceName={serviceName}
              serviceConfig={serviceConfig}
              onServiceClick={handleServiceClick}
            />
          ))}
        </div>
        
        {(!servicesData?.services || Object.keys(servicesData.services).length === 0) && (
          <div className="no-services">
            <Activity size={48} />
            <h3>No Services Found</h3>
            <p>No marionette-enabled services were discovered in your cluster.</p>
            <button onClick={() => handleRefreshServices(true)} className="discover-button">
              <Search size={20} />
              Discover Services
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default MarionetteControlPanel;