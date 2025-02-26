package com.mulesoft.connectors.mcp.client.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mulesoft.connectors.mcp.client.api.McpClientError;
import com.mulesoft.connectors.mcp.client.api.McpClientErrorProvider;
import com.mulesoft.connectors.mcp.client.api.McpOutputResolver;
import com.mulesoft.connectors.mcp.client.internal.config.MuleMCPClientConfiguration;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpError;
import io.modelcontextprotocol.spec.McpSchema;
import org.apache.avro.util.MapEntry;
import org.mule.runtime.api.transformation.TransformationService;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.support.ObjectNameManager;


import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mule.runtime.api.i18n.I18nMessageFactory.createStaticMessage;
import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;


/**
 * Mule MCPClient Operations exposing the MCP Client protocol as Mule operations
 */
public class MuleMCPClientOperations {
  private static Logger LOGGER = LoggerFactory.getLogger(MuleMCPClientOperations.class);

  @Inject
  private TransformationService transformationService;

  @MediaType(value = ANY, strict = false)
  public List<McpSchema.Tool> getListTools(@Config MuleMCPClientConfiguration config, @Connection MuleMCPClientConnection connection){
    McpSyncClient client = connection.getMcpClient();
    return client.listTools().tools();
  }

//  @MediaType(value = ANY, strict = false)
//  public List<McpSchema.Resource> getResources(@Config MuleMCPClientConfiguration config, @Connection MuleMCPClientConnection connection){
//    McpSyncClient client = connection.getMcpClient();
//    return client.listResources().resources();
//  }

  @Throws(McpClientErrorProvider.class)
  @OutputResolver(output= McpOutputResolver.class)
  @MediaType(value = ANY, strict = false)
  public List<McpSchema.Content> callTool(String operationName, Map<String, Object> arguments, @Config MuleMCPClientConfiguration config, @Connection MuleMCPClientConnection connection){
    McpSyncClient client = connection.getMcpClient();

    McpSchema.CallToolRequest request = new McpSchema.CallToolRequest(operationName, arguments);
    McpSchema.CallToolResult result = client.callTool(request);

    if (result.isError())
      throw new ModuleException(createStaticMessage("Error while invoking tool: " + result.content().get(0).toString()), McpClientError.MCP_CLIENT_ERROR);

    return result.content();
  }

  @OutputResolver(output= McpOutputResolver.class)
  @MediaType(value = ANY, strict = false)
  public LinkedHashMap<String, Object> getServerCapabilities(@Config MuleMCPClientConfiguration config, @Connection MuleMCPClientConnection connection){
    ObjectMapper mapper = new ObjectMapper();

    McpSyncClient client = connection.getMcpClient();
    McpSchema.ServerCapabilities sc = client.getServerCapabilities();
    try {
      String jsonString = mapper.writeValueAsString(sc);
      return mapper.readValue(jsonString, LinkedHashMap.class);
    } catch (JsonProcessingException e) {
      LOGGER.error("Unable to parse the ServerCapabilities object into a JSON: {}", e.getMessage());
      throw new ModuleException(createStaticMessage("Unable to parse the ServerCapabilities object into a JSON: {}", e.getMessage()), McpClientError.MCP_CLIENT_ERROR);
    }

  }

  @MediaType(value = ANY, strict = false)
  @OutputResolver(output= McpOutputResolver.class)
  public LinkedHashMap<String, Object> getPrompts(@Config MuleMCPClientConfiguration config, @Connection MuleMCPClientConnection connection){
    ObjectMapper mapper = new ObjectMapper();
    McpSyncClient client = connection.getMcpClient();

    try {
      List<McpSchema.Prompt> prompts = client.listPrompts().prompts();
      String jsonString = mapper.writeValueAsString(prompts);
      return mapper.convertValue(prompts, LinkedHashMap.class);
    }
    catch(JsonProcessingException jpe){
      throw new ModuleException(createStaticMessage("Error while generating output for getPrompts:  " + jpe.getMessage()), McpClientError.MCP_CLIENT_ERROR);
    }
    catch (McpError me){
      if (me.getJsonRpcError().code() == -32601){
          LOGGER.debug("Server return 'Method not found' which means the server doesn't have prompts in its capabilities");
          return new LinkedHashMap<>();
      } else {
        throw new ModuleException(createStaticMessage("JSONRPC error when talking to the server: :  " + me.getJsonRpcError().message() + " [" + + me.getJsonRpcError().code() + "]"), McpClientError.MCP_CLIENT_ERROR);
      }

    }
  }

