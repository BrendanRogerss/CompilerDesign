import sun.reflect.generics.tree.Tree;

import java.util.ArrayList;

import static com.sun.corba.se.impl.util.Utility.printStackTrace;

/**
 * Created by Brendan on 9/9/17.
 */
public class Parser {

    private ArrayList<Token> tokens;
    private SymbolTable symbolTable = new SymbolTable();
    TreeNode root;

    public Parser(ArrayList<Token> t) {
        tokens = t;
    }

    public void run() {
        root = program();
    }

    public boolean check(String tok, boolean throwError) {
        if (tokens.get(0).tok.equals(tok)) {
            tokens.remove(0);
            return true;
        }
        if (throwError) {
            System.out.println("Error: Unexpected token. Wanted: "+tok+" got: "+tokens.get(0).tok);
            printStackTrace();
            System.exit(1);
        }
        return false;
    }

    public boolean checkAndNotConsume(String tok, boolean throwError) {
        if (tokens.get(0).tok.equals(tok)) {
            return true;
        }
        if (throwError) {
            //throw error
        }
        return false;
    }

    public void setStRec(TreeNode node){
        String v = tokens.get(0).value;
        tokens.remove(0);
    }

    private TreeNode program() {
        check("TCD", true);
        //TODO id
        check("TIDNT",true);
        TreeNode node = new TreeNode(Node.NPROG);
        node.setLeft(globals());
        node.setMiddle(funcs());
        node.setRight(mainbody());
        return node;
    }

    private TreeNode globals() {
        TreeNode node = new TreeNode(Node.NGLOB);
        node.setLeft(consts());
        node.setMiddle(types());
        node.setRight(arrays());
        return node;
    }

    private TreeNode consts() {
        if (check("TCONS", false)) {
            return initlist();
        } else {
            return null;
        }

    }

    private TreeNode initlist() {
        TreeNode node = new TreeNode(Node.NILIST);
        node.setLeft(init());
        node.setRight(initlisttail());
        return node;
    }

    private TreeNode initlisttail() {
        if (check("TCOMA", false)) {
            return initlist();
        }
        return null;
    }

    private TreeNode init() {
        TreeNode node = new TreeNode(Node.NINIT);
        //TODO:deal with <id>
        check("TIDNT",true);
        check("TISKW", true);
        node.setLeft(expr());
        return node;
    }

    private TreeNode types() {
        if (check("TTYPS", false)) {
            return typelist();
        }
        return null;
    }

    private TreeNode arrays() {
        check("TARRS", true);
        return arrdecl();
    }

    private TreeNode funcs() {
        TreeNode node = new TreeNode(Node.NFUNCS);
        node.setLeft(func());
        if (check("TFUNC", false)) {
            node.setRight(funcs());
        }
        return node;
    }

    private TreeNode mainbody() {
        check("TMAIN", true);
        TreeNode node = new TreeNode(Node.NMAIN);
        node.setLeft(slist());
        check("begin", true);
        node.setRight(stats()); //maybe middle?
        check("end cd", true);
        //TODO: something about an id
        check("TIDNT",true);
        return node;

    }

    private TreeNode slist() {
        TreeNode node = new TreeNode(Node.NSDLST);
        node.setLeft(sdecl());
        node.setRight(slisttrail());
        return node;
    }

    private TreeNode slisttrail() {
        if (check("TCOMA", false)) {
            return slist();
        }
        return null;
    }

    private TreeNode typelist() {
        TreeNode node = new TreeNode(Node.NTYPEL);
        node.setLeft(type());
        node.setRight(typelisttail());
        return node;
    }

    private TreeNode typelisttail() {
        if (checkAndNotConsume("TIDNT", false)) {
            return typelist();
        }
        return null;
    }

    private TreeNode type() {
        //TODO: id stuff
        check("TIDNT",true);
        check("TISKW", true);
        return typetail();
    }

    private TreeNode typetail(TreeNode typeNode) {
        TreeNode node = new TreeNode(Node.NATYPE);
        if (check("TARRY", false)) {
            check("TLBRK", true);
            node.setLeft(expr());
            check("TRBRK", true);
            check("TOFKW", true);
            //node.setRight(); TODO struct id
            check("TIDNT",true);
        } else {
            node = new TreeNode(Node.NRTYPE);
            node.setLeft(fields());
            check("TENDK", true);
        }
        return node;
    }

