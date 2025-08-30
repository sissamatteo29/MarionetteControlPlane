package org.marionette.controlplane.usecases.domain.mappers;

import org.marionette.controlplane.domain.entities.ClassConfig;
import org.marionette.controlplane.domain.entities.MethodConfig;
import org.marionette.controlplane.domain.entities.ServiceConfig;
import org.marionette.controlplane.domain.values.ClassName;
import org.marionette.controlplane.domain.values.ServiceName;
import org.marionette.controlplane.usecases.domain.ClassConfigData;
import org.marionette.controlplane.usecases.domain.MethodConfigData;
import org.marionette.controlplane.usecases.domain.ServiceConfigData;

public class ServiceConfigDataMapper {

    public static ServiceConfig toDomainServiceConfig(ServiceConfigData serviceConfigData) {

        ServiceConfig resultingServiceConfig = new ServiceConfig(new ServiceName(serviceConfigData.serviceName()));

        for(ClassConfigData rawClassConfig : serviceConfigData.classConfigs()) {
            ClassConfig domainClassConfig = toDomainClassConfig(rawClassConfig);
            resultingServiceConfig = resultingServiceConfig.withAddedClassConfiguration(domainClassConfig.getClassName(), domainClassConfig);
        }

        return resultingServiceConfig;
    }

    private static ClassConfig toDomainClassConfig(ClassConfigData rawClassConfig) {
        ClassConfig resultingClassConfig = new ClassConfig(new ClassName(rawClassConfig.className()));

        for(MethodConfigData rawMethodConfig : rawClassConfig.methodConfigData()) {
            MethodConfig domainMethodConfig = toDomainMethodConfig(rawMethodConfig);
            resultingClassConfig = resultingClassConfig.withAddedMethodConfig(domainMethodConfig.getMethodName(), domainMethodConfig);
        }

        return resultingClassConfig;
    }

    private static MethodConfig toDomainMethodConfig(MethodConfigData rawMethodConfig) {
        return MethodConfig.of(
            rawMethodConfig.methodName(),
            rawMethodConfig.originalBehaviourId(),
            rawMethodConfig.originalBehaviourId(),
            rawMethodConfig.availableBehaviourIds()
        );
    }


}