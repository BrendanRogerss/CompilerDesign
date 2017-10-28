import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by Brendan on 23/8/17.
 */
public class A3 {

    private A1 scanner = new A1(); //scanner to get tokens from the file
    private ArrayList<Token> tokens; //tokens from the file
    private Parser parser; //the parser class


    public static void main(String[] args) {
        A3 a3 = new A3();
        //a3.run(args[0]);
        a3.run("TestCode/ptest2.txt");
    }

    public void run(String filename){
        tokens = scanner.run(filename); //get a list of all the tokens
        parser = new Parser(tokens); //init the parser
        System.out.println(listing(filename));
        parser.run(); //run the parser, building the tree
        //System.out.println();
        //printTree(parser.root, 0);
        System.out.println(); //space
        printSpec(parser.root); //print the tree
    }

    public void printSpec(TreeNode node){
        ArrayList<String> tree = new ArrayList<>(); //list of node ids
        printRec(node,tree); //get a list of all the node ids
        int col = 0;
        String output = "";
        for (String sNode : tree) { //go through the list
            if(col>=70){ //check length of row
                output+="\n";
                col = 0;
            }
            col+=sNode.length(); //increase row length
            output+=sNode; //add to output string
        }
        System.out.println(output); //print the output
    }

    //recursively goes through the tree, adding the id of each node to a list
    private ArrayList<String> printRec(TreeNode node, ArrayList<String> tree){
        if(node != null) {
            tree.add(node.toString()); //add node to list
            printRec(node.getLeft(),tree); //add left
            printRec(node.getMiddle(),tree); //add mid
            printRec(node.getRight(),tree); //add right
        }
        return tree; //return node id
    }

    //alt print methods
    public void printSpace(TreeNode node){
        if(node != null) {
            System.out.print(node.getValue() + " ");
            printSpace(node.getLeft());
            printSpace(node.getMiddle());
            printSpace(node.getRight());
        }
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

    public String listing(String filename){
        String output = "";
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filename)))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                // process the line.
                output+=i+") "+line+"\n";
                i++;
            }
        }catch (Exception e){

        }

        return output;
    }

}
