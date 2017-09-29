
public class OutputController {

    private int col = 0;

    //prints each token according to the specification
    public void print(Token t){
        if(t==null){
            return;
        }
        A1.tokens.add(t);

//        String s = t.tok;
//        if(t.value!=null){
//            s+= " "+t.value;
//        }
//        if(s.contains("TUNDF")){//output an undefined token
//            System.out.print("\nError on line: "+ InputController.row+". Undefined  Token");
//            System.out.println("\n"+s+"\n");
//            col=0;
//            return;
//        }
//        s+=" ";
//        while(s.length()%6!=0){ //format so each token lines up
//            s+=" ";
//        }
//        if(col>60){ //move to the next column
//            System.out.println();
//            col=0;
//        }
//        col+=s.length();
//        System.out.print(s);

    }

    //prints errors
    public void printError(String s){
        System.out.println(s);
        col = 0;
    }

}
