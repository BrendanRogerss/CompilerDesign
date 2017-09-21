import java.util.ArrayList;

import static com.sun.corba.se.impl.util.Utility.printStackTrace;

/**
 * Created by Brendan on 9/9/17.
 *
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

    private boolean check(String tok, boolean throwError) {
        if (tokens.get(0).tok.equals(tok)) {
            tokens.remove(0);
            return true;
        }
        if (throwError) {
            System.out.println("Error: Unexpected token. Wanted: " + tok + " got: " + tokens.get(0).tok);
            printStackTrace();
            System.exit(1);
        }
        return false;
    }

    private boolean checkAndNotConsume(String tok, boolean throwError) {
        if (tokens.get(0).tok.equals(tok)) {
            return true;
        }
        if (throwError) {
            //throw error
        }
        return false;
    }

    public void setStRec(TreeNode node) {
        String v = tokens.get(0).value;
        tokens.remove(0);
    }

    public void setLexem(TreeNode node){
        node.setName(new StRec(tokens.get(0).value,tokens.get(0).line));
        tokens.remove(0);
    }

    public void setType(TreeNode node){
        node.setType(new StRec(tokens.get(0).value,tokens.get(0).line));
        tokens.remove(0);
    }

    public void setType(TreeNode node, String type){
        node.setType(new StRec(type,tokens.get(0).line));
        //tokens.remove(0);
    }

    private TreeNode program() {
        check("TCD", true);
        //TODO id
        check("TIDNT", true);
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
        checkAndNotConsume("TIDNT", true);
        setLexem(node);
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
        if(check("TARRS", false)){
            return arrdecl();
        }
        return null;
    }

    private TreeNode funcs() {
        TreeNode node = new TreeNode(Node.NFUNCS);
        if (checkAndNotConsume("TFUNC", false)) {
            node.setLeft(func());
            node.setRight(funcs());
            return node;
        }
        return null;
    }

    private TreeNode mainbody() {
        check("TMAIN", true);
        TreeNode node = new TreeNode(Node.NMAIN);
        node.setLeft(slist());
        check("TBEGN", true);
        node.setRight(stats());
        check("TENDK", true);
        //TODO: something about an id
        check("TCD", true);
        check("TIDNT", true);
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

        TreeNode node = new TreeNode(Node.NATYPE);
        checkAndNotConsume("TIDNT", true);
        setLexem(node);
        check("TISKW", true);

        if (check("TARRY", false)) {
            check("TLBRK", true);
            node.setLeft(expr());
            check("TRBRK", true);
            check("TOFKW", true);
            checkAndNotConsume("TIDNT", true);
            setType(node);
        } else {
            node.setValue(Node.NRTYPE);
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
        checkAndNotConsume("TIDNT", true);
        setLexem(node);
        check("TCOLN", true);
        if (checkAndNotConsume("TIDNT", false)) {
            node.setValue(Node.NARRD);
            setType(node);
            return node;
        }
        setType(node, stype());
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
        checkAndNotConsume("TIDNT", true);
        setLexem(node);
        check("TCOLN", true);
        checkAndNotConsume("TIDNT", true);
        setType(node);
        return node;
    }

    private TreeNode func() {
        TreeNode node = new TreeNode(Node.NFUND);
        check("TFUNC", true);
        checkAndNotConsume("TIDNT", true);
        setLexem(node);
        check("TLPAR", true);
        node.setLeft(plist());
        check("TRPAR", true);
        check("TCOLN", true);
        setType(node,rtype());
        return funcbody(node);
    }

    private String rtype() {
        if (check("TVOID", false)) {
            return "VOID";
        }
        return stype();

    }

    private TreeNode plist() {
        if (checkAndNotConsume("TIDNT", false) || check("TCNST", false)) {
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
        if (check("TCNST", false)) {
            TreeNode node = new TreeNode(Node.NARRC);
            node.setLeft(arrdecl());
            return node;
        }

        checkAndNotConsume("TIDNT", true);
        TreeNode node = new TreeNode(Node.NUNDEF);
        setLexem(node);
        check("TCOLN", true);
        if (checkAndNotConsume("TIDNT", false)) {
            node.setValue(Node.NARRP);
            setType(node);
            return node;
        }
        node.setValue(Node.NSIMP);
        setType(node,stype());
        return node;
    }

    private TreeNode funcbody(TreeNode nfunc) {
        nfunc.setMiddle(locals());
        check("TBEGN", true);
        nfunc.setRight(stats());
        check("TENDK", true);
        return nfunc;
    }

    private TreeNode locals() {
        if (checkAndNotConsume("TIDNT", false)) {
            return dlist();
        }
        return null;
    }

    private TreeNode dlist() {
        TreeNode node = new TreeNode(Node.NDLIST);
        node.setLeft(sdecl());
        node.setRight(dlisttail());
        return node;
    }

    private TreeNode dlisttail() {
        if (check("TCOMA", false)) {
            return dlist();
        }
        return null;

    }

    private String stype() {
        if(check("TINTG",false)){
            return "integer";
        }else if(check("TREAL",false)){
            return "real";
        }else if(check("TBOOL",false)){
            return "boolean";
        }
        return null;
    }

    private TreeNode stats() {
        TreeNode node = new TreeNode(Node.NSTATS);
        if (checkAndNotConsume("TFORK", false) || checkAndNotConsume("TIFKW", false)) {//TODO
            node.setLeft(strstat());
        } else {
            node.setLeft(stat());
            check("TSEMI", true);
        }
        node.setRight(statstail());
        return node;
    }

    private TreeNode statstail() {
        if (checkAndNotConsume("TENDK", false) || checkAndNotConsume("TUNTL", false) ||
                checkAndNotConsume("TELSE", false)) {
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
        if (checkAndNotConsume("TREPT", false)) {
            return repstat();
        } else if (checkAndNotConsume("TIDNT", false)) {
            return idstat();
        } else if (checkAndNotConsume("TINKW", false) || checkAndNotConsume("TOUTP", false)) {
            return iostat();
        }
        checkAndNotConsume("TRETN", true);
        return returnstat();
    }

    private TreeNode idstat() {
        TreeNode node = new TreeNode(Node.NUNDEF);
        if (checkAndNotConsume("TIDNT", false)) {
            setLexem(node);
        }
        return idtail(node);
    }

    private TreeNode idtail(TreeNode id) {
        if (checkAndNotConsume("TLPAR", false)) {
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
        if (checkAndNotConsume("TIDNT", false)) {
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
        node.setLeft(varNode);
        check("TASGN", true);
        node.setRight(bool());
        return node;
    }

    private TreeNode iostat() {

        if (check("TINKW", false)) {
            TreeNode node = new TreeNode(Node.NINPUT);
            check("TINPT", true);
            node.setLeft(vlist());
            return node;
        } else if (check("TOUTP", false)) {//TODO: check true / flase
            TreeNode node = new TreeNode(Node.NOUTP);
            check("TASGN", true);
            return iostatmid(node);
        }
        System.out.println("Error in istat??");
        return null;
    }

    private TreeNode iostatmid(TreeNode outNode) {
        if (check("TOUTL", false)) {
            outNode.setValue(Node.NOUTL);
        } else {
            outNode.setMiddle(prlist());
            outNode = iostatpr(outNode);
        }
        return outNode;
    }

    private TreeNode iostatpr(TreeNode outNode) {
        if (check("TASGN", false) && check("TOUTL", false)) {
            outNode.setValue(Node.NOUTL);
        }
        return outNode;
    }

    private TreeNode callstat(TreeNode callNode) {
        callNode.setValue(Node.NCALL);
        check("TLPAR", true);
        callNode.setLeft(callstatelist());
        check("TRPAR", true);
        return callNode;
    }

    private TreeNode callstatelist() {
        if (checkAndNotConsume("TNOTK", false) || checkAndNotConsume("TIDNT", false) ||
                checkAndNotConsume("TTLIT", false) || checkAndNotConsume("TFLIT", false) ||
                checkAndNotConsume("TTRUE", false) || checkAndNotConsume("TFALS", false) ||
                checkAndNotConsume("TLPAR", false)) {

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
        if (checkAndNotConsume("TIDNT", false) || checkAndNotConsume("TILIT", false) ||
                checkAndNotConsume("TFLIT", false) || checkAndNotConsume("TTRUE", false) ||
                checkAndNotConsume("TFALS", false) || checkAndNotConsume("TLPAR", false)) {
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
        if (checkAndNotConsume("TIDNT", false)) {
            setLexem(node);
        }
        return vararr(node);
    }

    private TreeNode vararr(TreeNode varNode) {
        if (check("TLBRK", false)) {
            varNode.setValue(Node.NAELT);
            varNode.setLeft(expr());
            check("TRBRK", true);
            return vararrvar(varNode);
        }
        varNode.setValue(Node.NSIMV);
        return varNode;
    }

    private TreeNode vararrvar(TreeNode vararrNode) {
        if (check("TDOTT", false)) {
            vararrNode.setValue(Node.NARRV);
            if (checkAndNotConsume("TIDNT", true)) {
                TreeNode node = new TreeNode(Node.NSIMV);
                setLexem(node);
                vararrNode.setRight(node);
            }
        }
        return vararrNode;
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
        if (check("TANDK", false) || check("TORKW", false) || check("TXORK", false)) {
            TreeNode node = logop(relNode);
            node.setRight(rel());
            return booltail(node);
        }
        return relNode;
    }

    private TreeNode rel() {
        if (check("TNOTK", false)) {
            TreeNode node = new TreeNode(Node.NNOT);
            node.setMiddle(rel());
            return node;
        }
        return reltail(expr());
    } //TODO: fix NNOT

    private TreeNode reltail(TreeNode expr) {
        if (checkAndNotConsume("TDEQL", false) || checkAndNotConsume("TNEQL", false) || checkAndNotConsume("TGRTR", false) ||
                checkAndNotConsume("TLEQL", false) || checkAndNotConsume("TLESS", false) ||
                checkAndNotConsume("TGREQ", false)) {
            TreeNode node = relop(expr);
            node.setRight(expr());
            return node;
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

        if (node == null) {
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
        if (check("TADDT", false)) {
            TreeNode node = new TreeNode(Node.NADD);
            node.setLeft(term);
            node.setRight(term());
            return exprtail(node);
        } else if (check("TSUBT", false)) {
            TreeNode node = new TreeNode(Node.NSUB);
            node.setLeft(term);
            node.setRight(term());
            return exprtail(node);
        } else {
            return term;
        }
    }

    //todo: try and remove passing node
    private TreeNode term() {
        return termtail(fact());
    }

    private TreeNode termtail(TreeNode fact) {

        if (check("TMULT", false)) {
            TreeNode node = new TreeNode(Node.NMUL);
            node.setLeft(fact);
            node.setRight(term());
            return termtail(node);
        } else if (check("TDIVT", false)) {
            TreeNode node = new TreeNode(Node.NDIV);
            node.setLeft(fact);
            node.setRight(term());
            return termtail(node);
        } else if (check("TPERC", false)) {
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
        if (check("TCART", false)) {
            TreeNode node = new TreeNode(Node.NPOW);
            node.setLeft(exp);
            node.setRight(exponent());
            return facttail(node);
        }
        return exp;
    }

    private TreeNode exponent() {
        if (checkAndNotConsume("TIDNT", false)) {
            return idexp();
        } else if (checkAndNotConsume("TILIT", false)) {
            //todo symbol table shit
            TreeNode node = new TreeNode(Node.NILIT);
            setLexem(node);
            return node;
        }else if(checkAndNotConsume("TFLIT",false)){
            TreeNode node = new TreeNode(Node.NFLIT);
            setLexem(node);
            return node;
        } else if (check("TTRUE", false)) {
            TreeNode node = new TreeNode(Node.NTRUE);
            return node;
        } else if (check("TFALS", false)) {
            TreeNode node = new TreeNode(Node.NFALS);
            return node;
        } else if (check("TLPAR", false)) {
            TreeNode bool = bool();
            check("TRPAR", true);
            return bool;
        } else {
            //todo: throw error??
            System.out.println("something went wrong in exponent");
        }
        return null;
    }

    private TreeNode idexp() {
        TreeNode node = new TreeNode(Node.NUNDEF);
        if (checkAndNotConsume("TIDNT", false)) {
            //todo id stuff, should be checkandnotconsume
            setLexem(node);
        }
        return idexptail(node);
    }

    private TreeNode idexptail(TreeNode idexp) {
        if (checkAndNotConsume("TLPAR", false)) {
            return fncall(idexp);
        } else {
            return vararr(idexp);
        }
    }

    private TreeNode fncall(TreeNode idexp) {

        idexp.setValue(Node.NFCALL);
        check("TLPAR", true);
        idexp.setMiddle(fncalllist());
        check("TRPAR", true);

        return idexp;
    }

    private TreeNode fncalllist() {
        if (checkAndNotConsume("TNOTK", false) || checkAndNotConsume("TIDNT", false) || checkAndNotConsume("TFLIT", false) || checkAndNotConsume("TILIT", false) ||
                checkAndNotConsume("TTRUE", false) || checkAndNotConsume("TFALS", false) || checkAndNotConsume("TLPAR", false)) {
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
            return prlist();
        }
        return null;
    }

    private TreeNode printitem() {
        if (check("TSTRG", false)) {
            TreeNode node = new TreeNode(Node.NSTRG);
            //todo symbol shit
            return node;
        }
        return expr();
    }
}