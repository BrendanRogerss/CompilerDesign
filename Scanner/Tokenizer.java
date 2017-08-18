import java.util.HashMap;

/**
 * Created by Brendan on 15/8/17.
 * inputs a string and determines what token literal is associated with it
 */
public class Tokenizer {

    private HashMap<String, String> literals = new HashMap<>(100);
    private int couldBeFloat = 0;

    public Tokenizer(){
        //operators and delimiters
        String[] tokens = {"TCD  ","TCONS","TTYPS","TISKW","TMAIN","TBEGN","TENDK","TARRY","TOFKW", "TFUNC", "TVOID",
                "TCNST", "TINTG", "TREAL", "TBOOL", "TFORK", "TREPT", "TUNTL", "TIFKW", "TELSE", "TINKW", "TOUTP",
                "TOUTL", "TRETN", "TNOTK", "TANDK", "TORKW", "TXORK", "TTRUE", "TFALS","TLBRK", "TRBRK", "TLPAR",
                "TRPAR", "TSEMI", "TCOMA", "TCOLN", "TDOTT", "TASGN", "TINPT","TDEQL", "TNEQL", "TGRTR", "TLEQL",
                "TLESS", "TGREQ", "TADDT", "TSUBT", "TMULT", "TDIVT", "TPERC", "TCART"};
        String[] inputs = {"CD","CONSTANTS","TYPES","IS","MAIN","BEGIN","END","ARRAYS","OF","FUNC","VOID",
                "CONST","INTEGER","REAL","BOOLEAN","FOR","REPEAT","UNTIL","IF","ELSE","IN","OUT",
                "LINE","RETURN","NOT","AND","OR","XOR","TRUE","FALSE","[","]", "(",
                ")",";",",",":",".","<<",">>","==", "!=",">","<=",
                "<",">=","+","-","*","/","%","^"};

        //EOF, TILIT, TFLIT, TSTRG, TUNDF
        for (int i = 0; i < inputs.length; i++) {
            literals.put(inputs[i],tokens[i]);
        }
    }

    public String getToken(String token){
        //TODO: floats
        String output = "";
        String keyword = literals.get(token.toUpperCase());
        if(keyword != null){
            output = keyword;
            if(output.equals("TDOTT") && couldBeFloat == 1){
                couldBeFloat = 2;
            }else{
                couldBeFloat = 0;
            }
        }else if(token.substring(0,1).equals("\"")){ //check if string
            //string
            output = "TSTRG "+token;
            couldBeFloat = 0;
        }else if(Character.isDigit(token.charAt(0))){
            //int
            output = "TILIT " + token;
            if(couldBeFloat == 0){
                couldBeFloat = 1;
            }else if(couldBeFloat == 2){
                output = "TFLIT "+token; //found a float
                couldBeFloat = 0;
            }
        }else if(Character.isAlphabetic(token.charAt(0))) {
            //variable
            output = "TIDNT " + token;
            couldBeFloat = 0;

        }else{
            output = "TUNDF " + token;
            couldBeFloat = 0;
        }
        return output;
    }




}
