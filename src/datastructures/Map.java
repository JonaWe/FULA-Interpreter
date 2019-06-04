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
        /*Map<K, V> newMap = getCopy();
        List<Pair<K, V>> l = addMap.getPairList();
        for (l.toFirst();l.hasAccess();l.next()){
            newMap.add(l.getContent().getKey(), l.getContent().getValue());
        }*/
        List<Pair<K, V>> pairList = addMap.getPairList();
        Map<K, V> map = this;
        for (pairList.toFirst();pairList.hasAccess();pairList.next()){
            map = map.add(pairList.getContent());
        }
        return map;
    }

    private Map<K, V> getCopy(){
        Map<K, V> copy = new Map<>();
        for (pairList.toFirst();pairList.hasAccess();pairList.next()){
            copy.add(pairList.getContent().getKey(), pairList.getContent().getValue());
        }
        return copy;
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
