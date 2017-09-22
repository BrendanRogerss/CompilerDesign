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
        tokens = scanner.run("TestCode/ptest2.txt");
        parser = new Parser(tokens);
        parser.run();
        //System.out.println();
        //printTree(parser.root, 0);
        System.out.println();
        printSpec(parser.root);
    }

    public void printTree(TreeNode root, int indent){
        if(root!=null) {
            String space = "";
            for (int i = 0; i < indent; i++) {
                space+=i%4==0?"|":" ";
            }
            //space+="\\";
            System.out.println(space+root.getValue());
            printTree(root.getLeft(), indent+4);
            printTree(root.getMiddle(), indent+4);
            printTree(root.getRight(), indent+4);
        }

    }

    public void printSpec(TreeNode node){
        ArrayList<String> tree = new ArrayList<>();
        printRec(node,tree);
        int col = 0;
        String output = "";
        for (String sNode : tree) {
            if(col>=70){
                output+="\n";
                col = 0;
            }
            col+=sNode.length();
            output+=sNode;
        }
        System.out.println(output);
    }

    private ArrayList<String> printRec(TreeNode node, ArrayList<String> tree){
        if(node != null) {
            tree.add(node.toString());
            printRec(node.getLeft(),tree);
            printRec(node.getMiddle(),tree);
            printRec(node.getRight(),tree);
        }
        return tree;
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
