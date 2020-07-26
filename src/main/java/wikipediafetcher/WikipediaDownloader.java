package wikipediafetcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class WikipediaDownloader implements Runnable {

    private String keyword;

    public WikipediaDownloader() {

    }

    public WikipediaDownloader(String keyword) {
        this.keyword = keyword;
    }

    public void run() {
        //1 Get Clean keyword.
        //2 Get url for Wikipedia
        //3 Make get request to wikipedia
        //4 Parsing useful results using jsoup
        //5 showing results
        if (this.keyword == null || this.keyword.length() == 0) {
            return;
        }

        this.keyword = this.keyword.trim().replaceAll("[ ]+", "_");

        String wikiUrl = getWikipediaUrlForQuery(this.keyword);

        String response = "";
        String imageUrl = null;

        try {

            String wikipediaResponseHTML = HttpUrlConnection.sendGet(wikiUrl);
            // System.out.println(wikipediaResponseHTML);

            Document document = Jsoup.parse(wikipediaResponseHTML ,"https://en.wikipedia.org");
            Elements childElements = document.body().select(".mw-parser-output > *");
             //int childElements them alt + enter it automatically changes the datatype


            //this below code implements automata
            int state = 0;
            for (Element chileElement : childElements) {
                if (state == 0) {
                    if (chileElement.tagName().equals("table")) {
                        state = 1;
                    }
                } else if (state == 1) {
                    if (chileElement.tagName().equals("p")) {
                        {
                            state = 2;
                            response = chileElement.text();
                            break;
                        }
                    }
                }
                try {
                    imageUrl = document.body().select(".infobox img").get(0).attr("src");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }




        //you can push result into database as of now lets just use json
        WikiResult wikiResult = new WikiResult(this.keyword, response, imageUrl);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(wikiResult);
        System.out.println(json);



    }


    private String getWikipediaUrlForQuery(String cleanKeyword) {
        return "https://en.wikipedia.org/wiki/" + cleanKeyword;
    }


    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager(20);
        String arr[] = {"India", "United States"};

        for(String keywords : arr)
        {
            WikipediaDownloader temp = new WikipediaDownloader(keywords);

            System.out.println("this is the multithreaded wikepedia downloader crawler");
            taskManager.waitTillQueueIsFreeAndAddTask(temp);
        }

    }
}
