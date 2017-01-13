import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class MovieParser {

    public static void parseFrom(String site) throws Exception {
        URL oracle = new URL(site);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(oracle.openStream()));

        String inputLine;
        boolean flag = false;
        boolean movie_name_flag = false;
        ArrayList<String> scores = new ArrayList<>();
        ArrayList<String> movies = new ArrayList<>();

        if (site.contains("tomatoes")) {
            while ((inputLine = in.readLine()) != null)
                if (flag) {
                    if (inputLine.contains("<script>")) {
                        flag = false;

                    } else if (inputLine.contains("<span class=\"tMeterScore\">") || inputLine.contains("No Score Yet")){
                        scores.add(inputLine);
                    } else if (movie_name_flag){
                        if (inputLine.contains("</td>")){
                            movie_name_flag = false;
                        } else{
                            movies.add(inputLine);
                        }
                    } else if (inputLine.contains("<td class=\"middle_col\">")) {
                        movie_name_flag = true;
                    }
                } else if (inputLine.contains("<table id=\"Opening\" class=\"movie_list\">")) {
                    flag = true;

                }
        } else {
            System.out.println("adasdsa");

        }


        for (int i = 0; i < scores.size(); i++){
            scores.set( i, scores.get(i).replaceAll("\\s+",""));
            scores.set( i, scores.get(i).replaceAll("<spanclass=\"tMeterScore\">",""));
            scores.set( i, scores.get(i).replaceAll("<spanclass=\"tMeterIcontinynoRating\">",""));
            scores.set( i, scores.get(i).replaceAll("</span>",""));
            movies.set( i, movies.get(i).replaceAll("\\s+",""));
            movies.set( i, movies.get(i).replaceAll("</a>",""));
            for (String movie_name_split : movies.get(i).split(">", 2)) {
                if (movie_name_split.contains("<")){
                    movies.set( i, movies.get(i).replaceAll(movie_name_split,""));
                    movies.set( i, movies.get(i).replaceAll(">",""));
                }
            }
        }
        for (int i = 0; i < scores.size(); i++){

            System.out.printf("%1s  %-7s   %20s%n", i + 1,  movies.get(i), scores.get(i));
        }
        in.close();
    }

    public static void table(){

    }

    public static void main(String[] args) {
        try{
            //table();
            parseFrom("https://www.rottentomatoes.com/");
            //parseFrom("asdsad");

        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }

    }
}

