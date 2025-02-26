package com.mulesoft.connectors.mcp.client.api.configuration;

import com.mulesoft.connectors.mcp.client.internal.config.SSEConfiguration;
import com.mulesoft.connectors.mcp.client.internal.config.STDIOConfiguration;

import static org.mule.runtime.api.i18n.I18nMessageFactory.createStaticMessage;

public class ConnectionProviderConfig {
    private String transportType;
    private long requestTimeout;
    private STDIOConfiguration stdioConfiguration;
    private SSEConfiguration sseConfiguration;

    private boolean enableSampling;

    private Boolean notifyRootChanges;


    public static ConnectionProviderConfig builder(){
        return new ConnectionProviderConfig();
    }

    public ConnectionProviderConfig  setSSEConfiguration(SSEConfiguration sseConfiguration){
        this.sseConfiguration = sseConfiguration;
        return this;
    }

    public SSEConfiguration getSseConfiguration(){
        return this.sseConfiguration;
    }

    public ConnectionProviderConfig setSTDIOConfiguration(STDIOConfiguration stdioConfiguration){
        this.stdioConfiguration = stdioConfiguration;
        return this;
    }

    public ConnectionProviderConfig setRequestTimeout(long timeout){
        this.requestTimeout = timeout;
        return this;
    }

    public String getTransportType() {
        return transportType;
    }

    public long getRequestTimeout() {
        return requestTimeout;
    }

    public STDIOConfiguration getStdioConfiguration() {
        return stdioConfiguration;
    }


    public Boolean getNotifyRootChanges() {
        return notifyRootChanges;
    }

    public ConnectionProviderConfig setNotifyRootChanges(Boolean notifyRootChanges) {
        this.notifyRootChanges = notifyRootChanges;
        return this;
    }

    public Boolean isEnableSampling() {
        return enableSampling;
    }

    public ConnectionProviderConfig setEnableSampling(boolean enableSampling) {
        this.enableSampling = enableSampling;
        return this;
    }

}
