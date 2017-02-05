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
 * @date 01.02.2017
 */

class output{
    public static void instructions(){
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
    public static void printTable(){
        instructions();
    }
}

public class Main {
    private static void parseFrom() throws Exception {
        try {
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

            // Show movies opening this week section
            System.out.println("\nMOVIES OPENING THIS WEEK\n");
            System.out.printf("%1s %"+ (max_1 % 2 == 0?((max_1 + 2) /2 + 2 ):((max_1 + 2) /2 + 3 )) + "s %" + ((max_1 / 2) -2) + "s%n", "| Rotten Tomatoes | Metacritic |", "Movies", "|");
            System.out.printf("%1s %17s %12s %" + (max_1 + 2) + "s%n", "|", "|", "|", "|");
            for (i = 0; i < 5; i++) {
                for (g = 0; g < movies_meta.size(); g++){
                    if (Objects.equals(movies_meta.get(g), movies.get(i))){
                        System.out.printf("%1s %9s %7s %6s %5s %1s %"+ (max_1 - movies.get(i).length() + 1) + "s%n", "|",
                                ansi().render("      @|" + (scores.get(i).contains("?")? "cyan ": (Integer.parseInt(scores.get(i).substring(0,
                                        scores.get(i).length() - 1)) >= 60 || scores.get(i).contains("100")? "green ":"red ")) + (scores.get(i).length() <= 2? " " + scores.get(i):scores.get(i)) + "|@"),
                                "|",
                                ansi().render("    @|" + (scores_meta.get(g).contains("?")? "cyan ": Integer.parseInt(scores_meta.get(g).substring(0, scores_meta.get(g).length())) > 60? "green ":Integer.parseInt(scores_meta.get(g).substring(0, scores_meta.get(g).length())) >= 40? "yellow ":"red ") + scores_meta.get(g) + "|@"),
                                "|", movies.get(i), "|");
                        break;
                    } else if (g == movies_meta.size() - 1 ){
                        System.out.printf("%1s %9s %7s %6s %5s %1s %"+ (max_1 - movies.get(i).length() + 1) + "s%n", "|",
                                ansi().render("      @|" + (scores.get(i).contains("?")? "cyan ": (Integer.parseInt(scores.get(i).substring(0,
                                        scores.get(i).length() - 1)) >= 60 || scores.get(i).contains("100")? "green ":"red ")) + (scores.get(i).length() <= 2? " " + scores.get(i):scores.get(i)) + "|@"),
                                "|", ansi().render("    @|cyan ??|@"), "|", movies.get(i), "|");
                    }
                }
            }

            // Show top box office section
            System.out.println("\nTOP BOX OFFICE\n");
            System.out.printf("%1s %" + max_2_1 + "s %1s %" + (max_2_2 % 2 == 0?((max_2_2 + 2) /2 + 2 ):((max_2_2 + 2) /2 + 3 )) + "s %" + ((max_2_2 / 2) -2) + "s%n", "| Rotten Tomatoes | Metacritic |", "$", "|", "Movies", "|");
            System.out.printf("%1s %17s %12s %" + (max_2_1 + 2) + "s %" + (max_2_2 + 2) + "s%n", "|", "|", "|", "|", "|");
            for (i = 5; i < 15; i++) {
                for (g = 0; g < movies_meta.size(); g++){
                    if (Objects.equals(movies_meta.get(g), movies.get(i))){
                        System.out.printf("%1s %9s %7s %6s %5s %" + max_2_1 + "s %1s %1s %"+ (max_2_2 - movies.get(i).length() + 1) + "s%n", "|",
                                ansi().render("      @|" + (scores.get(i).contains("?")? "cyan ": (Integer.parseInt(scores.get(i).substring(0,
                                        scores.get(i).length() - 1)) >= 60 ||scores.get(i).contains("100")? "green ":"red ")) + (scores.get(i).length() <= 2? " " + scores.get(i):scores.get(i)) + "|@"),
                                "|",
                                ansi().render("    @|" + (scores_meta.get(g).contains("?")? "cyan ": Integer.parseInt(scores_meta.get(g).substring(0, scores_meta.get(g).length())) > 60? "green ":Integer.parseInt(scores_meta.get(g).substring(0, scores_meta.get(g).length())) >= 40? "yellow ":"red ") + scores_meta.get(g) + "|@"),
                                "|", money.get(i - 5), "|", movies.get(i), "|");
                        break;
                    } else if (g == movies_meta.size() - 1){
                        System.out.printf("%1s %9s %7s %6s %5s %" + max_2_1 + "s %1s %1s %"+ (max_2_2 - movies.get(i).length() + 1) + "s%n", "|",
                                ansi().render("      @|" + (scores.get(i).contains("?")? "cyan ": (Integer.parseInt(scores.get(i).substring(0,
                                        scores.get(i).length() - 1)) >= 60 || scores.get(i).contains("100")? "green ":"red ")) + (scores.get(i).length() <= 2? " " + scores.get(i):scores.get(i)) + "|@"),
                                "|", ansi().render("    @|cyan ??|@"), "|", money.get(i - 5), "|", movies.get(i), "|");
                    }
                }
            }

            // Show coming soon to theaters section
            System.out.println("\nCOMING SOON TO THEATERS\n");
            System.out.printf("%1s %"+ (max_3 % 2 == 0?((max_3 + 2) /2 + 2 ):((max_3 + 2) /2 + 3 )) + "s %" + ((max_3 / 2) -2) + "s%n", "| Rotten Tomatoes | Metacritic |", "Movies", "|");
            System.out.printf("%1s %17s %12s %" + (max_3 + 2) + "s%n", "|", "|", "|", "|");
            for (i = 15; i < scores.size(); i++) {
                for (g = 0; g < movies_meta.size(); g++){
                    if (Objects.equals(movies_meta.get(g), movies.get(i))){
                        System.out.printf("%1s %9s %7s %6s %5s %1s %"+ (max_3 - movies.get(i).length() + 1) + "s%n", "|",
                                ansi().render("@|" + (scores.get(i).contains("?")? "cyan ": (Integer.parseInt(scores.get(i).substring(0,
                                        scores.get(i).length() - 1)) >= 60 || scores.get(i).contains("100")? "green":"red")) + (scores.get(i).length() <= 2? " " + scores.get(i):scores.get(i)) + "|@"),
                                "|",
                                ansi().render("    @|" + (scores_meta.get(g).contains("?")? "cyan ": Integer.parseInt(scores_meta.get(g).substring(0, scores_meta.get(g).length())) > 60? "green ":Integer.parseInt(scores_meta.get(g).substring(0, scores_meta.get(g).length())) >= 40? "yellow ":"red ") + scores_meta.get(g) + "|@"),
                                "|", movies.get(i), "|");
                        break;
                    } else if (g == movies_meta.size() - 1){
                        System.out.printf("%1s %1s %7s %6s %5s %1s %"+ (max_3 - movies.get(i).length() + 1) + "s%n", "|",
                                ansi().render("      @|" + (scores.get(i).contains("?")? "cyan ": (Integer.parseInt(scores.get(i).substring(0,
                                        scores.get(i).length() - 1)) >= 60 || scores.get(i).contains("100")? "green ":"red ")) + (scores.get(i).length() <= 2? " " + scores.get(i):scores.get(i)) + "|@"),
                                "|", ansi().render("    @|cyan ??|@"), "|", movies.get(i), "|");
                    }
                }
            }

        }catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        output.printTable();
        try{
            parseFrom();
        }
        catch(Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

