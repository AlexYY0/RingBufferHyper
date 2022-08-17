package club.emperorws;

import club.emperorws.ringbuffer.RingBuffer;
import club.emperorws.ringbuffer.RingBufferCover;

/**
 * @auther: EmperorWS
 * @date: 2022/7/24 22:54
 * @description: RingBufferMain 环形队列启动测试类
 */
public class RingBufferMain {
    public static void main(String[] args) {

        RingBuffer<Integer> buffer = new RingBufferCover<>(10, Integer.class);

        for (int i = 0; i < 20; i++) {
            buffer.offer(i);
        }

        System.out.println("before poll buffer, buffer content is = " + buffer);

        buffer.pollList(15).forEach(System.out::println);

        System.out.println("16 is " + buffer.poll());

        for (int i = 20; i < 40; i++) {
            buffer.offer(i);
        }
        System.out.println("17 is " + buffer.poll());

        for (int i = 40; i < 60; i++) {
            buffer.offer(i);
        }
        System.out.println("18 is " + buffer.poll());
    }
}
