import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.fusesource.jansi.AnsiConsole;
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

                            scores.add(aCache);
                        } else {

                            movies.add(aCache);
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
                    } else{
                        movies.add(aCache);
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
                        }
                    }
                    g++;
                }

                for (int i = 0; i < scores.size(); i++) {
                    switch(i) {
                        case 0:
                            System.out.println("\nMOVIES OPENING THIS WEEK\n");
                            System.out.printf("%1s %1s %20s %1s%n", ansi().fg(Integer.parseInt(scores.get(i).substring(0,scores.get(i).length()-1))>60?GREEN:RED).a(scores.get(i)).reset(), "|", movies.get(i), "|");
                            break;
                        case 1:case 2:case 3:case 4:
                            System.out.printf("%1s %1s %20s %1s%n", ansi().fg(Integer.parseInt(scores.get(i).substring(0,scores.get(i).length()-1))>60?GREEN:RED).a(scores.get(i)).reset(), "|", movies.get(i), "|");
                            break;
                        case 5:
                             System.out.println("\nTOP BOX OFFICE\n");
                             System.out.printf("%1s %1s %20s %1s %40s %1s%n",  ansi().fg(Integer.parseInt(scores.get(i).substring(0,scores.get(i).length()-1))>60?GREEN:RED).a(scores.get(i)).reset(),  "|", money.get(i - 5), "|", movies.get(i), "|");
                             break;
                        case 6:case 7:case 8:case 9:case 10:case 11:case 12:case 13:case 14:
                             System.out.printf("%1s %1s %20s %1s %40s %1s%n",  ansi().fg(Integer.parseInt(scores.get(i).substring(0,scores.get(i).length()-1))>60?GREEN:RED).a(scores.get(i)).reset(),  "|", money.get(i - 5), "|", movies.get(i), "|");
                            break;
                        case 15:
                             System.out.println(ansi().eraseScreen().render("\nCOMING SOON TO THEATERS\n@|cyan ???|@ - No Score Yet\n"));
                            if (scores.get(i).contains("?")){
                                System.out.printf("%1s %1s %40s %1s%n",  ansi().fg(CYAN).a(scores.get(i)).reset(), "|", movies.get(i), "|");
                            } else {
                                System.out.printf("%1s %1s %40s %1s%n", ansi().fg(Integer.parseInt(scores.get(i).substring(0,scores.get(i).length()-1))>60?GREEN:RED).a(scores.get(i)).reset(), "|", movies.get(i), "|");
                            }
                             break;
                        default:
                            if (scores.get(i).contains("?")){
                                System.out.printf("%1s %1s %40s %1s%n",  ansi().fg(CYAN).a(scores.get(i)).reset(), "|", movies.get(i), "|");
                            } else {
                                System.out.printf("%1s %1s %40s %1s%n", ansi().fg(Integer.parseInt(scores.get(i).substring(0,scores.get(i).length()-1))>60?GREEN:RED).a(scores.get(i)).reset(), "|", movies.get(i), "|");
                            }
                            break;
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
            parseFrom("https://www.rottentomatoes.com/");

        }
        catch(Exception e) {
            System.out.println("Error" + e.getMessage());
        }
    }
}
