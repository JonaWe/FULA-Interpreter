import abiturklassen.linear.List;
import abiturklassen.linear.Queue;
import abiturklassen.linear.Stack;
import datastructures.Map;
import datastructures.Pair;
import expressions.*;
import expressions.bools.BoolExpression;
import expressions.bools.BoolType;
import expressions.lambda.AppExpression;
import expressions.lambda.FunExpression;
import expressions.lambda.IdExpression;
import expressions.term.MathExpression;
import expressions.term.MathType;
import tokens.ContainerToken;
import tokens.Token;
import tokens.TokenType;

public class Parser {
    private String sourceCode;
    private Scanner scanner = new Scanner();
    private List<Token> tokenList;

    public Parser(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    private boolean currentTokenTypeIs(TokenType type) {
        return (tokenList.hasAccess()) && tokenList.getContent().getType() == type;
    }

    private void nextToken() throws Exception {
        // erwartet, dass es ein weiteres Token gibt, sonst wird ein Error ausgegeben
        if (!tokenList.hasAccess()) throw new Exception("unexpected end of line");
        // setzt das aktuelle token auf das nächste Token
        tokenList.next();
    }

    private Exception wrongTokenException(String s){
        // gibt eine Exception zurück mit dem was erwartet war und was anstattdessen gefunden wurde
        return new Exception("'"+s+"' expected. Instead found "+tokenList.getContent().toString());
    }

    private Pair<String, Expression> getAssignment() throws Exception{
        // gibt ein Pair von String und Expression zurück (eine Zuweisung)
        if (!currentTokenTypeIs(TokenType.ID)) throw wrongTokenException("identifier");
        ContainerToken<String> identifierString = (ContainerToken<String>) tokenList.getContent();
        nextToken();
        if (!currentTokenTypeIs(TokenType.COLON))throw wrongTokenException(":=");
        nextToken();
        if (!currentTokenTypeIs(TokenType.EQUALS)) throw wrongTokenException(":=");
        nextToken();
        return new Pair<>(identifierString.getContent(), parseExp());
    }

    public Expression parse() throws Exception {
       tokenList = scanner.scan(sourceCode);
       tokenList.toFirst();

        Expression e = parseExp();

        if (!tokenList.hasAccess()) {
            return e;
        } else {
            throw new Exception("unexpected token '" + tokenList.getContent().toString()+"'");
        }
    }

    private Expression parseExp() throws Exception {
       if (currentTokenTypeIs(TokenType.LCURLYBRACKET)){
           // parsen von Funktionen
           nextToken();
           return parseFun();
       }
       else if(currentTokenTypeIs(TokenType.IF)){
           // parsen für If-Anweisungen
           nextToken();
           return parseIf();
       }
       else if (currentTokenTypeIs(TokenType.LET)){
           // parsen für Let-Anweisungen
           nextToken();
           return parseLet();
       } else return parseWhere(); // parsen von allem anderen war möglich ist
   }

    private Expression parseFun() throws Exception{
        // als erstes wird ein Bezeichner erwartet, dem einen Expression zugewiesen wird
        if (!currentTokenTypeIs(TokenType.ID)) throw wrongTokenException("IDENTIFIER");
        Stack<String> ids = new Stack<>();
        ContainerToken<String> current = (ContainerToken<String>) tokenList.getContent();
        // der Bezeichner wird auf dem Stack gespeichert
        ids.push(current.getContent());
        nextToken();
        // solange ein Komma vorhanden ist wird ein neuer Identifier auf den Stack gepushed
        while (currentTokenTypeIs(TokenType.COMMA)){
            nextToken();
            if (!currentTokenTypeIs(TokenType.ID)) throw wrongTokenException("identifier");
            current = (ContainerToken<String>) tokenList.getContent();
            ids.push(current.getContent());
            nextToken();
        }
        // nach dem letzten identifier wird das Funktionszuweisungssymbol vorhanden ist
        if (!currentTokenTypeIs(TokenType.MINUS)) throw wrongTokenException("->");
        nextToken();
        if (!currentTokenTypeIs(TokenType.GREATER)) throw wrongTokenException("->");
        nextToken();

        // in fun wird die Sub-Expression der Funktion gespeichert
        Expression fun = parseExp();

        // der Stack mit den Bezeichnern wird geleert
        // falls es mehere Bezeichner gibt werden die Funktionen ineinander verschachtelt
        while (!ids.isEmpty()){
            fun = new FunExpression(ids.top(), fun);
            ids.pop();
        }

        // überprüfung, ob das Zeichen zum beenden der Funktion vorhanden ist

        if (!currentTokenTypeIs(TokenType.RCURLYBRACKET)) throw wrongTokenException("}");
        nextToken();

        // testen, ob eine Funktionsanwendung direkt hinter der Funktion ist
        if (currentTokenTypeIs(TokenType.LBRACKET)){
            nextToken();
            // wenn dies der falls ist wird die parseApp Methode mit dem parameter der (verschachtelten) funktionen übergeben
            return parseApp(fun);
        } else return fun; // ansonsten wird nur die (verschachtelte) Funktion zurückgegeben
   }

    private Expression parseApp(Expression fun) throws Exception{
        // in der Queue werden die replacements für die Funktion gespeichert
        // mindestens eins und für jedes Komma ein weiteres
        Queue<Expression> replacements = new Queue<>();
        replacements.enqueue(parseExp());
        while (currentTokenTypeIs(TokenType.COMMA)){
            nextToken();
            replacements.enqueue(parseExp());
        }
        // nach dem letzten replacement wird die rechte Klammer erwartet, um die Application zu beenden
        if (!currentTokenTypeIs(TokenType.RBRACKET)) throw wrongTokenException(")");
        nextToken();
        // in app wird die erste Application gespeichert
        // mit der übergebenen Funktion und dem ersten replacement als Parameter
        Expression app = new AppExpression(fun, replacements.front());
        replacements.dequeue();

        // solange es noch mehr replacements gibt werden diese ineinander verschachtelt
        while (!replacements.isEmpty()){
            app = new AppExpression(app, replacements.front());
            replacements.dequeue();
        }
        // wenn eine Linke Klammer vorhanden ist wird die parseApp aufgerufen mit der Application(s) als Parameter
        // da function(x, y) = function(x)(y)
        if (currentTokenTypeIs(TokenType.LBRACKET)){
            nextToken();
            return parseApp(app);
        }
        // sonst wird die (verschachtelte) Application zurückgegeben
        return app;
   }

    private Expression parseLet() throws Exception{
        Map<String, Expression> map = new Map<>();
        // die erste Zuweisung ist verpflichtend
        map.add(getAssignment());
        // danach werden so lange Zuweisungen der Map hinzugefügt, bis das IN beginnt
        while (!currentTokenTypeIs(TokenType.IN)){
            map.add(getAssignment());
        }
        nextToken();
        // als nächstes wird die unter Expression geparsed
        Expression subExpression = parseExp();
        if (!currentTokenTypeIs(TokenType.END)) throw wrongTokenException("end");
        nextToken();
        // nachdem sichergestellt wurde, dass das END Zeichen vorhanden ist wird die Let-Expression mit der Map-Expression zurückgegeben
        return new LetExpression(map, subExpression);
   }

    private Expression parseIf() throws Exception {
        // zuerst wird die Bedingung und dann, das was passiert wenn sie wahr ist geparsed
        Expression condition = parseExp();
        Expression trueExpression = parseExp();
        if (!currentTokenTypeIs(TokenType.ELSE)) throw wrongTokenException("else");
        nextToken();
        // nach dem else wird das geparsed, was ausgeführt wird, wenn die Bedingung falsch ist
        Expression falseExpression = parseExp();

        return new IfExpression(condition, trueExpression, falseExpression);
    }

    private Expression parseWhere() throws Exception{
        // zuerst wird der linke Teil der where-Expression geparsed
        // Wenn dieser eine If-, Let-, Funtions- oder Anwendungs-Expression muss dieser umklammert werden
        // für den fall das eine Where-Expression geparsed wird
        Expression leftOperand = parseBoolOr();
        if(currentTokenTypeIs(TokenType.WHERE)){
            nextToken();
            Map<String, Expression> map = new Map<>();
            // die erste Zuweisung ist verpflichtend
            map.add(getAssignment());
            // danach werden so lange Zuweisungen der Map hinzugefügt, bis das END kommt
            while (!currentTokenTypeIs(TokenType.END)){
                map.add(getAssignment());
            }
            nextToken();
            // eine Let-Expression wird mit der Map und der linken unter Expression zurückgegeben
            return new LetExpression(map, leftOperand);

        } else return leftOperand; // wenn es keine Where Anweisung ist wird nur parseBoolOr zurückgegeben
    }

    private Expression parseBoolOr() throws Exception{
        // die folgenden parse-Methoden haben alle den gleichen Aufbau
        // als linke Expression wird die jeweilig untere geparsed
        // danach wird geprüft, ob der jeweilige Operator vorhanden ist
        // wenn ja, dann wird die Operator Expression mit einem rechten Teil zurückgegeben
        // sonst wird nur die linke Expression Zurückgegeben
        // erst die parseValueOrUnaryOperator-Methode wird wieder kommentiert sein, da diese einen anderen Aufbau hat
        Expression leftOperand = parseBoolAnd();
        if(currentTokenTypeIs(TokenType.OR)){
            nextToken();
            if (!currentTokenTypeIs(TokenType.OR)) throw wrongTokenException("||");
            nextToken();
            return new BoolExpression(BoolType.OR, leftOperand, parseBoolOr());
        } else return leftOperand;
    }

    private Expression parseBoolAnd() throws Exception{
        Expression leftOperand= parseBoolXOr();
        if (currentTokenTypeIs(TokenType.AND)){
            nextToken();
            if (!currentTokenTypeIs(TokenType.AND)) throw wrongTokenException("and");
            nextToken();
            return new BoolExpression(BoolType.AND, leftOperand, parseBoolAnd());
        } else return leftOperand;
    }

    private Expression parseBoolXOr() throws Exception{
        Expression leftOperand = parseBoolEquals();
        if (currentTokenTypeIs(TokenType.XOR)){
            nextToken();
            return new BoolExpression(BoolType.XOR, leftOperand, parseBoolXOr());
        } else return leftOperand;
    }

    private Expression parseBoolEquals() throws Exception{
        Expression leftOperand = parseBoolComp();
        if (currentTokenTypeIs(TokenType.EQUALS)){
            nextToken();
            if (!currentTokenTypeIs(TokenType.EQUALS)) throw wrongTokenException("==");
            nextToken();
            return new BoolExpression(BoolType.EQU, leftOperand, parseBoolEquals());
        } else if (currentTokenTypeIs(TokenType.NOT)){
            nextToken();
            if (!currentTokenTypeIs(TokenType.EQUALS)) throw wrongTokenException("!=");
            nextToken();
            return new BoolExpression(BoolType.NOTEQU, leftOperand, parseBoolEquals());
        } else return leftOperand;

    }

    private Expression parseBoolComp() throws Exception{
        Expression leftOperand = parseAddition();

        if (currentTokenTypeIs(TokenType.SMALLER)){
            nextToken();
            if (currentTokenTypeIs(TokenType.EQUALS)){
                nextToken();
                return new BoolExpression(BoolType.SMALEREQU, leftOperand, parseBoolComp());
            } else{
                return new BoolExpression(BoolType.SMALER, leftOperand, parseBoolComp());
            }
        } else if (currentTokenTypeIs(TokenType.GREATER)){
            nextToken();
            if (currentTokenTypeIs(TokenType.EQUALS)){
                nextToken();
                return new BoolExpression(BoolType.GREATEREQU, leftOperand, parseBoolComp());
            } else {
                return new BoolExpression(BoolType.GREATER, leftOperand, parseBoolComp());
            }
        } else return leftOperand;
    }

    private Expression parseAddition() throws Exception {
        Expression leftOperand = parseMultiplication();

        if (currentTokenTypeIs(TokenType.PLUS)) {
            nextToken();
            Expression rightOperand = parseAddition();
            return new MathExpression(MathType.ADD, leftOperand, rightOperand);
        } else if (currentTokenTypeIs(TokenType.MINUS)){
            nextToken();
            Expression rightOperand = parseAddition();
            return new MathExpression(MathType.SUB, leftOperand, rightOperand);
        } else{
            return leftOperand;
        }
    }

    private Expression parseMultiplication() throws Exception {
        Expression leftOperand = parsePower();

        if (currentTokenTypeIs(TokenType.MUL)) {
            nextToken();
            Expression rightOperand = parseMultiplication();
            return new MathExpression(MathType.MUL, leftOperand, rightOperand);
        } else if (currentTokenTypeIs(TokenType.DIV)){
            nextToken();
            Expression rightOperand = parseMultiplication();
            return new MathExpression(MathType.DIV, leftOperand, rightOperand);
        } else if(currentTokenTypeIs(TokenType.MODULO)){
            nextToken();
            Expression rightOperand = parseMultiplication();
            return new MathExpression(MathType.MODULO, leftOperand, rightOperand);
        }
        else{
            return leftOperand;
        }
    }

    private Expression parsePower() throws Exception{
        Expression leftOperand = parseValueOrUnaryOperator();
        if (currentTokenTypeIs(TokenType.POWER)){
            nextToken();
            Expression rightOperand = parsePower();
            return new MathExpression(MathType.POWER, leftOperand,rightOperand);
        } else
            return leftOperand;
    }

    private Expression parseValueOrUnaryOperator() throws Exception{
        // in dieser Methode werden alle unären Operatoren oder Werte geparsed

        // für negation bsp: -x
        if(currentTokenTypeIs(TokenType.MINUS)){
            nextToken();
            Expression subExpression = parseValueOrUnaryOperator();
            return new MathExpression(MathType.NEGATION, subExpression);
        }
        // für die Quadratwurzel bsp: sqrt x
        else if(currentTokenTypeIs(TokenType.SQRT)){
            nextToken();
            Expression subExpression = parseValueOrUnaryOperator();
            return new MathExpression(MathType.SQRT, subExpression);
        }
        // für den Sinus bsp: sin x
        else if(currentTokenTypeIs(TokenType.SIN)){
            nextToken();
            Expression subExpression = parseValueOrUnaryOperator();
            return new MathExpression(MathType.SIN, subExpression);
        }
        // für den Kosinus bsp: sin x
        else if(currentTokenTypeIs(TokenType.COS)){
            nextToken();
            Expression subExpression = parseValueOrUnaryOperator();
            return new MathExpression(MathType.COS, subExpression);
        }
        // für den Tangenz bsp: tan x
        else if(currentTokenTypeIs(TokenType.TAN)){
            nextToken();
            Expression subExpression = parseValueOrUnaryOperator();
            return new MathExpression(MathType.TAN, subExpression);
        }
        // für den natürlichen Logarithmus bsp: log x
        else if(currentTokenTypeIs(TokenType.LOG)){
            nextToken();
            Expression subExpression = parseValueOrUnaryOperator();
            return new MathExpression(MathType.LOG, subExpression);
        }
        // für Exponentialfunktionen bsp: exp x
        else if(currentTokenTypeIs(TokenType.EXP)){
            nextToken();
            Expression subExpression = parseValueOrUnaryOperator();
            return new MathExpression(MathType.EXP, subExpression);
        }
        // für die Klammerung bzw Operatorprädenzen
        else if (currentTokenTypeIs(TokenType.LBRACKET)){
            nextToken();
            Expression exp = parseExp();
            if (currentTokenTypeIs(TokenType.RBRACKET)){
                nextToken();
                return exp;
            } else throw wrongTokenException(")");
        }
        // für die Negation bsp: !true
        else if(currentTokenTypeIs(TokenType.NOT)){
           nextToken();
           return new BoolExpression(BoolType.NOT, parseValueOrUnaryOperator());
        }
        // Input aus der Console zu bekommen
        else if (currentTokenTypeIs(TokenType.INPUT)){
            nextToken();
            return new InputExpression();
        }
        // Einen Wert in der Console auszugeben
        else if (currentTokenTypeIs(TokenType.PRINT)) {
            nextToken();
            return new PrintExpression(parseValueOrUnaryOperator());
        }
        // parsen von Floats
        else if (currentTokenTypeIs(TokenType.FLOAT)){
            ContainerToken<Double> t = (ContainerToken<Double>) tokenList.getContent();
            nextToken();
            return new ValueExpression<>(t.getContent());
        }
        // einen Bezeichner zu parsen
        else if(currentTokenTypeIs(TokenType.ID)){
            ContainerToken<String> t = (ContainerToken<String>) tokenList.getContent();
            nextToken();
            // für die mögliche Application auf Funktionen
            if (currentTokenTypeIs(TokenType.LBRACKET)){
                nextToken();
                return parseApp(new IdExpression(t.getContent()));
            } else
                return new IdExpression(t.getContent());
        }
        // parsen von Booleans
        else if(currentTokenTypeIs(TokenType.BOOLEAN)){
            ContainerToken<Boolean> bool = (ContainerToken<Boolean>) tokenList.getContent();
            nextToken();
            return new ValueExpression<>(bool.getContent());
        }else throw wrongTokenException("Float, boolean, identifier or unary operator");
   }
}