/*created by Jona Wessendorf
 *on27.05.2019
 */

package expressions;

import datastructures.Map;
import expressions.list.ListExpression;

public class PrintExpression extends Expression {
    private Expression subExpression;

    public PrintExpression(Expression subExpression){
        this.subExpression = subExpression;
    }

    @Override
    public Expression evaluate() throws Exception {
        // unter Expression wird ausgewertet
        subExpression = subExpression.evaluate();
        // wenn die unter Expression ein Wert ist wird diese in der Konsole ausgegeben
        if (subExpression instanceof ValueExpression) System.out.println(((ValueExpression) subExpression).getValue());
        else if (subExpression instanceof ListExpression) System.out.println("[" + ((ListExpression) subExpression).printList());
        // danach wird die unter Expression augegeben
        return subExpression;
    }

    @Override
    public Expression addMap(Map<String, Expression> map) {
        // die addMap Methode wird für die unter Expression aufgerufen
        return new PrintExpression(subExpression.addMap(map));
    }

    @Override
    public Expression replace(String id, Expression replacement) {
        // die replace Methode wird für die unter Expression aufgerufen
        return new PrintExpression(subExpression.replace(id,replacement));
    }
}
