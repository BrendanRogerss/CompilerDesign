import java.util.ArrayList;

/**
 * Created by Brendan on 9/9/17.
 *
 */
public class Parser {

    private ArrayList<Token> tokens; //tokens scanned in from the parser
    TreeNode root; //link to the root node

    public Parser(ArrayList<Token> t) {
        tokens = t;
    }

    public void run() {
        root = program();
    }

    //checks if the passed the next token is the wanted token
    private boolean check(String tok, boolean throwError) {
        if (tokens.get(0).tok.equals(tok)) { //checks token
            tokens.remove(0); //removes if its equal
            return true;
        }
        if (throwError) { //if we need to throw an error
            System.out.println("Error on line "+tokens.get(0).line+": Unexpected token. Wanted: " + tok + " got: " +
                    tokens.get(0).tok); //print the error message
            statementRecovery(); //try to recover
        }
        return false;
    }

    //same as previous message but doesnt consume token if equal
    private boolean checkAndNotConsume(String tok, boolean throwError) {
        if (tokens.get(0).tok.equals(tok)) {
            return true;
        }
        if (throwError) {
            System.out.println("Error on line "+tokens.get(0).line+": Unexpected token. Wanted: " + tok + " got: " + tokens.get(0).tok);
            statementRecovery();
        }
        return false;
    }

    //tries to find the next statement in the program
    private void statementRecovery(){
        //Thread.dumpStack();
        //System.exit(0);
        while (!tokens.isEmpty()){
            if(tokens.get(0).tok.equals("TSEMI")){ //go to the end of the next statement
                tokens.remove(0);
                return;//move onto the next statement
            }
            tokens.remove(0);
        }
        System.exit(1); //if no other statement exists, end parsing.
    }

    private void setLexeme(TreeNode node){ //adds a lexeme to the node
        node.setName(new StRec(tokens.get(0).value,tokens.get(0).line));
        tokens.remove(0);
    }

    private void setType(TreeNode node){ //adds the next token as a type in the node
        node.setType(new StRec(tokens.get(0).value,tokens.get(0).line));
        tokens.remove(0);
    }

    private void setType(TreeNode node, String type){ //adds the string passed in as the type in the node
        node.setType(new StRec(type,tokens.get(0).line));
        //tokens.remove(0);
    }

    //the start of the parser tree
    private TreeNode program() {
        check("TCD", true); //check for TCD token
        check("TIDNT", true); //check for the identifier that follows
        TreeNode node = new TreeNode(Node.NPROG); //create a node
        node.setLeft(globals()); //set the globals
        node.setMiddle(funcs()); //set the functions
        node.setRight(mainbody()); //set the main body
        return node;
    }

    //parses the globals of the program
    private TreeNode globals() {
        TreeNode node = new TreeNode(Node.NGLOB);
        node.setLeft(consts()); //set the constants
        node.setMiddle(types()); //set the types
        node.setRight(arrays()); //set the arrays
        return node;
    }

    //check if there are any constants
    private TreeNode consts() {
        if (check("TCONS", false)) {
            return initlist();
        } else {
            return null;
        }
    }

    //initialise the consts
    private TreeNode initlist() {
        TreeNode node = new TreeNode(Node.NILIST);
        node.setLeft(init()); //set the constant to the left
        node.setRight(initlisttail()); //check if more need to be added
        return node;
    }

    private TreeNode initlisttail() {
        if (check("TCOMA", false)) {
            return initlist();
        }
        return null;
    }

    //adds the lexeme into the type into the node
    private TreeNode init() {
        TreeNode node = new TreeNode(Node.NINIT);
        checkAndNotConsume("TIDNT", true);
        setLexeme(node);
        check("TISKW", true);
        node.setLeft(expr());
        return node;
    }

    //deals with types
    private TreeNode types() {
        if (check("TTYPS", false)) {
            return typelist();//return the list of types
        }
        return null;
    }

    //deals with the arrays declarations
    private TreeNode arrays() {
        if(check("TARRS", false)){
            return arrdecls(); //return some declarations
        }
        return null;
    }

