import java.util.HashMap;

/**
 * Created by Brendan on 15/8/17.
 * inputs a string and determines what token literal is associated with it
 */
public class Tokenizer {

    private HashMap<String, String> literals = new HashMap<>(100);

    public Tokenizer(){
        //operators and delimiters
        String[] tokens = {"TCD","TCONS","TTYPS","TISKW","TMAIN","TBEGN","TENDK","TARRY","TOFKW", "TFUNC", "TVOID",
                "TCNST", "TINTG", "TREAL", "TBOOL", "TFORK", "TREPT", "TUNTL", "TIFKW", "TELSE", "TINKW", "TOUTP",
                "TOUTL", "TRETN", "TNOTK", "TANDK", "TORKW", "TXORK", "TTRUE", "TFALS","TLBRK", "TRBRK", "TLPAR",
                "TRPAR", "TSEMI", "TCOMA", "TCOLN", "TDOTT", "TASGN", "TINPT","TDEQL", "TNEQL", "TGRTR", "TLEQL",
                "TLESS", "TGREQ", "TADDT", "TSUBT", "TMULT", "TDIVT", "TPERC", "TCART"};
        String[] inputs = {"CD","CONSTANTS","TYPES","IS","MAIN","BEGIN","END","ARRAYS","OF","FUNC","VOID",
                "CONST","INTEGER","REAL","BOOLEAN","FOR","trept","tuntil","IF","ELSE","IN","outp",
                "toutl","tretn","NOT","AND","OR","XOR","TRUE","FALSE","[","]", "(",
                ")",";",",",":",":",".","<<",">>","==", "!=",">","<=",
                "<",">=","+","-","*","/","%","^"};

        //EOF, TILIT, TFLIT, TSTRG, TUNDF
        for (int i = 0; i < tokens.length; i++) {
            literals.put(tokens[i],inputs[i]);
        }
    }

    public String getToken(String token){
        //TODO: floats
        String output = "";
        String keyword = literals.get(token.toUpperCase());
        if(keyword != null){
            output = keyword;
        }else if(keyword.substring(0,0).equals("\"")){ //check if string
            //string
            output = "TSTRG "+token;
        }else if(Character.isDigit(token.charAt(0))){
            //int
            output = "TILIT" + token;
        }else if(Character.isAlphabetic(token.charAt(0))){
            //variable
            output = "TIDNT" + token;
        }else{
            output = "TUNDF" + token;
        }

        return output;
    }




}
