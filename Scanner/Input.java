import java.io.*;
import java.util.Arrays;

/**
 * Created by Brendan on 15/8/17.
 */
public class Input {

    private PushbackReader reader = null;
    public int row = 0;
    public int col = 0;

    public Input(String filename){

        try {
            File file = new File(filename);
            reader = new PushbackReader(new FileReader(file), 100);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get() throws IOException {
        String input = Character.toString((char)reader.read());
        if(input.equals("\n")){
            row++;
            col = 1;
        }
        col++;
        return input;
    }

    public String getWord() throws IOException {
        String output = "";
        String letter = get();
        while (Character.isAlphabetic(letter.charAt(0)) || Character.isDigit(letter.charAt(0))){
            output+=letter;
            letter = get();
        }
        reader.unread(letter.charAt(0));
        return output.toLowerCase();
    }
    public String getLine() throws IOException {
        String line = "";
        String letter = get();
        while(!letter.equals("\n")&&!letter.equals("\r")){
            line+=letter;
            letter=get();
        }
        return line;
    }

    public boolean eof() throws IOException {
        int eof = reader.read();
        reader.unread(eof);
        return eof==65535;
    }

    public void pushback(String s) throws IOException {
        reader.unread(s.charAt(0));
    }

}