    //deals with the functions
    private TreeNode funcs() {
        if (checkAndNotConsume("TFUNC", false)) {
            TreeNode node = new TreeNode(Node.NFUNCS); //makes a new function node
            node.setLeft(func()); //set the main func stuff
            node.setRight(funcs()); //if there are multiple functions
            return node;
        }
        return null; //if there arent any (more) functions
    }

    //main chuck of the program
    private TreeNode mainbody() {
        check("TMAIN", true); //check for main
        TreeNode node = new TreeNode(Node.NMAIN); //make the node
        node.setLeft(slist()); //set some variables
        check("TBEGN", true); //check for BEGIN
        node.setRight(stats()); //parse the statements
        check("TENDK", true); //check for "END"
        check("TCD", true);//etc..
        check("TIDNT", true);
        return node;

    }

    //to parse the variable declarations
    private TreeNode slist() {
        TreeNode node = new TreeNode(Node.NSDLST); //make the node
        node.setLeft(sdecl()); //set a variable
        node.setRight(slisttrail()); //if we need more
        return node;
    }

    private TreeNode slisttrail() {
        if (check("TCOMA", false)) { //check if there are more
            return slist(); //adds em
        }
        return null;
    }

    //declare some types
    private TreeNode typelist() {
        TreeNode node = new TreeNode(Node.NTYPEL); //set the node
        node.setLeft(type()); //add the type
        node.setRight(typelisttail()); //if we need more
        return node;
    }

    private TreeNode typelisttail() {
        if (checkAndNotConsume("TIDNT", false)) { //check for more types
            return typelist(); //go back to typelist
        }
        return null;
    }

    //used in declaring types
    private TreeNode type() {
        TreeNode node = new TreeNode(Node.NATYPE); //make the node
        checkAndNotConsume("TIDNT", true); //get the identifier
        setLexeme(node); //set it in the node
        check("TISKW", true); //check IS keyword

        if (check("TARRY", false)) { //if were setting arrays
            check("TLBRK", true);
            node.setLeft(expr()); //size of the array
            check("TRBRK", true);//check keywords
            check("TOFKW", true);
            checkAndNotConsume("TIDNT", true); //type of the array
            setType(node); //add type into the node
        } else { //if were not setting arrays
            node.setValue(Node.NRTYPE); //change node name
            node.setLeft(fields()); //set the types in the struct
            check("TENDK", true); //end setting the types
        }
        return node;
    }

    //used to set the shit that goes into a struct
    private TreeNode fields() {
        TreeNode node = new TreeNode(Node.NFLIST); //make a node
        node.setLeft(sdecl()); //chuck one of them on the side
        node.setRight(fieldsTail()); //put more on the other side
        return node;
    }

    private TreeNode fieldsTail() {
        if (check("TCOMA", false)) { //if there are mode variables
            return fields(); //return them
        }
        return null;
    }

    //used to declare everything
    private TreeNode sdecl() {
        TreeNode node = new TreeNode(Node.NSDECL); //set the node
        checkAndNotConsume("TIDNT", true); //grab the identifier
        setLexeme(node);
        check("TCOLN", true); //check :
        if (checkAndNotConsume("TIDNT", false)) { //if there is another identifier
            node.setValue(Node.NARRD); //change value of node
            setType(node);
            return node;
        }
        setType(node, stype()); //set the type
        return node;
    }

    //used to declare all the arrays
    private TreeNode arrdecls() {
        TreeNode node = new TreeNode(Node.NALIST); //set the node
        node.setLeft(arrdecl());//set the first array
        node.setRight(arrdeclstail()); //if there are more
        return node;
    }

    private TreeNode arrdeclstail() {
        if (check("TCOMA", false)) { //check for more
            return arrdecls(); //return it
        }
        return null;
    }

    //declare an array
    private TreeNode arrdecl() {
        TreeNode node = new TreeNode(Node.NARRD);//make the node
        checkAndNotConsume("TIDNT", true); //check for an identifier
        setLexeme(node); //put it in the node
        check("TCOLN", true); //check :
        checkAndNotConsume("TIDNT", true); //check another identifier
        setType(node);//set the type
        return node;
    }

