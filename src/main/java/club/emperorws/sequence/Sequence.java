package club.emperorws.sequence;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * 环形队列序号管理器
 * 环形队列桶下标的计算方式：序号%长度 == 索引，为了加快运算效率，采用位运算的方式，但是要求队列的容量为2的N次方
 *
 * @author: EmperorWS
 * @date: 2022/8/17 22:30
 * @description: Sequence: 环形队列序号管理器
 */
public class Sequence extends Number {

    //需要解决缓存行伪共享的问题@Contended
    //private AtomicInteger values;
    private AtomicIntegerArray values;

    /**
     * 解决缓存行伪共享的问题
     */
    private static final int VALUE_OFFSET = 15;

    private final int startValue;

    private final int endValue;

    public Sequence(int startValue, int endValue) {
        this.values = new AtomicIntegerArray(31);
        this.values.set(VALUE_OFFSET, startValue);
        this.startValue = startValue + 1;
        this.endValue = endValue;
    }

    /**
     * 环形数组桶下标的定位实现逻辑
     *
     * @return 下一个桶下标
     * @deprecated This method is deprecated and will be removed in a future release.
     */
    @Deprecated
    public final int incrementAndGet(int i) {
        int next;
        do {
            next = values.incrementAndGet(VALUE_OFFSET);
            if (next > endValue && values.compareAndSet(VALUE_OFFSET, next, startValue)) {
                return startValue;
            }
        } while (next > endValue);
        return next;
    }

    /**
     * “无限长数组”桶下标的定位实现逻辑（取模计算）
     *
     * @return 下一个桶下标
     */
    public int incrementAndGet() {
        return getIndex(values.incrementAndGet(VALUE_OFFSET));
    }

    /**
     * 修改当前桶值
     *
     * @param expect 期望变更的值
     * @param update 期望变更的值-->变更后的值
     * @return true 修改成功. 期望值expect与预期不符
     */
    public final boolean compareAndSet(int expect, int update) {
        return values.compareAndSet(VALUE_OFFSET, expect, update);
    }

    public int get() {
        return values.get(VALUE_OFFSET);
    }

    @Override
    public int intValue() {
        return values.get(VALUE_OFFSET);
    }

    @Override
    public long longValue() {
        return values.get(VALUE_OFFSET);
    }

    @Override
    public float floatValue() {
        return values.get(VALUE_OFFSET);
    }

    @Override
    public double doubleValue() {
        return values.get(VALUE_OFFSET);
    }

    /**
     * 获取环形队列的桶的位置下标（取模计算）
     *
     * @param cursor 当前位置的累加计数
     * @return 环形队列的桶的位置下标
     */
    public int getIndex(int cursor) {
        return cursor & (endValue - 1);
    }
}
