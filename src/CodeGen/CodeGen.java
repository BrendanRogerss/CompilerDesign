import java.util.ArrayList;

/**
 * Created by Brendan on 4/11/2017.
 */
public class CodeGen {

    private ArrayList<String> opcodes = new ArrayList();
    private ArrayList<String> integers = new ArrayList<>();
    private ArrayList<String> reals = new ArrayList<>();
    private int literalIndex = 0;

    public void addOp(String s){
        if(s.length()==1){
            s = "0"+s;
        }

        opcodes.add(s);
    }

    public void run(TreeNode root){
        TreeNode main = root.getRight();

        int literals = 1;
        TreeNode nsdlst = main.getLeft();
        while(nsdlst.getRight()!=null){
            literals++;
            nsdlst = nsdlst.getRight();
        }

        addOp("41"); //get ready to aloc space for everything
        addOp(Integer.toString(literals));
        addOp("52");

        TreeNode currentStat = main.getRight();
        while (currentStat!=null && currentStat.getLeft()!=null){
            switch (currentStat.getLeft().getValue()){
                case NASGN:
                    NASGN(currentStat.getLeft());
                    break;
                case NOUTL:
                    NOUTL(currentStat.getLeft());
                    break;
            }
            currentStat = currentStat.getRight();
        }

    }

    public void NASGN(TreeNode node){
        TreeNode asgn = node;
        //load address
        addOp("91");
        //need to add some 0s?
        addOp("00");
        addOp("00");
        addOp("00");
        if(asgn.getLeft().getName().offset == -1){
            //need to add offset
            asgn.getLeft().getName().offset = literalIndex*8;
            literalIndex++;
        }
        addOp(Integer.toString(asgn.getLeft().getName().offset));
        if(node.getRight().getRight() == null){ //no children, therefore no expression
            loadValue(node.getRight().getName());//todo
        }else{
            loadValue(node.getRight().getLeft().getName());
            loadValue(node.getRight().getRight().getName());
            switch (node.getRight().getValue()){
                case NADD:addOp("11");break;
                case NSUB:addOp("12");break;
                case NMUL:addOp("13");break;
                case NDIV:addOp("14");break;
                case NMOD:addOp("15");break;
                case NPOW:addOp("16");break;
            }

        }
        addOp("43");
    }

    public void NOUTL(TreeNode node){
        loadValue(node.getMiddle().getLeft().getName());
        addOp("64");
        addOp("67");
    }

    public void loadValue(StRec value){
        if(Character.isDigit(value.getName().charAt(0))){
            //loading a literal
            addOp("80");
            addOp("00");
            addOp("00");
            addOp("00");
            if(!integers.contains(value.getName())&&!reals.contains(value.getName())) {
                if (value.getName().contains(".")) {
                    reals.add(value.getName());
                }else{
                    integers.add(value.getName());
                }
            }
            addOp("!"+value.getName());
        }else{
            //loading a variable
            addOp("81");
            addOp("00");
            addOp("00");
            addOp("00");

            addOp(Integer.toString(value.offset));
        }
    }

    public String generateModule(){
        addOp("00");
        while(opcodes.size()%8!=0){//pad opcodes
            addOp("00");
        }
        int offset = opcodes.size();
        String module = Integer.toString(offset/8)+"\n";
        //find and replace
        for (String literal : integers) {
            findAndReplace(literal,offset);
            offset+=8;
        }
        for (String literal : reals) {
            findAndReplace(literal,offset);
            offset+=8;
        }

        //build module file
        for (int i = 0; i < opcodes.size()/8; i++) {
            for (int j = 0; j < 8; j++) {
                module+=(opcodes.get(i*8+j).length()==2?"  ":" ")+opcodes.get(i*8+j);
            }
            module+="\n";
        }
        //add integers
        module+=Integer.toString(integers.size())+"\n";
        for (int i = 0; i < integers.size(); i++) {
            module+=" "+integers.get(i)+"\n";
        }
        module+=Integer.toString(reals.size())+"\n";
        for (int i = 0; i < reals.size(); i++) {

            module+=" "+reals.get(i)+"\n";
        }
        module+="0";
        return module;
    }

    public void findAndReplace(String literal, int offset){
        for (int i = 0; i < opcodes.size(); i++){
            if(opcodes.get(i).charAt(0)=='!'&&opcodes.get(i).substring(1).equals(literal)){
                opcodes.remove(i);
                opcodes.add(i,Integer.toString(offset));
            }
        }
    }

}
