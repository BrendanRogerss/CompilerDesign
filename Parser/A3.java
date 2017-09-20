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
        System.out.println();
//        printTree(parser.root, 0);
        System.out.println();
        printSpace(parser.root);
    }

    public void printTree(TreeNode root, int indent){
        if(root!=null) {
            String space = "";
            for (int i = 0; i < indent-1; i++) {
                space+=i%3==0?"|":" ";
            }
            //space+="\\";
            System.out.println(space+root.getValue());
            printTree(root.getLeft(), indent+4);
            printTree(root.getMiddle(), indent+4);
            printTree(root.getRight(), indent+4);
        }

    }

    public void printSpace(TreeNode node){
        if(node != null) {
            System.out.print(node.getValue() + " ");
            printSpace(node.getLeft());
            printSpace(node.getMiddle());
            printSpace(node.getRight());
        }
    }

}