    //function declaration
    private TreeNode func() {
        TreeNode node = new TreeNode(Node.NFUND); //make the node
        check("TFUNC", true); //check key word
        checkAndNotConsume("TIDNT", true); //get name of function
        setLexeme(node);
        check("TLPAR", true); //more checks
        node.setLeft(plist()); //input the parameters
        check("TRPAR", true);//more checks
        check("TCOLN", true);
        setType(node,rtype()); //set the type
        node.setMiddle(locals()); //set the local variables
        check("TBEGN", true); //check begin
        node.setRight(stats()); //go over the statements
        check("TENDK", true); //end
        return node;//return the function
    }

    private String rtype() { //the return type
        if (check("TVOID", false)) { //check if its void
            return "VOID";
        }
        return stype(); //if not return the other types

    }

    private TreeNode plist() { //go over the parameter list
        if (checkAndNotConsume("TIDNT", false) || check("TCNST", false)) {
            return params();
        }
        return null;
    }

    private TreeNode params() { //getting ready to parser the parameters
        TreeNode node = new TreeNode(Node.NPLIST); //make the node
        node.setLeft(param()); //set the parameter
        node.setRight(paramstail());//set more if needed
        return node;
    }

    private TreeNode paramstail() { //for many, many parameters
        if (check("TCOMA", false)) {//check ,
            return params();///return more parameters
        }
        return null;
    }

    //set a parameter
    private TreeNode param() {
        if (check("TCNST", false)) { //check if its a constant
            TreeNode node = new TreeNode(Node.NARRC);//make node
            node.setLeft(arrdecl()); //set declaration
            return node;//retn
        }

        checkAndNotConsume("TIDNT", true);//check for an idnt
        TreeNode node = new TreeNode(Node.NUNDEF); //make a node, not sure what type it is
        setLexeme(node);//put idnt into node
        check("TCOLN", true); //check :
        if (checkAndNotConsume("TIDNT", false)) { //check idnt
            node.setValue(Node.NARRP); //set value now we know
            setType(node);//put it in the node
            return node; //return out
        }
        node.setValue(Node.NSIMP); //set node type if there isnt another idnt
        setType(node,stype()); //set the type
        return node;
    }

    private TreeNode locals() {//deal with local variables
        if (checkAndNotConsume("TIDNT", false)) {//check for an idnt
            return dlist(); //go to the deceleration list
        }
        return null;
    }

    //declaration list
    private TreeNode dlist() {
        TreeNode node = new TreeNode(Node.NDLIST); //make the node
        node.setLeft(sdecl()); //get some declarations
        node.setRight(dlisttail()); //get more if uts needed
        return node;
    }

    private TreeNode dlisttail() {
        if (check("TCOMA", false)) { // if there are more declarations
            return dlist();
        }
        return null;

    }

    private String stype() { //return the type name
        if(check("TINTG",false)){
            return "integer";
        }else if(check("TREAL",false)){
            return "real";
        }else if(check("TBOOL",false)){
            return "boolean";
        }
        return null;
    }

    //list of statements
    //this is where the error recovery starts
    private TreeNode stats() {
        TreeNode node = new TreeNode(Node.NSTATS);//make a node
        if(checkAndNotConsume("TFORK", false)) {//check for
            node.setLeft(forstat()); //set a for statement
            if (node.getLeft() == null) { //check if something went wrong
                return statstail(); //try again
            }
        }else if(checkAndNotConsume("TIFKW", false)){//check for if
            node.setLeft(ifstat()); //launch into if statements
            if (node.getLeft() == null) {//check if something went wrong
                return statstail();//try again
            }
        } else {
            node.setLeft(stat());//go into a statement
            if(node.getLeft()!=null){//check if everything went ok
                check("TSEMI", true);//consume the ;
            }else{
                return statstail();//if something went wrong, try again
            }
        }
        node.setRight(statstail());
        return node;
    }

    //check for more statements
    private TreeNode statstail() {
        if (checkAndNotConsume("TENDK", false) || checkAndNotConsume("TUNTL", false) ||
                checkAndNotConsume("TELSE", false)) {
            return null;
        }
        return stats();
    }

