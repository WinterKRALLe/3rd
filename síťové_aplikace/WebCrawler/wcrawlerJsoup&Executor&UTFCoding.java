import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

class URIinfo {
    URI uri;
    int depth;

    URIinfo(URI uri, int depth) {
        this.uri = uri;
        this.depth = depth;
    }

}

class ParserCallback {
    URI pageURI;
    int depth = 0, maxDepth = 0;
    HashSet<URI> visitedURIs;
    LinkedList<URIinfo> foundURIs;
    int debugLevel = 0;

    private final HashMap<String, Integer> wordFrequency = new HashMap<>();

    private void updateWordFrequency(String word) {
        word = word.toLowerCase();
        wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
    }

    ParserCallback(Set<URI> visitedURIs, LinkedList<URIinfo> foundURIs) {
        this.foundURIs = foundURIs;
        this.visitedURIs = new HashSet<>(visitedURIs);
    }

    public void handleStartTag(Element element) {
        URI uri;
        if (depth < maxDepth) {
            if (element.tagName().equals("a")) {
                String href = element.attr("href");
                uri = pageURI.resolve(href);
                if (!uri.isOpaque() && !visitedURIs.contains(uri)) {
                    visitedURIs.add(uri);
                    foundURIs.add(new URIinfo(uri, depth + 1));
                    if (debugLevel > 0)
                        System.err.println("Adding URI: " + uri);
                }
            }
        }
    }

    public void handleText(String text) {
        System.out.println("handleText: " + text);

        String[] words = text.split("[^a-zA-Z]+");
        for (String word : words) {
            updateWordFrequency(word);
        }
    }

    public void printTopWords() {
        System.out.println("Top 20 words:");

        wordFrequency.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .limit(20)
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    }
}

public class Parser {

    public static void main(String[] args) {
        LinkedList<URIinfo> foundURIs = new LinkedList<>();
        Set<URI> visitedURIs = Collections.newSetFromMap(new ConcurrentHashMap<>());

        URI uri;
        try {
            if (args.length < 1) {
                System.err.println("Missing command line parameter - start URL. Try http://neverssl.com or https://yahoo.com.");
                return;
            }

            uri = new URI(args[0] + "/");
            foundURIs.add(new URIinfo(uri, 0));
            visitedURIs.add(uri);
            ParserCallback callBack = new ParserCallback(visitedURIs, foundURIs);

            ExecutorService executor = Executors.newFixedThreadPool(5); // Adjust the number of threads as needed

            while (!foundURIs.isEmpty()) {
                URIinfo URIinfo = foundURIs.removeFirst();
                callBack.depth = URIinfo.depth;
                callBack.pageURI = uri = URIinfo.uri;
                System.err.println("Analyzing " + uri);

                URI finalUri = uri;
                executor.execute(() -> {
                    try {
                        URLConnection conn = finalUri.toURL().openConnection();
                        String type = conn.getContentType();
                        String charset = "utf-8";
                        int encodingIndex = type.indexOf(';');
                        if (encodingIndex >= 0) {
                            String encoding = type.substring(type.indexOf(';') + 2);
                            charset = encoding.substring(encoding.indexOf('=') + 1);
                        }
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
                        Document parsedDoc = Jsoup.parse(reader.lines().collect(Collectors.joining("\n")));
                        callBack.handleText(parsedDoc.text());

                        Document doc = Jsoup.connect(finalUri.toString()).header("Content-Encoding", charset).get();

                        callBack.handleText(doc.text());
                        Elements links = doc.select("a[href]");
                        for (Element link : links) {
                            callBack.handleStartTag(link);
                        }
                    } catch (IOException e) {
                        System.err.println("Error loading page:" + e.getLocalizedMessage());
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            callBack.printTopWords();
        } catch (URISyntaxException | InterruptedException e) {
            System.err.println("Zachycena neošetřená výjimka, končíme...");
            e.printStackTrace();
        }
    }
}
