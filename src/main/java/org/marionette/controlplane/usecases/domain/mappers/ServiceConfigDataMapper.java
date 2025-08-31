package org.marionette.controlplane.usecases.domain.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.marionette.controlplane.domain.entities.ClassConfig;
import org.marionette.controlplane.domain.entities.MethodConfig;
import org.marionette.controlplane.domain.entities.ServiceConfig;
import org.marionette.controlplane.domain.values.ClassName;
import org.marionette.controlplane.domain.values.MethodName;
import org.marionette.controlplane.domain.values.ServiceName;
import org.marionette.controlplane.usecases.domain.ClassConfigData;
import org.marionette.controlplane.usecases.domain.MethodConfigData;
import org.marionette.controlplane.usecases.domain.ServiceConfigData;

public class ServiceConfigDataMapper {

    // From data to domain objects
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
            rawMethodConfig.defaultBehaviourId(),
            rawMethodConfig.defaultBehaviourId(),
            rawMethodConfig.availableBehaviourIds()
        );
    }



    // From domain to data
    public static ServiceConfigData fromDomainServiceConfig(ServiceConfig domainServiceConfig) {

        String serviceName = domainServiceConfig.serviceNameAsString();
        List<ClassConfigData> classConfigs = new ArrayList<>();
        for(Map.Entry<ClassName, ClassConfig> classEntry : domainServiceConfig.getClassConfigurations().entrySet()) {
            ClassConfigData classConfigData = fromDomainClassConfig(classEntry.getValue());
            classConfigs.add(classConfigData);
        }

        return new ServiceConfigData(serviceName, classConfigs);

    }

    private static ClassConfigData fromDomainClassConfig(ClassConfig domainClassConfig) {
        String className = domainClassConfig.classNameAsString();

        List<MethodConfigData> methodConfigs = new ArrayList<>();
        for(Map.Entry<MethodName, MethodConfig> methodEntry : domainClassConfig.getMethodsConfigurations().entrySet()) {
            MethodConfigData methodConfig = fromDomainMethodConfig(methodEntry.getValue());
            methodConfigs.add(methodConfig);
        }

        return new ClassConfigData(className, methodConfigs);
    }

    private static MethodConfigData fromDomainMethodConfig(MethodConfig domainMethodConfig) {
        List<String> availableBehaviourIds = domainMethodConfig.getAvailableBehaviourIds().getBehaviours().stream().map(el -> el.getBehaviourId()).collect(Collectors.toList());
        
        return new MethodConfigData(
            domainMethodConfig.methodNameAsString(),
            domainMethodConfig.defaultBehaviourIdAsString(),
            availableBehaviourIds
        );
    }






}