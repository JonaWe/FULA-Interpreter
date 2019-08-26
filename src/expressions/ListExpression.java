/**
 * created by Jona Wessendorf
 * on 22.08.2019
 */

package expressions;

import datastructures.Map;
import expressions.lambda.AppExpression;

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
    
    public ListExpression drop(Expression function) throws Exception {
        Expression evaluated = content.evaluate();
        if (!(evaluated instanceof ValueExpression))
            throw new Exception("couldn't evaluate a list parameter");
        Expression isDropped = new AppExpression(function.evaluate(), evaluated).evaluate();
        if (!(isDropped instanceof ValueExpression && ((ValueExpression) isDropped).getValue() instanceof Boolean))
            throw new Exception("couldn't apply the drop function on one of the list parameters");
        if ((Boolean) ((ValueExpression) isDropped).getValue()){
            return remove().drop(function);
        } else if (next != null){
            next.drop(function);
            return this;
        } else return this;
        
    }
    
    public ListExpression remove() throws Exception{
        if (previous == null && next == null) throw new Exception("Lists can not be empty");
        else if (previous == null) {
            next.setPrevious(null);
            return next;
        } else if (next == null) {
            previous.setNext(null);
            return previous;
        } else {
            previous.setNext(next);
            next.setPrevious(previous);
            return previous;
        }
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
    
    public void setPrevious(ListExpression previous) {
        this.previous = previous;
    }
}
