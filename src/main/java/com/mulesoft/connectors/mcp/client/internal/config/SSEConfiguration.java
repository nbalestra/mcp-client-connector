package com.mulesoft.connectors.mcp.client.internal.config;

import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

public class SSEConfiguration {

    @Parameter
    @DisplayName(value="Enable SSE Transport")
    @Placement(tab = Placement.CONNECTION_TAB)
    private boolean enableSSETransport;

    @Parameter
    @Optional(defaultValue = "http://localhost:8082")
    @Placement(tab = Placement.CONNECTION_TAB)
    private String sseURL;

    @Parameter
    @Summary(value="SSE connection timeout in milliseconds. Defult to 30 seconds")
    @Optional(defaultValue = "30000")
    @Placement(tab = Placement.CONNECTION_TAB)
    private long connectionTimeout;

    public String getSseURL() {
        return sseURL;
    }

    public void setSseURL(String sseURL) {
        this.sseURL = sseURL;
    }

    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }


    public boolean isEnableSSETransport() {
        return enableSSETransport;
    }

    public void setEnableSSETransport(boolean enableSSETransport) {
        this.enableSSETransport = enableSSETransport;
    }
}
