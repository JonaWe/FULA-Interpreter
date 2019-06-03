package expressions.lambda;

import expressions.Expression;
import datastructures.Map;

public class AppExpression extends Expression {
    private Expression leftSubExpression;
    private Expression rightSubExpression;

    public AppExpression(Expression leftSubExpression, Expression rightSubExpression){
        this.leftSubExpression =  leftSubExpression;
        this.rightSubExpression = rightSubExpression;
    }

    @Override
    public Expression evaluate() throws Exception {
        leftSubExpression = leftSubExpression.evaluate();
        if (leftSubExpression instanceof FunExpression){
            leftSubExpression = ((FunExpression) leftSubExpression).apply(rightSubExpression.evaluate());
            return leftSubExpression.evaluate();
        } else throw new Exception("Terms can only be applied on functions");
    }

    @Override
    public Expression addMap(Map<String, Expression> map) {
        return new AppExpression(leftSubExpression.addMap(map),
                rightSubExpression.addMap(map));
    }

    @Override
    public Expression replace(String id, Expression replacement) {
        return new AppExpression(leftSubExpression.replace(id, replacement),
                rightSubExpression.replace(id, replacement));
    }
}
