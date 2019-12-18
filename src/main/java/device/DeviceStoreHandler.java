package device;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class DeviceStoreHandler {
    private Vertx vertx;
    private DeviceDao deviceMessageWriteDao;
    public DeviceStoreHandler(Vertx vertx){
        this.vertx = vertx;
        this.deviceMessageWriteDao = new MySQLDeviceDao(vertx);
    }
    public void saveMessage(String operationString, String targetString , String usernameString,
                            String devicenameString, Future<JsonObject> future){
  /*  String tableName = this.messageRouteHandler.getDBTableName(operationString, targetString ,  usernameString,
           devicenameString);*/
    String tableName = "deviceTable";
    if (tableName == null){
        future.fail("Table name not exist");
        return;
    }
        Future<JsonObject> saveFu = Future.future();
        saveFu.setHandler(saveRes -> {
            if (saveRes.succeeded()){
                future.complete(saveRes.result());
                return;
            }
            System.out.println("save failed because" + saveRes.cause());
        });
    this.deviceMessageWriteDao.insert(tableName,devicenameString,future);
    }
}
