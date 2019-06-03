import expressions.ValueExpression;
import expressions.Expression;

import java.io.BufferedReader;
import java.io.FileReader;


public class Interpreter {

    public void execute(String filePath) throws Exception {

        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        StringBuilder sourceCode = new StringBuilder();

        while ((line = reader.readLine()) != null){
            sourceCode.append(line);
            sourceCode.append("\n");
        }


        Parser parser = new Parser(sourceCode.toString());

        Expression expression = parser.parse();
        expression = expression.evaluate();
        if (!(expression instanceof ValueExpression)) throw new Exception("Sourcecode could not be resolved");
    }
}
