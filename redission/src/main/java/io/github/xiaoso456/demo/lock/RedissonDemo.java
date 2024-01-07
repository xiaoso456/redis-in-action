package io.github.xiaoso456.demo.lock;

public class RedissonDemo {
    public static void main(String[] args) {

        new Thread(new EchoTask("lockKey2", 1500L, "message1")).start();
        new Thread(new EchoTask("lockKey2", 1000L, "message2")).start();



    }
}
