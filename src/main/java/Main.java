import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * @author Serhii Stets
 * @version 1.0
 * @project movie-parser
 * @date 01.03.2017
 */

class Data{
    // take last modified date of file
    private static String FileTime(FileTime fileTime) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy - hh:mm:ss");
        return dateFormat.format(fileTime.toMillis());
    }

    // take all elements of array into string with |
    private static String write_in_file(List<String> array){
        String msg = "";
        msg += array.get(0);
        for (int i = 1; i < array.size(); i++){
            msg += "|" + array.get(i);
        }
        msg += ";";
        return msg;
    }


    // Save data from last good session
    public static void save_last_session(List<String> list_movies, List<String> rotten_score, List<String> movies_to_compare, List<String> metascore, List<String> money_box, int m1, int m2, int m21, int m3){
        try {
            FileWriter writer = new FileWriter("last session.txt", false);
            writer.append(write_in_file(list_movies));
            writer.append(write_in_file(movies_to_compare));
            writer.append(write_in_file(rotten_score));
            writer.append(write_in_file(metascore));
            writer.append(write_in_file(money_box));
            writer.append(m1 + ";");
            writer.append(m2 + ";");
            writer.append(m21 + ";");
            writer.append(m3 + ";");

            writer.flush();

        }catch (IOException e){
            System.err.println("\nFile error: " + e.getMessage());
        }

    }

    // take data from file if there is an error or no internet connection
    public static void take_last_session(){
        Path path = Paths.get("last session.txt");
        FileTime fileTime;

        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for(String line: lines){
                if (line.contains("You have 0 updates")){
                    System.out.println(line);
                } else{
                    try {
                        fileTime = Files.getLastModifiedTime(path);
                        System.out.print("Your last update is : " + FileTime(fileTime) + "\n");


                    } catch (IOException e) {
                        System.err.println("Cannot get the last modified time - " + e);
                    }
                    List<String> cache = Arrays.asList(line.split(";"));
                    List<String> list_movies = Arrays.asList(cache.get(0).split("\\|"));
                    List<String> movies_to_compare = Arrays.asList(cache.get(1).split("\\|"));
                    List<String> rotten_score = Arrays.asList(cache.get(2).split("\\|"));
                    List<String> metascore = Arrays.asList(cache.get(3).split("\\|"));
                    List<String> money_box = Arrays.asList(cache.get(4).split("\\|"));
                    int m1 = Integer.parseInt(cache.get(5));
                    int m2 = Integer.parseInt(cache.get(6));
                    int m21 = Integer.parseInt(cache.get(7));
                    int m3 = Integer.parseInt(cache.get(8));

                    Output.movie_opening_this_week(list_movies, movies_to_compare, rotten_score,  metascore,  m1);
                    Output.top_box_office(list_movies, movies_to_compare, rotten_score, metascore, money_box, m2, m21);
                    Output.coming_soon(list_movies, movies_to_compare, rotten_score, metascore, m3);

                }
            }
        } catch (IOException e){
            System.err.println("\nFile error: " + e.getMessage());
        }
    }
}

class MovieParser {
    private static List<String> scores = new ArrayList<>(); // All scores from Rotten Tomatoes
    private static List<String> movies = new ArrayList<>(); // Movie names from Rotten Tomatoes
    private static List<String> money = new ArrayList<>(); // Box office from Rotten Tomatoes
    private static List<String> scores_meta = new ArrayList<>(); //  All scores from Metacritic
    private static List<String> movies_meta = new ArrayList<>(); // Movie names from Metacritic
    // Variables for table width
    private static int max_1 = "Movies".length();
    private static int max_2_1 = "$".length();
    private static int max_2_2 = "Movies".length();
    private static int max_3 = "Movies".length();


