package expressions.bools;

import expressions.Expression;
import expressions.ValueExpression;
import datastructures.Map;

public class BoolExpression extends Expression {
    private Expression leftSubExpression;
    private Expression rightSubExpression;
    private BoolType type;

    public BoolExpression(BoolType type, Expression leftSubExpression, Expression rightSubExpression){
        this.leftSubExpression = leftSubExpression;
        this.rightSubExpression = rightSubExpression;
        this.type = type;
    }

    public BoolExpression(BoolType type, Expression subExpression){
        this.leftSubExpression = subExpression;
        this.type = type;
    }

    private boolean typeIs(BoolType type){
        return this.type == type;
    }

    @Override
    public Expression evaluate() throws Exception {
        if (rightSubExpression != null){        //binary
            leftSubExpression = leftSubExpression.evaluate();
            if (typeIs(BoolType.OR) || typeIs(BoolType.AND)){
                if (leftSubExpression instanceof ValueExpression){
                    if (!(((ValueExpression) leftSubExpression).getValue() instanceof Boolean)) throw new Exception("Boolean expected");
                    if (typeIs(BoolType.OR) && (Boolean) ((ValueExpression) leftSubExpression).getValue())
                        return new ValueExpression<>(true);
                    else if (typeIs(BoolType.AND) && !((Boolean) ((ValueExpression) leftSubExpression).getValue()))
                        return new ValueExpression<>(false);
                }
            }
            rightSubExpression = rightSubExpression.evaluate();
            if (leftSubExpression instanceof ValueExpression && rightSubExpression instanceof ValueExpression) {
                if (((ValueExpression) leftSubExpression).getValue() instanceof Boolean
                        && ((ValueExpression) rightSubExpression).getValue() instanceof Boolean){
                    boolean left = (boolean) ((ValueExpression) leftSubExpression).getValue();
                    boolean right = (boolean) ((ValueExpression) rightSubExpression).getValue();
                    boolean bool;
                    if (typeIs(BoolType.AND))
                        bool = left && right;
                    else if (typeIs(BoolType.OR))
                        bool = left || right;
                    else if (typeIs(BoolType.XOR))
                        bool = left ^ right;
                    else if (typeIs(BoolType.EQU))
                        bool = left == right;
                    else if (typeIs(BoolType.NOTEQU))
                        bool = left != right;
                    else throw new Exception("AND, OR, EQUALS, NOT EQUALS or XOR operation expected");
                    return new ValueExpression<>(bool);
                } else if(((ValueExpression) leftSubExpression).getValue() instanceof Double
                        && ((ValueExpression) rightSubExpression).getValue() instanceof Double){
                    double left = (double) ((ValueExpression) leftSubExpression).getValue();
                    double right = (double) ((ValueExpression) rightSubExpression).getValue();
                    boolean bool;
                    if (typeIs(BoolType.SMALER))
                        bool = left < right;
                    else if (typeIs(BoolType.SMALEREQU))
                        bool = left <= right;
                    else if (typeIs(BoolType.GREATER))
                        bool = left > right;
                    else if (typeIs(BoolType.GREATEREQU))
                        bool = left >= right;
                    else if (typeIs(BoolType.EQU))
                        bool = left == right;
                    else if (typeIs(BoolType.NOTEQU))
                        bool = left != right;
                    else throw new Exception("<, >=, >, >=, = or != expected");
                    return new ValueExpression<>(bool);
                } else throw new Exception("Boolean or Integer expected");
            } else return new BoolExpression(type, leftSubExpression, rightSubExpression);
        } else{                             //unary
            leftSubExpression = leftSubExpression.evaluate();
            if (leftSubExpression instanceof ValueExpression){
                if (!(((ValueExpression) leftSubExpression).getValue() instanceof Boolean))
                    throw new Exception("Boolean expected instead found Double");
                if (typeIs(BoolType.NOT))
                    return new ValueExpression<>(!((boolean) ((ValueExpression) leftSubExpression).getValue()));
                else
                    throw new Exception("Not operation expected");
            } else return new BoolExpression(type, leftSubExpression);
        }
    }

    @Override
    public Expression addMap(Map<String, Expression> map) {
        if (rightSubExpression == null){
            return new BoolExpression(type, leftSubExpression.addMap(map));
        } else {
            return new BoolExpression(type,
                    leftSubExpression.addMap(map),
                    rightSubExpression.addMap(map));
        }
    }

    @Override
    public Expression replace(String id, Expression replacement) {
        if (rightSubExpression == null) {
            return new BoolExpression(type, leftSubExpression.replace(id, replacement));
        } else {
            return new BoolExpression(type,
                    leftSubExpression.replace(id, replacement),
                    rightSubExpression.replace(id, replacement));
        }
    }
}