import java.io.IOException;

/**
 * Created by Brendan on 15/8/17.
 */
public class A1 {

    private Scanner scanner;
    private Input input;
    static OutputController out = new OutputController();

    public static void main(String[] args) throws IOException {
        A1 run = new A1();
        //run.run(args[0]);
        run.run("TestCode/cdHelloWorld.txt");

    }

    public void run(String filename) throws IOException {
        input = new Input(filename);
        scanner = new Scanner(input);


        while(!scanner.eof()){
            out.print(scanner.getToken());
        }
        out.print("EOF");

    }
}
