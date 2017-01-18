import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

import org.fusesource.jansi.AnsiConsole;
import org.jsoup.select.Elements;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

public class MovieParser {

    private static void parseFrom(String site) throws Exception {
        try {
            Document doc = Jsoup.connect(site).get();
            ArrayList<String> scores = new ArrayList<>();
            ArrayList<String> movies = new ArrayList<>();
            ArrayList<String> money = new ArrayList<>();
            int g = 1;
            int max_1 = "Movies".length();
            int max_2_1 = "$".length();
            int max_2_2 = "Movies".length();
            int max_3 = "Movies".length();

            if (site.contains("tomatoes")) {

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

            } else if (site.contains("imdb")){

                doc = Jsoup.connect(site).timeout(0).get();
                Elements elements = doc.select(".overview-top");
                ArrayList<String> cache = new ArrayList<>();
                ArrayList<String> scores_meta = new ArrayList<>();
                ArrayList<String> movies_meta = new ArrayList<>();
                for (Element e : elements) {
                    Elements elements1 = e.select("h4[itemprop=\"name\"]");
                    //System.out.println(q.text());
                    cache.addAll(elements1.stream().map(e1 -> e1.text().replaceAll(" \\(\\d+\\)", "").replace(" - [Limited]", "")).collect(Collectors.toList()));

                    Elements element2 = e.select(".metascore");
                    //System.out.println(s.text());
                    cache.addAll(element2.stream().map(Element::text).collect(Collectors.toList()));
                }

                int i = 0;
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
                System.out.println(movies_meta);
                System.out.println(scores_meta);
            }

            for (int i = 0; i < scores.size(); i++) {
                switch(i) {
                    case 0:
                        System.out.println(ansi().render("@|blue ???|@/@|blue ??|@ - No Score Yet\n"));
                        System.out.printf("%1s %"+ max_1 + "s %1s%n", "\nMOVIES OPENING THIS WEEK\n\n| Rotten Tomatoes | Metacritic |", "Movies", "|");
                        System.out.printf("%1s %17s %12s %"+ (max_1+2) + "s%n", "|", "|", "|", "|");
                        if (scores.get(i).contains("?")){
                            System.out.printf("%1s %1s %7s %"+ max_1 + "s %1s%n", "|",  ansi().fg(BLUE).a("      " + scores.get(i)).reset(), "|", movies.get(i), "|");
                        } else {
                            System.out.printf("%1s %1s %7s %"+ max_1 + "s %1s%n", "|", ansi().fg(Integer.parseInt(scores.get(i).substring(0, scores.get(i).length() - 1)) > 60 ? GREEN : RED).a("      " + scores.get(i)).reset(), "|", movies.get(i), "|");
                        }
                        break;
                    case 1:case 2:case 3:case 4:
                        if (scores.get(i).contains("?")){
                            System.out.printf("%1s %1s %7s %"+ max_1 + "s %1s%n", "|",  ansi().fg(BLUE).a("      " + scores.get(i)).reset(), "|", movies.get(i), "|");
                        } else {
                            System.out.printf("%1s %1s %7s %"+ max_1 + "s %1s%n", "|", ansi().fg(Integer.parseInt(scores.get(i).substring(0, scores.get(i).length() - 1)) > 60 ? GREEN : RED).a("      " + scores.get(i)).reset(), "|", movies.get(i), "|");
                        }
                        break;
                    case 5:
                        System.out.printf("%1s %"+ max_2_1 + "s %1s %" + max_2_2 + "s %1s%n", "\nTOP BOX OFFICE\n\n| Rotten Tomatoes |", "$", "|", "Movies", "|");
                        System.out.printf("%1s %17s %"+ (max_2_1+2) + "s %" + (max_2_2+2) + "s%n", "|", "|", "|", "|");
                        System.out.printf("%1s %1s %7s %" + max_2_1 + "s %1s %" + max_2_2 + "s %1s%n", "|",  ansi().fg(Integer.parseInt(scores.get(i).substring(0,scores.get(i).length()-1))>60?GREEN:RED).a("      " + scores.get(i)).reset(),  "|", money.get(i - 5), "|", movies.get(i), "|");
                        break;
                    case 6:case 7:case 8:case 9:case 10:case 11:case 12:case 13:case 14:
                        System.out.printf("%1s %1s %7s %" + max_2_1 + "s %1s %" + max_2_2 + "s %1s%n", "|",  ansi().fg(Integer.parseInt(scores.get(i).substring(0,scores.get(i).length()-1))>60?GREEN:RED).a("      " + scores.get(i)).reset(),  "|", money.get(i - 5), "|", movies.get(i), "|");
                        break;
                    case 15:
                        System.out.println(ansi().render("\nCOMING SOON TO THEATERS\n"));
                        System.out.printf("%1s %" + max_3 + "s %1s%n","| Rotten Tomatoes |", "Movies", "|");
                        System.out.printf("%1s %17s %" + (max_3+2) + "s%n", "|", "|", "|");
                        if (scores.get(i).contains("?")){
                            System.out.printf("%1s %1s %7s %" + max_3 + "s %1s%n", "|",  ansi().fg(BLUE).a("      " + scores.get(i)).reset(), "|", movies.get(i), "|");
                        } else {
                            System.out.printf("%1s %1s %7s %" + max_3 + "s %1s%n", "|", ansi().fg(Integer.parseInt(scores.get(i).substring(0,scores.get(i).length()-1))>60?GREEN:RED).a("      " + scores.get(i)).reset(), "|", movies.get(i), "|");
                        }
                        break;
                    default:
                        if (scores.get(i).contains("?")){
                            System.out.printf("%1s %1s %7s %" + max_3 + "s %1s%n", "|",  ansi().fg(BLUE).a("      " + scores.get(i)).reset(), "|", movies.get(i), "|");
                        } else {
                            System.out.printf("%1s %1s %7s %" + max_3 + "s %1s%n", "|", ansi().fg(Integer.parseInt(scores.get(i).substring(0,scores.get(i).length()-1))>60?GREEN:RED).a("      " + scores.get(i)).reset(), "|", movies.get(i), "|");
                        }
                        break;
                }
            }
        }catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
		AnsiConsole.systemUninstall();
        try{
            parseFrom("https://www.rottentomatoes.com/");
            parseFrom("http://www.imdb.com/movies-in-theaters/?ref_=nv_mv_inth_1");

        }
        catch(Exception e) {
            System.out.println("Error" + e.getMessage());
        }
    }
}
