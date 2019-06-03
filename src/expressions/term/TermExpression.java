package expressions.term;
import expressions.Expression;
import expressions.ValueExpression;
import datastructures.Map;

public class TermExpression extends Expression {
    private Expression leftSubExpression;
    private Expression rightSubExpression;
    private TermType type;

    public TermExpression(TermType type, Expression leftSubExpression, Expression rightSubExpression){
        this.leftSubExpression = leftSubExpression;
        this.rightSubExpression = rightSubExpression;
        this.type = type;
    }

    public TermExpression(TermType type, Expression subExpression){
        this.leftSubExpression = subExpression;
        this.type = type;
    }

    private boolean typeIs(TermType type){
        return this.type == type;
    }

    @Override
    public Expression evaluate() throws Exception {
        if (rightSubExpression != null){    //binary
            leftSubExpression = leftSubExpression.evaluate();
            rightSubExpression = rightSubExpression.evaluate();
            if (leftSubExpression instanceof ValueExpression && rightSubExpression instanceof ValueExpression){
                if (!(((ValueExpression) leftSubExpression).getValue() instanceof Double
                        || ((ValueExpression) rightSubExpression).getValue() instanceof Double))
                    throw new Exception("Double expected");
                double left = (double) ((ValueExpression) leftSubExpression).getValue();
                double right = (double) ((ValueExpression) rightSubExpression).getValue();
                double result;

                if (typeIs(TermType.ADD))
                    result = left + right;
                else if (typeIs(TermType.SUB))
                    result = left - right;
                else if (typeIs(TermType.MUL))
                    result = left * right;
                else if (typeIs(TermType.DIV))
                    result = left / right;
                else if (typeIs(TermType.POWER))
                    result = Math.pow(left, right);
                else if (typeIs(TermType.MODULO))
                    result = left % right;
                else
                    throw new Exception("ADD, SUB, MUL, DIV, POWER or MODULO type expected");
                return new ValueExpression<>(result);
            } else return new TermExpression(type, leftSubExpression, rightSubExpression);

        } else{                             //unary
            leftSubExpression = leftSubExpression.evaluate();
            if (leftSubExpression instanceof ValueExpression){
                if (!(((ValueExpression) leftSubExpression).getValue() instanceof Double))
                    throw new Exception("Double expected");
                if (typeIs(TermType.NEGATION))
                    return new ValueExpression<>(-(Double)((ValueExpression) leftSubExpression).getValue());
                else if(typeIs(TermType.SQRT))
                    return new ValueExpression<>(Math.sqrt((Double) ((ValueExpression) leftSubExpression).getValue()));
                else if(typeIs(TermType.SIN))
                    return new ValueExpression<>(Math.sin((Double) ((ValueExpression) leftSubExpression).getValue()));
                else if(typeIs(TermType.COS))
                    return new ValueExpression<>(Math.cos((Double) ((ValueExpression) leftSubExpression).getValue()));
                else if(typeIs(TermType.TAN))
                    return new ValueExpression<>(Math.tan((Double) ((ValueExpression) leftSubExpression).getValue()));
                else if(typeIs(TermType.LOG))
                    return new ValueExpression<>(Math.log((Double) ((ValueExpression) leftSubExpression).getValue()));
                else if(typeIs(TermType.EXP))
                    return new ValueExpression<>(Math.exp((Double) ((ValueExpression) leftSubExpression).getValue()));
                else
                    throw new Exception("negation, SQRT, SIN, COS, TAN or LOG expected");

            } else
                return new TermExpression(type, leftSubExpression);
        }
    }

    @Override
    public Expression addMap(Map<String, Expression> map) {
        if (rightSubExpression == null){
            return new TermExpression(type, leftSubExpression.addMap(map));
        } else {
            return new TermExpression(type,
                    leftSubExpression.addMap(map),
                    rightSubExpression.addMap(map));
        }
    }

    @Override
    public Expression replace(String id, Expression replacement) {
        if (rightSubExpression == null){
            return new TermExpression(type, leftSubExpression.replace(id, replacement));
        } else {
            return new TermExpression(type,
                    leftSubExpression.replace(id, replacement),
                    rightSubExpression.replace(id, replacement));
        }
    }
}