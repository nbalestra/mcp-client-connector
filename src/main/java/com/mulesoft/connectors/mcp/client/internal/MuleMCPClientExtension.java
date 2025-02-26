package com.mulesoft.connectors.mcp.client.internal;

import com.mulesoft.connectors.mcp.client.api.McpClientError;
import com.mulesoft.connectors.mcp.client.internal.config.MuleMCPClientConfiguration;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;
import org.mule.sdk.api.annotation.JavaVersionSupport;
import org.mule.sdk.api.meta.JavaVersion;


/**
 * This is the main class of an extension, is the entry point from which configurations, connection providers, operations
 * and sources are going to be declared.
 */
@Xml(prefix = "mule-mcp-client")
@Extension(name = "Mule MCP Client")
@Configurations(MuleMCPClientConfiguration.class)
@JavaVersionSupport(value= JavaVersion.JAVA_17)
@ErrorTypes(McpClientError.class)
public class MuleMCPClientExtension {

}
