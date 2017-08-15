import java.util.HashMap;

/**
 * Created by Brendan on 15/8/17.
 * inputs a string and determines what token literal is associated with it
 */
public class Tokenizer {

    HashMap<String, String> literals = new HashMap<>(100);

    public Tokenizer(){
        //insert all the tokens
        literals.put(".", "TDOTT");
    }

    public String getToken(String token){
        return "";
    }




}
