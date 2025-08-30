package org.marionette.controlplane.usecases.inputports.fetchconfig;

import org.marionette.controlplane.usecases.inputports.FetchMarionetteConfigurationUseCase;
import org.marionette.controlplane.usecases.outputports.fetchconfig.NodeConfigGateway;

import static java.util.Objects.requireNonNull;

import java.net.URI;

public class FetchMarionetteConfigurationUseCaseImpl implements FetchMarionetteConfigurationUseCase {

    // Global config exposed through env vars
    private final String marionetteServiceInternalPath;
    private final NodeConfigGateway nodeConfigGateway;

    public FetchMarionetteConfigurationUseCaseImpl(String marionetteServiceInternalPath, NodeConfigGateway nodeConfigGateway) {

        requireNonNull(marionetteServiceInternalPath, "The marionette service internal path cannot be null");
        requireNonNull(nodeConfigGateway, "The node config cannot be null");

        this.marionetteServiceInternalPath = marionetteServiceInternalPath;
        this.nodeConfigGateway = nodeConfigGateway;
    }

    @Override
    public FetchMarionetteConfigurationResult execute(FetchMarionetteConfigurationRequest request) {
        // Build URL
        URI serviceEndpoint = request.serviceData().endpoint();
        URI completeServiceEndpoint = serviceEndpoint.resolve(marionetteServiceInternalPath);
        nodeConfigGateway.fetchConfiguration(completeServiceEndpoint);
    }
    
}
