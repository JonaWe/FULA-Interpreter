package datastructures;

import abiturklassen.linear.List;

public class Map<K, V> {
    private List<Pair<K, V>> pairList;

    public Map(){
        pairList = new List<>();
    }

    public Map(K key, V value){
        pairList = new List<>();
        add(key, value);
    }

    public Map<K, V> add(K key, V value){
        if (containsKey(key))
            remove(key);
        pairList.append(new Pair<>(key,value));
        return this;
    }

    public Map<K, V> add(Pair<K, V> pair){
        return add(pair.getKey(), pair.getValue());
    }

    public Map<K, V> add(Map<K, V> addMap){
        List<Pair<K, V>> pairList = addMap.getPairList();
        for (pairList.toFirst();pairList.hasAccess();pairList.next()){
            add(pairList.getContent());
        }
        return this;
    }

    public boolean remove(K key){
        for (pairList.toFirst();pairList.hasAccess();pairList.next()){
            if (pairList.getContent().getKey().equals(key)){
                pairList.remove();
                return true;
            }
        }
        return false;
    }

    public boolean containsKey(K key){
        for (pairList.toFirst();pairList.hasAccess();pairList.next()){
            if (pairList.getContent().getKey().equals(key))
                return true;
        }
        return false;
    }

    public V getValue(K key){
        for (pairList.toFirst();pairList.hasAccess();pairList.next()){
            if (pairList.getContent().getKey().equals(key))
                return pairList.getContent().getValue();
        }
        return null;
    }

    public List<Pair<K, V>> getPairList() {
        return pairList;
    }
}
