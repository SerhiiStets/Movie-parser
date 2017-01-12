import java.net.*;
import java.io.*;

public class MovieParser {
    public static void main(String[] args) throws Exception {

        URL oracle = new URL("https://www.rottentomatoes.com/");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(oracle.openStream()));

        String inputLine;
        boolean flag = false;
        while ((inputLine = in.readLine()) != null)
            if (flag) {
                if (inputLine.contains("<script>")){
                    flag = false;

                }
                System.out.println(inputLine);
            }
            else if (inputLine.contains("<table id=\"Opening\" class=\"movie_list\">")){
                flag = true;

            }
        in.close();
    }
}