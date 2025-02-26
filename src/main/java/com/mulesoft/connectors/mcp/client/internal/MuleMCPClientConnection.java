package com.mulesoft.connectors.mcp.client.internal;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mulesoft.connectors.mcp.client.api.configuration.ConnectionProviderConfig;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.ClientMcpTransport;
import io.modelcontextprotocol.spec.McpSchema;
import org.mule.runtime.api.exception.MuleRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.time.Duration;
import io.modelcontextprotocol.client.McpClient;
import static org.mule.runtime.api.i18n.I18nMessageFactory.createStaticMessage;

/**
 * This class represents an extension connection just as example (there is no real connection with anything here c:).
 */
public final class MuleMCPClientConnection {
  private final Logger LOGGER = LoggerFactory.getLogger(MuleMCPClientConnection.class);

  //TODO: Should support async as well
  private McpSyncClient client;
  private ConnectionProviderConfig config;

  public MuleMCPClientConnection(ConnectionProviderConfig config){
    this.config = config;

    initializeClient();

  }

  private void initializeClient(){
    LOGGER.debug("Initializing MCP Client...");
    McpSchema.ClientCapabilities.Builder capabilitiesBuilder = McpSchema.ClientCapabilities.builder()
            .roots(this.config.getNotifyRootChanges());
    if (config.isEnableSampling()){
      LOGGER.debug("Sampling enabled...");
      capabilitiesBuilder = capabilitiesBuilder.sampling();
    }

    this.client = McpClient.sync(this.getTransport())
            .requestTimeout(Duration.ofMillis(this.config.getRequestTimeout()))
            .capabilities(capabilitiesBuilder.build())
            //TODO Required to provide logging capabilities to this client
            //.loggingConsumer()
            .build();

    try {
       this.client.initialize();
    } catch (RuntimeException rte){
      throw new MuleRuntimeException(createStaticMessage("Error while initializing MCP Client: " + rte.getMessage(), rte));
    }
    //TODO? Do we need to check anything of initResult?

    LOGGER.debug("MCP Client initialized!");

  }
  private ClientMcpTransport getTransport(){
    ClientMcpTransport transport = null;

    //Only one transport should be enabled
    if (this.config.getStdioConfiguration().isEnableSTDIOTransport() && this.config.getSseConfiguration().isEnableSSETransport()){
      throw new MuleRuntimeException(createStaticMessage("Both SSE and STDIO transports have been enabled. Please only enable one transport type."));
    }

    //At least one transport should be enabled
    if (!this.config.getStdioConfiguration().isEnableSTDIOTransport() && !this.config.getSseConfiguration().isEnableSSETransport()){
      throw new MuleRuntimeException(createStaticMessage("No transport have been enabled!"));
    }

    if (this.config.getSseConfiguration().isEnableSSETransport()){
      LOGGER.debug("Creating an SSE Transport");

      HttpClient.Builder builder = HttpClient.newBuilder()
              .connectTimeout(Duration.ofMillis(config.getSseConfiguration().getConnectionTimeout()));

      transport = new HttpClientSseClientTransport(
                      builder, this.config.getSseConfiguration().getSseURL(), new ObjectMapper());

      LOGGER.debug("SSE transport created");
    }

    if (this.config.getStdioConfiguration().isEnableSTDIOTransport()){
      LOGGER.debug("Creating an STDIO transport");

      //TODO - Manage STDIO parameters
      ServerParameters.Builder parameters = new ServerParameters.Builder(this.config.getStdioConfiguration().getCommandName());
      if (this.config.getStdioConfiguration().getParams() != null)
        this.config.getStdioConfiguration().getParams().forEach(parameters::args);

      if (this.config.getStdioConfiguration().getEnvVariables() != null)
        this.config.getStdioConfiguration().getEnvVariables().forEach(parameters::addEnvVar);
      transport = new StdioClientTransport(parameters.build());

      LOGGER.debug("STDIO Transport created");
    }

    return transport;

  }

  public McpSyncClient getMcpClient(){
    return this.client;
  }

  public void invalidate() {
    // do something to invalidate this connection!
    this.client.closeGracefully();
    this.client = null;
  }
}
