package dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


import dao.ISQLDBDao;
import dao.impl.BaseSQLDB;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;


/**
 *
 *
 * <code>MysqlDB<code> <strong></strong>
 * <p>
 * 说明：提供对MySQL的增改查操作
 * <li></li>
 * </p>
 *
 * @since NC6.5
 * @version 2018年8月16日 下午3:55:54
 * @author sifh
 * @createDate 2018年8月16日 上午11:33:46
 */
public class MysqlDB extends BaseSQLDB implements ISQLDBDao {

    private Vertx vertx;

    private JsonObject config;

    private AsyncSQLClient mySQLClient;

    public MysqlDB(Vertx vertx, JsonObject dbConfig, boolean shared, String sharedPollName) {
        this.vertx = vertx;
        this.config = dbConfig;
        if (shared) {
            this.mySQLClient = MySQLClient.createShared(this.vertx, this.config, sharedPollName);
        } else {
            this.mySQLClient = MySQLClient.createNonShared(this.vertx, this.config);
        }

    }
    public MysqlDB(Vertx vertx){
        this.vertx = vertx;
    }

    @Override
    public void queryByID(String table, String idKey, String idValue, List<String> fields,
                          Future<List<JsonObject>> future) {
        String sql = BaseSQLDB.buildeSelectSQL(table, fields);

        if (sql == null || "".equals(sql)) {
            future.fail("error param");
            return;
        }

        ArrayList<Object> values = new ArrayList<Object>();
        String placeholder = "?";
        String logic = "and";
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put(idKey, idValue);

        sql += " where " + BaseSQLDB.buildeWhereSQLWithParam(conditions, placeholder, logic, values);

        Handler<AsyncResult<ResultSet>> handler = new Handler<AsyncResult<ResultSet>>() {

            @Override
            public void handle(AsyncResult<ResultSet> dbRes) {
                if (dbRes.succeeded()) {
                    ResultSet resultSet = dbRes.result();
                    future.complete(resultSet.getRows());
                } else {
                    String errMsg = "query " + table + " db failed, reason: " + dbRes.cause().getMessage();
                    logger.error(errMsg);
                    future.fail(errMsg);
                }
            }

        };
        JsonArray params = new JsonArray(values);
        doQueryWithParams(sql, params, handler);
    }

    @Override
    public void query(String table, List<String> fields, Map<String, Object> conditions,
                      Future<List<JsonObject>> future) {
        String sql = BaseSQLDB.buildeSelectSQL(table, fields);

        if (sql == null || "".equals(sql)) {
            future.fail("error param");
            return;
        }

        ArrayList<Object> values = new ArrayList<Object>();
        String placeholder = "?";
        String logic = "and";

        sql += " where " + BaseSQLDB.buildeWhereSQLWithParam(conditions, placeholder, logic, values);

        Handler<AsyncResult<ResultSet>> handler = new Handler<AsyncResult<ResultSet>>() {

            @Override
            public void handle(AsyncResult<ResultSet> dbRes) {
                if (dbRes.succeeded()) {
                    ResultSet resultSet = dbRes.result();
                    future.complete(resultSet.getRows());
                } else {
                    String errMsg = "query " + table + " db failed, reason: " + dbRes.cause().getMessage();
                    logger.error(errMsg);
                    future.fail(errMsg);
                }
            }

        };
        JsonArray params = new JsonArray(values);
        doQueryWithParams(sql, params, handler);

    }

    @Override
    public void query(String table, String query, Future<List<JsonObject>> future) {
        Handler<AsyncResult<ResultSet>> handler = new Handler<AsyncResult<ResultSet>>() {

            @Override
            public void handle(AsyncResult<ResultSet> dbRes) {
                if (dbRes.succeeded()) {
                    ResultSet resultSet = dbRes.result();
                    future.complete(resultSet.getRows());
                } else {
                    String errMsg = "query " + table + " db failed, reason: " + dbRes.cause().getMessage();
                    logger.error(errMsg);
                    future.fail(errMsg);
                }
            }

        };

        doQuery(query, handler);
    }

    public void queryWithParams(String table, String query, JsonArray params,
                                Future<List<JsonObject>> future) {
        Handler<AsyncResult<ResultSet>> handler = new Handler<AsyncResult<ResultSet>>() {

            @Override
            public void handle(AsyncResult<ResultSet> dbRes) {
                if (dbRes.succeeded()) {
                    ResultSet resultSet = dbRes.result();
                    future.complete(resultSet.getRows());
                } else {
                    String errMsg = "query " + table + " db failed, reason: " + dbRes.cause().getMessage();
                    logger.error(errMsg);
                    future.fail(errMsg);
                }
            }

        };
        doQueryWithParams(query, params, handler);
    }

