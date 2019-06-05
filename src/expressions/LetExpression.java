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
        // es wird die Map zu der sub Expression hinzugefügt
        // danach wird die unter Expression ausgewertet
        return subExpression.addMap(map).evaluate();
    }

    @Override
    public Expression addMap(Map<String, Expression> map) {
        // die addMap Methode wird für die unter Expression aufgerufen
        return new LetExpression(map.add(this.map), subExpression);
    }

    @Override
    public Expression replace(String id, Expression replacement) {
        // die addMap Methode wird für die unter Expression aufgerufen
        return new LetExpression(this.map, subExpression.replace(id, replacement));
    }
}
