package expressions.term;
import expressions.Expression;
import expressions.ValueExpression;
import datastructures.Map;

public class MathExpression extends Expression {
    private Expression leftSubExpression;
    private Expression rightSubExpression;
    private MathType type;

    public MathExpression(MathType type, Expression leftSubExpression, Expression rightSubExpression){
        this.leftSubExpression = leftSubExpression;
        this.rightSubExpression = rightSubExpression;
        this.type = type;
    }

    public MathExpression(MathType type, Expression subExpression){
        this.leftSubExpression = subExpression;
        this.type = type;
    }

    private boolean typeIs(MathType type){
        return this.type == type;
    }

    @Override
    public Expression evaluate() throws Exception {
        if (rightSubExpression != null){    // wenn der Operator binär ist
            leftSubExpression = leftSubExpression.evaluate();
            rightSubExpression = rightSubExpression.evaluate();
            // wenn beide unter Expressions Werte sind
            if (leftSubExpression instanceof ValueExpression && rightSubExpression instanceof ValueExpression){
                // fehler zurückgeben wenn sie keine Doubles sind
    
                if (((ValueExpression) leftSubExpression).getValue() instanceof String
                        && ((ValueExpression) rightSubExpression).getValue() instanceof String)
                    return new ValueExpression<>(((String)((ValueExpression) leftSubExpression).getValue()) + ((String) ((ValueExpression) rightSubExpression).getValue()));
        
                else if (!(((ValueExpression) leftSubExpression).getValue() instanceof Double
                    || ((ValueExpression) rightSubExpression).getValue() instanceof Double))
                    throw new Exception("Double expected");

                // ausrechnen des Ergebnisses

                double left = (double) ((ValueExpression) leftSubExpression).getValue();
                double right = (double) ((ValueExpression) rightSubExpression).getValue();
                double result;

                if (typeIs(MathType.ADD))
                    result = left + right;
                else if (typeIs(MathType.SUB))
                    result = left - right;
                else if (typeIs(MathType.MUL))
                    result = left * right;
                else if (typeIs(MathType.DIV))
                    result = left / right;
                else if (typeIs(MathType.POWER))
                    result = Math.pow(left, right);
                else if (typeIs(MathType.MODULO))
                    result = left % right;
                else
                    throw new Exception("ADD, SUB, MUL, DIV, POWER or MODULO type expected");
                //Rückgabe des Ergebnisses
                return new ValueExpression<>(result);
            } // wenn die unter Expressions keine Werte sind wird einen neue MathExpression
            // mit den ausgewerteten unter Expressions zurückgegeben
            else return new MathExpression(type, leftSubExpression, rightSubExpression);

        } else{//wenn der Operator unär ist
            leftSubExpression = leftSubExpression.evaluate();
            // wenn die unter Expressions eine Werte Expression ist
            if (leftSubExpression instanceof ValueExpression){
                // fehler zurückgeben wenn der Wert kein Float ist
                if (!(((ValueExpression) leftSubExpression).getValue() instanceof Double))
                    throw new Exception("Double expected");
                // Auswertung der unären Operatoren
                if (typeIs(MathType.NEGATION))
                    return new ValueExpression<>(-(Double)((ValueExpression) leftSubExpression).getValue());
                else if(typeIs(MathType.SQRT))
                    return new ValueExpression<>(Math.sqrt((Double) ((ValueExpression) leftSubExpression).getValue()));
                else if(typeIs(MathType.SIN))
                    return new ValueExpression<>(Math.sin((Double) ((ValueExpression) leftSubExpression).getValue()));
                else if(typeIs(MathType.COS))
                    return new ValueExpression<>(Math.cos((Double) ((ValueExpression) leftSubExpression).getValue()));
                else if(typeIs(MathType.TAN))
                    return new ValueExpression<>(Math.tan((Double) ((ValueExpression) leftSubExpression).getValue()));
                else if(typeIs(MathType.LOG))
                    return new ValueExpression<>(Math.log((Double) ((ValueExpression) leftSubExpression).getValue()));
                else if(typeIs(MathType.EXP))
                    return new ValueExpression<>(Math.exp((Double) ((ValueExpression) leftSubExpression).getValue()));
                else
                    throw new Exception("negation, SQRT, SIN, COS, TAN or LOG expected");

            }// wenn die unter Expression kein Wert ist wird einen neue MathExpression
            // mit der ausgewerteten unter Expression zurückgegeben
            else
                return new MathExpression(type, leftSubExpression);
        }
    }

    @Override
    public Expression addMap(Map<String, Expression> map) {
        if (rightSubExpression == null){// Übergabe der map für unäre Operatoren
            return new MathExpression(type, leftSubExpression.addMap(map));
        } else {// übergabe der Map für binäre Operatoren
            return new MathExpression(type,
                    leftSubExpression.addMap(map),
                    rightSubExpression.addMap(map));
        }
    }

    @Override
    public Expression replace(String id, Expression replacement) {
        if (rightSubExpression == null){// Ersetzen für unäre Operatoren
            return new MathExpression(type, leftSubExpression.replace(id, replacement));
        } else {//Ersetzen für binäre Operatoren
            return new MathExpression(type,
                    leftSubExpression.replace(id, replacement),
                    rightSubExpression.replace(id, replacement));
        }
    }
}