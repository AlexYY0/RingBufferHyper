package club.emperorws.ringbuffer;

import club.emperorws.sequence.Sequence;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 覆盖型环形队列实现类
 * 环形队列（适用于多生产者，多消费者的环形队列）
 * 注：如果生产者的生产速率 > 消费者的消费速率，则会出现生产者覆盖未消费的数据
 * 此环形队列允许丢失部分数据，队列已满：不可阻塞添加；队列为空：不可阻塞消费。（容忍部分数据丢失）
 *
 * @param <T> 环形队列数据类型
 * @author: EmperorWS
 * @date: 2022/8/17 23:28
 * @description: RingBufferCover: 覆盖型环形队列实现类
 */
public class RingBufferCover<T> implements RingBuffer<T> {

    /**
     * 环形队列容量
     */
    private final int bufferSize;

    /**
     * 写数据的游标位置
     */
    private Sequence writeCursor;

    /**
     * 读数据的游标位置
     */
    private Sequence readCursor;

    /**
     * 环形队列存储数据的实际数组
     */
    private T[] buffer;

    /**
     * 初始化环形队列
     *
     * @param sourceBufferSize 环形队列容量（最终计算为2的N次方）
     * @param bufferObjClazz   环形队列的实体类型（方便初始化数组）
     */
    @SuppressWarnings("unchecked")
    public RingBufferCover(int sourceBufferSize, Class<T> bufferObjClazz) {
        //为了方便计算，环形队列的实际容量为2的N次方
        this.bufferSize = (int) Math.pow(2, Math.ceil(Math.log(sourceBufferSize) / Math.log(2)));

        this.writeCursor = new Sequence(-1, bufferSize);
        this.readCursor = new Sequence(-1, bufferSize);
        this.buffer = (T[]) Array.newInstance(bufferObjClazz, bufferSize);
    }

    /**
     * 往环形队列中存数据
     *
     * @param data 数据
     */
    public void offer(T data) {
        int index = writeCursor.incrementAndGet();
        buffer[index] = data;
    }

    /**
     * 从环形队列中取一条数据
     *
     * @return 环形队列中取出的数据
     */
    public T poll() {
        //下一个环形队列的桶下标
        int nextReadCursor = 0;
        //临时存储当前的读游标
        int tempReadCursor = 0;
        T temp = null;
        do {
            tempReadCursor = readCursor.get();
            //预计算、获取读数据游标
            nextReadCursor = readCursor.getIndex(tempReadCursor + 1);
            temp = buffer[nextReadCursor];
            //如果为null，则直接返回，读数据的游标也没有真正的移动
            if (Objects.isNull(temp)) {
                return null;
            }
        } while (!readCursor.compareAndSet(tempReadCursor, tempReadCursor + 1));
        //清空环形队列数据
        buffer[nextReadCursor] = null;
        return temp;
    }

    /**
     * 从环形队列中取pollNum条数据
     *
     * @param pollNum 取数据的个数
     * @return 环形队列中取出的pollNum条数据
     */
    public List<T> pollList(int pollNum) {
        List<T> pollList = new ArrayList<>();
        for (int i = 0; i < pollNum; i++) {
            T data = poll();
            if (Objects.isNull(data)) {
                break;
            }
            pollList.add(data);
        }
        return pollList;
    }

    @Override
    public String toString() {
        return "RingBufferCover{" +
                "bufferSize=" + bufferSize +
                ", writeCursor=" + writeCursor +
                ", readCursor=" + readCursor +
                ", buffer=" + Arrays.toString(buffer) +
                '}';
    }
}
