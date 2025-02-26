package com.mulesoft.connectors.mcp.client.api;

import org.mule.metadata.api.builder.BaseTypeBuilder;
import org.mule.metadata.api.model.AnyType;
import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.metadata.resolving.AttributesTypeResolver;
import org.mule.runtime.api.metadata.resolving.OutputStaticTypeResolver;
import org.mule.runtime.api.metadata.resolving.OutputTypeResolver;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;

import java.lang.annotation.Annotation;

import static org.mule.metadata.api.model.MetadataFormat.JAVA;

public class McpOutputResolver extends OutputStaticTypeResolver {

    //public class HttpMetadataResolver extends OutputStaticTypeResolver {

        private static final AnyType ANY_TYPE = BaseTypeBuilder.create(JAVA).anyType().build();

        @Override
        public String getCategoryName() {
            return "MCP";
        }

        @Override
        public MetadataType getStaticMetadata() {
            return ANY_TYPE;
        }

//    }

}
