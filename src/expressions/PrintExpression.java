/*created by Jona Wessendorf
 *on27.05.2019
 */

package expressions;

import datastructures.Map;

public class PrintExpression extends Expression {
    private Expression subExpression;

    public PrintExpression(Expression subExpression){
        this.subExpression = subExpression;
    }

    @Override
    public Expression evaluate() throws Exception {
        subExpression = subExpression.evaluate();
        if (subExpression instanceof ValueExpression) System.out.println(((ValueExpression) subExpression).getValue());
        return subExpression;
    }

    @Override
    public Expression addMap(Map<String, Expression> map) {
        return new PrintExpression(subExpression.addMap(map));
    }

    @Override
    public Expression replace(String id, Expression replacement) {
        return new PrintExpression(subExpression.replace(id,replacement));
    }
}