    @Override
    public void insert(String table, Map<String, Object> data, Future<Boolean> future) {
        ArrayList<Object> values = new ArrayList<Object>();
        String placeholder = "?";

        String sql = BaseSQLDB.buildeInsertSQLWithParam(table, data, placeholder, values);

        if (sql == null || "".equals(sql)) {
            future.fail("error param");
            return;
        }

        JsonArray params = new JsonArray(values);

        doUpdateSingleWithParams(table, sql, params, future);
    }

    @Override
    public void insert(String table, String data, Future<JsonObject> future) {

    }


    @Override
    public void insert(String table, Set<String> fieldSet, List<Map<String, Object>> data,
                       Future<JsonObject> future) {
        ArrayList<Object> values = new ArrayList<Object>();
        String placeholder = "?";

        String sql =
                BaseSQLDB.buildeBatchInsertSQLWithParam(table, fieldSet, data, placeholder, values);

        if (sql == null || "".equals(sql)) {
            future.fail("error param");
            return;
        }

        JsonArray params = new JsonArray(values);

        doUpdateWithParams(table, sql, params, future);
    }


    @Override
    public void updateByID(String table, String idKey, String idValue, Map<String, Object> data,
                           Future<Boolean> future) {

        ArrayList<Object> values = new ArrayList<Object>();
        String placeholder = "?";

        String sql = BaseSQLDB.buildeUpdateSQLWithParam(table, data, placeholder, values);

        if (sql == null || "".equals(sql)) {
            future.fail("error param");
            return;
        }

        String logic = "and";
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put(idKey, idValue);

        sql += " where " + BaseSQLDB.buildeWhereSQLWithParam(conditions, placeholder, logic, values);

        JsonArray params = new JsonArray(values);
        doUpdateSingleWithParams(table, sql, params, future);
    }


    @Override
    public void update(String table, Map<String, Object> data, Map<String, Object> conditions,
                       Future<JsonObject> future) {
        ArrayList<Object> values = new ArrayList<Object>();
        String placeholder = "?";

        String sql = BaseSQLDB.buildeUpdateSQLWithParam(table, data, placeholder, values);

        if (sql == null || "".equals(sql)) {
            future.fail("error param");
            return;
        }

        String logic = "and";

        String where = BaseSQLDB.buildeWhereSQLWithParam(conditions, placeholder, logic, values);

        if (where == null || "".equals(where)) {
            future.fail("error conditions param");
            return;
        }

        sql += " where " + where;

        JsonArray params = new JsonArray(values);
        doUpdateWithParams(table, sql, params, future);
    }


    @Override
    public void updateSingle(String table, Map<String, Object> data, Map<String, Object> conditions,
                             Future<Boolean> future) {
        Future<JsonObject> updateFuture = Future.future();
        updateFuture.setHandler(updateRes -> {
            if (updateRes.succeeded()) {
                boolean success = Optional.ofNullable(updateRes.result())
                        .map(result -> result.getInteger("updated")).map(updated -> {
                            if (updated == 1) {
                                future.complete(true);
                            } else {
                                future.complete(false);
                            }
                            return true;
                        }).orElse(false);
                if (!success) {
                    String errMsg = "update " + table + " db failed, reason: db result is illegal";
                    logger.error(errMsg);
                    future.fail(errMsg);
                }
            } else {
                String errMsg = "update " + table + " db failed, reason: " + updateRes.cause().getMessage();
                logger.error(errMsg);
                future.fail(errMsg);
            }
        });
        update(table, data, conditions, updateFuture);
    }


    @Override
    public void update(String table, String update, Future<JsonObject> future) {
        doUpdate(table, update, future);
    }


    @Override
    public void updateSingle(String table, String update, Future<Boolean> future) {
        doUpdateSingle(table, update, future);
    }

    public void updateWithParams(String table, String update, JsonArray params,
                                 Future<JsonObject> future) {
        doUpdateWithParams(table, update, params, future);
    }


    @Override
    public void close() {
        mySQLClient.close();
    }


    @Override
    public void close(Handler<AsyncResult<Void>> handler) {
        mySQLClient.close(handler);
    }

    private void doQuery(String sql, Handler<AsyncResult<ResultSet>> handler) {
        mySQLClient.getConnection(res -> {

            if (res.succeeded()) {
                SQLConnection connection = res.result();
                connection.query(sql, re -> {
                    if (re.succeeded()) {
                        handler.handle(Future.succeededFuture(re.result()));
                    } else {
                        handler.handle(Future.failedFuture(re.cause()));
                    }
                    connection.close();
                });
            } else {
                handler.handle(Future.failedFuture(res.cause()));
            }
        });
    }

