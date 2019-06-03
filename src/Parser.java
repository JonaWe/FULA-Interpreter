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
import expressions.term.TermExpression;
import expressions.term.TermType;
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
        if (!tokenList.hasAccess()) throw new Exception("unexpected end of line");
        tokenList.next();
    }

    private Exception wrongTokenException(String s){
        return new Exception("'"+s+"' expected. Instead found "+tokenList.getContent().toString());
    }

    private Pair<String, Expression> getAssignment() throws Exception{
        if (!currentTokenTypeIs(TokenType.ID)) throw wrongTokenException("identifier");
        ContainerToken<String> t = (ContainerToken<String>) tokenList.getContent();
        nextToken();
        if (!currentTokenTypeIs(TokenType.COLON))throw wrongTokenException(":=");
        nextToken();
        if (!currentTokenTypeIs(TokenType.EQUALS)) throw wrongTokenException(":=");
        nextToken();
        return new Pair<>(t.getContent(), parseExp());
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
           nextToken();
           return parseFun();
       }
       else if(currentTokenTypeIs(TokenType.IF)){
           nextToken();
           return parseIf();
       }
       else if (currentTokenTypeIs(TokenType.LET)){
           nextToken();
           return parseLet();
       } else return parseWhere();
   }

    private Expression parseFun() throws Exception{
        if (!currentTokenTypeIs(TokenType.ID)) throw wrongTokenException("IDENTIFIER");
        Stack<String> ids = new Stack<>();
        ContainerToken<String> current = (ContainerToken<String>) tokenList.getContent();
        ids.push(current.getContent());
        nextToken();
        while (currentTokenTypeIs(TokenType.COMMA)){
            nextToken();
            if (!currentTokenTypeIs(TokenType.ID)) throw wrongTokenException("identifier");
            current = (ContainerToken<String>) tokenList.getContent();
            ids.push(current.getContent());
            nextToken();
        }
        if (!currentTokenTypeIs(TokenType.MINUS)) throw wrongTokenException("->");
        nextToken();
        if (!currentTokenTypeIs(TokenType.GREATER)) throw wrongTokenException("->");
        nextToken();
        Expression fun = parseExp();

        while (!ids.isEmpty()){
            fun = new FunExpression(ids.top(), fun);
            ids.pop();
        }

        if (!currentTokenTypeIs(TokenType.RCURLYBRACKET)) throw wrongTokenException("}");
        nextToken();

        if (currentTokenTypeIs(TokenType.LBRACKET)){
            nextToken();
            return parseApp(fun);
        } else return fun;
   }

    private Expression parseApp(Expression fun) throws Exception{
        Queue<Expression> replacements = new Queue<>();
        replacements.enqueue(parseExp());
        while (currentTokenTypeIs(TokenType.COMMA)){
            nextToken();
            replacements.enqueue(parseExp());
        }
        if (!currentTokenTypeIs(TokenType.RBRACKET)) throw wrongTokenException(")");
        nextToken();
        Expression app = new AppExpression(fun, replacements.front());
        replacements.dequeue();
        while (!replacements.isEmpty()){
            app = new AppExpression(app, replacements.front());
            replacements.dequeue();
        }
        if (currentTokenTypeIs(TokenType.LBRACKET)){
            nextToken();
            return parseApp(app);
        }
        return app;
   }

    private Expression parseLet() throws Exception{
        Map<String, Expression> map = new Map<>();
        while (!currentTokenTypeIs(TokenType.IN)){
            map.add(getAssignment());
        }
        nextToken();
        Expression subExpression = parseExp();
        if (!currentTokenTypeIs(TokenType.END)) throw wrongTokenException("end");
        nextToken();
        return new LetExpression(map, subExpression);
   }

    private Expression parseIf() throws Exception {
       Expression condition = parseExp();
       Expression trueExpression = parseExp();
       if (!currentTokenTypeIs(TokenType.ELSE)) throw wrongTokenException("else");
       nextToken();
       Expression falseExpression = parseExp();

       return new IfExpression(condition, trueExpression, falseExpression);
    }

    private Expression parseWhere() throws Exception{
        Expression leftOperand = parseBoolOr();
        if(currentTokenTypeIs(TokenType.WHERE)){
            Map<String, Expression> map = new Map<>();
            nextToken();
            while (!currentTokenTypeIs(TokenType.END)){
                map.add(getAssignment());
            }
            nextToken();
            return new LetExpression(map, leftOperand);

        } else return leftOperand;
    }

    private Expression parseBoolOr() throws Exception{
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
            return new TermExpression(TermType.ADD, leftOperand, rightOperand);
        } else if (currentTokenTypeIs(TokenType.MINUS)){
            nextToken();
            Expression rightOperand = parseAddition();
            return new TermExpression(TermType.SUB, leftOperand, rightOperand);
        } else{
            return leftOperand;
        }
    }

    private Expression parseMultiplication() throws Exception {
        Expression leftOperand = parsePower();

        if (currentTokenTypeIs(TokenType.MUL)) {
            nextToken();
            Expression rightOperand = parseMultiplication();
            return new TermExpression(TermType.MUL, leftOperand, rightOperand);
        } else if (currentTokenTypeIs(TokenType.DIV)){
            nextToken();
            Expression rightOperand = parseMultiplication();
            return new TermExpression(TermType.DIV, leftOperand, rightOperand);
        } else if(currentTokenTypeIs(TokenType.MODULO)){
            nextToken();
            Expression rightOperand = parseMultiplication();
            return new TermExpression(TermType.MODULO, leftOperand, rightOperand);
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
            return new TermExpression(TermType.POWER, leftOperand,rightOperand);
        } else
            return leftOperand;
    }

    private Expression parseValueOrUnaryOperator() throws Exception{
        if(currentTokenTypeIs(TokenType.MINUS)){
            nextToken();
            Expression subExpression = parseValueOrUnaryOperator();
            return new TermExpression(TermType.NEGATION, subExpression);
        }
        else if(currentTokenTypeIs(TokenType.SQRT)){
            nextToken();
            Expression subExpression = parseValueOrUnaryOperator();
            return new TermExpression(TermType.SQRT, subExpression);
        }
        else if(currentTokenTypeIs(TokenType.SIN)){
            nextToken();
            Expression subExpression = parseValueOrUnaryOperator();
            return new TermExpression(TermType.SIN, subExpression);
        }
        else if(currentTokenTypeIs(TokenType.COS)){
            nextToken();
            Expression subExpression = parseValueOrUnaryOperator();
            return new TermExpression(TermType.COS, subExpression);
        }
        else if(currentTokenTypeIs(TokenType.TAN)){
            nextToken();
            Expression subExpression = parseValueOrUnaryOperator();
            return new TermExpression(TermType.TAN, subExpression);
        }
        else if(currentTokenTypeIs(TokenType.LOG)){
            nextToken();
            Expression subExpression = parseValueOrUnaryOperator();
            return new TermExpression(TermType.LOG, subExpression);
        }
        else if(currentTokenTypeIs(TokenType.EXP)){
            nextToken();
            Expression subExpression = parseValueOrUnaryOperator();
            return new TermExpression(TermType.EXP, subExpression);
        }
        else if (currentTokenTypeIs(TokenType.LBRACKET)){
            nextToken();
            Expression exp = parseExp();
            if (currentTokenTypeIs(TokenType.RBRACKET)){
                nextToken();
                return exp;
            } else throw wrongTokenException(")");
        }
        else if(currentTokenTypeIs(TokenType.NOT)){
           nextToken();
           return new BoolExpression(BoolType.NOT, parseValueOrUnaryOperator());
        }
        else if (currentTokenTypeIs(TokenType.INPUT)){
            nextToken();
            return new InputExpression();
        }
        else if (currentTokenTypeIs(TokenType.PRINT)) {
            nextToken();
            return new PrintExpression(parseValueOrUnaryOperator());
        }
        else if (currentTokenTypeIs(TokenType.FLOAT)){
            ContainerToken<Double> t = (ContainerToken<Double>) tokenList.getContent();
            nextToken();
            return new ValueExpression<>(t.getContent());
        }
        else if(currentTokenTypeIs(TokenType.ID)){
            ContainerToken<String> t = (ContainerToken<String>) tokenList.getContent();
            nextToken();
            if (currentTokenTypeIs(TokenType.LBRACKET)){
                nextToken();
                return parseApp(new IdExpression(t.getContent()));
            } else
                return new IdExpression(t.getContent());
        }
        else if(currentTokenTypeIs(TokenType.BOOLEAN)){
            ContainerToken<Boolean> bool = (ContainerToken<Boolean>) tokenList.getContent();
            nextToken();
            return new ValueExpression<>(bool.getContent());
        }else throw wrongTokenException("Float, boolean, identifier or unary operator");
   }

}