  @MediaType(value = MediaType.APPLICATION_JSON, strict = false)
  public String getPrompt(String promptName, Map<String, Object> arguments, @Config MuleMCPClientConfiguration config, @Connection MuleMCPClientConnection connection){
    McpSyncClient client = connection.getMcpClient();

    McpSchema.GetPromptRequest request = new McpSchema.GetPromptRequest(promptName, arguments);


    try {
      List<McpSchema.PromptMessage> result = client.getPrompt(request).messages();
      ObjectMapper mapper = new ObjectMapper();
      return mapper.writeValueAsString(result);
    }
    catch(JsonProcessingException jpe){
      throw new ModuleException(createStaticMessage("Error while generating output for getPrompt:  " + jpe.getMessage()), McpClientError.MCP_CLIENT_ERROR);
    }
    catch (McpError me){
      if (me.getJsonRpcError().code() == -32601){
        LOGGER.debug("Server return 'Method not found' which means the server doesn't have prompts in its capabilities");
        return "";
      } else {
        throw new ModuleException(createStaticMessage("JSONRPC error when talking to the server: :  " + me.getJsonRpcError().message() + " [" + + me.getJsonRpcError().code() + "]"), McpClientError.MCP_CLIENT_ERROR);
      }

    }
  }

  @MediaType(value = ANY, strict = false)
  public List<McpSchema.PromptMessage> prompt(String promptName,
                                       Map<String, Object> parameters,
                                       @Config MuleMCPClientConfiguration config,
                                       @Connection MuleMCPClientConnection connection){
    McpSyncClient client = connection.getMcpClient();
    McpSchema.GetPromptRequest request = new McpSchema.GetPromptRequest(promptName, parameters);

    McpSchema.GetPromptResult result = client.getPrompt(request);
    return result.messages();
  }

  @MediaType(value = ANY, strict = false)
  @OutputResolver(output= McpOutputResolver.class)
  public LinkedHashMap<String, Object> getResources(@Config MuleMCPClientConfiguration config, @Connection MuleMCPClientConnection connection){
    McpSyncClient client = connection.getMcpClient();

    ObjectMapper mapper = new ObjectMapper();

    try {
      List<McpSchema.Resource> resources = client.listResources().resources();
      return mapper.convertValue(resources, LinkedHashMap.class);

    } catch (McpError me){
      if (me.getJsonRpcError().code() == -32601){
        LOGGER.debug("Server return 'Method not found' which means the server doesn't have resources in its capabilities");
        return new LinkedHashMap<>();
      } else {
        throw new ModuleException(createStaticMessage("JSONRPC error when talking to the server: :  " + me.getJsonRpcError().message() + " [" + + me.getJsonRpcError().code() + "]"), McpClientError.MCP_CLIENT_ERROR);
      }
    }
  }
//  /**
//   * Example of an operation that uses the configuration and a connection instance to perform some action.
//   */
//  @MediaType(value = ANY, strict = false)
//  public String retrieveInfo(@Config MuleMCPClientConfiguration configuration, @Connection MulemcpclientConnection connection){
//    return "Using Configuration [" + configuration.getConfigId() + "] with Connection id [" + connection.getId() + "]";
//  }
//
//  /**
//   * Example of a simple operation that receives a string parameter and returns a new string message that will be set on the payload.
//   */
//  @MediaType(value = ANY, strict = false)
//  public String sayHi(String person) {
//    return buildHelloMessage(person);
//  }
//
//  /**
//   * Private Methods are not exposed as operations
//   */
//  private String buildHelloMessage(String person) {
//    return "Hello " + person + "!!!";
//  }
}
