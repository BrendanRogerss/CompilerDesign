public class OutputController {

    private int col = 0;
    private int row = 0;
    public void print(String s){
        String buffer = " ";
        if(col > 60){
            col=s.length()+1;
            System.out.print("\n"+s);
            row++;
        }else{
            if(col%6!=0){
                for (int i = 0; i < 5-col%6; i++) {
                    buffer+=" ";
                }
            }
            if(row == 0 && col == 0){
                col+=s.length()+buffer.length();
                System.out.print(s);
            }else {
                col += s.length() + buffer.length();
                System.out.print(buffer + s);

            }
        }
    }

    public void printError(String s){

    }

}
