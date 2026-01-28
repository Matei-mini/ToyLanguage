package model.adt;

import java.util.HashMap;
import java.util.Map;

public class MyDictionary<K, V> implements MyIDictionary<K, V> {
    private final Map<K, V> map;

    public MyDictionary() {
        this.map = new HashMap<>();
    }

    @Override
    public V get(K key) {
        return map.get(key);
    }

    @Override
    public void put(K key, V value) {
        map.put(key, value);
    }

    @Override
    public boolean isDefined(K key) {
        return map.containsKey(key);
    }

    @Override
    public Map<K, V> getContent(){
        return map;
    }

    @Override
    public String toString() {
        return map.toString();
    }

    @Override
    public MyIDictionary<K, V> deepcopy() {
        MyIDictionary<K, V> copy = new MyDictionary<>();
        for(K key: map.keySet()){
            copy.put(key, map.get(key));
        }
        return copy;
    }
}
