/**
 * created by Jona Wessendorf
 * on 22.08.2019
 */

package expressions;

import datastructures.Map;
import expressions.lambda.AppExpression;
import expressions.lambda.FunExpression;

public class ListExpression extends Expression {
    private ListExpression previous;
    private ListExpression next;
    private Expression content;
    
    public ListExpression(ListExpression previous, ListExpression next, Expression content){
        this.previous = previous;
        this.next = next;
        this.content = content;
    }
    
    public String print() throws Exception {
        content = content.evaluate();
        if (!(content instanceof ValueExpression)) throw new Exception("Value expected");
        if (next != null)
            return ((ValueExpression) content).getValue().toString() + "; " + next.print();
        else
            return ((ValueExpression) content).getValue().toString() + "]";
    }
    public ListExpression map(Expression function) throws Exception {
        if (content != null){
            content = new AppExpression(function.evaluate(), content.evaluate());
        }
        if (next != null){
            next = next.map(function);
        }
        return this;
    }
    
    @Override
    public Expression evaluate() throws Exception {
        if (next != null)
            next = (ListExpression) next.evaluate();
        if (content != null)
            content = content.evaluate();
        return new ListExpression(previous, next, content);
    }
    
    @Override
    public Expression addMap(Map<String, Expression> map) {
        if (next != null)
            next = (ListExpression) next.addMap(map);
        if (content != null)
            content = content.addMap(map);
        return new ListExpression(previous, next, content);
    }
    
    @Override
    public Expression replace(String id, Expression replacement) {
        if (next != null)
            next = (ListExpression) next.replace(id, replacement);
        if (content != null)
            content = content.replace(id, replacement);
        return new ListExpression(previous, next, content);
    }
    
    public void setNext(ListExpression next) {
        this.next = next;
    }
}
