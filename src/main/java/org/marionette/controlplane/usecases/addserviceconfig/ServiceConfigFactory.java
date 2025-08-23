package org.marionette.controlplane.usecases.addserviceconfig;

import java.util.List;

import org.marionette.controlplane.domain.entities.ClassConfig;
import org.marionette.controlplane.domain.entities.MethodConfig;
import org.marionette.controlplane.domain.entities.ServiceConfig;
import org.marionette.controlplane.domain.values.ClassName;
import org.marionette.controlplane.domain.values.ServiceName;
import org.marionette.controlplane.usecases.addserviceconfig.request.ClassConfigData;
import org.marionette.controlplane.usecases.addserviceconfig.request.MethodConfigData;

public class ServiceConfigFactory {

    public ServiceConfig createServiceConfig(String serviceName, List<ClassConfigData> classConfigData) {

        ServiceConfig resultingServiceConfig = new ServiceConfig(new ServiceName(serviceName));

        for(ClassConfigData rawClassConfig : classConfigData) {
            ClassConfig domainClassConfig = toDomainClassConfig(rawClassConfig);
            resultingServiceConfig = resultingServiceConfig.withAddedClassConfiguration(domainClassConfig.getClassName(), domainClassConfig);
        }

        return resultingServiceConfig;
    }

    private ClassConfig toDomainClassConfig(ClassConfigData rawClassConfig) {
        ClassConfig resultingClassConfig = new ClassConfig(new ClassName(rawClassConfig.className()));

        for(MethodConfigData rawMethodConfig : rawClassConfig.methodConfigData()) {
            MethodConfig domainMethodConfig = toDomainMethodConfig(rawMethodConfig);
            resultingClassConfig = resultingClassConfig.withAddedMethodConfig(domainMethodConfig.getMethodName(), domainMethodConfig);
        }

        return resultingClassConfig;
    }

    private MethodConfig toDomainMethodConfig(MethodConfigData rawMethodConfig) {
        return MethodConfig.of(
            rawMethodConfig.methodName(),
            rawMethodConfig.originalBehaviourId(),
            rawMethodConfig.originalBehaviourId(),
            rawMethodConfig.availableBehaviourIds()
        );
    }


}