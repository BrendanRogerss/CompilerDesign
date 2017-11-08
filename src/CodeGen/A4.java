import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by Brendan on 2/11/17.
 */
public class A4 {



    public static void main(String[] args) {
        A4 a4 = new A4();
        //a3.run(args[0]);
        a4.run("TestCode/cdA.txt");
    }

    public void run(String s){
        A3 a3 = new A3();
        CodeGen cg = new CodeGen();
        TreeNode root = a3.run(s);
        cg.run(root);
        String moduleCodes = cg.generateModule();
        System.out.println(moduleCodes);
        makeFile(moduleCodes,s);
    }

    public void makeFile(String opcodes, String sourceName){
        try(  PrintWriter out = new PrintWriter(sourceName.substring(0,sourceName.indexOf('.'))+".sm")  ){
            out.println( opcodes );
        }catch (Exception e){

        }
    }

}
