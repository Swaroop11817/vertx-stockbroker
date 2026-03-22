package com.swaroop.udemy.broker;


import com.swaroop.udemy.broker.assets.AssetsRestApi;
import com.swaroop.udemy.broker.config.BrokerConfig;
import com.swaroop.udemy.broker.config.ConfigLoader;
import com.swaroop.udemy.broker.quotes.QuotesRestApi;
import com.swaroop.udemy.broker.watchlist.WatchListRestApi;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.HttpServer;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.pgclient.PgConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestApiVerticle extends VerticleBase {
     private static final Logger LOG = LoggerFactory.getLogger(RestApiVerticle.class);
//     private static final int PORT = 8888;


    @Override
    public Future<?> start() {
      return ConfigLoader.load(vertx).onFailure(error -> {
        LOG.error("Failed to load config", error);
      }).compose(config -> {
        LOG.info("Config loaded: {}", config);
        return startHttpServerAndAttachRoutes(config);
      });


    }
  private Future<HttpServer> startHttpServerAndAttachRoutes(BrokerConfig config) {

    final Pool db = createDbPool(config);




    final Router restApi = Router.router(vertx);
    restApi.route().handler(BodyHandler.create()).failureHandler(errorContext -> {
      if(errorContext.response().ended()){
        return;
      }
      LOG.error("Route Error:",errorContext.failure());
      errorContext.response()
        .setStatusCode(500)
        .end(new JsonObject().put("message", "Something went wrong:(").toBuffer());
    });
    AssetsRestApi.attach(restApi,db);
    QuotesRestApi.attach(restApi,db);
    WatchListRestApi.attach(restApi);

    return vertx.createHttpServer().requestHandler(restApi)
      .exceptionHandler(error -> LOG.error("HTTP Server error: ", error))
      .listen(config.getServerPort()).onSuccess(http -> {

        LOG.info("HTTP server started on port {}", config.getServerPort());
      });
  }

  private Pool createDbPool(BrokerConfig config) {
    final var connectOptions = new PgConnectOptions()
      .setHost(config.getDbConfig().getHost())
      .setPort(config.getDbConfig().getPort())
      .setDatabase(config.getDbConfig().getDatabase())
      .setUser(config.getDbConfig().getUser())
      .setPassword(config.getDbConfig().getPassword());

    var poolOptions = new PoolOptions()
      .setMaxSize(4);

    //Create DB Pool
    return Pool.pool(vertx,connectOptions,poolOptions);
  }
}
