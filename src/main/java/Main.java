import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * @author Serhii Stets
 * @version 1.0
 * @project movie-parser
 * @date 01.03.2017
 */

class output{
    static void instructions(){
        // Instruction block
        System.out.println("\nINSTRUCTION\n");
        System.out.println("\tRotten Tomatoes:");
        System.out.println(ansi().render("\t\t@|green 96%|@ - (Fresh) The Tomatometer is @|green 60%|@ or higher"));
        System.out.println(ansi().render("\t\t@|red 33%|@ - (Rotten) The Tomatometer is @|red 59%|@ or lower"));
        System.out.println(ansi().render("\t\t@|cyan ???|@ - No Score Yet\n"));
        System.out.println("\tMetacritic:");
        System.out.println(ansi().render("\t\t@|green 85|@ - (@|green 61|@ - @|green 100|@) Universal Acclaim / Generally Favorable Reviews"));
        System.out.println(ansi().render("\t\t@|yellow 53|@ - (@|yellow 40|@ - @|yellow 60|@) Mixed or Average Reviews"));
        System.out.println(ansi().render("\t\t@|red 27|@ - (@|red 0|@ - @|red 39|@) Generally Unfavorable Reviews / Overwhelming Dislike"));
        System.out.println(ansi().render("\t\t@|cyan ??|@ - No Score Yet"));
    }


    static void movie_opening_this_week(ArrayList<String> movies_rotten , ArrayList<String> movies_metacritic, ArrayList<String> scores_rotten, ArrayList<String> scores_metacritic, int max){
        // Show movies opening this week section
        System.out.println("\nMOVIES OPENING THIS WEEK\n");
        System.out.printf("%1s %"+ (max % 2 == 0?((max + 2) /2 + 2 ):((max + 2) /2 + 3 )) + "s %" + ((max / 2) -2) + "s%n", "| Rotten Tomatoes | Metacritic |", "Movies", "|");
        System.out.printf("%1s %17s %12s %" + (max + 2) + "s%n", "|", "|", "|", "|");
        for (int i = 0; i < 5; i++) {
            for (int g = 0; g < movies_metacritic.size(); g++){
                if (Objects.equals(movies_metacritic.get(g), movies_rotten.get(i))){
                    System.out.printf("%1s %9s %7s %" + (scores_metacritic.get(g).equals("100")?"5":"6") + "s %5s %1s %"+ (max - movies_rotten.get(i).length() + 1) + "s%n", "|",
                            ansi().render("      @|" + (scores_rotten.get(i).contains("?")? "cyan ": (Integer.parseInt(scores_rotten.get(i).substring(0,
                                    scores_rotten.get(i).length() - 1)) >= 60 || scores_rotten.get(i).contains("100")? "green ":"red ")) + (scores_rotten.get(i).length() <= 2? " " + scores_rotten.get(i):scores_rotten.get(i)) + "|@"),
                            "|",
                            ansi().render("    @|" + (scores_metacritic.get(g).contains("?")? "cyan ": Integer.parseInt(scores_metacritic.get(g).substring(0, scores_metacritic.get(g).length())) > 60? "green ":Integer.parseInt(scores_metacritic.get(g).substring(0,
                                    scores_metacritic.get(g).length())) >= 40? "yellow ":"red ") + (scores_metacritic.get(g).length() <= 1? " " + scores_metacritic.get(g):scores_metacritic.get(g)) + "|@"),
                            "|", movies_rotten.get(i), "|");
                    break;
                } else if (g == movies_metacritic.size() - 1 ){
                    System.out.printf("%1s %9s %7s %6s %5s %1s %"+ (max - movies_rotten.get(i).length() + 1) + "s%n", "|",
                            ansi().render("      @|" + (scores_rotten.get(i).contains("?")? "cyan ": (Integer.parseInt(scores_rotten.get(i).substring(0,
                                    scores_rotten.get(i).length() - 1)) >= 60 || scores_rotten.get(i).contains("100")? "green ":"red ")) + (scores_rotten.get(i).length() <= 2? " " + scores_rotten.get(i):scores_rotten.get(i)) + "|@"),
                            "|", ansi().render("    @|cyan ??|@"), "|", movies_rotten.get(i), "|");
                }
            }
        }
    }

