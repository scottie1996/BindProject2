package device;

import dao.ISQLDBDao;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * @author zhouz
 */
public interface DeviceDao extends ISQLDBDao {



public void add(String tableName,String deviceName, Future<JsonObject> future);

    @Override
    public void insert(String table, String data, Future<JsonObject> future);

}

