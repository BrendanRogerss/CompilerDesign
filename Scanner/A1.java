import java.io.IOException;

/**
 * Created by Brendan on 15/8/17.
 * Student Number: 3208972
 */
public class A1 {

    private Scanner scanner;
    private InputController inputController;
    static OutputController out = new OutputController();

    public static void main(String[] args) throws IOException {
        A1 run = new A1();
        //run.run(args[0]);
        run.run("TestCode/cdsrc1.txt");

    }

    public void run(String filename) throws IOException {
        inputController = new InputController(filename); //instantiate some objects
        scanner = new Scanner(inputController);


        while(!scanner.eof()){ //loop until the file ends
            out.print(scanner.getToken()); //get token and send it to the output controller
        }
        out.print("TEOF"); //send out the EOD token

    }
}