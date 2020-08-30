/**
 * created by Jona Wessendorf
 * on 30.08.2020
 */

package expressions.list;

import datastructures.Map;
import expressions.Expression;

public class EmptyListExpression extends ListExpression {
    @Override
    public Expression evaluate() throws Exception {
        return this;
    }
    
    @Override
    public Expression addMap(Map<String, Expression> map) {
        return this;
    }
    
    @Override
    public Expression replace(String id, Expression replacement) {
        return this;
    }
    
    @Override
    public String printList() {
        return " ]";
    }
    
    @Override
    public ListExpression drop(Expression function) {
        return this;
    }
    
    @Override
    public ListExpression remove() {
        return this;
    }
}