    static void top_box_office(ArrayList<String> movies_rotten , ArrayList<String> movies_metacritic, ArrayList<String> scores_rotten, ArrayList<String> scores_metacritic, ArrayList<String> box_office, int max_1, int max_2){
        // Show top box office section
        System.out.println("\nTOP BOX OFFICE\n");
        System.out.printf("%1s %" + max_1 + "s %1s %" + (max_2 % 2 == 0?((max_2 + 2) /2 + 2 ):((max_2 + 2) /2 + 3 )) + "s %" + ((max_2 / 2) -2) + "s%n", "| Rotten Tomatoes | Metacritic |", "$", "|", "Movies", "|");
        System.out.printf("%1s %17s %12s %" + (max_1 + 2) + "s %" + (max_2 + 2) + "s%n", "|", "|", "|", "|", "|");
        for (int i = 5; i < 15; i++) {
            for (int g = 0; g < movies_metacritic.size(); g++){
                if (Objects.equals(movies_metacritic.get(g), movies_rotten.get(i))){
                    System.out.printf("%1s %9s %7s %" + (scores_metacritic.get(g).equals("100")?"5":"6") + "s %5s %" + max_1 + "s %1s %1s %"+ (max_2 - movies_rotten.get(i).length() + 1) + "s%n", "|",
                            ansi().render("      @|" + (scores_rotten.get(i).contains("?")? "cyan ": (Integer.parseInt(scores_rotten.get(i).substring(0,
                                    scores_rotten.get(i).length() - 1)) >= 60 || scores_rotten.get(i).contains("100")? "green ":"red ")) + (scores_rotten.get(i).length() <= 2? " " + scores_rotten.get(i):scores_rotten.get(i)) + "|@"),
                            "|",
                            ansi().render("    @|" + (scores_metacritic.get(g).contains("?")? "cyan ": Integer.parseInt(scores_metacritic.get(g).substring(0, scores_metacritic.get(g).length())) > 60? "green ":Integer.parseInt(scores_metacritic.get(g).substring(0,
                                    scores_metacritic.get(g).length())) >= 40? "yellow ":"red ") + (scores_metacritic.get(g).length() <= 1? " " + scores_metacritic.get(g):scores_metacritic.get(g)) + "|@"),
                            "|", box_office.get(i - 5), "|", movies_rotten.get(i), "|");
                    break;
                } else if (g == movies_metacritic.size() - 1){
                    System.out.printf("%1s %9s %7s %6s %5s %" + max_1 + "s %1s %1s %"+ (max_2 - movies_rotten.get(i).length() + 1) + "s%n", "|",
                            ansi().render("      @|" + (scores_rotten.get(i).contains("?")? "cyan ": (Integer.parseInt(scores_rotten.get(i).substring(0,
                                    scores_rotten.get(i).length() - 1)) >= 60 || scores_rotten.get(i).contains("100")? "green ":"red ")) + (scores_rotten.get(i).length() <= 2? " " + scores_rotten.get(i):scores_rotten.get(i)) + "|@"),
                            "|", ansi().render("    @|cyan ??|@"), "|", box_office.get(i - 5), "|", movies_rotten.get(i), "|");
                }
            }
        }
    }

