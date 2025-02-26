package com.mulesoft.connectors.mcp.client.internal.config;

import com.mulesoft.connectors.mcp.client.internal.MuleMCPClientConnectionProvider;
import com.mulesoft.connectors.mcp.client.internal.MuleMCPClientOperations;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;

/**
 * This class represents an extension configuration, values set in this class are commonly used across multiple
 * operations since they represent something core from the extension.
 */
@Operations(MuleMCPClientOperations.class)
@ConnectionProviders(MuleMCPClientConnectionProvider.class)
public class MuleMCPClientConfiguration {

//  @Parameter
//  private String configId;
//
//  public String getConfigId(){
//    return configId;
//  }
}