    private TreeNode fields() {
        TreeNode node = new TreeNode(Node.NFLIST);
        node.setLeft(sdecl());
        node.setRight(fieldsTail());
        return node;
    }

    private TreeNode fieldsTail() {
        if (check("TCOMA", false)) {
            return fields();
        }
        return null;
    }

    private TreeNode sdecl() {
        TreeNode node = new TreeNode(Node.NSDECL);
        //node.setLeft(); todo: id
        check("TIDNT",true);
        check("TCOLN", true);
        stype();//todo: some type shit?
        return node;
    }

    private TreeNode arrdecls() {
        TreeNode node = new TreeNode(Node.NALIST);
        node.setLeft(arrdecl());
        node.setRight(arrdeclstail());
        return node;
    }

    private TreeNode arrdeclstail() {
        if (check("TCOMA", false)) {
            return arrdecls();
        }
        return null;
    }

    private TreeNode arrdecl() {
        TreeNode node = new TreeNode(Node.NARRD);
        check("TIDNT", true); //TODO: id stuff
        check("TCOLN", true);
        check("TIDNT", true); //TODO:type id
        return node;
    }

    private TreeNode udecl() {
        check("TIDNT", true);
        //TODO
        check("TCOLN", true);
        TreeNode node = null;
        if (check("TIDNT", false)) {
            node = new TreeNode(Node.NARRD); //todo lots of id shit
            return node;
        }
        node = new TreeNode(Node.NSDECL);
        stype();//todo: stype shit
        return node;
    }
/*
    private TreeNode udecltail() {
        //todo: this
        System.out.println("udecltail");
        if (check("TIDNT", false)) {
            return new TreeNode(Node.NARRD);//TODO:typeid
            //NARRD	    <udecltail>	::=	<typeid>
        }
        //check("");????


        //NSDECL	<udecltail>	::=	<stype>

        return null;
    }
    */

    private TreeNode func() {
        //NFUND	<func>	::=	func  <id> ( <plist> ) : <rtype> <funcbody>
        TreeNode node = new TreeNode(Node.NFUND);
        check("TFUNC", true);
        //todo: id
        check("TIDNT",true);
        check("TLPAR", true);
        node.setLeft(plist());
        check("TRPAR", true);
        check("TCOLN", true);
        //todo rtype
        rtype(); //todo?
        return funcbody(node);
    }

    private String rtype() {
        if(check("TVOID",false)){
            return "VOID";
        }
        return stype();

    }

    private TreeNode plist() {
        if(checkAndNotConsume("TIDNT",false)||check("TCNST",false)){
            return params();
        }
        return null;
    }

    private TreeNode params() {
        TreeNode node = new TreeNode(Node.NPLIST);
        node.setLeft(param());
        node.setRight(paramstail());
        return node;
    }

    private TreeNode paramstail() {
        if (check("TCOMA", false)) {
            return params();
        }
        return null;
    }

    private TreeNode param() {
        if(check("TCNST",false)){
            TreeNode node = new TreeNode(Node.NARRC);
            node.setLeft(arrdecl());
            return node;
        }
        TreeNode node = udecl();
        if(node.getValue() == Node.NSDECL){ //todo: find a better way
            node.setValue(Node.NSIMP);
        }else{
            node.setValue(Node.NARRP);
        }
        return node;
    }

    private TreeNode funcbody(TreeNode nfunc) {
        nfunc.setMiddle(locals());
        check("TBEGN",true);
        nfunc.setRight(stats());
        check("TENDK",true);
        return nfunc;
    }

    private TreeNode locals() {
        if(checkAndNotConsume("TIDNT",false)){
            return dlist();
        }
        return null;
    }

    private TreeNode dlist() {
        TreeNode node = new TreeNode(Node.NDLIST);
        node.setLeft(decl());
        node.setRight(dlisttail());
        return node;
    }

