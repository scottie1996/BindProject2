import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.ArrayList;

/**
 * @author zhouz
 */
public class ServerVerticle extends AbstractVerticle {
    private HttpServer httpServer = null;
    private ArrayList<String> requestArray = new ArrayList<String>();
    private JsonObject r = new JsonObject();
    String operationString;
    String targetString;
    String usernameString ;
    String devicenameString ;


    @Override
    public void start(Future<Void> startFuture){
        startFuture.complete();
        httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);
        //获取并处理请求的消息体
        router.route().handler(BodyHandler.create());
        Route routeGet = router.route().method(HttpMethod.GET);
        Route routePost = router.route().method(HttpMethod.POST);
        routeGet.handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/plain");
            response.end("This is a get request!");
        });
        routePost.handler(routingContext -> {
            JsonObject request = routingContext.getBodyAsJson();
            operationString = request.getString("operation");
            targetString = request.getString("target");
            usernameString = request.getString("username");
            devicenameString = request.getString("devicename");
            HttpServerResponse response = routingContext.response();
            //response.putHeader("content-type", "text/plain");
            //response.end("This is a post request! ");
            // String destination = request.g
            if (targetString.equals("User")||targetString.equals("Device")){
                vertx.eventBus().send(targetString,request,messageAsyncResult -> {

                    if (messageAsyncResult.succeeded()){
                        String reply = (String) messageAsyncResult.result().body();
                        JsonObject r = new JsonObject();
                        r.put("Reply",reply);
                        response.end(r.toBuffer());
                    }
                    else {
                        response.end("User name or device name WRONG!");
                        //返回失败
                    }
                });
            }
            else if (targetString.equals("Bind")){

                vertx.eventBus().send("Device",request,deviceMessageAsyncResult -> {

                    if (deviceMessageAsyncResult.succeeded()){
                        if (deviceMessageAsyncResult.result().body().equals("true")){
                            vertx.eventBus().send("User",request,userMessageAsyncResult -> {
                                if (userMessageAsyncResult.result().body().equals("true")){
                                    if (userMessageAsyncResult.succeeded()){
                                        vertx.eventBus().send("Bind", request,bindMessageAsyncResult -> {
                                            if (bindMessageAsyncResult.succeeded()){
                                                String reply = (String) bindMessageAsyncResult.result().body();
                                                r.put("Reply",reply);
                                                response.end(r.toBuffer());
                                            }
                                        });
                                    }
                                }
                                else{
                                    String reply = (String) deviceMessageAsyncResult.result().body();
                                    r.put("Reply",reply);
                                    response.end(r.toBuffer());
                                }
                            });
                        }
                        else{
                            String reply = (String) deviceMessageAsyncResult.result().body();
                            r.put("Reply",reply);
                            response.end(r.toBuffer());
                        }
                           /* String reply =  (String) messageAsyncResult.result().body();
                            vertx.eventBus().send(request.getString("User"),request,messageAsyncResult2 -> {
                                String reply2 = (String) messageAsyncResult.result().body();
                            });*/
                    }
                });
                    /*vertx.eventBus().send("Bind", request,messageAsyncResult -> {
                       if (messageAsyncResult.succeeded()){

                           String reply = (String) messageAsyncResult.result().body();
                           JsonObject r = new JsonObject();
                           r.put("Reply",reply);
                           response.end(r.toBuffer());
                       }
                    });*/

            }

           /* vertx.deployVerticle(new ServerSenderVerticle(request),stringAsyncResult -> {
                if(stringAsyncResult.succeeded()){
                    System.out.println("send!!");
                }
                else{
                    System.out.println("not send ");
                }

            });*/
        });
        httpServer.requestHandler(router::accept).listen(8080);
    }

}
