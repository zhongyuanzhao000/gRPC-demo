package server;

import grpc.HelloGrpc;
import grpc.HelloMessage;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

/**
 * 该类包含一个server端的简单逻辑
 * @author zhongyuan zhao
 * @date 2020.10.08
 */
public class ServerDemo {

    // 定义一个Server对象，监听端口来获取rpc请求，以进行下面的处理
    private Server server;

    //使用main方法来测试server端
    public static void main(String[] args) throws IOException, InterruptedException {

        final ServerDemo serverDemo = new ServerDemo();

        //启动server
        serverDemo.start();

        //block 一直到退出程序
        serverDemo.blockUntilShutdown();
    }

    /**
     * 启动一个Server实例，监听client端的请求并处理
     * @throws IOException
     */
    private void start() throws IOException {

        //server运行在的端口号
        int port = 50051;

        // 给server添加监听端口号，添加 包含业务处理逻辑的类，然后启动
        server = ServerBuilder.forPort(port)
                .addService(new HelloImpl())
                .build()
                .start();

    }

    /**
     * 阻塞server直到关闭程序
     * @throws InterruptedException
     */
    private void blockUntilShutdown() throws InterruptedException {

        if (server != null) {
            server.awaitTermination();
        }

    }


    /**
     * proto文件被编译后，在生成的HelloGrpc的抽象内部类HelloImplBase中包含了 proto中定义的服务接口的简单实现
     * 该HelloImpl类需要重写这些方法，添加需要的处理逻辑
     */
    static class HelloImpl extends HelloGrpc.HelloImplBase {

        // proto文件中的sayHello服务接口被编译后，在生成的HelloGrpc的抽象内部类HelloImplBase中有一个简单的实现
        // 因此，在server端需要重写这个方法，添加上相应的逻辑
        @Override
        public void sayHello(HelloMessage.HelloRequest req, StreamObserver<HelloMessage.HelloResponse> responseObserver) {

            HelloMessage.HelloResponse reply = HelloMessage.HelloResponse.newBuilder().setMessage("(server端的sayHello()方法处理结果) Hello," + req.getName()).build();


            // 调用onNext()方法来通知gRPC框架把reply 从server端 发送回 client端
            responseObserver.onNext(reply);

            // 表示完成调用
            responseObserver.onCompleted();
        }
    }

}