    private void doQueryWithParams(String sql, JsonArray params,
                                   Handler<AsyncResult<ResultSet>> handler) {
        mySQLClient.getConnection(res -> {
            if (res.succeeded()) {
                SQLConnection connection = res.result();
                connection.queryWithParams(sql, params, re -> {
                    if (re.succeeded()) {
                        handler.handle(Future.succeededFuture(re.result()));
                    } else {
                        handler.handle(Future.failedFuture(re.cause()));
                    }
                    connection.close();
                });
            } else {
                handler.handle(Future.failedFuture(res.cause()));
            }
        });
    }

    private void doUpdate(String table, String sql, Future<JsonObject> future) {
        Handler<AsyncResult<UpdateResult>> handler = new Handler<AsyncResult<UpdateResult>>() {

            @Override
            public void handle(AsyncResult<UpdateResult> updateClientRes) {
                if (updateClientRes.succeeded()) {
                    UpdateResult result = updateClientRes.result();
                    future.complete(result.toJson());
                } else {
                    String errMsg =
                            "update " + table + " db failed, reason: " + updateClientRes.cause().getMessage();
                    logger.error(errMsg);
                    future.fail(errMsg);
                }
            }

        };

        mySQLClient.getConnection(res -> {

            if (res.succeeded()) {
                SQLConnection connection = res.result();
                connection.update(sql, re -> {
                    if (re.succeeded()) {
                        handler.handle(Future.succeededFuture(re.result()));
                    } else {
                        handler.handle(Future.failedFuture(re.cause()));
                    }
                    connection.close();
                });
            } else {
                handler.handle(Future.failedFuture(res.cause()));
            }
        });
    }

    private void doUpdateSingle(String table, String sql, Future<Boolean> future) {
        Future<JsonObject> updateFuture = Future.future();
        updateFuture.setHandler(updateRes -> {
            if (updateRes.succeeded()) {
                boolean success = Optional.ofNullable(updateRes.result())
                        .map(result -> result.getInteger("updated")).map(updated -> {
                            if (updated == 1) {
                                future.complete(true);
                            } else {
                                future.complete(false);
                            }
                            return true;
                        }).orElse(false);
                if (!success) {
                    String errMsg = "update " + table + " db failed, reason: db result is illegal";
                    logger.error(errMsg);
                    future.fail(errMsg);
                }
            } else {
                String errMsg = "update " + table + " db failed, reason: " + updateRes.cause().getMessage();
                logger.error(errMsg);
                future.fail(errMsg);
            }
        });

        doUpdate(table, sql, updateFuture);
    }

    private void doUpdateWithParams(String table, String sql, JsonArray params,
                                    Future<JsonObject> future) {
        Handler<AsyncResult<UpdateResult>> handler = new Handler<AsyncResult<UpdateResult>>() {

            @Override
            public void handle(AsyncResult<UpdateResult> updateClientRes) {
                if (updateClientRes.succeeded()) {
                    UpdateResult result = updateClientRes.result();
                    future.complete(result.toJson());
                } else {
                    String errMsg =
                            "update " + table + " db failed, reason: " + updateClientRes.cause().getMessage();
                    logger.error(errMsg);
                    future.fail(errMsg);
                }
            }

        };

        mySQLClient.getConnection(res -> {
            if (res.succeeded()) {
                SQLConnection connection = res.result();
                connection.updateWithParams(sql, params, re -> {
                    if (re.succeeded()) {
                        handler.handle(Future.succeededFuture(re.result()));
                    } else {
                        handler.handle(Future.failedFuture(re.cause()));
                    }
                    connection.close();
                });
            } else {
                handler.handle(Future.failedFuture(res.cause()));
            }
        });
    }

    private void doUpdateSingleWithParams(String table, String sql, JsonArray params,
                                          Future<Boolean> future) {
        Future<JsonObject> updateFuture = Future.future();
        updateFuture.setHandler(updateRes -> {
            if (updateRes.succeeded()) {
                boolean success = Optional.ofNullable(updateRes.result())
                        .map(result -> result.getInteger("updated")).map(updated -> {
                            if (updated == 1) {
                                future.complete(true);
                            } else {
                                future.complete(false);
                            }
                            return true;
                        }).orElse(false);
                if (!success) {
                    String errMsg = "update " + table + " db failed, reason: db result is illegal";
                    logger.error(errMsg);
                    future.fail(errMsg);
                }
            } else {
                String errMsg = "update " + table + " db failed, reason: " + updateRes.cause().getMessage();
                logger.error(errMsg);
                future.fail(errMsg);
            }
        });
        doUpdateWithParams(table, sql, params, updateFuture);
    }
}
