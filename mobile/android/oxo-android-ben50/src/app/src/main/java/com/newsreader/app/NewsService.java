package com.newsreader.app;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsService {
    private static final String RSS_URL = "https://feeds.bbci.co.uk/news/rss.xml";
    private static final String ALTERNATIVE_API = "https://api.rss2json.com/v1/api.json?rss_url=https://feeds.bbci.co.uk/news/rss.xml";
    private OkHttpClient client;
    private ExecutorService executor;

    public interface NewsCallback {
        void onSuccess(List<NewsItem> articles);
        void onError(String error);
    }

    public NewsService() {
        client = new OkHttpClient();
        executor = Executors.newFixedThreadPool(4);
    }

    public void getTopHeadlines(NewsCallback callback) {
        executor.execute(() -> {
            try {
                Request request = new Request.Builder()
                        .url(ALTERNATIVE_API)
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    List<NewsItem> articles = parseRSSJsonResponse(jsonData);
                    callback.onSuccess(articles);
                } else {
                    tryDirectRSSFeed(callback);
                }
            } catch (Exception e) {
                tryDirectRSSFeed(callback);
            }
        });
    }

    private void tryDirectRSSFeed(NewsCallback callback) {
        try {
            Request request = new Request.Builder()
                    .url(RSS_URL)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful() && response.body() != null) {
                String xmlData = response.body().string();
                List<NewsItem> articles = parseRSSXMLResponse(xmlData);
                callback.onSuccess(articles);
            } else {
                callback.onSuccess(getMockNews());
            }
        } catch (Exception e) {
            callback.onSuccess(getMockNews());
        }
    }

    private List<NewsItem> parseRSSJsonResponse(String jsonData) {
        List<NewsItem> articles = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            for (int i = 0; i < Math.min(itemsArray.length(), 20); i++) {
                JSONObject item = itemsArray.getJSONObject(i);

                String title = item.optString("title", "");
                String description = item.optString("description", "");
                String link = item.optString("link", "");
                String pubDate = item.optString("pubDate", "");
                String author = item.optString("author", "BBC News");
                String thumbnail = item.optString("thumbnail", "");

                if (description.length() > 200) {
                    description = description.substring(0, 200) + "...";
                }

                NewsItem newsItem = new NewsItem(
                    title,
                    description,
                    author,
                    link,
                    thumbnail,
                    pubDate,
                    "general"
                );

                articles.add(newsItem);
            }
        } catch (Exception e) {
            return getMockNews();
        }

        return articles.isEmpty() ? getMockNews() : articles;
    }

    private List<NewsItem> parseRSSXMLResponse(String xmlData) {
        List<NewsItem> articles = new ArrayList<>();

        try {
            String[] items = xmlData.split("<item>");

            for (int i = 1; i < Math.min(items.length, 21); i++) {
                String item = items[i];

                String title = extractXMLTag(item, "title");
                String description = extractXMLTag(item, "description");
                String link = extractXMLTag(item, "link");
                String pubDate = extractXMLTag(item, "pubDate");

                if (title.isEmpty()) continue;

                if (description.length() > 200) {
                    description = description.substring(0, 200) + "...";
                }

                NewsItem newsItem = new NewsItem(
                    title,
                    description,
                    "BBC News",
                    link,
                    "",
                    pubDate,
                    "general"
                );

                articles.add(newsItem);
            }
        } catch (Exception e) {
            return getMockNews();
        }

        return articles.isEmpty() ? getMockNews() : articles;
    }

    private String extractXMLTag(String xml, String tag) {
        try {
            String startTag = "<" + tag + ">";
            String endTag = "</" + tag + ">";
            int start = xml.indexOf(startTag);
            int end = xml.indexOf(endTag);

            if (start != -1 && end != -1 && start < end) {
                String content = xml.substring(start + startTag.length(), end);
                return content.replaceAll("<[^>]+>", "").trim();
            }
        } catch (Exception e) {
            // Ignore parsing errors
        }
        return "";
    }

    private List<NewsItem> getMockNews() {
        List<NewsItem> mockNews = new ArrayList<>();

        mockNews.add(new NewsItem(
            "Tech Giants Report Strong Q3 Earnings",
            "Major technology companies exceed expectations with robust quarterly results driven by cloud services and AI investments.",
            "Business Reporter",
            "https://example.com/tech-earnings-q3",
            "https://via.placeholder.com/300x200",
            "2024-10-15T14:30:00Z",
            "technology"
        ));

        mockNews.add(new NewsItem(
            "Climate Summit Reaches Breakthrough Agreement",
            "World leaders commit to accelerated renewable energy targets at the international climate conference.",
            "Environmental Correspondent",
            "https://example.com/climate-summit-agreement",
            "https://via.placeholder.com/300x200",
            "2024-10-15T12:15:00Z",
            "environment"
        ));

        mockNews.add(new NewsItem(
            "New Archaeological Discovery Rewrites History",
            "Ancient artifacts found in Egypt provide new insights into early civilizations and trade routes.",
            "History Writer",
            "https://example.com/archaeological-discovery",
            "https://via.placeholder.com/300x200",
            "2024-10-15T10:45:00Z",
            "science"
        ));

        mockNews.add(new NewsItem(
            "Sports Championship Finals Set for Weekend",
            "Top teams prepare for highly anticipated championship matches with record ticket sales expected.",
            "Sports Editor",
            "https://example.com/championship-finals",
            "https://via.placeholder.com/300x200",
            "2024-10-15T09:20:00Z",
            "sports"
        ));

        mockNews.add(new NewsItem(
            "Medical Breakthrough in Cancer Treatment",
            "Researchers announce promising results from new immunotherapy trials showing significant improvement rates.",
            "Medical Correspondent",
            "https://example.com/cancer-treatment-breakthrough",
            "https://via.placeholder.com/300x200",
            "2024-10-15T08:30:00Z",
            "health"
        ));

        return mockNews;
    }
}