    static void coming_soon(ArrayList<String> movies_rotten , ArrayList<String> movies_metacritic, ArrayList<String> scores_rotten, ArrayList<String> scores_metacritic, int max){
        // Show coming soon to theaters section
        System.out.println("\nCOMING SOON TO THEATERS\n");
        System.out.printf("%1s %"+ (max % 2 == 0?((max + 2) /2 + 2 ):((max + 2) /2 + 3 )) + "s %" + ((max / 2) -2) + "s%n", "| Rotten Tomatoes | Metacritic |", "Movies", "|");
        System.out.printf("%1s %17s %12s %" + (max + 2) + "s%n", "|", "|", "|", "|");
        for (int i = 15; i < scores_rotten.size(); i++) {
            for (int g = 0; g < movies_metacritic.size(); g++){
                if (Objects.equals(movies_metacritic.get(g), movies_rotten.get(i))){
                    System.out.printf("%1s %9s %7s %" + (scores_metacritic.get(g).equals("100")?"5":"6") + "s %5s %1s %"+ (max - movies_rotten.get(i).length() + 1) + "s%n", "|",
                            ansi().render("      @|" + (scores_rotten.get(i).contains("?")? "cyan ": (Integer.parseInt(scores_rotten.get(i).substring(0,
                                    scores_rotten.get(i).length() - 1)) >= 60 || scores_rotten.get(i).contains("100")? "green ":"red ")) + (scores_rotten.get(i).length() <= 2? " " + scores_rotten.get(i):scores_rotten.get(i)) + "|@"),
                            "|",
                            ansi().render("    @|" + (scores_metacritic.get(g).contains("?")? "cyan ": Integer.parseInt(scores_metacritic.get(g).substring(0, scores_metacritic.get(g).length())) > 60? "green ":Integer.parseInt(scores_metacritic.get(g).substring(0,
                                    scores_metacritic.get(g).length())) >= 40? "yellow ":"red ") + (scores_metacritic.get(g).length() <= 1? " " + scores_metacritic.get(g):scores_metacritic.get(g)) + "|@"),
                            "|", movies_rotten.get(i), "|");
                    break;
                } else if (g == movies_metacritic.size() - 1){
                    System.out.printf("%1s %1s %7s %6s %5s %1s %"+ (max - movies_rotten.get(i).length() + 1) + "s%n", "|",
                            ansi().render("      @|" + (scores_rotten.get(i).contains("?")? "cyan ": (Integer.parseInt(scores_rotten.get(i).substring(0,
                                    scores_rotten.get(i).length() - 1)) >= 60 || scores_rotten.get(i).contains("100")? "green ":"red ")) + (scores_rotten.get(i).length() <= 2? " " + scores_rotten.get(i):scores_rotten.get(i)) + "|@"),
                            "|", ansi().render("    @|cyan ??|@"), "|", movies_rotten.get(i), "|");
                }
            }
        }
    }

}

