import java.util.ArrayList;

/**
 * Created by Brendan on 23/8/17.
 */
public class A3 {


    private A1 scanner = new A1();
    private ArrayList<Token> tokens;
    private Parser parser;


    public static void main(String[] args) {
        A3 a3 = new A3();
        a3.run();
    }

    public void run(){
        tokens = scanner.run("TestCode/cdsrc99.txt");
        parser = new Parser(tokens);
        parser.run();
        printTree(parser.root);
    }

    public void printTree(TreeNode root){
        if(root!=null) {
            System.out.println(root.getValue());
            printTree(root.getLeft());
            printTree(root.getMiddle());
            printTree(root.getRight());
        }

    }

}
