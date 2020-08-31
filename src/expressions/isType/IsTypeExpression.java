/**
 * created by Jona Wessendorf
 * on 31.08.2020
 */

package expressions.isType;

import datastructures.Map;
import expressions.Expression;
import expressions.ValueExpression;

public class IsTypeExpression extends Expression {
    private Expression subExpression;
    private final ValueTypes type;
    
    public IsTypeExpression(Expression subExpression, ValueTypes type){
        this.subExpression = subExpression;
        this.type = type;
    }
    
    @Override
    public Expression evaluate() throws Exception {
        subExpression = subExpression.evaluate();
        
        if (subExpression instanceof ValueExpression){
            Object value  = ((ValueExpression) subExpression).getValue();
            
            if (value instanceof Boolean && type == ValueTypes.BOOLEAN ||
                    value instanceof String && type == ValueTypes.STRING ||
                    (value instanceof Float || value instanceof Double) && type == ValueTypes.FLOAT)
                return new ValueExpression<>(true);
            else return new ValueExpression<>(false);
        }
        
        return new IsTypeExpression(subExpression, type);
    }
    
    @Override
    public Expression addMap(Map<String, Expression> map) {
        return new IsTypeExpression(subExpression.addMap(map), type);
    }
    
    @Override
    public Expression replace(String id, Expression replacement) {
        return new IsTypeExpression(subExpression.replace(id, replacement), type);
    }
}
