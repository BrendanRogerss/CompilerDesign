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
                "CONST","INTEGER","REAL","BOOLEAN","FOR","REPEAT","UNTIL","IF","ELSE","IN","OUT",
                "LINE","RETURN","NOT","AND","OR","XOR","TRUE","FALSE","[","]", "(",
                ")",";",",",":",".","<<",">>","==", "!=",">","<=",
                "<",">=","+","-","*","/","%","^"};

        //Creates a hashmap of all the keyword and delimiter tokens
        for (int i = 0; i < inputs.length; i++) {
            literals.put(inputs[i],tokens[i]);
        }
    }

    public String getToken(String token){
        String output = "";
        String keyword = literals.get(token.toUpperCase()); //check if the token was a keyword or delimiter
        if(keyword != null){
            output = keyword;

        }else if(token.substring(0,1).equals("\"")){ //check if string
            //string
            output = "TSTRG "+token;
        }else if(Character.isDigit(token.charAt(0))){
            //token is an int or float
            if(token.contains(".")){ //float
                if(token.substring(0,2).equals("00")){//needs 2 leading 0s to be undefined
                    output = "TUNDF " + token;
                }else {
                    output = "TFLIT " + token;
                }
            }else{ //int
                if(token.substring(0,1).equals("0") && token.length()>1){ //check leading 0
                    output = "TUNDF " + token;
                }else {
                    output = "TILIT " + token;
                }
            }

        }else if(Character.isAlphabetic(token.charAt(0))) {
            //variable
            output = "TIDNT " + token;

        }else{
            output = "TUNDF " + token; //undefined token
        }
        return output;
    }




}
