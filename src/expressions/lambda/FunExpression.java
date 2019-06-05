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
        // IN der unter Expression der Funktion werden alle Bezeichner mit der id = id durch replacement ersetzt
        subExpression = subExpression.replace(id, replacement);
        return subExpression;
    }

    @Override
    public Expression evaluate() throws Exception {
        return new FunExpression(id, subExpression);
    }

    @Override
    public Expression addMap(Map<String, Expression> map) {
        // die Map wird der unter Expression weitergegeben
        return new FunExpression(this.id, subExpression.addMap(map));
    }

    @Override
    public Expression replace(String id, Expression replacement) {
        // weitergabe der Ersetzung and die unter Expression
        return new FunExpression(this.id, subExpression.replace(id,replacement));
    }
}