package model.adt;

import java.util.Map;

public interface MyIDictionary<K, V> {
    V get(K key);
    void put(K key, V value);
    boolean isDefined(K key);
    Map<K, V> getContent();
    MyIDictionary<K,V> deepcopy();
}
