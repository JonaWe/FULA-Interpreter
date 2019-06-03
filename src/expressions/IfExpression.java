package expressions;

import datastructures.Map;

public class IfExpression extends Expression {
    private Expression trueExpression;
    private Expression falseExpression;
    private Expression condition;

    public IfExpression(Expression condition, Expression trueExpression, Expression falseExpression){
        this.condition = condition;
        this.trueExpression = trueExpression;
        this.falseExpression = falseExpression;
    }

    @Override
    public Expression evaluate() throws Exception {
        condition = condition.evaluate();
        if (condition instanceof ValueExpression){
            if (!(((ValueExpression) condition).getValue() instanceof Boolean))
                throw new Exception("IfExpression requires a Boolean as a condition");
            if ((boolean) ((ValueExpression) condition).getValue()){
                //assert condition == true
                return trueExpression.evaluate();
            } else{
                //assert condition == false
                return falseExpression.evaluate();

            }
        } else return new IfExpression(condition, trueExpression, falseExpression);
    }

    @Override
    public Expression addMap(Map<String, Expression> map) {
        return new IfExpression(condition.addMap(map),
                trueExpression.addMap(map),
                falseExpression.addMap(map));
    }

    @Override
    public Expression replace(String id, Expression replacement) {
        return new IfExpression(condition.replace(id, replacement),
                trueExpression.replace(id, replacement),
                falseExpression.replace(id, replacement));
    }
}
