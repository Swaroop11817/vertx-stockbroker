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
  DbConfig dbConfig;

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
    .dbConfig(parseDbConfig(config))
    .build();
  }

  private static DbConfig parseDbConfig(final JsonObject config) {
    return DbConfig.builder()
      .host(config.getString(ConfigLoader.DB_HOST))
      .port(config.getInteger(ConfigLoader.DB_PORT))
      .database(config.getString(ConfigLoader.DB_DATABASE))
      .user(config.getString(ConfigLoader.DB_USER))
      .password(config.getString(ConfigLoader.DB_PASSWORD))
      .build();
  }
}
