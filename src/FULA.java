public class FULA {

    public static void main(String[] args) throws Exception {
        Interpreter i = new Interpreter();
        if (args.length == 0) 
            i.execute("code.fula");
        else
            i.execute(args[0]);

        
    }
}