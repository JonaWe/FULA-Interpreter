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
        if (rightSubExpression != null){// wenn der Operator binär ist
            // linke Expression wird ausgewertet
            leftSubExpression = leftSubExpression.evaluate();
            // wenn der Operator Oder oder Und ist wird getestet, ob die rechte Expression nicht ausgewertet werden
            // muss um ein Ergebnis zu liefern. false && leftExpression müsste leftExpression nicht auswerten,
            // da unabhängig davon false zurückgegeben wird
            if ((typeIs(BoolType.OR) || typeIs(BoolType.AND)) && leftSubExpression instanceof ValueExpression){
                if (!(((ValueExpression) leftSubExpression).getValue() instanceof Boolean)) throw new Exception("Boolean expected");
                if (typeIs(BoolType.OR) && (Boolean) ((ValueExpression) leftSubExpression).getValue())
                    return new ValueExpression<>(true);
                else if (typeIs(BoolType.AND) && !((Boolean) ((ValueExpression) leftSubExpression).getValue()))
                    return new ValueExpression<>(false);
            }
            rightSubExpression = rightSubExpression.evaluate();
            // wenn beide unter Expressions Werte Expressions sind
            if (leftSubExpression instanceof ValueExpression && rightSubExpression instanceof ValueExpression) {
                if (((ValueExpression) leftSubExpression).getValue() instanceof Boolean
                        && ((ValueExpression) rightSubExpression).getValue() instanceof Boolean){
                    // wenn sie vom typen Boolean sind wird das Ergebnis berechnet und zurückgegeben
                    boolean left = (boolean) ((ValueExpression) leftSubExpression).getValue();
                    boolean right = (boolean) ((ValueExpression) rightSubExpression).getValue();
                    boolean result;
                    if (typeIs(BoolType.AND))
                        result = left && right;
                    else if (typeIs(BoolType.OR))
                        result = left || right;
                    else if (typeIs(BoolType.XOR))
                        result = left ^ right;
                    else if (typeIs(BoolType.EQU))
                        result = left == right;
                    else if (typeIs(BoolType.NOTEQU))
                        result = left != right;
                    else throw new Exception("AND, OR, EQUALS, NOT EQUALS or XOR operation expected");
                    return new ValueExpression<>(result);
                    // sonst wird geprüft, ob beide unter Expressions Doubles sind
                } else if(((ValueExpression) leftSubExpression).getValue() instanceof Double
                        && ((ValueExpression) rightSubExpression).getValue() instanceof Double){
                    // wenn beide Doubles sind wird das Ergebnis berechnet
                    double left = (double) ((ValueExpression) leftSubExpression).getValue();
                    double right = (double) ((ValueExpression) rightSubExpression).getValue();
                    boolean result;
                    if (typeIs(BoolType.SMALER))
                        result = left < right;
                    else if (typeIs(BoolType.SMALEREQU))
                        result = left <= right;
                    else if (typeIs(BoolType.GREATER))
                        result = left > right;
                    else if (typeIs(BoolType.GREATEREQU))
                        result = left >= right;
                    else if (typeIs(BoolType.EQU))
                        result = left == right;
                    else if (typeIs(BoolType.NOTEQU))
                        result = left != right;
                    else throw new Exception("<, >=, >, >=, == or != expected");
                    return new ValueExpression<>(result);
                    // wenn nicht beide booleans oder Doubles sinf wird ein Fehler zurückgegeben
                } else throw new Exception("Boolean or Integer expected");
            } else return new BoolExpression(type, leftSubExpression, rightSubExpression);
        } else{// wenn der Operator unär ist
            leftSubExpression = leftSubExpression.evaluate();
            // testen, ob der Operator eine Werte-Expression ist
            if (leftSubExpression instanceof ValueExpression){
                // wenn die Werte Expression kein bool ist Fehler zurückgeben
                if (!(((ValueExpression) leftSubExpression).getValue() instanceof Boolean))
                    throw new Exception("Boolean expected instead found Double");
                //die Negation wird zurückgeben
                if (typeIs(BoolType.NOT))
                    return new ValueExpression<>(!((boolean) ((ValueExpression) leftSubExpression).getValue()));
                else
                    throw new Exception("Not operation expected");
            } else return new BoolExpression(type, leftSubExpression);
        }
    }

    @Override
    public Expression addMap(Map<String, Expression> map) {
        if (rightSubExpression == null){// Übergabe der map für unäre Operatoren
            return new BoolExpression(type, leftSubExpression.addMap(map));
        } else {// übergabe der Map für binäre Operatoren
            return new BoolExpression(type,
                    leftSubExpression.addMap(map),
                    rightSubExpression.addMap(map));
        }
    }

    @Override
    public Expression replace(String id, Expression replacement) {
        if (rightSubExpression == null) {// Ersetzen für unäre Operatoren
            return new BoolExpression(type, leftSubExpression.replace(id, replacement));
        } else {//Ersetzen für binäre Operatoren
            return new BoolExpression(type,
                    leftSubExpression.replace(id, replacement),
                    rightSubExpression.replace(id, replacement));
        }
    }
}