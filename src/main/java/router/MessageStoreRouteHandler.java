package router;

import javax.naming.Binding;

public class MessageStoreRouteHandler {

    public String getDBTableName(String operationString, String targetString,
                               String usernameString, String devicenameString) {
        if ("Device".equals(targetString)){
            return "deviceTable";
        }
        else if ("Bind".equals(targetString)){
           return "BindingTable";
        }
        else {
            return "userTable";
        }
    }
}
