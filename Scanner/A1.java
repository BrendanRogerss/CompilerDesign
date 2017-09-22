import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Brendan on 15/8/17.
 * Student Number: 3208972
 */
public class A1 {

    private Scanner scanner;
    private InputController inputController;
    static OutputController out = new OutputController();
    static ArrayList<Token> tokens = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        A1 run = new A1();
        //run.run(args[0]);
        run.run("TestCode/cdsrc2.txt");

    }

    public ArrayList<Token> run(String filename) {

        try{
            inputController = new InputController(filename); //instantiate some objects
            scanner = new Scanner(inputController);


            while (!scanner.eof()) { //loop until the file ends
                out.print(scanner.getToken()); //get token and send it to the output controller
            }
            out.print(new Token("TEOF", InputController.row)); //send out the EOD token
        }catch (Exception e){

        }
        return tokens;
    }
}