    private static void rottentomatoes(Document text){
        ArrayList<String> cache = new ArrayList<>(); // Temporary array for all data

        // MOVIES OPENING THIS WEEK
        Element table = text.select("table[id=Opening]").first(); // Get all information from table with id "Opening"

        for (Element element : table.select("td")) {
            cache.add(element.text());
        }

        int i = 1;

        // Split array
        for (String aCache : cache) {
            // We have 5 movies, 5 scores and 5 dates at all, so we need to skip date elements
            if (i != 3 && i != 6 && i != 9 && i != 12 && i != 15) {
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
            i++;
        }

        // TOP BOX OFFICE
        table = text.select("table[id=Top-Box-Office]").first(); // Get all information from table with id Top-Box-Office
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
        table = text.select("table[id=Top-Coming-Soon]").first();
        Iterator<Element> coming_soon = table.select("td").iterator();
        cache = new ArrayList<>();
        while (coming_soon.hasNext()) {
            cache.add(coming_soon.next().text());
        }

        i = 1;
        for (String aCache : cache) {
            // Skip data elements
            if (i != 3 && i != 6 && i != 9 && i != 12 && i != 15) {
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
            i++;
        }
    }


    private static void metacritic(Document text) {
        Elements title_meta = text.select("div[class=title");
        movies_meta.addAll(title_meta.stream().map(Element::text).collect(Collectors.toList()));
        Elements score_meta = text.select("a[class=metascore_anchor]");
        for (Element score : score_meta) {
            String linkHref = score.attr("href");
            if (linkHref.contains("critic-reviews")) {
                if (score.text().contains("tbd")){
                    scores_meta.add("??");
                } else{
                    scores_meta.add(score.text());
                }
            }
        }
    }


    private static void table(){
        Output.movie_opening_this_week(movies, movies_meta, scores, scores_meta, max_1);
        Output.top_box_office(movies, movies_meta, scores, scores_meta, money, max_2_1, max_2_2);
        Output.coming_soon(movies, movies_meta, scores, scores_meta, max_3);
    }

    public static void parseFrom() throws Exception {
        try {
            Document doc = Jsoup.connect("https://www.rottentomatoes.com/").get(); // Connect to Rotten Tomatoes
            rottentomatoes(doc);

            doc = Jsoup.connect("http://www.metacritic.com/browse/movies/release-date/theaters/date?ttype[]=1").timeout(5000).userAgent("Mozilla").get();
            metacritic(doc);

            doc = Jsoup.connect("http://www.metacritic.com/browse/movies/release-date/coming-soon/date").timeout(5000).userAgent("Mozilla").get();
            metacritic(doc);

            table();
            Data.save_last_session(movies, scores , movies_meta, scores_meta, money, max_1, max_2_1, max_2_2, max_3);

        }catch (IOException e) {
            System.err.println("\nParse error: " + e.getMessage() + "\nCheck your internet connection\n");
            Data.take_last_session();
        }
    }
}

class Output {
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


    public static void movie_opening_this_week(List<String> movies_rotten , List<String> movies_metacritic, List<String> scores_rotten, List<String> scores_metacritic, int max){
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

    public static void top_box_office(List<String> movies_rotten , List<String> movies_metacritic, List<String> scores_rotten, List<String> scores_metacritic, List<String> box_office, int max_1, int max_2){
        // Show top box office section
        System.out.println("\nTOP BOX OFFICE\n");
        System.out.printf("%1s %" + (max_2 % 2 == 0?((max_2 + 2) /2 + 2 ):((max_2 + 2) /2 + 3 )) + "s %" + ((max_2 / 2) -2) + "s %" + max_1 + "s %1s%n", "| Rotten Tomatoes | Metacritic |", "Movies", "|", "$", "|");
        System.out.printf("%1s %17s %12s %" + (max_2 + 2) + "s %" + (max_1 + 2) + "s%n", "|", "|", "|", "|", "|");
        for (int i = 5; i < 15; i++) {
            for (int g = 0; g < movies_metacritic.size(); g++){
                if (Objects.equals(movies_metacritic.get(g), movies_rotten.get(i))){
                    System.out.printf("%1s %9s %7s %" + (scores_metacritic.get(g).equals("100")?"5":"6") + "s %5s %1s %"+ (max_2 - movies_rotten.get(i).length() + 1) + "s %" + max_1 + "s %1s%n", "|",
                            ansi().render("      @|" + (scores_rotten.get(i).contains("?")? "cyan ": (Integer.parseInt(scores_rotten.get(i).substring(0,
                                    scores_rotten.get(i).length() - 1)) >= 60 || scores_rotten.get(i).contains("100")? "green ":"red ")) + (scores_rotten.get(i).length() <= 2? " " + scores_rotten.get(i):scores_rotten.get(i)) + "|@"),
                            "|",
                            ansi().render("    @|" + (scores_metacritic.get(g).contains("?")? "cyan ": Integer.parseInt(scores_metacritic.get(g).substring(0, scores_metacritic.get(g).length())) > 60? "green ":Integer.parseInt(scores_metacritic.get(g).substring(0,
                                    scores_metacritic.get(g).length())) >= 40? "yellow ":"red ") + (scores_metacritic.get(g).length() <= 1? " " + scores_metacritic.get(g):scores_metacritic.get(g)) + "|@"),
                            "|", movies_rotten.get(i), "|", box_office.get(i - 5), "|");
                    break;
                } else if (g == movies_metacritic.size() - 1){
                    System.out.printf("%1s %9s %7s %6s %5s %1s %"+ (max_2 - movies_rotten.get(i).length() + 1) + "s %" + max_1 + "s %1s%n", "|",
                            ansi().render("      @|" + (scores_rotten.get(i).contains("?")? "cyan ": (Integer.parseInt(scores_rotten.get(i).substring(0,
                                    scores_rotten.get(i).length() - 1)) >= 60 || scores_rotten.get(i).contains("100")? "green ":"red ")) + (scores_rotten.get(i).length() <= 2? " " + scores_rotten.get(i):scores_rotten.get(i)) + "|@"),
                            "|", ansi().render("    @|cyan ??|@"), "|", movies_rotten.get(i), "|", box_office.get(i - 5), "|");
                }
            }
        }
    }

    public static void coming_soon(List<String> movies_rotten , List<String> movies_metacritic, List<String> scores_rotten, List<String> scores_metacritic, int max){
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
    public static void main(String[] args) {

        Output.instructions();

        try{
            MovieParser.parseFrom();
        }
        catch(Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
