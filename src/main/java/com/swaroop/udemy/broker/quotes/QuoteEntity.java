package com.swaroop.udemy.broker.quotes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swaroop.udemy.broker.assets.Asset;
import io.vertx.core.json.JsonObject;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class QuoteEntity {

  Asset asset;
  BigDecimal bid;
  BigDecimal ask;
  @JsonProperty("last_price")
  BigDecimal lastPrice;
  BigDecimal volume;

  public JsonObject toJsonObject(){
    return JsonObject.mapFrom(this);
  }
}
