package com.swaroop.udemy.broker;

import com.swaroop.udemy.broker.config.ConfigLoader;
import com.swaroop.udemy.broker.db.migration.FlywayMigration;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MainVerticle extends VerticleBase {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);
  public static final int PORT = 8888;

  public static void main(String[] args){
//    System.setProperty(ConfigLoader.SERVER_PORT,"9800");
    var vertx = Vertx.vertx();
    vertx.exceptionHandler(error -> {
      LOG.error("Unhandled:", error);
    });
    vertx.deployVerticle(new MainVerticle())
      .onFailure(err-> {
        LOG.error("Failed to deploy:", err);
      })
     .onSuccess(id -> {
       LOG.info("Deployed {} with id: {}", MainVerticle.class.getName(), id);
     });
  }


  @Override
  public Future<?> start() {
   return vertx.deployVerticle(VersionInfoVerticle.class.getName())
      .onFailure(error -> {
        LOG.error("Failed to deploy VersionInfoVerticle: {}", error);
      })
     .onSuccess(id -> {
       LOG.info("Deployed {} with id: {}", VersionInfoVerticle.class.getSimpleName(), id);
     })
      .compose(next -> migrateDatabase())
      .onFailure(error -> LOG.info("Database migration failed: {}", error))
      .onSuccess(msg -> LOG.info("Database migration completed: {}", msg))
      .compose(next -> deployRestApiVerticle());


  }

  private Future<Void> migrateDatabase() {
    return ConfigLoader.load(vertx)
      .compose(config -> {
        return FlywayMigration.migrate(vertx, config.getDbConfig());
      });

  }

  private Future<String> deployRestApiVerticle() {
    return vertx.deployVerticle(RestApiVerticle.class.getName(), new DeploymentOptions().setInstances(processors())).onFailure(error -> {
      LOG.error("Failed to deploy RestApiVerticle", error);
    }).onSuccess(id -> {
      LOG.info("Deployed {} with id: {}", RestApiVerticle.class.getSimpleName(), id);
    });
  }

  private static int processors() {
    return Math.max(1,Runtime.getRuntime().availableProcessors()/2);
  }
}
