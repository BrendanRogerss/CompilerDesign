import java.io.*;
import java.util.Arrays;

/**
 * Created by Brendan on 15/8/17.
 */
public class Input {

    private PushbackReader reader = null;

    public Input(String filename){

        try {
            File file = new File(filename);
            reader = new PushbackReader(new FileReader(file));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get() throws IOException {
        return Character.toString((char) reader.read()).toLowerCase();
    }

    public String getWord() throws IOException {
        String[] accept = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
        "1","2","3","4","5","6","7","8","9","0"};
        String output = "";
        String letter = get();
        while (Arrays.asList(accept).contains(letter)){
            output+=letter;
        }
        reader.unread(letter.charAt(0));
        return output.toLowerCase();
    }
//    public String getLine() throws IOException {
//        return reader.readLine().toLowerCase();
//    }

    public boolean eof() throws IOException {
        return reader.ready();
    }

}
