package expressions;

import datastructures.Map;

public abstract class Expression {
    public abstract Expression evaluate() throws Exception;

    public abstract Expression addMap(Map<String, Expression> map);

    public abstract Expression replace(String id, Expression replacement);

}
