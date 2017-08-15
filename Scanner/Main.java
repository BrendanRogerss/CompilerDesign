import java.io.IOException;

/**
 * Created by Brendan on 15/8/17.
 */
public class Main {

    private Scanner scanner;
    private Input input;

    public static void main(String[] args) throws IOException {
        Main run = new Main();
        run.run(args[0]);
    }

    public void run(String filename) throws IOException {
        input = new Input(filename);
        scanner = new Scanner(input);
        while(!scanner.eof()){
            System.out.println(scanner.getToken());
        }
    }
}
