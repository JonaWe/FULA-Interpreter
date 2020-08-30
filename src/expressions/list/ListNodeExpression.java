/**
 * created by Jona Wessendorf
 * on 22.08.2019
 */

package expressions.list;

import datastructures.Map;
import expressions.Expression;
import expressions.ValueExpression;
import expressions.lambda.AppExpression;

public class ListNodeExpression extends ListExpression {
    private ListNodeExpression previous;
    private ListNodeExpression next;
    private Expression content;
    
    public ListNodeExpression(ListNodeExpression previous, ListNodeExpression next, Expression content){
        this.previous = previous;
        this.next = next;
        this.content = content;
    }
    
    public String printList() throws Exception {
        content = content.evaluate();
        if (!(content instanceof ValueExpression)) throw new Exception("Value expected");
        if (next != null)
            return ((ValueExpression) content).getValue().toString() + "; " + next.printList();
        else
            return ((ValueExpression) content).getValue().toString() + "]";
    }
    public ListNodeExpression map(Expression function) throws Exception {
        if (content != null){
            content = new AppExpression(function, content);
        }
        if (next != null){
            next = next.map(function);
        }
        return this;
    }
    
    public ListExpression drop(Expression function) throws Exception {
        content = content.evaluate();
        if (!(content instanceof ValueExpression))
            throw new Exception("couldn't evaluate a list parameter");
        Expression isDropped = new AppExpression(function.evaluate(), content).evaluate();
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
        if (previous == null && next == null)
            return new EmptyListExpression();
            //throw new Exception("Lists can not be empty");
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
            next = (ListNodeExpression) next.evaluate();
        if (content != null)
            content = content.evaluate();
        return new ListNodeExpression(previous, next, content);
    }
    
    @Override
    public Expression addMap(Map<String, Expression> map) {
        if (next != null)
            next = (ListNodeExpression) next.addMap(map);
        if (content != null)
            content = content.addMap(map);
        return new ListNodeExpression(previous, next, content);
    }
    
    @Override
    public Expression replace(String id, Expression replacement) {
        if (next != null)
            next = (ListNodeExpression) next.replace(id, replacement);
        if (content != null)
            content = content.replace(id, replacement);
        return new ListNodeExpression(previous, next, content);
    }
    
    public void setNext(ListNodeExpression next) {
        this.next = next;
    }
    
    public void setPrevious(ListNodeExpression previous) {
        this.previous = previous;
    }
}