    private TreeNode dlisttail() {
        if (check("TCOMA", false)) {
            return dlist();
        }
        return null;

    }

    private TreeNode decl() {
        return udecl();
    }

    private String stype() {
        //TODO
        //consume();
        tokens.remove(0);
        //Special	<stype>	::=	integer
        //Special	<stype>	::=	real
        //Special	<stype>	::=	boolean
        return null;
    }

    private TreeNode stats() {
        TreeNode node = new TreeNode(Node.NSTATS);
        if (checkAndNotConsume("TFORK", false) || checkAndNotConsume("TIFKW",false)) {//TODO
            node.setLeft(strstat());
        } else{
            node.setLeft(stat());
            check("TSEMI",true);
        }
        node.setRight(statstail());
        return node;
    }

    private TreeNode statstail() {
        if (check("TENDK", false) || check("TUNTL",false) || check("TELSE",false)) {
            return null;
        }
        return stats();
    }

    private TreeNode strstat() {
        if (checkAndNotConsume("TIFKW", false)) {
            return ifstat();
        }
        checkAndNotConsume("TFORK", true);
        return forstat();
    }

    private TreeNode stat() {
        if (check("TREPT", false)) {
            return repstat();
        } else if (check("TIDNT", false)) {
            return idstat();
        } else if (check("TINKW", false) || check("TOUTP", false)) {
            return iostat();
        }
        check("TRETN", true);
        return returnstat();
    }

    private TreeNode idstat() {
        TreeNode node = new TreeNode(Node.NUNDEF);
        if(checkAndNotConsume("TIDNT",false)){
            //todo: id shit
        }
        return idtail(node);
    }

    private TreeNode idtail(TreeNode id) {
        if (check("TLPAR", false)) {
            return callstat(id);
        }
        return idasgnstat(vararr(id));
    }

    private TreeNode forstat() {
        TreeNode node = new TreeNode(Node.NFORL);
        check("TFORK", true);
        check("TLPAR", true);
        node.setLeft(asgnlist());
        check("TSEMI", true);
        node.setMiddle(bool());
        check("TRPAR", true);
        node.setRight(stats());
        check("TENDK", true);
        return node;
    }

    private TreeNode repstat() {
        TreeNode node = new TreeNode(Node.NREPT);
        check("TREPT", true);
        check("TLPAR", true);
        node.setLeft(asgnlist());
        check("TRPAR", true);
        node.setMiddle(stats());
        check("TUNTL", true);
        node.setRight(bool());
        return node;
    }

    private TreeNode asgnlist() {
        if (checkAndNotConsume("TIDNT", false)) {//TODO
            return alist();
        }
        return null;
    }

    private TreeNode alist() {
        TreeNode node = new TreeNode(Node.NASGNS);
        node.setLeft(asgnstat());
        node.setRight(alisttail());
        return node;
    }

    private TreeNode alisttail() {
        if (check("TCOMA", false)) {
            return alist();
        }
        return null;
    }

    private TreeNode ifstat() {
        TreeNode node = new TreeNode(Node.NIFTH);
        check("TIFKW", true);
        check("TLPAR", true);
        node.setLeft(bool());
        check("TRPAR", true);
        node.setMiddle(stats());
        node.setRight(elsestat());
        check("TENDK", true);
        return node;
    }

    private TreeNode elsestat() {
        //todo ########################### might need to pass node through
        if (check("TELSE", false)) {
            return stats();
        }
        return null;
    }

    private TreeNode asgnstat() {
        TreeNode node = new TreeNode(Node.NASGN);
        node.setLeft(var());
        check("TASGN", true);
        node.setRight(bool());
        return node;
    }

    private TreeNode idasgnstat(TreeNode varNode) {
        TreeNode node = new TreeNode(Node.NASGN);
        check("TASGN", true);
        node.setLeft(bool());
        return node;
    }

    private TreeNode iostat() {
        TreeNode node = new TreeNode(Node.NINPUT);
        if (check("TINKW", false)) {
            check("TINPT", true);
            node.setLeft(vlist());

        } else if (check("TOUTP", false)) {//TODO: check true / flase
            node = new TreeNode(Node.NOUTP);
            check("TASGN", true);
        }
        return node;
    }

