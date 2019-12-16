import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;


import java.util.ArrayList;

/**
 * @author zhouz
 */
public class DeviceManagerVerticle extends AbstractVerticle  {
    String address = "Device";
    String name = "DeviceVerticle";
    String operationString;
    String targetString;
    String usernameString ;
    String devicenameString ;
    ArrayList<String> deviceList = new ArrayList<String>();

    @Override
    public void start(Future<Void> startFuture){
        startFuture.complete();
        System.out.println("UserManager start successfully");
        vertx.eventBus().consumer(address , message -> {
            JsonObject request = (JsonObject) message.body();
            operationString= request.getString("operation");
            targetString= request.getString("target");
            usernameString = request.getString("username");
            devicenameString = request.getString("devicename");
            //request = getRequestArray.getRequestArray(j);
            if ("Device".equals(targetString)){
                if ("Create".equals(operationString)){
                    deviceList.add(devicenameString);
                    //System.out.println(deviceList);
                    message.reply(" Device "+ devicenameString + " add successfully!");
                }
            }
            else if (targetString.equals("Bind")){
                String deviceName = devicenameString;
                if (!deviceList.contains(deviceName)){
                    message.reply("Device not found!");
                }
                else{
                    message.reply("true");
                }
            }

        });

    }
}
