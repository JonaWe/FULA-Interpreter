package datastructures;

import abiturklassen.linear.List;

public class Map<Key, Value> {
    private List<Pair<Key, Value>> pairList;

    public Map(){
        pairList = new List<>();
    }

    public Map(Key key, Value value){
        pairList = new List<>();
        add(key, value);
    }

    public Map<Key, Value> add(Key key, Value value){
        if (containsKey(key))
            remove(key);
        pairList.append(new Pair<>(key,value));
        return this;
    }

    public Map<Key, Value> add(Pair<Key, Value> pair){
        return add(pair.getKey(), pair.getValue());
    }

    public Map<Key, Value> add(Map<Key, Value> addMap){
        List<Pair<Key, Value>> pairList = addMap.getPairList();
        for (pairList.toFirst();pairList.hasAccess();pairList.next()){
            add(pairList.getContent());
        }
        return this;
    }

    public boolean remove(Key key){
        for (pairList.toFirst();pairList.hasAccess();pairList.next()){
            if (pairList.getContent().getKey().equals(key)){
                pairList.remove();
                return true;
            }
        }
        return false;
    }

    public boolean containsKey(Key key){
        for (pairList.toFirst();pairList.hasAccess();pairList.next()){
            if (pairList.getContent().getKey().equals(key))
                return true;
        }
        return false;
    }

    public Value getValue(Key key){
        for (pairList.toFirst();pairList.hasAccess();pairList.next()){
            if (pairList.getContent().getKey().equals(key))
                return pairList.getContent().getValue();
        }
        return null;
    }

    public List<Pair<Key, Value>> getPairList() {
        return pairList;
    }
}