    //the main statement
    private TreeNode stat() {
        if (checkAndNotConsume("TREPT", false)) {//check for loops
            return repstat();
        } else if (checkAndNotConsume("TIDNT", false)) {//check for an idnt
            TreeNode node = new TreeNode(Node.NUNDEF);//make a node
            setLexeme(node);//put the idnt in it
            if (checkAndNotConsume("TLPAR", false)) {
                return callstat(node);//making a function call
            }
            return idasgnstat(vararr(node));//assigning call
        } else if (checkAndNotConsume("TINKW", false) || checkAndNotConsume("TOUTP", false)) { //check for ins and outs
            return iostat();
        } else if (checkAndNotConsume("TRETN", true)){//check for a return
            return returnstat();//return statements
        }
        return null;
    }

    private TreeNode forstat() {//go through a for statement
        TreeNode node = new TreeNode(Node.NFORL);//set node
        check("TFORK", true);//consume for node
        if(!check("TLPAR", true)){return null;} //consume left par
        node.setLeft(asgnlist());//set the iterator
        if(node.getLeft()==null){return null;}//error recovery
        if(!check("TSEMI", true)){return null;}//check ;
        node.setMiddle(bool()); //set the exit condition
        if(node.getMiddle()==null){return null;}//error checking
        if(!check("TRPAR", true)){return null;}//check )
        node.setRight(stats());//go through all the statements
        if(node.getRight()==null){return null;} //see if there was a problem in the stats
        if(!check("TENDK", true)){return null;}//end
        return node;//return
    }

    //loop statement
    private TreeNode repstat() {
        TreeNode node = new TreeNode(Node.NREPT); //make a node
        check("TREPT", true); //check repeat
        if(!check("TLPAR", true)){return null;} //check (
        node.setLeft(asgnlist()); //assign some shit
        if(!check("TRPAR", true)){return null;} //check )
        node.setMiddle(stats()); //set the statements
        if(node.getMiddle()==null){return null;} //check if something went wrong
        if(!check("TUNTL", true)){return null;}// check until
        node.setRight(bool());//end condition
        if(node.getRight()==null){return null;}//check if something else went wrong
        return node;//return
    }

    private TreeNode asgnlist() {//check if something needs to be assigned
        if (checkAndNotConsume("TIDNT", false)) {
            return alist();//assign it
        }
        return null;//dont assign it
    }

    private TreeNode alist() {//a list of of stuff to assign
        TreeNode node = new TreeNode(Node.NASGNS);//make a node
        node.setLeft(asgnstat());//start assigning
        if(node.getLeft()==null){return null;}//see if something was wrong
        node.setRight(alisttail());//set
        return node;
    }

    private TreeNode alisttail() {//if there is more shit to assign
        if (check("TCOMA", false)) {
            return alist();//assign it
        }
        return null; //dont
    }

    private TreeNode ifstat() { //if statement
        TreeNode node = new TreeNode(Node.NIFTH); //set the node
        check("TIFKW", true); //check stuff
        if(!check("TLPAR", true)){return null;}
        node.setLeft(bool());//set the check expression
        if(node.getLeft()==null){return null;}//check if something has gone wrong
        if(!check("TRPAR", true)){return null;}
        node.setMiddle(stats()); //set the statements
        if(node.getMiddle()==null){return null;}
        node.setRight(elsestat()); //set else if we need it
        if(!check("TENDK", true)){return null;}
        return node;
    }

    private TreeNode elsestat() {
        if (check("TELSE", false)) {//check if we need else
            return stats();
        }
        return null;
    }

    //assign statements
    private TreeNode asgnstat() {
        TreeNode node = new TreeNode(Node.NASGN);//make a node
        node.setLeft(var());//set the left variable
        if(node.getLeft()==null){return null;}
        if(!check("TASGN", true)){return null;}
        node.setRight(bool());
        if(node.getRight()==null){return null;}
        return node;
    }

    //assign a node that gets passed in
    private TreeNode idasgnstat(TreeNode varNode) {
        TreeNode node = new TreeNode(Node.NASGN);
        node.setLeft(varNode);
        if(node.getLeft()==null){return null;}
        if(!check("TASGN", true)){return null;}
        node.setRight(bool());
        if(node.getRight()==null){return null;}
        return node;
    }

