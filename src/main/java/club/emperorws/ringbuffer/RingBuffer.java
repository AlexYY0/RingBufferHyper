package club.emperorws.ringbuffer;

import java.util.List;

/**
 * @param <T> 环形队列数据类型
 * @auther: EmperorWS
 * @date: 2022/7/24 22:59
 * @description: RingBuffer 环形队列实现类
 */
public interface RingBuffer<T> {

    /**
     * 往环形队列中存数据
     *
     * @param data 数据
     */
    void offer(T data);

    /**
     * 从环形队列中取一条数据
     *
     * @return 环形队列中取出的数据
     */
    T poll();

    /**
     * 从环形队列中取pollNum条数据
     *
     * @param pollNum 取数据的个数
     * @return 环形队列中取出的pollNum条数据
     */
    List<T> pollList(int pollNum);

}
