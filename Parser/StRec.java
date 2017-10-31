//
// COMP3290/6290 CD Compiler
//
// Symbol Table Record class - Contains all information reqd about a name.
//
// M.Hannaford
// 16-Jul-2005		as StRec for CD5
//
//	Modified:
//		06-Jun-2006	as StRec for CD6
//		10-Aug-2014	as StRec for CD14
//		01-Aug-2015	as StRec for CD15
//		23-Jul-2016	as StRec for CD16
//		20-Aug-2016	CD16 Semantics: Added attributes isDecl, typeName, declPlace (for funcs)
//		03-Sep-2016	added an id classifier: attribute idType
//
// P. Mahata
// Modified:
// 		26-May-2017 altered to CD compiler
//

import java.util.HashMap;

public class StRec {

    private String type;        // Token type giving rise to this StRecord
    private String name;        // name of id (lexeme for hash table lookup)
    private IdClass idType;        // How is this variable name originally declared?
    private int value;        // for an Integer Constant
    private double fvalue;        // for a floating point constant
    private int dline;        // line where declared
    private boolean decl;        // set when declared
    private StRec typeName;        // link between this identifier/literal and it's type

    private TreeNode declPlace;    // where the decl syntax sub-tree is for a func - for param type checks

    private HashMap<String, StRec> hashTable;// = new HashMap<>();    // Complete hash table for a list of fields in a particular struct
    // Only needed if we wish to allow field names to be re-used in structs

    private int base;    // base register number		// added for code generator
    private int offset;    // allocated offset		// added for code generator

    public StRec(String s) {
        this(s, 0, -1);
    }

    public StRec(String s, boolean self, StRec other) {    // Special constructor for global simple types
        this(s, 0, -1);
        decl = true;
        if (self) typeName = this;
        else typeName = other;
    }

    public StRec(String s, int ln) {
        this(s, 0, ln);
    }

    public StRec(String s, int v, int ln) {
        type = "TIDNT";
        name = s.substring(0);
        idType = IdClass.IUNDEF;
        if (Character.isDigit(name.charAt(0))) type = "TILIT";
        if (name.charAt(0) == '"') type = "TSTRG";
        value = v;
        fvalue = 0.0;
        dline = ln;
        decl = false;
        typeName = VOIDTYPE;    // Do we also use this for struct id within array type decl?
        declPlace = null;
        hashTable = null;

        base = -1;    // indicates yet to be decided
        offset = -1;    // indicates yet to be allocated
    }

    public StRec(String s, double v, int ln) {
        type = "TFLIT";
        name = s.substring(0);
        value = 0;
        fvalue = v;
        dline = ln;

        base = -1;    // indicates yet to be decided
        offset = -1;    // indicates yet to be allocated
    }

    public StRec(String s, boolean bl) {    // Special constructor for global simple types
        this(s, 0, -1);
        decl = true;
        typeName = BOOLTYPE;
        if (bl) value = 1;
    }


    public boolean isDecl() {
        return decl;
    }

    public StRec getTypeName() {
        return typeName;
    }

    public void declare(StRec ty, IdClass cl) {
        decl = true;
        typeName = ty;
        idType = cl;
    }

    public void declareIntConst(StRec ty, IdClass cl, int val) {
        decl = true;
        typeName = ty;
        idType = cl;
        value = val;
    }

    public void declareRealConst(StRec ty, IdClass cl, double val) {
        decl = true;
        typeName = ty;
        idType = cl;
        fvalue = val;
    }

    public IdClass getIdType() {
        return idType;
    }

    public void setIdType(IdClass cl) {
        idType = cl;
    }

    public TreeNode getDeclPlace() {
        return declPlace;
    }

    public void setDeclPlace(TreeNode tr) {
        declPlace = tr;
    }

    public HashMap<String, StRec> getHashTable() {
        return hashTable;
    }    // A separate symbol table for each struct.

    // Keep the complete hash table to re-establish context
    public void setHashTable(HashMap<String, StRec> st) {
        hashTable = st;
    }    //   since struct fields can be referenced anywhere.

    // Established at struct decl time, looked up as reqd.
    public String getName() {
        return name;
    }

    public int getBase() {
        return base;
    }

    public int getOffset() {
        return offset;
    }

    public int getIntValue() {
        return value;
    }

    public double getFloatValue() {
        return fvalue;
    }

    public void allocate(int br, int os) {
        base = br;
        offset = os;
    }

    public void setTypeName(StRec customType){
        typeName = customType;
    }

    public void setTypeName(String sType){
        //System.out.println(sType);
        switch(sType){
            case "integer" :
                typeName = INTTYPE;
                break;
            case "VOID" :
                typeName = VOIDTYPE;
                break;
            case "real" :
                typeName = REALTYPE;
                break;
            case "boolean":
                typeName = BOOLTYPE;
                break;
        }
    }

    public String toString() {
        if (type != "TFLIT")
            return name + " " + value + " " + dline;    // + " " + base + " " + offset;	// for code generator
        else
            return name + " " + fvalue + " " + dline;    // + " " + base + " " + offset;	// for code generator
    }

    //  Global static StRec's for type checking simple types and boolean values
    //  ------------------------------------------------------------------------

    public static final StRec VOIDTYPE = new StRec("void", true, null);      // All simple types point at these records
    public static final StRec INTTYPE = new StRec("intg", false, VOIDTYPE);  //  They are set up to try to stop null
    public static final StRec REALTYPE = new StRec("real", false, VOIDTYPE);  //  reference errors after a semantic error
    public static final StRec BOOLTYPE = new StRec("bool", false, VOIDTYPE);  //  has been found.

    public static final StRec TRUE = new StRec("bool", true);      // All simple types point at these records
    public static final StRec FALSE = new StRec("bool", false);  //  They are set up to try to stop null

}