public class Main {
    private static void parseFrom() throws Exception {
        ArrayList<String> scores = new ArrayList<>(); // All scores from Rotten Tomatoes
        ArrayList<String> movies = new ArrayList<>(); // Movie names from Rotten Tomatoes
        ArrayList<String> money = new ArrayList<>(); // Box office from Rotten Tomatoes
        ArrayList<String> scores_meta = new ArrayList<>(); //  All scores from Metacritic
        ArrayList<String> movies_meta = new ArrayList<>(); // Movie names from Metacritic
        int g = 1;
        int i = 0;
        // Variables for table width
        int max_1 = "Movies".length();
        int max_2_1 = "$".length();
        int max_2_2 = "Movies".length();
        int max_3 = "Movies".length();

        try {
            // MOVIES OPENING THIS WEEK
            Document doc = Jsoup.connect("https://www.rottentomatoes.com/").get(); // Connect to Rotten Tomatoes
            Element table = doc.select("table[id=Opening]").first(); // Get all information from table with id "Opening"
            Iterator<Element> opening = table.select("td").iterator(); // Get info from td

            ArrayList<String> cache = new ArrayList<>(); // Temporary array for all data
            while (opening.hasNext()) {
                cache.add(opening.next().text()); // While Iterator has next character we add text from our table
            }

            // Split array
            for (String aCache : cache) {
                // We have 5 movies, 5 scores and 5 dates at all, so we need to skip date elements
                if (g != 3 && g != 6 && g != 9 && g != 12 && g != 15) {
                    // Take scores
                                if (aCache.contains("%") || aCache.contains("No Score Yet")) {
                                    // If movie doesn't have score right now and if it does
                                    if (aCache.contains("No Score Yet")){
                                        scores.add("???");
                                    } else{
                                        if (Objects.equals(aCache, "100%")){
                                scores.add("100");
                            } else {
                                scores.add(aCache);
                            }
                        }
                    }
                    // Take movies
                    else {
                        movies.add(aCache);

                        // Find msx length movie for table width
                        if (aCache.length() > max_1){
                            max_1 = aCache.length();
                        }
                    }
                }
                g++;
            }

            // TOP BOX OFFICE
            table = doc.select("table[id=Top-Box-Office]").first(); // Get all information from table with id Top-Box-Office
            Iterator<Element> top_box_office = table.select("td").iterator(); // Get information from td
            cache = new ArrayList<>(); // Clear cache array
            // Add all info from table
            while (top_box_office.hasNext()) {
                cache.add(top_box_office.next().text());
            }
            // Do the same as in "MOVIES OPENING THIS WEEK" but now we have instead of date elements money elements which we collect
            for (String aCache : cache) {
                if (aCache.contains("%") || aCache.contains("No Score Yet")) {
                    if (aCache.contains("No Score Yet")){
                        scores.add("???");
                    } else {
                        if (Objects.equals(aCache, "100%")){
                            scores.add("100");
                        } else {
                            scores.add(aCache);
                        }
                    }
                } else if (aCache.contains("$")){
                    money.add(aCache);
                    // Find msx length number for table width
                    if (aCache.length() > max_2_1){
                        max_2_1 = aCache.length();
                    }
                } else{
                    movies.add(aCache);
                    // Find msx length movie for table width
                    if (aCache.length() > max_2_2){
                        max_2_2 = aCache.length();
                    }
                }

            }

            //COMING SOON TO THEATERS
            table = doc.select("table[id=Top-Coming-Soon]").first();
            Iterator<Element> coming_soon = table.select("td").iterator();
            cache = new ArrayList<>();
            while (coming_soon.hasNext()) {
                cache.add(coming_soon.next().text());
            }
            g = 1;
            for (String aCache : cache) {
                // Skip data elements
                if (g != 3 && g != 6 && g != 9 && g != 12 && g != 15) {
                    if (aCache.contains("%") || aCache.contains("No Score Yet")) {
                        if (aCache.contains("No Score Yet")){
                            scores.add("???");
                        } else{
                            if (Objects.equals(aCache, "100%")){
                                scores.add("100");
                            } else {
                                scores.add(aCache);
                            }
                        }
                    } else {
                        movies.add(aCache);
                        // Find msx length movie for table width
                        if (aCache.length() > max_3){
                            max_3 = aCache.length();
                        }
                    }
                }
                g++;
            }


            // I have some troubles with Metacritic website so i take metascore from imdb.com instead
            doc = Jsoup.connect("http://www.imdb.com/movies-in-theaters/?ref_=nv_mv_inth_1").timeout(0).get();
            Elements elements = doc.select(".overview-top"); // Take all elements in classes
            cache = new ArrayList<>();
            for (Element e : elements) {
                Elements elements1 = e.select("h4[itemprop=\"name\"]"); // Take movie name from itemprop
                cache.addAll(elements1.stream().map(e1 -> e1.text().replaceAll(" \\(\\d+\\)",
                        "").replace(" - [Limited]", "")).collect(Collectors.toList())); // Delete info in brackets and " - [Limited]"

                Elements element2 = e.select(".metascore"); // Take metascore
                cache.addAll(element2.stream().map(Element::text).collect(Collectors.toList())); // Add to our list
            }
            // My metascore element now looks like "Metascore: 43/100 (11 reviews)", we need to delete all after "/" and "Metascore: "
            while (i < cache.size()-1){
                // If next element have "Metascore" in it
                if (cache.get(i+1).contains("Metascore")){
                    movies_meta.add(cache.get(i));
                    scores_meta.add(cache.get(i+1).replace("Metascore: ", "").split("/")[0]);
                    i++;
                }
                // If movie don't have score
                else{
                    movies_meta.add(cache.get(i));
                    scores_meta.add("??");
                }
                i++;
            }


            output.movie_opening_this_week(movies, movies_meta, scores, scores_meta, max_1);
            output.top_box_office(movies, movies_meta, scores, scores_meta, money, max_2_1, max_2_2);
            output.coming_soon(movies, movies_meta, scores, scores_meta, max_3);

        }catch (IOException e) {
            System.out.println("Parse error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        output.instructions();

        try{
            parseFrom();
        }
        catch(Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
