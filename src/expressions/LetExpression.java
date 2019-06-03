package expressions;

import datastructures.Map;

public class LetExpression extends Expression {
    private Map<String, Expression> map;
    private Expression subExpression;

    public LetExpression(Map<String, Expression> map, Expression subExpression) {
        this.map = map;
        this.subExpression = subExpression;
    }

    @Override
    public Expression evaluate() throws Exception {
        return subExpression.addMap(map).evaluate();
    }

    @Override
    public Expression addMap(Map<String, Expression> map) {
        return new LetExpression(map.add(this.map), subExpression);
    }

    @Override
    public Expression replace(String id, Expression replacement) {
        return new LetExpression(this.map, subExpression.replace(id, replacement));
    }
}
