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
        // linke Expression wird ausgewertet
        leftSubExpression = leftSubExpression.evaluate();
        // wenn die linke Expression keine Funktion ist wird ein Fehler ausgegeben
        if (leftSubExpression instanceof FunExpression){
            // auf die linke Funktion wird die rechte Expression angewandt mit der apply Methode
            // zuvor wied die rechte Expression ausgewertet
            leftSubExpression = ((FunExpression) leftSubExpression).apply(rightSubExpression.evaluate());
            // Danach wird die neue Expression ausgewertet und zurückgegeben
            return leftSubExpression.evaluate();
        } else throw new Exception("Terms can only be applied on functions");
    }

    @Override
    public Expression addMap(Map<String, Expression> map) {
        // Weitergabe der Map an die unter Expressions
        return new AppExpression(leftSubExpression.addMap(map),
                rightSubExpression.addMap(map));
    }

    @Override
    public Expression replace(String id, Expression replacement) {
        // Weitergabe der Ersetzung an die unter Expression
        return new AppExpression(leftSubExpression.replace(id, replacement),
                rightSubExpression.replace(id, replacement));
    }
}