    //input and output
    private TreeNode iostat() {
        if (check("TINKW", false)) { //check in
            TreeNode node = new TreeNode(Node.NINPUT);
            if(!check("TINPT", true)){return null;}
            node.setLeft(vlist()); //get list
            if(node.getLeft()==null) {return null;}
            return node;
        } else if (check("TOUTP", false)) { //check out
            TreeNode node = new TreeNode(Node.NOUTP);
            if(!check("TASGN", true)){return null;} //assign
            if (check("TOUTL", false)) {//out
                node.setValue(Node.NOUTL);
            } else {
                node.setMiddle(prlist());
                if(node.getMiddle()==null){return null;}
                if (check("TASGN", false) && check("TOUTL", false)) {
                    node.setValue(Node.NOUTL);
                }
            }
            return node;
        }
        return null;
    }

    //function call
    private TreeNode callstat(TreeNode callNode) {
        callNode.setValue(Node.NCALL);
        if(!check("TLPAR", true)){return null;}
        callNode.setLeft(callstatelist()); //arguments
        if(callNode.getLeft()==null){return null;}
        if(!check("TRPAR", true)){return null;}
        return callNode;
    }

    //check for arguments
    private TreeNode callstatelist() {
        if (checkAndNotConsume("TNOTK", false) || checkAndNotConsume("TIDNT", false) ||
                checkAndNotConsume("TTLIT", false) || checkAndNotConsume("TFLIT", false) ||
                checkAndNotConsume("TTRUE", false) || checkAndNotConsume("TFALS", false) ||
                checkAndNotConsume("TLPAR", false)) {

            return elist(); //return them
        }
        return null;//dont
    }

    //return statement
    private TreeNode returnstat() {
        TreeNode node = new TreeNode(Node.NRETN);
        if(!check("TRETN", true)){return null;}
        node.setLeft(returnstattail());
        return node;
    }

    private TreeNode returnstattail() {
        if (checkAndNotConsume("TIDNT", false) || checkAndNotConsume("TILIT", false) ||
                checkAndNotConsume("TFLIT", false) || checkAndNotConsume("TTRUE", false) ||
                checkAndNotConsume("TFALS", false) || checkAndNotConsume("TLPAR", false)) {
            return expr(); //check for experssions
        }
        return null;
    }

    //variable list
    private TreeNode vlist() {
        TreeNode node = new TreeNode(Node.NVLIST);
        node.setLeft(var());
        if(node.getRight()==null){return null;}
        node.setRight(vlisttail());
        return node;
    }

    //more variables
    private TreeNode vlisttail() {
        if (check("TCOMA", false)) {
            return vlist();
        }
        return null;
    }

    //variable
    private TreeNode var() {
        TreeNode node = new TreeNode(Node.NSIMV);
        if (checkAndNotConsume("TIDNT", false)) {
            setLexeme(node);
        }
        return vararr(node);
    }

    //array variable
    private TreeNode vararr(TreeNode varNode) {
        if (check("TLBRK", false)) {
            varNode.setValue(Node.NAELT);
            varNode.setLeft(expr());
            if(varNode.getLeft()==null){return null;}
            if(!check("TRBRK", true)){return null;}
            return vararrvar(varNode);
        }
        varNode.setValue(Node.NSIMV);
        return varNode;
    }

    //array type struct
    private TreeNode vararrvar(TreeNode vararrNode) {
        if (check("TDOTT", false)) {
            vararrNode.setValue(Node.NARRV);
            if (checkAndNotConsume("TIDNT", true)) {
                TreeNode node = new TreeNode(Node.NSIMV);
                setLexeme(node);
                vararrNode.setRight(node);
                if(vararrNode.getRight()==null){return null;}
            }
        }
        return vararrNode;
    }

