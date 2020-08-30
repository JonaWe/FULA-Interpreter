/**
 * created by Jona Wessendorf
 * on 30.08.2020
 */

package expressions.list;

import datastructures.Map;
import expressions.Expression;

public abstract class ListExpression extends Expression {
    public abstract String printList() throws Exception;
    public abstract ListExpression drop(Expression function) throws Exception;
    public abstract ListExpression remove() throws Exception;
}
