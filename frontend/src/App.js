import React, { useState, useEffect } from 'react';
import { ChevronRight, ChevronDown, Settings, Activity, Layers, Code } from 'lucide-react';
import './App.css';

// API base URL - update this to match your Spring Boot server
const API_BASE_URL = 'http://localhost:8080/api';

// API functions to communicate with your Java backend
const fetchAllServices = async () => {
  const response = await fetch(`${API_BASE_URL}/services`);
  if (!response.ok) throw new Error('Failed to fetch services');
  return response.json();
};

const updateMethodBehavior = async (serviceName, className, methodName, newBehavior) => {
  const response = await fetch(
    `${API_BASE_URL}/services/${serviceName}/classes/${className}/methods/${methodName}/behavior`,
    {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ newBehaviourId: newBehavior }),
    }
  );
  if (!response.ok) throw new Error('Failed to update behavior');
  return response.text();
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

const ServiceDetailView = ({ serviceName, serviceConfig, onBack, onBehaviorChange }) => {
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
        </div>
      </div>

      <div className="service-detail-content">
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
    </div>
  );
};

function App() {
  const [configRegistry, setConfigRegistry] = useState({});
  const [selectedService, setSelectedService] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Load data from your Java API when component mounts
  useEffect(() => {
    const loadServices = async () => {
      try {
        setLoading(true);
        const services = await fetchAllServices();
        setConfigRegistry(services);
      } catch (err) {
        setError(err.message);
        console.error('Failed to load services:', err);
      } finally {
        setLoading(false);
      }
    };

    loadServices();
  }, []);

  const handleServiceClick = (serviceName) => {
    setSelectedService(serviceName);
  };

  const handleBack = () => {
    setSelectedService(null);
  };

  const handleBehaviorChange = async (serviceName, className, methodName, newBehavior) => {
    try {
      // Optimistically update the UI
      setConfigRegistry(prev => ({
        ...prev,
        [serviceName]: {
          ...prev[serviceName],
          classes: {
            ...prev[serviceName].classes,
            [className]: {
              ...prev[serviceName].classes[className],
              methods: {
                ...prev[serviceName].classes[className].methods,
                [methodName]: {
                  ...prev[serviceName].classes[className].methods[methodName],
                  currentBehaviourId: newBehavior
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
      alert('Failed to update behavior: ' + err.message);
    }
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
          <button onClick={() => window.location.reload()} className="retry-button">
            Retry
          </button>
        </div>
      </div>
    );
  }

  if (selectedService) {
    return (
      <ServiceDetailView
        serviceName={selectedService}
        serviceConfig={configRegistry[selectedService]}
        onBack={handleBack}
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
              <p>Manage microservice behavior configurations</p>
            </div>
          </div>
        </div>
      </div>

      <div className="app-content">
        <div className="services-section">
          <h2>Services Overview</h2>
          <p>Click on a service to configure its behavior settings</p>
        </div>

        <div className="services-grid">
          {Object.entries(configRegistry).map(([serviceName, serviceConfig]) => (
            <ServiceOverviewCard
              key={serviceName}
              serviceName={serviceName}
              serviceConfig={serviceConfig}
              onServiceClick={handleServiceClick}
            />
          ))}
        </div>
      </div>
    </div>
  );
}

export default App;