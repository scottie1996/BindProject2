import io.vertx.core.*;

import java.util.ArrayList;

public class ServerLauncher {
    private ArrayList<String> verticles = new ArrayList<String>();
    private Vertx vertx;
    public static void main(String args[]){
        new ServerLauncher().start();
    }
    private void start(){
        getVerticles();
        loadVerticle();
    }

    private void getVerticles() {//规定Verticle的启动顺序
        this.verticles.add(UserManagerVerticle.class.getName());
        this.verticles.add(DeviceManagerVerticle.class.getName());
        this.verticles.add(BindingRelationVerticle.class.getName());
        this.verticles.add(ServerVerticle.class.getName());
    }
    private void loadVerticle(){
        Future<Void> createVertxFut = Future.future();
        createVertx(createVertxFut);
        createVertxFut.setHandler(voidAsyncResult -> {
            if (voidAsyncResult.succeeded()){
                this.deployVerticles(voidAsyncResult1 -> {
                  if (voidAsyncResult1.succeeded()){
                      System.out.println("部署成功");
                  }
                  else{
                      System.out.println("部署失败");
                  }
                });
            }
            else {
                System.out.println("创建Vertx失败：" + voidAsyncResult.cause());
            }
        });
    }

    private void createVertx(Future<Void> future) {
        vertx = Vertx.vertx();
        future.complete();
    }

    private void deployVerticles(Handler<AsyncResult<Void>> handler) {
         this.deployOne(0, handler);
    }

    private void deployOne(int index, Handler<AsyncResult<Void>> handler){
        if (index >= this.verticles.size()) {
            handler.handle(Future.failedFuture("部署失败，verticle数量有错"));
        } else {
            String verticleName = this.verticles.get(index);
            this.deploy(verticleName, r -> {
                if (r.succeeded()) {
                    if (index == this.verticles.size() - 1) {
                        handler.handle(Future.succeededFuture());
                    } else {
                        this.deployOne(index + 1, handler);
                    }
                } else {
                    handler.handle(Future.failedFuture(r.cause()));
                }
            });
        }
    }
    private void deploy(String name ,Handler<AsyncResult<String>> handler){
        vertx.deployVerticle(name,stringAsyncResult -> {
            if (stringAsyncResult.succeeded()){
                String deployId = stringAsyncResult.result();
                handler.handle(Future.succeededFuture(deployId));
                System.out.println(name + " 部署成功 [" + deployId + "]");
            }
            else{
                handler.handle(Future.failedFuture(stringAsyncResult.cause()));
                System.out.println(name + " 部署失败  " + stringAsyncResult.cause());
            }
        });
    }

}

