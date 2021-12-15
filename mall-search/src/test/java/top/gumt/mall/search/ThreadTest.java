package top.gumt.mall.search;

import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.*;


public class ThreadTest {

    public static ExecutorService service = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main start");
//        CompletableFuture.runAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果：" + i);
//        }, service);

//        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, service);
//        Integer integer = completableFuture.get();

//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() ->{
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, service).whenComplete((res, exception) -> {
//            // whenComplete 虽然可以得到异常信息，但是没法修改返回数据
//            System.out.println("异步任务完成了...结果是：" + res + ";异常是：" + exception);
//        }).exceptionally(throwable -> {
//            // 如果出现异常 返回默认值
//            return 10;
//        });
//        System.out.println(future.get());

//        CompletableFuture<Integer> handle = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 5;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, service).handle((res, thr) -> {
//            if (res != null) {
//                return res * 2;
//            }
//            if (thr != null) {
//                return 0;
//            }
//            return 0;
//        });
//        Integer integer = handle.get();
//        System.out.println("结果：" + integer);

//        CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 4;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, service).thenAcceptAsync((res) -> {
//            System.out.println("任务2启动了..." + res);
//        }, service);

        CompletableFuture<String> futureImg = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品图片");
            return "xxx.png";
        }, service);

        CompletableFuture<String> futureAttr = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品属性");
            return "黑色+256G";
        }, service);
        CompletableFuture<String> futureDesc = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品简介");
            return "华为";
        }, service);
//        CompletableFuture<Void> allOf = CompletableFuture.allOf(futureImg, futureAttr, futureDesc);
        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(futureImg, futureAttr, futureDesc);
        anyOf.get();

        System.out.println("main end");
    }

    public static class Thread01 extends Thread {
        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }
    }

    public static class Runable01 implements Runnable {
        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }
    }

    public static class Callable01 implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            return i;
        }
    }
}