    private TreeNode iostatmid(TreeNode outNode) {
        //TODO
        //Special	<iostatmid>	::=	<prlist> <iostatpr>
        //NOUTL 	<iostatmid>	::=	Line
        return null;
    }

    private TreeNode iostatpr(TreeNode outNode) {
        TreeNode node = new TreeNode(Node.NOUTL);
        if (check("TINPT", false) && check("TOUTL", false)) {
            return node;
        }
        return null;
    }

    private TreeNode callstat(Node callNode) {
        TreeNode node = new TreeNode(Node.NCALL);
        check("TLPAR", true);
        node.setLeft(callstatelist());
        check("TRPAR", true);
        return node;
    }

    private TreeNode callstatelist() {
        if (check("something", false)) {//TODO
            return elist();
        }
        return null;
    }

    private TreeNode returnstat() {
        TreeNode node = new TreeNode(Node.NRETN);
        check("TRETN", true);
        node.setLeft(returnstattail());
        return node;
    }

    private TreeNode returnstattail() {
        if (check("something", false)) {//TODO
            return expr();
        }
        return null;
    }

    private TreeNode vlist() {
        TreeNode node = new TreeNode(Node.NVLIST);
        node.setLeft(var());
        node.setRight(vlisttail());
        return node;
    }

    private TreeNode vlisttail() {
        if (check("TCOMA", false)) {
            return vlist();
        }
        return null;
    }

    private TreeNode var() {
        TreeNode node = new TreeNode(Node.NSIMV);
        check("TIDNT", true); //TODO idnt stuff
        node.setLeft(vararr());
        return node;
    }

    private TreeNode vararr(TreeNode n) {
        if (check("TLBRK", false)) {
            TreeNode node = new TreeNode(Node.NAELT);
            node.setLeft(expr());
            check("TRBRK", true);
            node.setRight(vararrvar());
            return node;
        }
        return null;
    }

    private TreeNode vararrvar(TreeNode vararrNode) {
        TreeNode node = new TreeNode(Node.NARRV);
        if (check("TCOMA", false)) {
            //todo some id shit, idk.
            check("TIDNT",true);
            return node;
        }
        return null;
    }

    private TreeNode elist() {
        TreeNode node = new TreeNode(Node.NEXPL);
        node.setLeft(bool());
        node.setRight(elisttail());
        return node;
    }

    private TreeNode elisttail() {
        if (check("TCOMA", false)) {
            return elist();
        }
        return null;
    }

    private TreeNode bool() {
        return booltail(rel());
    }

    private TreeNode booltail(TreeNode relNode) {
        if(check("TANDK",false)||check("TORKW",false)||check("TXORK",false)){
            TreeNode node = logop(relNode);
            node.setRight(rel());
            return booltail(node);
        }
        return relNode;
    }

    private TreeNode rel() {
        if(check("TNOTK",false)){
            TreeNode node = new TreeNode(Node.NNOT);
            node.setMiddle(rel());
            return node;
        }
        return reltail(expr());
    } //TODO: fix NNOT

    private TreeNode reltail(TreeNode expr) {
        if(checkAndNotConsume("TDEQL",false)||checkAndNotConsume("TNEQL",false)||checkAndNotConsume("TGRTR",false)||
                checkAndNotConsume("TLEQL",false)|| checkAndNotConsume("TLESS",false) ||
                checkAndNotConsume("TGREQ",false)){
            TreeNode node = relop(expr);
            node.setRight(expr());
            return  node;
        }
        return expr;
    }

    private TreeNode logop(TreeNode rel) {
        TreeNode node = null;//todo check true / false
        if (check("TANDK", false)) {
            node = new TreeNode(Node.NAND);
        } else if (check("TORKW", false)) {
            node = new TreeNode(Node.NOR);
        } else if (check("TXORK", false)) {
            node = new TreeNode(Node.NXOR);
        }
        node.setLeft(rel);
        return node;
    }

