/*created by Jona Wessendorf
 *on27.05.2019
 */

package expressions;

import datastructures.Map;

import java.util.Scanner;

public class InputExpression extends Expression {
    @Override
    public Expression evaluate() throws Exception {
        System.out.print("input requested: ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if (input.equals("true"))
            return new ValueExpression<>(true);
        else if (input.equals("false"))
            return new ValueExpression<>(true);
        else return new ValueExpression<>(Double.parseDouble(input));

    }

    @Override
    public Expression addMap(Map<String, Expression> map) {
        return this;
    }

    @Override
    public Expression replace(String id, Expression replacement) {
        return this;
    }
}
