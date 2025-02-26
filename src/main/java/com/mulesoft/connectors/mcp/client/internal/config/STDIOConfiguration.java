package com.mulesoft.connectors.mcp.client.internal.config;

import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;

import java.util.List;
import java.util.Map;

public class STDIOConfiguration {

    @Parameter
    @DisplayName(value="Enable STDIO Transport")
    @Placement(tab = Placement.CONNECTION_TAB)
    private boolean enableSTDIOTransport;

    @Parameter
    @DisplayName(value="Comand Name")
    @Placement(tab = Placement.CONNECTION_TAB)
    private String commandName;

    @Parameter
    @Optional
    @Placement(tab = Placement.CONNECTION_TAB)
    @DisplayName(value = "Command Parameters")
    private List<String> params;

    @Parameter
    @Optional
    @Placement(tab = Placement.CONNECTION_TAB)
    @DisplayName(value = "Environment Variables")
    private Map<String, String> envVariables;

    public STDIOConfiguration(){}

    public boolean isEnableSTDIOTransport() {
        return enableSTDIOTransport;
    }

    public void setEnableSTDIOTransport(boolean enableSTDIOTransport) {
        this.enableSTDIOTransport = enableSTDIOTransport;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public Map<String, String> getEnvVariables() {
        return envVariables;
    }

    public void setEnvVariables(Map<String, String> envVariables) {
        this.envVariables = envVariables;
    }
}
