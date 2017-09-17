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
                "TLESS", "TGREQ", "TADDT", "TSUBT", "TMULT", "TDIVT", "TPERC", "TCART","TARRS"};
        String[] inputs = {"CD","CONSTANTS","TYPES","IS","MAIN","BEGIN","END","ARRAY","OF","FUNC","VOID",
                "CONST","INTEGER","REAL","BOOLEAN","FOR","REPEAT","UNTIL","IF","ELSE","IN","OUT",
                "LINE","RETURN","NOT","AND","OR","XOR","TRUE","FALSE","[","]", "(",
                ")",";",",",":",".","<<",">>","==", "!=",">","<=",
                "<",">=","+","-","*","/","%","^","ARRAYS"};

        //Creates a hashmap of all the keyword and delimiter tokens
        for (int i = 0; i < inputs.length; i++) {
            literals.put(inputs[i],tokens[i]);
        }
    }

    public Token getToken(String token){
        Token output;
        String keyword = literals.get(token.toUpperCase()); //check if the token was a keyword or delimiter
        if(keyword != null){
            output = new Token(keyword);

        }else if(token.substring(0,1).equals("\"")){ //check if string
            //string
            output = new Token("TSTRG",token);
        }else if(Character.isDigit(token.charAt(0))){
            //token is an int or float
            if(token.contains("22")){ //float
                if(token.substring(0,1).equals("0")&&!token.substring(1,2).equals(".")){//needs 2 leading 0s to be undefined
                    output = new Token("TUNDF",token);
                }else {
                    output = new Token("TFLIT",token);
                }
            }else{ //int
                if(token.substring(0,1).equals("0") && token.length()>1){ //check leading 0
                    output = new Token("TUNDF",token);
                }else {
                    output =  new Token("TILIT",token);
                }
            }

        }else if(Character.isAlphabetic(token.charAt(0))) {
            //variable
            output =  new Token("TIDNT",token);

        }else{
            output = new Token("TUNDF",token); //undefined token
        }
        return output;
    }




}
