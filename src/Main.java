import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        Interpreter i = new Interpreter();
        i.execute("code.txt");
        /*System.out.print("input requested: ");
        Scanner scanner = new Scanner(System.in);
        String min = scanner.nextLine();
        System.out.print("input requested: ");
        String max = scanner.nextLine();

        long start = System.currentTimeMillis();

        getPs(Double.parseDouble(min), Double.parseDouble(max));

        long end = System.currentTimeMillis();

        System.out.println(end-start);*/
    }

    private static boolean isN(double x){
        return x%1 == 0 && x >= 0;
    }

    private static boolean sub(double x, double y){
        if (x>1)
            return y%x != 0 && sub(x-1, y);
        else return true;
    }

    private static boolean isP(double x){
        return x != 0 && isN(x) && sub(x-1, x);
    }

    private static double getPs(double min, double max){
        if (min >= max)
            return 0;
        else if (isP(min))
            System.out.println(min);
        return getPs(min+1,max);
    }
}