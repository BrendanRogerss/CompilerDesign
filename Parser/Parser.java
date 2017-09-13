import java.util.ArrayList;

/**
 * Created by Brendan on 9/9/17.
 */
public class Parser {

    private ArrayList<String> tokens;

    public Parser(ArrayList<String> t){
        tokens = t;
    }

    public void run(){

    }

    public boolean check(String tok){
        if(tokens.get(0).equals(tok)){
            tokens.remove(0);
            return true;
        }
        return false;
    }


    private TreeNode program(){
        check("TCD");
        //TODO: something with <id>
        TreeNode node = new TreeNode(Node.NPROG);
        node.setLeft(globals());
        node.setMiddle(funcs());
        node.setRight(mainbody());
        return node;
    }
    private TreeNode globals(){
        TreeNode node = new TreeNode(Node.NGLOB);
        node.setLeft(consts());
        node.setMiddle(types());
        node.setRight(arrays());
        return node;
    }
    private TreeNode consts(){
        if(check("TCONS")){
            return initlist();
        }else{
            return null;
        }

    }
    private TreeNode initlist(){
        TreeNode node = new TreeNode(Node.NILIST);
        node.setLeft(init());
        node.setRight(initlisttail());
        return node;
    }
    private TreeNode initlisttail(){
        if(check("TCOMA")){
            return initlist();
        }
        return null;
    }
    private TreeNode init(){
        TreeNode node = new TreeNode(Node.NINIT);
        //TODO:deal with <id>
        check("TISKW");
        node.setLeft(expr());
        return node;
    }
    private TreeNode types(){
        if(check("TTYPS")){
            return typelist();
        }
        return null;
    }
    private TreeNode arrays(){
        check("TARRS");
        return arrdecl();
    }
    private TreeNode funcs(){
        TreeNode node = new TreeNode(Node.NFUNCS);
        node.setLeft(func());
        if(check("TFUNC")){
            node.setRight(funcs());
        }
        return node;
    }
    private TreeNode mainbody(){
        check("TMAIN");
        TreeNode node = new TreeNode(Node.NMAIN);
        node.setLeft(slist());
        check("begin");
        node.setRight(stats()); //maybe middle?
        check("end cd");
        //TODO: something about an id
        return node;

    }
    private TreeNode slist(){
        TreeNode node = new TreeNode(Node.NSDLST);
        node.setLeft(sdecl());
        node.setRight(slisttrail());
        return node;
    }
    private TreeNode slisttrail(){
        if(check("TCOMA")){
            return slist();
        }
        return null;
    }
    private TreeNode typelist(){
        TreeNode node = new TreeNode(Node.NTYPEL);
        node.setLeft(type());
        node.setRight(typelisttail());
        return node;
    }
    private TreeNode typelisttail(){
        if(check("TIDNT")){
            return typelist();
        }
        return null;
    }
    private TreeNode type(){
        //TODO: id stuff
        check("TISKW");
        return typetail();
    }
    private TreeNode typetail(){
        TreeNode node = new TreeNode(Node.NATYPE);
        if(check("TARRY")){
            check("TLBRK");
            node.setLeft(expr());
            check("TRBRK");
            check("TOFKW");
            //node.setRight(); TODO struct id
        }else{
            node = new TreeNode(Node.NRTYPE);
            node.setLeft(fields());
            check("TENDK");
        }
        return node;
    }
    private TreeNode fields(){
        TreeNode node = new TreeNode(Node.NFLIST);
        node.setLeft(sdecl());
        node.setRight(fieldsTail());
        return node;
    }
    private TreeNode fieldsTail(){
        if(check("TCOMA")){
            return fields();
        }
        return null;
    }
    private TreeNode sdecl(){
        TreeNode node = new TreeNode(Node.NSDECL);
        //node.setLeft(); todo: id
        check("TCOLN");
        node.setRight(stype());
        return node;
    }
    private TreeNode arrdecls(){

    }
    private TreeNode arrdeclstail(){}
    private TreeNode arrdecl(){}
    private TreeNode udecl(){}
    private TreeNode udecltail(){}
    private TreeNode func(){}
    private TreeNode rtype(){}
    private TreeNode plist(){}
    private TreeNode params(){}
    private TreeNode paramstail(){}
    private TreeNode param(){}
    private TreeNode paramtail(){}
    private TreeNode param(){}
    private TreeNode funcbody(){}
    private TreeNode locals(){}
    private TreeNode dlist(){}
    private TreeNode dlisttail(){}
    private TreeNode decl(){}
    private TreeNode stype(){}
    private TreeNode stats(){}
    private TreeNode statstail(){}
    private TreeNode strstat(){}
    private TreeNode stat(){}
    private TreeNode idstat(){}
    private TreeNode idtail(){}
    private TreeNode forstat(){}
    private TreeNode repstat(){}
    private TreeNode asgnlist(){}
    private TreeNode alist(){}
    private TreeNode alisttail(){}
    private TreeNode ifstat(){}
    private TreeNode elsestat(){}
    private TreeNode assgnstat(){}
    private TreeNode idasgnstat(){}
    private TreeNode iostat(){}
    private TreeNode iostatmid(){}
    private TreeNode iostatpr(){}
    private TreeNode callstat(){}
    private TreeNode callstatelist(){}
    private TreeNode returnstat(){}
    private TreeNode returnstattail(){}
    private TreeNode vlist(){}
    private TreeNode vlisttail(){}
    private TreeNode var(){}
    private TreeNode vararr(){}
    private TreeNode vararrvar(){}
    private TreeNode elist(){}
    private TreeNode elisttail(){}
    private TreeNode bool(){}
    private TreeNode booltail(){}
    private TreeNode rel(){} //TODO: fix NNOT
    private TreeNode reltail(){}
    private TreeNode logop(){}
    private TreeNode relop(){}
    private TreeNode expr(){}
    private TreeNode exprtail(){}
    private TreeNode term(){}
    private TreeNode termtail(){}
    private TreeNode fact(){}
    private TreeNode facttail(){}
    private TreeNode exponent(){}
    private TreeNode idexp(){}
    private TreeNode fncall(){}
    private TreeNode fncalllist(){}
    private TreeNode prlist(){}
    private TreeNode prlisttail(){}
    private TreeNode printitem(){}
}
