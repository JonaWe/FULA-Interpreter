import abiturklassen.linear.List;
import tokens.ContainerToken;
import tokens.Token;
import tokens.TokenType;


public class Scanner {
    private boolean isWhitespace(char c) {
        return c == ' ' || c == '\n' || c == '\t';
    }

    private boolean isSeparator(char c) {
        return isWhitespace(c) ||  c == '*' || c == '+' || c == '-' || c == '/' || c == '^' || c == '%'|| c == '('
                || c == ')' || c == '!' || c == '=' || c == '<' || c == '>' || c == '|' || c == '&' || c == '{'
                || c == '}' || c == ',' || c == ':' || c == '#' || c == '~' || c == ';' || c == '[' || c == ']';
    }

    private boolean isLowercaseChar(char c){
        return c >= 'a' && c <= 'z';
    }

    private boolean isUppercaseChar(char c){
        return c >= 'A' && c <= 'Z';
    }

    private boolean isNumberChar(char c){
        return c >= '0' && c <= '9';
    }

    private List<String> split(String input) {
        List<String> stringList = new List<>();
        String currentSubString = "";
        boolean comment = false;
        boolean blockComment = false;

        int i = 0;
        while (i < input.length()) {
            if (blockComment){
                // testen, ob der Block-Kommentar beendet werden soll
               if (i+1 < input.length() && input.charAt(i) == '#' && input.charAt(i+1) == '#'){
                   i++;
                   blockComment = false;
               }
            } else if (comment){
                // testen, ob der Kommentar beendet werden soll
                if (input.charAt(i) == '\n'){
                    comment = false;
                }
            }else{
                currentSubString = "";
                while (i < input.length() && !isSeparator(input.charAt(i))) {
                    currentSubString = currentSubString + (input.charAt(i));
                    i++;
                }

                if (currentSubString.length() > 0) {
                    stringList.append(currentSubString);

                }
                if (i < input.length() && !isWhitespace(input.charAt(i))) {
                    // testen, ob ein Kommentar folgt
                    if (input.charAt(i) == '#'){
                        comment = true;
                        // testen, ob es sich um einen Block-Kommentar nandelt
                        if (i+1 < input.length() && input.charAt(i+1) == '#'){
                            i++;
                            comment = false;
                            blockComment = true;
                        }
                    }
                    else stringList.append(Character.toString(input.charAt(i)));
                }

            }
            i = i + 1;
        }
        return stringList;
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

    private boolean isBoolean(String s){
        return s.equals("true") || s.equals("false");
    }

    private boolean isIdentifier(String s){
        String state = "S";
        for (int i = 0; i < s.length(); i++){
            if (state.equals("S")){
                if (isLowercaseChar(s.charAt(i))) state = "ID";
                else return false;
            } else{
                if (!(isLowercaseChar(s.charAt(i))
                        || isUppercaseChar(s.charAt(i))
                        || isNumberChar(s.charAt(i))))
                    return false;
            }
        }
        return state.equals("ID");
    }

    private Token translate(String tokenString) throws Exception {
        if (tokenString.equals("+"))
            return new Token(TokenType.PLUS, tokenString);
        else if (tokenString.equals("-"))
            return new Token(TokenType.MINUS, tokenString);
        else if (tokenString.equals("*"))
            return new Token(TokenType.MUL, tokenString);
        else if (tokenString.equals("/"))
            return new Token(TokenType.DIV, tokenString);
        else if (tokenString.equals("^"))
            return new Token(TokenType.POWER, tokenString);
        else if (tokenString.equals("sqrt"))
            return new Token(TokenType.SQRT, tokenString);
        else if (tokenString.equals("%"))
            return new Token(TokenType.MODULO, tokenString);
        else if (tokenString.equals("("))
            return new Token(TokenType.LBRACKET, tokenString);
        else if (tokenString.equals(")"))
            return new Token(TokenType.RBRACKET, tokenString);
        else if(tokenString.equals("PI"))
            return new ContainerToken<>(TokenType.FLOAT, tokenString, Math.PI);
        else if(tokenString.equals("E"))
            return new ContainerToken<>(TokenType.FLOAT, tokenString, Math.E);
        else if (isFloat(tokenString)){
            return new ContainerToken<>(TokenType.FLOAT, tokenString, Double.parseDouble(tokenString));}

        else if(tokenString.equals("!"))
            return new Token(TokenType.NOT, tokenString);
        else if(tokenString.equals("="))
            return new Token(TokenType.EQUALS, tokenString);
        else if(tokenString.equals("&"))
            return new Token(TokenType.AND, tokenString);
        else if(tokenString.equals("|"))
            return new Token(TokenType.OR, tokenString);
        else if (tokenString.equals("~"))
            return new Token(TokenType.XOR, tokenString);
        else if(tokenString.equals("<"))
            return new Token(TokenType.SMALLER, tokenString);
        else if(tokenString.equals(">"))
            return new Token(TokenType.GREATER, tokenString);
        else if(isBoolean(tokenString))
            return new ContainerToken<>(TokenType.BOOLEAN, tokenString, Boolean.parseBoolean(tokenString));

        else if(tokenString.equals("then"))
            return new Token(TokenType.THEN, tokenString);
        else if(tokenString.equals("else"))
            return new Token(TokenType.ELSE, tokenString);
        else if(tokenString.equals("{"))
            return new Token(TokenType.LCURLYBRACKET, tokenString);
        else if(tokenString.equals("}"))
            return new Token(TokenType.RCURLYBRACKET, tokenString);
        else if (tokenString.equals(","))
            return new Token(TokenType.COMMA, tokenString);
        else if (tokenString.equals(":"))
            return new Token(TokenType.COLON, tokenString);
        else if(tokenString.equals("let"))
            return new Token(TokenType.LET, tokenString);
        else if(tokenString.equals("in"))
            return new Token(TokenType.IN, tokenString);
        else if (tokenString.equals("where"))
            return new Token(TokenType.WHERE, tokenString);
        else if (tokenString.equals("end"))
            return new Token(TokenType.END, tokenString);
        else if(tokenString.equals("if"))
            return new Token(TokenType.IF, tokenString);
        else if (tokenString.equals("print"))
            return new Token(TokenType.PRINT, tokenString);
        else if (tokenString.equals("input"))
            return new Token(TokenType.INPUT, tokenString);

        else if (tokenString.equals("sin"))
            return new Token(TokenType.SIN, tokenString);
        else if (tokenString.equals("cos"))
            return new Token(TokenType.COS, tokenString);
        else if (tokenString.equals("tan"))
            return new Token(TokenType.TAN, tokenString);
        else if (tokenString.equals("log"))
            return new Token(TokenType.LOG, tokenString);
        else if (tokenString.equals("exp"))
            return new Token(TokenType.EXP, tokenString);
        
        else if (tokenString.equals(";"))
            return new Token(TokenType.SEMICOLON, tokenString);
        else if (tokenString.equals("["))
            return new Token(TokenType.LSQUAREBRACKET, tokenString);
        else if (tokenString.equals("]"))
            return new Token(TokenType.RSQUAREBRACKET, tokenString);
        else if (tokenString.equals("add"))
            return new Token(TokenType.ADD, tokenString);
        else if (tokenString.equals("map"))
            return new Token(TokenType.MAP, tokenString);

        else if(isIdentifier(tokenString))
            return new ContainerToken<>(TokenType.ID, tokenString, tokenString);

        else {
            throw new Exception("unknown token '" + tokenString+"'");
        }
    }

    public List<Token> scan(String input) throws Exception {
        List<Token> tokenList = new List<>();
        List<String> stringList = split(input);

        for (stringList.toFirst(); stringList.hasAccess(); stringList.next()) {
            tokenList.append(translate(stringList.getContent()));
        }
        return tokenList;
    }
}