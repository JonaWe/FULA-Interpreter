import expressions.ListExpression;
import expressions.ValueExpression;
import expressions.Expression;

import java.io.BufferedReader;
import java.io.FileReader;


public class Interpreter {

    public void execute(String filePath) throws Exception {

        // einlesen des Quellcodes
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        StringBuilder sourceCode = new StringBuilder();

        // solange es eine Zeile gibt wird sie dem sourcecode hinzugefügt
        // und ein \n wird hinzugefügt um zu zeigen, dass eine neue Zeile beginnt
        while ((line = reader.readLine()) != null){
            sourceCode.append(line);
            sourceCode.append("\n");
        }


        // es wird ein Parser mit dem Sourcecode erstellt
        Parser parser = new Parser(sourceCode.toString());

        // der Sourcecode wird in den ASK geparsed
        Expression expression = parser.parse();
        // der AST wird ausgewertet
        expression = expression.evaluate();

        // es wird geprüft, ob der AST zu einem Wert reduziert werden konnte
        if (!(expression instanceof ValueExpression || expression instanceof ListExpression)) throw new Exception("Sourcecode could not be resolved");
    }
}
