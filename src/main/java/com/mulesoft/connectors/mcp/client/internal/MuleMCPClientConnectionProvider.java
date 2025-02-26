package com.mulesoft.connectors.mcp.client.internal;

import com.mulesoft.connectors.mcp.client.api.configuration.ConnectionProviderConfig;
import com.mulesoft.connectors.mcp.client.internal.config.SSEConfiguration;
import com.mulesoft.connectors.mcp.client.internal.config.STDIOConfiguration;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.connection.PoolingConnectionProvider;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.connection.CachedConnectionProvider;

import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.display.Summary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class (as it's name implies) provides connection instances and the funcionality to disconnect and validate those
 * connections.
 * <p>
 * All connection related parameters (values required in order to create a connection) must be
 * declared in the connection providers.
 * <p>
 * This particular example is a {@link PoolingConnectionProvider} which declares that connections resolved by this provider
 * will be pooled and reused. There are other implementations like {@link CachedConnectionProvider} which lazily creates and
 * caches connections or simply {@link ConnectionProvider} if you want a new connection each time something requires one.
 */
public class MuleMCPClientConnectionProvider implements CachedConnectionProvider<MuleMCPClientConnection> {

  private final Logger LOGGER = LoggerFactory.getLogger(MuleMCPClientConnectionProvider.class);

  @Parameter
  @Optional(defaultValue = "false")
  @Summary("Whether this client will notify the server when the root folder content has changed")
  private boolean notifyOnRootChange;

  @ParameterGroup(name = "SSE Configuration")
  private SSEConfiguration sseConfiguration;

  @ParameterGroup(name = "STDIO Configuration")
  private STDIOConfiguration stdioConfiguration;

  @Parameter
  @Summary(value="Provides a standardized way for servers to request LLM sampling (completions or generations) " +
          "from language models via clients. This flow allows clients to maintain control over model access, " +
          "selection, and permissions while enabling servers to leverage AI capabilities?with no server API keys necessary. " +
          "Servers can request text or image-based interactions and optionally include context from MCP servers in their prompts.")
  private boolean enableSampling;

  @Parameter
  @Summary(value="Duration to wait for server responses before timing out requests. This timeout applies to all requests " +
          "made through the client, including tool calls, resource access, and prompt operations.")
  @Optional(defaultValue = "10000")
  private long requestTimeout;

  @Override
  public MuleMCPClientConnection connect() throws ConnectionException {
      ConnectionProviderConfig config = ConnectionProviderConfig.builder()
              .setSTDIOConfiguration(this.stdioConfiguration)
              .setSSEConfiguration(this.sseConfiguration)
              .setNotifyRootChanges(this.notifyOnRootChange)
              .setEnableSampling(this.enableSampling)
              .setRequestTimeout(this.requestTimeout);

    return new MuleMCPClientConnection(config);
  }

  @Override
  public void disconnect(MuleMCPClientConnection connection) {
    try {
      connection.invalidate();
    } catch (Exception e) {
      LOGGER.error("Error while disconnecting: {}", e.getMessage(), e);
    }
  }

  @Override
  public ConnectionValidationResult validate(MuleMCPClientConnection connection) {
    return ConnectionValidationResult.success();
  }
}
