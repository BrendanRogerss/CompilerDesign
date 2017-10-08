import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Brendan on 11/9/17.
 *
 * Symbol Table Record
 *
 */
public class StRec {

    private String name; //name of id (lexeme for hash table lookup)
    private String type; //token type ie TIDNT
    private String idType; //how is this variable name originally declared
    private int value; //for integer constant
    private double fvalue; //for floating point constant
    private int dLine; //line declared
    private boolean decl; //set when declared
    private StRec typeName; //link between this identifier/literal and its type
    private TreeNode declPlace; //where the decl syntax sub-tree is for a func - for param checks
    //private HashTable hashTable = new HashTable(); //complete has table for a list of fields in a particlar struct
    private HashMap<String, StRec> hashTable;
    private int base; //base register number
    private int offset; //allocated offset


    public StRec(String s){
        this(s,0,-1);
    }

    public StRec(String s, int ln){
        this(s,0,ln);
    }

    public StRec(String s, boolean self, StRec other){
        //special constructor for global simple types
        this(s,0,-1);
        decl = true;
        if (self) typeName = this; else typeName = other;
    }

    public StRec(String s, int v, int ln){
        type = "TIDNT";
        name = s;
        idType = "NUNDEF";
        if(Character.isDigit(name.charAt(0))) type = "TILIT";
        if(name.charAt(0)=='"') type = "TSTRG";
        value = v;
        fvalue = 0.0;
        dLine = ln;
        decl = false;
        typeName = VOIDTYPE; // do we also use this for struct id within array type decl
        declPlace = null;
        hashTable = null;

        base = -1;
        offset = -1;
    }

    public String getName(){
        return name;
    }

    //Global static StRec's for type checking simple types and boolean values

    public static final StRec VOIDTYPE = new StRec("void",true,null);
    public static final StRec INTTYPE = new StRec("intg", false, VOIDTYPE);
    public static final StRec REALTYPE = new StRec("real",false, VOIDTYPE);
    public static final StRec BOOLTYPE = new StRec("bool", false, VOIDTYPE);
    public static final StRec TRUE = new StRec("bool", true);
    public static final StRec FALSE = new StRec("bool",false);


}
