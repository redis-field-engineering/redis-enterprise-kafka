package com.redis.kafka.connect.sink;

import java.util.Objects;
import java.util.function.Function;

import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.async.BaseRedisAsyncCommands;
import io.lettuce.core.api.async.RedisKeyAsyncCommands;

import com.redis.spring.batch.writer.operation.AbstractKeyWriteOperation;

public class Publish<K, V, T> extends AbstractKeyWriteOperation<K, V, T> {

    private Function<T, V> valueFunction;

    public void setValueFunction(Function<T, V> function) {
        this.valueFunction = function;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected RedisFuture<Long> execute(BaseRedisAsyncCommands<K, V> commands, T item, K key) {
        V value = valueFunction.apply(item);
        if (Objects.isNull(value)) {
            return ((RedisKeyAsyncCommands<K, V>) commands).del(key);
        }
        return ((BaseRedisAsyncCommands<K, V>) commands).publish(key, value);
    }

}