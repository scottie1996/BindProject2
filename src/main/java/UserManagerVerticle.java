import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;

public class UserManagerVerticle extends AbstractVerticle  {
    String address = "User";
    String name = "UserVerticle";
    String operationString;
    String targetString;
    String usernameString ;
    String devicenameString;

    //ArrayList<String> request = new ArrayList<String>();
    ArrayList<String> userList = new ArrayList<String>();

    @Override
    public void start(Future<Void> startFuture){
        try{
            startFuture.complete();
            vertx.eventBus().consumer(address,message -> {
                JsonObject request = (JsonObject) message.body();
                operationString= request.getString("operation");
                targetString= request.getString("target");
                usernameString = request.getString("username");
                devicenameString = request.getString("devicename");
                //request = getRequestArray.getRequestArray(j);
                if (targetString.equals("User")){
                    if(operationString.equals("Create")){
                        userList.add(usernameString);
                        message.reply(" User " + usernameString + " add successfully!");
                    }
                }
                else if (targetString.equals("Bind")){
                    String Username = usernameString;
                    if (!userList.contains(Username)){
                        message.reply("User not found!");
                    }
                    else{
                        message.reply("true");
                    }
                }
            });
        }catch (Exception e){
            startFuture.fail("something goes wrong..." + e.toString());
        }
    }

}