    private TreeNode relop(TreeNode expr) {
        TreeNode node = null;
        if (check("TDEQL", false)) {
            node = new TreeNode(Node.NEQL);
        } else if (check("TNEQL", false)) {
            node = new TreeNode(Node.NNEQ);
        } else if (check("TGRTR", false)) {
            node = new TreeNode(Node.NGRT);
        } else if (check("TLEQL", false)) {
            node = new TreeNode(Node.NLEQ);
        } else if (check("TLESS", false)) {
            node = new TreeNode(Node.NLSS);
        } else if (check("TGREQ", false)) {
            node = new TreeNode(Node.NGEQ);
        }

        if(node == null){
            System.out.println(tokens.get(0).tok);
        }
        node.setLeft(expr);
        return node;
    }

    //todo: try and remove passing node
    private TreeNode expr() {
        return exprtail(term());
    }

    private TreeNode exprtail(TreeNode term) {
        if(check("TADDT",false)){
            TreeNode node = new TreeNode(Node.NADD);
            node.setLeft(term);
            node.setRight(term());
            return exprtail(node);
        }else if(check("TSUBT",false)){
            TreeNode node = new TreeNode(Node.NSUB);
            node.setLeft(term);
            node.setRight(term());
            return exprtail(node);
        }else{
            return term;
        }
    }
    //todo: try and remove passing node
    private TreeNode term() {
        return termtail(fact());
    }

    private TreeNode termtail(TreeNode fact) {

        if(check("TMULT",false)){
            TreeNode node = new TreeNode(Node.NMUL);
            node.setLeft(fact);
            node.setRight(term());
            return termtail(node);
        }else if(check("TDIVT",false)){
            TreeNode node = new TreeNode(Node.NDIV);
            node.setLeft(fact);
            node.setRight(term());
            return termtail(node);
        }else if(check("TPERC",false)){
            TreeNode node = new TreeNode(Node.NMOD);
            node.setLeft(fact);
            node.setRight(term());
            return termtail(node);
        }
        return fact;
    }

    private TreeNode fact() {
        return facttail(exponent());
    }

    private TreeNode facttail(TreeNode exp) {
        if(check("TCART",false)){
            TreeNode node = new TreeNode(Node.NPOW);
            node.setLeft(exp);
            node.setRight(exponent());
            return facttail(node);
        }
        return exp;
    }

    private TreeNode exponent() {
        if(check("TIDNT",false)){
            return idexp();
        }else if(check("TILIT",false)){
            //todo symbol table shit
            TreeNode node = new TreeNode(Node.NILIT);
            return node;
        }else if(check("TTRUE",false)){
            TreeNode node = new TreeNode(Node.NFLIT);
            return node;
        }else if(check("TFALS",false)){
            TreeNode node = new TreeNode(Node.NTRUE);
            return node;
        }else if(check("TLPAR",false)){
            TreeNode bool = bool();
            check("TRPAR",true);
            return bool;
        }else{
            //todo: throw error??
        }
        return null;
    }

    private TreeNode idexp() {
        TreeNode node = new TreeNode(Node.NUNDEF);
        if(check("TIDNT",false)){
            //todo id stuff, should be checkandnotconsume
        }
        return idexptail(node);
    }

    private TreeNode idexptail(TreeNode idexp){
        TreeNode node;
        if(check("TLPAR",false)){
            return fncall(idexp);
        }else{
            node = vararr();
            if(node==null){

            }
        }
        return idexp;

    }

    private TreeNode fncall(TreeNode idexp) {

        idexp.setValue(Node.NFCALL);
        check("TLPAR",true);
        idexp.setMiddle(fncalllist());
        check("TRPAR",true);

        return idexp;
    }

    private TreeNode fncalllist() {
        if(check("TNOTK",false) || check("TIDNT",false)||check("TFLIT",false)||check("TILIT",false) ||
                check("TTRUE",false) || check("TFALS",false)||check("TLPAR",false)){
            return elist();
        }
        return null;
    }

    private TreeNode prlist() {
        TreeNode node = new TreeNode(Node.NPRLST);
        node.setLeft(printitem());
        node.setRight(prlisttail());
        return node;
    }

    private TreeNode prlisttail() {
        if (check("TCOMA", false)) {
            return null;
        }
        return prlist();

    }

    private TreeNode printitem() {
        return null;
    }
}
