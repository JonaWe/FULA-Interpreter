/*created by Jona Wessendorf
 *on27.05.2019
 */

package expressions;

import datastructures.Map;

import java.util.Scanner;

public class InputExpression extends Expression {
    private boolean isNumberChar(char c){
        return c >= '0' && c <= '9';
    }

    private boolean isFloat(String s){
        return isFloatZ(s,0);
    }

    private boolean isFloatZ(String s, int pos){
        if (pos == s.length())
            return true;
        else if (isNumberChar(s.charAt(pos)))
            return isFloatZ(s, pos+1);
        else if (s.charAt(pos) == '.')
            return isFloatK(s, pos + 1);
        else
            return false;
    }

    private boolean isFloatK(String s, int pos){
        if (s.length() <= pos) return false;
        if (isNumberChar(s.charAt(pos))){
            if (pos+1 == s.length())
                return true;
            else
                return isFloatK(s, pos);
        } else return false;
    }

    @Override
    public Expression evaluate() throws Exception {
        System.out.print("input requested: ");
        // neuen Scanner für die Eingabe der Console
        Scanner scanner = new Scanner(System.in);
        // liest eine Zeile ein
        String input = scanner.nextLine();
        // parsen der zeile in einen Float oder Boolean
        if (input.equals("true"))
            return new ValueExpression<>(true);
        else if (input.equals("false"))
            return new ValueExpression<>(true);
        else if (isFloat(input)) // Grammatik zum prüfen, ob es eine Float ist
            return new ValueExpression<>(Double.parseDouble(input));
        else throw new Exception("invalid input: Boolean or Float expected");

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
