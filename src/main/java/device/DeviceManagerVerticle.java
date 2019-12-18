package device;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import consts.IEventBusAddressConst;

import java.util.ArrayList;

/**
 * @author zhouz
 */
public class DeviceManagerVerticle extends AbstractVerticle  {

    private DeviceStoreHandler deviceStoreHandler;
    JsonObject request;
    String operationString;
    String targetString;
    String usernameString ;
    String devicenameString ;
    ArrayList<String> deviceList = new ArrayList<String>();

    @Override
    public void start(){
        this.deviceStoreHandler = new DeviceStoreHandler(vertx);
        initMsgStoreConsumer();
    }



    private void initMsgStoreConsumer() {
        vertx.eventBus().consumer(IEventBusAddressConst.SAVE_DEVICE_MESSAGE, message -> {
            request = (JsonObject) message.body();
            operationString= request.getString("operation");
            targetString= request.getString("target");
            usernameString = request.getString("username");
            devicenameString = request.getString("devicename");
            Future<JsonObject> saveFu = Future.future();
            saveFu.setHandler(saveRes -> {
                if (saveRes.failed()){
                    System.out.println( "saving error because" + saveRes.cause());
                    return;
                }
                message.reply(saveRes.result());
            });
            this.deviceStoreHandler.saveMessage(operationString,targetString,usernameString,devicenameString
            ,saveFu);
           /* if ("Device".equals(targetString)){
                if ("Create".equals(operationString)){
                    deviceList.add(devicenameString);
                    //System.out.println(deviceList);
                    message.reply(" Device "+ devicenameString + " add successfully!");
                }
            }
            else if ("Bind".equals(targetString)){
                String deviceName = devicenameString;
                if (!deviceList.contains(deviceName)){
                    message.reply("Device not found!");
                }
                else{
                    message.reply("true");
                }
            }*/


        });
    }
}