    //
    private TreeNode elist() {
        TreeNode node = new TreeNode(Node.NEXPL);
        node.setLeft(bool());
        if(node.getLeft()==null){return null;}
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
            if(node==null){return null;}
            node.setRight(rel());
            if(node.getRight()==null){return null;}
            return booltail(node);
        }
        return relNode;
    }

    private TreeNode rel() {
        if (check("TNOTK", false)) {
            TreeNode node = new TreeNode(Node.NNOT);
            node.setMiddle(rel());
            if(node.getMiddle()==null){return null;}
            return node;
        }
        return reltail(expr());
    }

    private TreeNode reltail(TreeNode expr) {
        if (checkAndNotConsume("TDEQL", false) || checkAndNotConsume("TNEQL", false) || checkAndNotConsume("TGRTR", false) ||
                checkAndNotConsume("TLEQL", false) || checkAndNotConsume("TLESS", false) ||
                checkAndNotConsume("TGREQ", false)) {
            TreeNode node = relop(expr);
            if(node==null){return null;}
            node.setRight(expr());
            if(node.getRight()==null){return null;}
            return node;
        }
        return expr;
    }

    private TreeNode logop(TreeNode rel) {
        TreeNode node = null;
        if (check("TANDK", false)) {
            node = new TreeNode(Node.NAND);
        } else if (check("TORKW", false)) {
            node = new TreeNode(Node.NOR);
        } else if (check("TXORK", false)) {
            node = new TreeNode(Node.NXOR);
        }
        if(node==null){return null;}
        node.setLeft(rel);
        if(node.getLeft()==null){return null;}
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
        if (node == null) {return null;}
        node.setLeft(expr);
        if(node.getLeft()==null){return null;}
        return node;
    }

    private TreeNode expr() {
        return exprtail(term());
    }

    private TreeNode exprtail(TreeNode term) {
        if (check("TADDT", false)) {
            TreeNode node = new TreeNode(Node.NADD);
            node.setLeft(term);
            if(node.getLeft()==null){return null;}
            node.setRight(term());
            if(node.getRight()==null){return null;}
            return exprtail(node);
        } else if (check("TSUBT", false)) {
            TreeNode node = new TreeNode(Node.NSUB);
            node.setLeft(term);
            if(node.getLeft()==null){return null;}
            node.setRight(term());
            if(node.getRight()==null){return null;}
            return exprtail(node);
        } else {
            return term;
        }
    }

    private TreeNode term() {
        return termtail(fact());
    }

    private TreeNode termtail(TreeNode fact) {

        if (check("TMULT", false)) {
            TreeNode node = new TreeNode(Node.NMUL);
            node.setLeft(fact);
            if(node.getLeft()==null){return null;}
            node.setRight(term());
            if(node.getRight()==null){return null;}
            return termtail(node);
        } else if (check("TDIVT", false)) {
            TreeNode node = new TreeNode(Node.NDIV);
            node.setLeft(fact);
            if(node.getLeft()==null){return null;}
            node.setRight(term());
            if(node.getRight()==null){return null;}
            return termtail(node);
        } else if (check("TPERC", false)) {
            TreeNode node = new TreeNode(Node.NMOD);
            node.setLeft(fact);
            if(node.getLeft()==null){return null;}
            node.setRight(term());
            if(node.getRight()==null){return null;}
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
            if(node.getLeft()==null){return null;}
            node.setRight(exponent());
            if(node.getRight()==null){return null;}
            return facttail(node);
        }
        return exp;
    }

    private TreeNode exponent() {
        if (checkAndNotConsume("TIDNT", false)) {
            return idexp();
        } else if (checkAndNotConsume("TILIT", false)) {
            TreeNode node = new TreeNode(Node.NILIT);
            setLexeme(node);
            return node;
        }else if(checkAndNotConsume("TFLIT",false)){
            TreeNode node = new TreeNode(Node.NFLIT);
            setLexeme(node);
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
        }
        return null;
    }

    private TreeNode idexp() {
        TreeNode node = new TreeNode(Node.NUNDEF);
        if (checkAndNotConsume("TIDNT", false)) {
            setLexeme(node);
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
        if(!check("TLPAR", true)){return null;}
        idexp.setMiddle(fncalllist());
        if(!check("TRPAR", true)){return null;}

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
        if(node.getLeft()==null){return null;}
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
        if (checkAndNotConsume("TSTRG", false)) {
            TreeNode node = new TreeNode(Node.NSTRG);
            setLexeme(node);
            return node;
        }
        return expr();
    }
}