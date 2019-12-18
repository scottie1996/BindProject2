package device;

import dao.ISQLDBDao;
import dao.impl.MysqlDB;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MySQLDeviceDao implements DeviceDao {

    private Vertx vertx;
    private ISQLDBDao messageDBClient;

    MySQLDeviceDao(Vertx vertx){
        this.vertx = vertx;
        this.messageDBClient = new MysqlDB(vertx);
    }
    @Override
    public void add(String tableName, String deviceName, Future<JsonObject> future){
    //this.messageDBClient.insert();
    }



    @Override
    public void queryByID(String table, String idKey, String idValue, List<String> fields, Future<List<JsonObject>> future) {

    }

    @Override
    public void query(String table, List<String> fields, Map<String, Object> conditions, Future<List<JsonObject>> future) {

    }

    @Override
    public void query(String table, String query, Future<List<JsonObject>> future) {

    }

    @Override
    public void insert(String table, Map<String, Object> data, Future<Boolean> future) {

    }

    @Override
    public void insert(String table, String data, Future<JsonObject> future) {
        this.messageDBClient.insert(table, data ,future);
    }

    @Override
    public void insert(String table, Set<String> fieldSet, List<Map<String, Object>> data, Future<JsonObject> future) {

    }


    @Override
    public void updateByID(String table, String idKey, String idValue, Map<String, Object> data, Future<Boolean> future) {

    }

    @Override
    public void updateSingle(String table, Map<String, Object> data, Map<String, Object> conditions, Future<Boolean> future) {

    }

    @Override
    public void update(String table, Map<String, Object> data, Map<String, Object> conditions, Future<JsonObject> future) {

    }

    @Override
    public void update(String table, String update, Future<JsonObject> future) {

    }

    @Override
    public void updateSingle(String table, String update, Future<Boolean> future) {

    }

    @Override
    public void close() {

    }

    @Override
    public void close(Handler<AsyncResult<Void>> handler) {

    }
}
