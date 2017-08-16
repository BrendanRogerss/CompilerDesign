import java.io.CharArrayReader;
import java.io.IOException;

/**
 * Created by Brendan on 15/8/17.
 */
public class Scanner {

    Input r;
    String buffer;

    Tokenizer tokenizer = new Tokenizer();

    public Scanner(Input input){
        r = input;
    }

    public boolean eof() throws IOException {

        return r.eof();
    }

    public String charType(char input){
        String output = "";
        if(Character.isAlphabetic(input)){
            output = "z";
        }
        return output;
    }

    public String getToken() throws IOException {

        String character;

        while(true){
            character = r.get();
            char c = character.charAt(0);
            switch(charType(c)){
                case "z":
                    buffer=character+r.getWord();
                    return tokenizer.getToken(buffer);

                case "1":

                case "0":

                case "\"": //opened up a string
                    //consume until another "
                case"/": //could be start of a comment
                    //check for rest of comment /-- then consume rest of the line




            }

            break;
        }




        return buffer;
    }

}
