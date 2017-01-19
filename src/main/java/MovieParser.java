import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Collectors;

import org.fusesource.jansi.AnsiConsole;
import org.jsoup.select.Elements;

import static org.fusesource.jansi.Ansi.*;

public class MovieParser {

    private static void parseFrom() throws Exception {
        try {
            Document doc = Jsoup.connect("https://www.rottentomatoes.com/").get();
            ArrayList<String> scores = new ArrayList<>();
            ArrayList<String> movies = new ArrayList<>();
            ArrayList<String> money = new ArrayList<>();
            ArrayList<String> scores_meta = new ArrayList<>();
            ArrayList<String> movies_meta = new ArrayList<>();
            int g = 1;
            int i = 0;
            int max_1 = "Movies".length();
            int max_2_1 = "$".length();
            int max_2_2 = "Movies".length();
            int max_3 = "Movies".length();


            // MOVIES OPENING THIS WEEK
            Element table = doc.select("table[id=Opening]").first();
            Iterator<Element> opening = table.select("td").iterator();

            ArrayList<String> cache = new ArrayList<>();
            while (opening.hasNext()) {
                cache.add(opening.next().text());
            }

            for (String aCache : cache) {
                if (g != 3 && g != 6 && g != 9 && g != 12 && g != 15) {
                    if (aCache.contains("%") || aCache.contains("No Score Yet")) {
                        if (aCache.contains("No Score Yet")){
                            scores.add("???");
                        } else{
                            scores.add(aCache);
                        }
                    } else {
                        movies.add(aCache);
                        if (aCache.length() > max_1){
                            max_1 = aCache.length();
                        }
                    }
                }
                g++;
            }

            // TOP BOX OFFICE
            table = doc.select("table[id=Top-Box-Office]").first();
            Iterator<Element> top_box_office = table.select("td").iterator();
            cache = new ArrayList<>();
            while (top_box_office.hasNext()) {
                cache.add(top_box_office.next().text());
            }
            for (String aCache : cache) {
                if (aCache.contains("%") || aCache.contains("No Score Yet")) {
                    scores.add(aCache);
                } else if (aCache.contains("$")){
                    money.add(aCache);
                    if (aCache.length() > max_2_1){
                        max_2_1 = aCache.length();
                    }
                } else{
                    movies.add(aCache);
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
                if (g != 3 && g != 6 && g != 9 && g != 12 && g != 15) {
                    if (aCache.contains("%") || aCache.contains("No Score Yet")) {
                        if (aCache.contains("No Score Yet")){
                            scores.add("???");
                        } else{
                            scores.add(aCache);
                        }
                    } else {
                        movies.add(aCache);
                        if (aCache.length() > max_3){
                            max_3 = aCache.length();
                        }
                    }
                }
                g++;
            }

            doc = Jsoup.connect("http://www.imdb.com/movies-in-theaters/?ref_=nv_mv_inth_1").timeout(0).get();
            Elements elements = doc.select(".overview-top");
            cache = new ArrayList<>();
            for (Element e : elements) {
                Elements elements1 = e.select("h4[itemprop=\"name\"]");
                cache.addAll(elements1.stream().map(e1 -> e1.text().replaceAll(" \\(\\d+\\)", "").replace(" - [Limited]", "")).collect(Collectors.toList()));

                Elements element2 = e.select(".metascore");
                cache.addAll(element2.stream().map(Element::text).collect(Collectors.toList()));
            }

            while (i < cache.size()-1){
                if (cache.get(i+1).contains("Metascore")){
                    movies_meta.add(cache.get(i));
                    scores_meta.add(cache.get(i+1).replace("Metascore: ", "").split("/")[0]);
                    i++;
                } else{
                    movies_meta.add(cache.get(i));
                    scores_meta.add("??");
                }
                i++;
            }

            System.out.println(ansi().render("\n@|cyan ???|@/@|cyan ??|@ - No Score Yet\n"));
            System.out.println("\nMOVIES OPENING THIS WEEK\n");
            System.out.printf("%1s %"+ (max_1 % 2 == 0?((max_1 + 2) /2 + 2 ):((max_1 + 2) /2 + 3 )) + "s %" + ((max_1 / 2) -2) + "s%n", "| Rotten Tomatoes | Metacritic |", "Movies", "|");
            System.out.printf("%1s %17s %12s %" + (max_1 + 2) + "s%n", "|", "|", "|", "|");
            for (i = 0; i < 5; i++) {
                for (g = 0; g < movies_meta.size(); g++){
                    if (Objects.equals(movies_meta.get(g), movies.get(i))){
                        System.out.printf("%1s %9s %7s %6s %5s %1s %"+ (max_1 - movies.get(i).length() + 1) + "s%n", "|",
                                ansi().render("      @|" + (scores.get(i).contains("?")? "cyan ": (Integer.parseInt(scores.get(i).substring(0, scores.get(i).length() - 1)) > 60? "green ":"red ")) + scores.get(i) + "|@"),
                                "|",
                                ansi().render("    @|" + (scores_meta.get(g).contains("?")? "cyan ": Integer.parseInt(scores_meta.get(g).substring(0, scores_meta.get(g).length())) > 60? "green ":"red ") + scores_meta.get(g) + "|@"),
                                "|", movies.get(i), "|");
                        break;
                    } else if (g == movies_meta.size() - 1 ){
                        System.out.printf("%1s %9s %7s %6s %5s %1s %"+ (max_1 - movies.get(i).length() + 1) + "s%n", "|",
                                ansi().render("      @|" + (scores.get(i).contains("?")? "cyan ": (Integer.parseInt(scores.get(i).substring(0, scores.get(i).length() - 1)) > 60? "green ":"red ")) + scores.get(i) + "|@"),
                                "|", ansi().render("    @|cyan ??|@"), "|", movies.get(i), "|");
                    }
                }
            }

            System.out.println("\nTOP BOX OFFICE\n");
            System.out.printf("%1s %" + max_2_1 + "s %1s %" + (max_2_2 % 2 == 0?((max_2_2 + 2) /2 + 2 ):((max_2_2 + 2) /2 + 3 )) + "s %" + ((max_2_2 / 2) -2) + "s%n", "| Rotten Tomatoes | Metacritic |", "$", "|", "Movies", "|");
            System.out.printf("%1s %17s %12s %" + (max_2_1 + 2) + "s %" + (max_2_2 + 2) + "s%n", "|", "|", "|", "|", "|");
            for (i = 5; i < 15; i++) {
                for (g = 0; g < movies_meta.size(); g++){
                    if (Objects.equals(movies_meta.get(g), movies.get(i))){
                        System.out.printf("%1s %9s %7s %6s %5s %" + max_2_1 + "s %1s %1s %"+ (max_2_2 - movies.get(i).length() + 1) + "s%n", "|",
                                ansi().render("      @|" + (scores.get(i).contains("?")? "cyan ": (Integer.parseInt(scores.get(i).substring(0, scores.get(i).length() - 1)) > 60? "green ":"red ")) + scores.get(i) + "|@"),
                                "|",
                                ansi().render("    @|" + (scores_meta.get(g).contains("?")? "cyan ": Integer.parseInt(scores_meta.get(g).substring(0, scores_meta.get(g).length())) > 60? "green ":"red ") + scores_meta.get(g) + "|@"),
                                "|", money.get(i - 5), "|", movies.get(i), "|");
                        break;
                    } else if (g == movies_meta.size() - 1){
                        System.out.printf("%1s %9s %7s %6s %5s %" + max_2_1 + "s %1s %1s %"+ (max_2_2 - movies.get(i).length() + 1) + "s%n", "|",
                                ansi().render("      @|" + (scores.get(i).contains("?")? "cyan ": (Integer.parseInt(scores.get(i).substring(0, scores.get(i).length() - 1)) > 60? "green ":"red ")) + scores.get(i) + "|@"),
                                "|", ansi().render("    @|cyan ??|@"), "|", money.get(i - 5), "|", movies.get(i), "|");
                    }
                }
            }

            System.out.println("\nCOMING SOON TO THEATERS\n");
            System.out.printf("%1s %"+ (max_3 % 2 == 0?((max_3 + 2) /2 + 2 ):((max_3 + 2) /2 + 3 )) + "s %" + ((max_3 / 2) -2) + "s%n", "| Rotten Tomatoes | Metacritic |", "Movies", "|");
            System.out.printf("%1s %17s %12s %" + (max_3 + 2) + "s%n", "|", "|", "|", "|");
            for (i = 15; i < scores.size(); i++) {
                for (g = 0; g < movies_meta.size(); g++){
                    if (Objects.equals(movies_meta.get(g), movies.get(i))){
                        System.out.printf("%1s %9s %7s %6s %5s %1s %"+ (max_3 - movies.get(i).length() + 1) + "s%n", "|",
                                ansi().render("@|" + (scores.get(i).contains("?")? "cyan ": (Integer.parseInt(scores.get(i).substring(0, scores.get(i).length() - 1)) > 60? "green":"red")) + scores.get(i) + "|@"),
                                "|",
                                ansi().render("    @|" + (scores_meta.get(g).contains("?")? "cyan ": Integer.parseInt(scores_meta.get(g).substring(0, scores_meta.get(g).length())) > 60? "green ":"red ") + scores_meta.get(g) + "|@"),
                                "|", movies.get(i), "|");
                        break;
                    } else if (g == movies_meta.size() - 1){
                        System.out.printf("%1s %1s %7s %6s %5s %1s %"+ (max_3 - movies.get(i).length() + 1) + "s%n", "|",
                                ansi().render("      @|" + (scores.get(i).contains("?")? "cyan ": (Integer.parseInt(scores.get(i).substring(0, scores.get(i).length() - 1)) > 60? "green ":"red ")) + scores.get(i) + "|@"),
                                "|", ansi().render("    @|cyan ??|@"), "|", movies.get(i), "|");
                    }
                }
            }

        }catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
		AnsiConsole.systemUninstall();
        try{
            parseFrom();

        }
        catch(Exception e) {
            System.out.println("Error" + e.getMessage());
        }
    }
}
