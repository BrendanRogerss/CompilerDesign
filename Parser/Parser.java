import sun.reflect.generics.tree.Tree;

import java.util.ArrayList;

/**
 * Created by Brendan on 9/9/17.
 */
public class Parser {

    private ArrayList<Token> tokens;
    private SymbolTable symbolTable = new SymbolTable();

    public Parser(ArrayList<Token> t) {
        tokens = t;
    }

    public void run() {

    }

    public boolean check(String tok, boolean throwError) {
        if (tokens.get(0).tok.equals(tok)) {
            tokens.remove(0);
            return true;
        }
        if (throwError) {
            System.out.println("Error: Unexpected token. Wanted: "+tok+" got: "+tokens.get(0).tok);
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
        if (check("TIDNT", false)) {
            return typelist();
        }
        return null;
    }

    private TreeNode type() {
        //TODO: id stuff
        check("TISKW", true);
        return typetail();
    }

    private TreeNode typetail() {
        TreeNode node = new TreeNode(Node.NATYPE);
        if (check("TARRY", false)) {
            check("TLBRK", true);
            node.setLeft(expr());
            check("TRBRK", true);
            check("TOFKW", true);
            //node.setRight(); TODO struct id
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
        check("TCOLN", true);
        node.setRight(stype());
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
        check("TCOMA", true);
        return udecltail();
    }

    private TreeNode udecltail() {
        //todo: this
        if (check("TIDNT", false)) {
            return new TreeNode(Node.NARRD);//TODO:typeid
            //NARRD	    <udecltail>	::=	<typeid>
        }
        //check("");????


        //NSDECL	<udecltail>	::=	<stype>

        return null;
    }

    private TreeNode func() {
        //NFUND	<func>	::=	func  <id> ( <plist> ) : <rtype> <funcbody>
        TreeNode node = new TreeNode(Node.NFUND);
        check("TFUNC", true);
        //todo: id
        check("TLPAR", true);
        node.setLeft(plist());
        check("TRPAR", true);
        check("TCOLN", true);
        //todo rtype
        funcbody(node);
        return node;
    }

    private TreeNode rtype() {
        //Special <rtype>	::=	<stype>
        //Special <rtype>	::=	void
        return null;
    }

    private TreeNode plist() {
        //Special <plist>	::=	<params>
        //Special <plist>	::=	epsilon
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
        //TODO
        //NSIMP	<param>	::=	<udecl>
        //NARRP	<param>	::=	<udecl>
        //NARRC	<param>	::=	const <arrdecl>
        return null;
    }

    private TreeNode funcbody(TreeNode nfund) {
        //Special	<funcbody>	::=	<locals> begin <stats> end
        return null;
    }

    private TreeNode locals() {
        //Special	<locals>	::=	<dlist>
        //Special	<locals>	::=	epsilon
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
        //Special	<stype>	::=	integer
        //Special	<stype>	::=	real
        //Special	<stype>	::=	boolean
        return null;
    }

    private TreeNode stats() {
        TreeNode node = new TreeNode(Node.NSTATS);
        if (check("whateverthefuckgoeshere", false)) {//TODO
            node.setLeft(stat());
            check("TSEMI", true);
            node.setRight(statstail());
        } else {
            node.setLeft(strstat());
            node.setRight(statstail());
        }
        return node;
    }

    private TreeNode statstail() {
        if (check("TIDNT", false)) {
            return stats();
        }
        return null;
    }

    private TreeNode strstat() {
        if (check("TIFKW", false)) {
            return ifstat();
        }
        check("TFORK", true);
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
        //Special	<idstat>	::=	<id> <idtail>
        return null;
    }

    private TreeNode idtail() {
        //Special	<idtail>	::=	<varrarr> <idasgnstat>
        if (check("TLPAR", false)) {
            return callstat();
        }

        //Special	<idtail>	::=	<callstat>
        return null;
    }

    private TreeNode forstat() {
        TreeNode node = new TreeNode(Node.NFORL);
        check("TFORK", true);
        check("TLPAR", true);
        node.setLeft(asgnlist());
        check("TESMI", true);
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
        if (check("something", false)) {//TODO
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

    private TreeNode idasgnstat() {
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

    private TreeNode iostatmid() {
        //TODO
        //Special	<iostatmid>	::=	<prlist> <iostatpr>
        //NOUTL 	<iostatmid>	::=	Line
        return null;
    }

    private TreeNode iostatpr() {
        TreeNode node = new TreeNode(Node.NOUTL);
        if (check("TINPT", false) && check("TOUTL", false)) {
            return node;
        }
        return null;
    }

    private TreeNode callstat() {
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

    private TreeNode vararr() {
        if (!check("TLBRK", false)) {
            return null;
        }
        TreeNode node = new TreeNode(Node.NAELT);
        node.setLeft(expr());
        check("TRBRK", true);
        node.setRight(vararrvar());
        return node;
    }

    private TreeNode vararrvar() {
        TreeNode node = new TreeNode(Node.NARRV);
        if (check("TCOMA", false)) {
            //todo some id shit, idk.
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
        //Special	<bool>	::=	<rel><booltail>
    }

    private TreeNode booltail() {
    }

    private TreeNode rel() {
    } //TODO: fix NNOT

    private TreeNode reltail() {
    }

    private TreeNode logop() {
        TreeNode node = null;//todo check true / false
        if (check("TANDK", false)) {
            node = new TreeNode(Node.NAND);
        } else if (check("TORKW", false)) {
            node = new TreeNode(Node.NOR);
        } else if (check("TXORK", false)) {
            node = new TreeNode(Node.NXOR);
        }
        return node;
    }

    private TreeNode relop() {
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
        return node;
    }

    private TreeNode expr() {
    }

    private TreeNode exprtail() {
    }

    private TreeNode term() {
    }

    private TreeNode termtail() {
    }

    private TreeNode fact() {
    }

    private TreeNode facttail() {
    }

    private TreeNode exponent() {
    }

    private TreeNode idexp() {
    }

    private TreeNode fncall() {
    }

    private TreeNode fncalllist() {
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
    }
}
