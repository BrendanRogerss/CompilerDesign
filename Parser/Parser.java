import sun.reflect.generics.tree.Tree;

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
        TreeNode node = new TreeNode(Node.NALIST);
        node.setLeft(arrdecl());
        node.setRight(arrdeclstail());
        return node;
    }
    private TreeNode arrdeclstail(){
        if(check("TCOMA")){
            return arrdecls();
        }
        return null;
    }
    private TreeNode arrdecl(){
        TreeNode node = new TreeNode(Node.NARRD);
        check("TIDNT"); //TODO: id stuff
        check("TCOLN");
        check("TIDNT"); //TODO:type id
        return node;
    }
    private TreeNode udecl(){
        check("TIDNT");
        check("TCOMA");
        return udecltail();
    }
    private TreeNode udecltail(){
        //todo: this nonsense
        //NSDECL	<udecltail>	::=	<stype>
        //NARRD	    <udecltail>	::=	<typeid>
        return null;
    }
    private TreeNode func(){
        //NFUND	<func>	::=	func  <id> ( <plist> ) : <rtype> <funcbody>
        return null;
    }
    private TreeNode rtype(){
        //Special <rtype>	::=	<stype>
        //Special <rtype>	::=	void
        return null;
    }
    private TreeNode plist(){
        //Special <plist>	::=	<params>
        //Special <plist>	::=	epsilon
        return null;
    }
    private TreeNode params(){
        TreeNode node = new TreeNode(Node.NPLIST);
        node.setLeft(param());
        node.setRight(paramstail());
        return node;
    }
    private TreeNode paramstail(){
        if(check("TCOMA")){
            return params();
        }
        return null;
    }
    private TreeNode param(){
        //TODO
        //NSIMP	<param>	::=	<udecl>
        //NARRP	<param>	::=	<udecl>
        //NARRC	<param>	::=	const <arrdecl>
        return null;
    }
    private TreeNode funcbody(){
        //Special	<funcbody>	::=	<locals> begin <stats> end
        return null;
    }
    private TreeNode locals(){
        //Special	<locals>	::=	<dlist>
        //Special	<locals>	::=	epsilon
        return null;
    }
    private TreeNode dlist(){
        TreeNode node = new TreeNode(Node.NDLIST);
        node.setLeft(decl());
        node.setRight(dlisttail());
        return node;
    }
    private TreeNode dlisttail(){
        if(check("TCOMA")){
            return dlist();
        }
        return null;

    }
    private TreeNode decl(){
        return udecl();
    }
    private TreeNode stype(){
        //TODO
        //Special	<stype>	::=	integer
        //Special	<stype>	::=	real
        //Special	<stype>	::=	boolean
        return null;
    }
    private TreeNode stats(){
        TreeNode node = new TreeNode(Node.NSTATS);
        if(check("whateverthefuckgoeshere")){//TODO
            node.setLeft(stat());
            check("TSEMI");
            node.setRight(statstail());
        }else{
            node.setLeft(strstat());
            node.setRight(statstail());
        }
        return node;
    }
    private TreeNode statstail(){
        if(check("TIDNT")){
            return stats();
        }
        return null;
    }
    private TreeNode strstat(){
        //Special	<strstat>	::=	<forstat>
        //Special	<strstat>	::=	<ifstat>
        return null;
    }
    private TreeNode stat(){
        //Special	<stat>	::=	<reptstat>
        //Special	<stat>	::=	<idstat>
        //Special	<stat>	::=	<iostat>
        //Special	<stat>	::=	<returnstat>
        return null;
    }
    private TreeNode idstat(){
        //Special	<idstat>	::=	<id> <idtail>
        return null;
    }
    private TreeNode idtail(){
        //Special	<idtail>	::=	<varrarr> <idasgnstat>
        //Special	<idtail>	::=	<callstat>
        return null;
    }
    private TreeNode forstat(){
        TreeNode node = new TreeNode(Node.NFORL);
        check("TFORK");
        check("TLPAR");
        node.setLeft(asgnlist());
        check("TESMI");
        node.setMiddle(bool());
        check("TRPAR");
        node.setRight(stats());
        check("TENDK");
        return node;
    }
    private TreeNode repstat(){
        TreeNode node = new TreeNode(Node.NREPT);
        check("TREPT");
        check("TLPAR");
        node.setLeft(asgnlist());
        check("TRPAR");
        node.setMiddle(stats());
        check("TUNTL");
        node.setRight(bool());
        return node;
    }
    private TreeNode asgnlist(){
        if(check("something")) {//TODO
            return alist();
        }
        return null;
    }
    private TreeNode alist(){
        TreeNode node = new TreeNode(Node.NASGNS);
        node.setLeft(asgnstat());
        node.setRight(alisttail());
        return node;
    }
    private TreeNode alisttail(){
        if(check("TCOMA")){
            return alist();
        }
        return null;
    }
    private TreeNode ifstat(){
        TreeNode node = new TreeNode(Node.NIFTH);
        check("TIFKW");
        check("TLPAR");
        node.setLeft(bool());
        check("TRPAR");
        node.setMiddle(stats());
        node.setRight(elsestat());
        check("TENDK");
        return node;
    }
    private TreeNode elsestat(){
        if(check("TELSE")){
            return stats();
        }
        return null;
    }
    private TreeNode asgnstat(){
        TreeNode node = new TreeNode(Node.NASGN);
        node.setLeft(var());
        check("TASGN");
        node.setRight(bool());
        return node;
    }
    private TreeNode idasgnstat(){
        TreeNode node = new TreeNode(Node.NASGN);
        check("TASGN");
        node.setLeft(bool());
        return node;
    }
    private TreeNode iostat(){
        TreeNode node = new TreeNode(Node.NINPUT);
        if(check("TINKW")) {
            check("TINPT");
            node.setLeft(vlist());

        }else if(check("TOUTP")){
            node = new TreeNode(Node.NOUTP);
            check("TASGN");
        }
        return node;
    }
    private TreeNode iostatmid(){
        //TODO
        //Special	<iostatmid>	::=	<prlist> <iostatpr>
        //NOUTL 	<iostatmid>	::=	Line
        return null;
    }
    private TreeNode iostatpr(){
        TreeNode node = new TreeNode(Node.NOUTL);
        if(check("TINPT") && check("TOUTL")){
            return node;
        }
        return null;
    }
    private TreeNode callstat(){
        TreeNode node = new TreeNode(Node.NCALL);
        check("TLPAR");
        node.setLeft(callstatelist());
        check("TRPAR");
        return node;
    }
    private TreeNode callstatelist(){
        if(check("something")){//TODO
            return elist();
        }
        return null;
    }
    private TreeNode returnstat(){
        TreeNode node = new TreeNode(Node.NRETN);
        check("TRETN");
        node.setLeft(returnstattail());
        return node;
    }
    private TreeNode returnstattail(){
        if(check("something")){//TODO
            return expr();
        }
        return null;
    }
    private TreeNode vlist(){
        TreeNode node = new TreeNode(Node.NVLIST);
        node.setLeft(var());
        node.setRight(vlisttail());
        return node;
    }
    private TreeNode vlisttail(){
        if(check("TCOMA")){
            return vlist();
        }
        return null;
    }
    private TreeNode var(){
        TreeNode node = new TreeNode(Node.NSIMV);
        check("TIDNT"); //TODO idnt stuff
        node.setLeft(vararr());
        return node;
    }
    private TreeNode vararr(){
        if(!check("TLBRK")){
            return null;
        }
        TreeNode node = new TreeNode(Node.NAELT);
        node.setLeft(expr());
        check("TRBRK");
        node.setRight(vararrvar());
        return node;
    }
    private TreeNode vararrvar(){
        TreeNode node = new TreeNode(Node.NARRV);
        if(check("TCOMA")){
            //todo some id shit, idk.
            return node;
        }
        return null;
    }
    private TreeNode elist(){
        TreeNode node = new TreeNode(Node.NEXPL);
        node.setLeft(bool());
        node.setRight(elisttail());
        return node;
    }
    private TreeNode elisttail(){
        if(check("TCOMA")){
            return elist();
        }
        return null;
    }
    private TreeNode bool(){
        //Special	<bool>	::=	<rel><booltail>
    }
    private TreeNode booltail(){}
    private TreeNode rel(){} //TODO: fix NNOT
    private TreeNode reltail(){}
    private TreeNode logop(){
        TreeNode node = null;
        if(check("TANDK")){
            node = new TreeNode(Node.NAND);
        }else if(check("TORKW")){
            node = new TreeNode(Node.NOR);
        }else if(check("TXORK")){
            node = new TreeNode(Node.NXOR);
        }
        return node;
    }
    private TreeNode relop(){
        TreeNode node = null;
        if(check("TDEQL")){
            node = new TreeNode(Node.NEQL);
        }else if(check("TNEQL")){
            node = new TreeNode(Node.NNEQ);
        }else if(check("TGRTR")){
            node = new TreeNode(Node.NGRT);
        }else if(check("TLEQL")){
            node = new TreeNode(Node.NLEQ);
        }else if(check("TLESS")){
            node = new TreeNode(Node.NLSS);
        }else if(check("TGREQ")){
            node = new TreeNode(Node.NGEQ);
        }
        return node;
    }
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
    private TreeNode prlist(){
        TreeNode node = new TreeNode(Node.NPRLST);
        node.setLeft(printitem());
        node.setRight(prlisttail());
        return node;
    }
    private TreeNode prlisttail(){
        if(check("TCOMA")){
            return null;
        }
        return prlist();

    }
    private TreeNode printitem(){}
}
