package com.swaroop.udemy.broker.config;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.util.Objects;

@Builder
@Value
@ToString
public class BrokerConfig {

  int serverPort;
  String version;

  public static  BrokerConfig from(final JsonObject config) {

    final Integer serverPort = config.getInteger("SERVER_PORT");
    if(Objects.isNull(serverPort)) {
      throw new RuntimeException(ConfigLoader.SERVER_PORT + "is not configured");
    }
    final String version = config.getString("version");
    if (Objects.isNull(version)) {
      throw new RuntimeException("version is not configured");
    }
  return BrokerConfig.builder()
    .version(version)
    .serverPort(config.getInteger("SERVER_PORT"))
    .build();
  }
}
