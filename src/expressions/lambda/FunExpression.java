package expressions.lambda;
import expressions.Expression;
import datastructures.Map;

public class FunExpression extends Expression {
    private Expression subExpression;
    private String id;

    public FunExpression(String id, Expression subExpression){
        this.id = id;
        this.subExpression = subExpression;
    }

    Expression apply(Expression replacement){
        subExpression = subExpression.replace(id, replacement);
        return subExpression;
    }

    @Override
    public Expression evaluate() throws Exception {
        return new FunExpression(id, subExpression);
    }

    @Override
    public Expression addMap(Map<String, Expression> map) {
        return new FunExpression(this.id, subExpression.addMap(map));
    }

    @Override
    public Expression replace(String id, Expression replacement) {
        return new FunExpression(this.id, subExpression.replace(id,replacement));
    }
}