import io.vertx.core.json.JsonObject;

import java.util.ArrayList;

public interface getRequestArray {
    public static ArrayList<String> getRequestArray(JsonObject request){
        ArrayList<String> requestArray = new ArrayList<String>();
        String operationString= request.getString("operation");
        String targetString= request.getString("target");
        String usernameString = request.getString("username");
        String devicenameString = request.getString("devicename");
        requestArray.add(operationString);
        requestArray.add(targetString);
        requestArray.add(usernameString);
        requestArray.add(devicenameString);
        return requestArray;
    }
}
