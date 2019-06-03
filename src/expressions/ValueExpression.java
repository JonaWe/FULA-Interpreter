package expressions;

import datastructures.Map;

public class ValueExpression<Type> extends Expression {
    private Type value;

    public ValueExpression(Type value){
        this.value = value;
    }

    @Override
    public Expression evaluate() {
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

    public Type getValue(){
        return value;
    }
}
