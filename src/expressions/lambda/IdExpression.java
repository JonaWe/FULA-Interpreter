package expressions.lambda;

import datastructures.Map;
import expressions.Expression;

public class IdExpression extends Expression {
    private String id;
    private Map<String, Expression> map;

    public IdExpression(String id) {
        this.id = id;
        map = new Map<>();
    }

    @Override
    public Expression evaluate() throws Exception {
        if (map.containsKey(id)) {
            Expression replacement = map.getValue(id);
            replacement = replacement.addMap(map);
            return replacement;
        } else return new IdExpression(id).addMap(map);
    }

    @Override
    public Expression addMap(Map<String, Expression> map) {
        this.map = map.add(this.map);
        return this;
    }

    @Override
    public Expression replace(String id, Expression replacement) {
        if (this.id.equals(id)) return replacement;
        else return new IdExpression(this.id).addMap(this.map.add(id, replacement));
    }
}