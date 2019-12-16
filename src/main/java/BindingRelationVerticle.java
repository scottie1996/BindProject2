import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * @author zhouz
 */
public class BindingRelationVerticle extends AbstractVerticle {
    String address = "Bind";
    String name = "BindingVerticle";
    String operationString;
    String targetString;
    String usernameString ;
    String devicenameString ;
    HashMap<String,String> bindMap = new HashMap<>();

    @Override
    public void start(Future<Void> startFuture){
        startFuture.complete();
        vertx.eventBus().consumer(address,message -> {
            JsonObject request = (JsonObject) message.body();
            operationString= request.getString("operation");
            targetString= request.getString("target");
            usernameString = request.getString("username");
            devicenameString = request.getString("devicename");
            //request = getRequestArray.getRequestArray(j);
            //if(operationString.equals("Create")){
            if("Create".equals(operationString)){
                bindMap.put(usernameString,devicenameString);
               // System.out.println(BindMap);
                message.reply("User " + usernameString +" and Device " + devicenameString + " bind successfully!");
            }
        });
    }

}
