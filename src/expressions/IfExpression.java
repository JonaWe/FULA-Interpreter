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
        // wertet die Bedingung der If-Expression aus
        condition = condition.evaluate();
        // Wenn die Bedingung ein Wert ist wird die richtige Auswertung gestartet
        if (condition instanceof ValueExpression){
            // wenn die Bedingung keinn Bool ist wird ein Fehler gegeben
            if (!(((ValueExpression) condition).getValue() instanceof Boolean))
                throw new Exception("IfExpression requires a Boolean as a condition");
            // wenn die Bedingung wahr ist wird die ausgewertete wahre Expression zurückgegeben
            if ((boolean) ((ValueExpression) condition).getValue()){
                //assert condition == true
                return trueExpression.evaluate();
            // sonst wird die ausgewertete false Expression zurückgegeben
            } else{
                //assert condition == false
                return falseExpression.evaluate();

            }
        } // wenn die Bedingung kein Wert ist wird einen neue If-Expression mit der ausgewerteten Bedingung zurückgegeben
        else return new IfExpression(condition, trueExpression, falseExpression);
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
