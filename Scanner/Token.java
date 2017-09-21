/**
 * Created by Brendan on 17/9/17.
 */
public class Token {
    String tok;
    String value;
    int line;

    public Token(String t, int l){
        tok = t;
        line = l;
    }

    public Token(String t, String v, int l){
        tok = t;
        value = v;
        line = l;
    }


}
