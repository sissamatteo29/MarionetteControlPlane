import React, { useState, useEffect } from 'react';
import { ChevronRight, ChevronDown, Settings, Activity, Layers, Code } from 'lucide-react';

// API base URL - update this to match your Spring Boot server
const API_BASE_URL = 'http://localhost:8080/api';

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
      className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow cursor-pointer border-l-4 border-blue-500"
      onClick={() => onServiceClick(serviceName)}
    >
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-3">
          <div className="p-2 bg-blue-100 rounded-lg">
            <Activity className="w-6 h-6 text-blue-600" />
          </div>
          <div>
            <h3 className="text-xl font-semibold text-gray-800">{serviceName}</h3>
            <p className="text-gray-600 text-sm">Microservice Configuration</p>
          </div>
        </div>
        <ChevronRight className="w-5 h-5 text-gray-400" />
      </div>
      
      <div className="mt-4 grid grid-cols-3 gap-4">
        <div className="text-center">
          <div className="text-2xl font-bold text-blue-600">{classCount}</div>
          <div className="text-xs text-gray-500 flex items-center justify-center">
            <Layers className="w-3 h-3 mr-1" />
            Classes
          </div>
        </div>
        <div className="text-center">
          <div className="text-2xl font-bold text-green-600">{methodCount}</div>
          <div className="text-xs text-gray-500 flex items-center justify-center">
            <Code className="w-3 h-3 mr-1" />
            Methods
          </div>
        </div>
        <div className="text-center">
          <div className="text-2xl font-bold text-orange-600">{behaviorStats.modified}</div>
          <div className="text-xs text-gray-500 flex items-center justify-center">
            <Settings className="w-3 h-3 mr-1" />
            Modified
          </div>
        </div>
      </div>
    </div>
  );
};

const BehaviorBadge = ({ behavior, isDefault, isCurrent }) => {
  const getBadgeColor = (behavior) => {
    switch (behavior) {
      case 'NORMAL': return 'bg-green-100 text-green-800 border-green-200';
      case 'SLOW': return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      case 'ERROR': return 'bg-red-100 text-red-800 border-red-200';
      case 'TIMEOUT': return 'bg-purple-100 text-purple-800 border-purple-200';
      default: return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  return (
    <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium border ${getBadgeColor(behavior)} ${isCurrent ? 'ring-2 ring-blue-300' : ''}`}>
      {behavior}
      {isDefault && <span className="ml-1 text-xs">(default)</span>}
    </span>
  );
};

const MethodConfigRow = ({ method, onBehaviorChange }) => {
  const [isExpanded, setIsExpanded] = useState(false);

  return (
    <div className="border border-gray-200 rounded-lg mb-2">
      <div 
        className="p-4 flex items-center justify-between cursor-pointer hover:bg-gray-50"
        onClick={() => setIsExpanded(!isExpanded)}
      >
        <div className="flex items-center space-x-3">
          {isExpanded ? 
            <ChevronDown className="w-4 h-4 text-gray-500" /> : 
            <ChevronRight className="w-4 h-4 text-gray-500" />
          }
          <Code className="w-4 h-4 text-blue-600" />
          <span className="font-medium text-gray-800">{method.methodName}</span>
        </div>
        <BehaviorBadge 
          behavior={method.currentBehaviourId} 
          isCurrent={true}
        />
      </div>
      
      {isExpanded && (
        <div className="px-4 pb-4 border-t border-gray-100">
          <div className="mt-3">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Current Behavior
            </label>
            <select 
              value={method.currentBehaviourId}
              onChange={(e) => onBehaviorChange(method.methodName, e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              {method.availableBehaviourIds.map(behaviorId => (
                <option key={behaviorId} value={behaviorId}>
                  {behaviorId} {behaviorId === method.defaultBehaviourId ? '(default)' : ''}
                </option>
              ))}
            </select>
          </div>
          
          <div className="mt-3">
            <span className="text-sm font-medium text-gray-700">Available Behaviors:</span>
            <div className="flex flex-wrap gap-2 mt-2">
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
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 mb-4">
      <div 
        className="p-4 flex items-center justify-between cursor-pointer hover:bg-gray-50"
        onClick={() => setIsExpanded(!isExpanded)}
      >
        <div className="flex items-center space-x-3">
          {isExpanded ? 
            <ChevronDown className="w-5 h-5 text-gray-600" /> : 
            <ChevronRight className="w-5 h-5 text-gray-600" />
          }
          <Layers className="w-5 h-5 text-green-600" />
          <div>
            <h4 className="text-lg font-semibold text-gray-800">{classConfig.className}</h4>
            <p className="text-sm text-gray-600">{methodCount} method{methodCount !== 1 ? 's' : ''}</p>
          </div>
        </div>
      </div>
      
      {isExpanded && (
        <div className="px-4 pb-4">
          <div className="space-y-2">
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
    <div className="min-h-screen bg-gray-50">
      <div className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-4">
              <button
                onClick={onBack}
                className="text-blue-600 hover:text-blue-800 font-medium"
              >
                ← Back to Services
              </button>
              <div className="flex items-center space-x-3">
                <div className="p-2 bg-blue-100 rounded-lg">
                  <Activity className="w-6 h-6 text-blue-600" />
                </div>
                <div>
                  <h1 className="text-2xl font-bold text-gray-800">{serviceName}</h1>
                  <p className="text-gray-600">{classCount} classes, {methodCount} methods</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 py-6">
        <div className="space-y-4">
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

// API functions to communicate with your Java backend
const fetchAllServices = async () => {
  const response = await fetch(`${API_BASE_URL}/services`);
  if (!response.ok) throw new Error('Failed to fetch services');
  return response.json();
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
      // You might want to revert the UI change here or show an error message
      alert('Failed to update behavior: ' + err.message);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading services...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="text-red-600 text-lg mb-2">Error loading services</div>
          <p className="text-gray-600">{error}</p>
          <button 
            onClick={() => window.location.reload()} 
            className="mt-4 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          >
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
    <div className="min-h-screen bg-gray-50">
      <div className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 py-6">
          <div className="flex items-center space-x-3">
            <Settings className="w-8 h-8 text-blue-600" />
            <div>
              <h1 className="text-3xl font-bold text-gray-800">Marionette Control Panel</h1>
              <p className="text-gray-600">Manage microservice behavior configurations</p>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 py-8">
        <div className="mb-6">
          <h2 className="text-xl font-semibold text-gray-800 mb-2">Services Overview</h2>
          <p className="text-gray-600">Click on a service to configure its behavior settings</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
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
};

export default MarionetteControlPanel;