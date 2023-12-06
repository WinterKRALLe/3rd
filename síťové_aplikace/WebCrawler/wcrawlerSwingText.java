import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.ParserDelegator;
import javax.swing.text.*;

class URIinfo {
    URI uri;
    int depth;

    URIinfo(URI uri, int depth) {
        this.uri = uri;
        this.depth = depth;
    }

}

/**
 * Třída ParserCallback je používána parserem DocumentParser,
 * je implementován přímo v JDK a umí parsovat HTML do verze 3.0.
 * Při parsování (analýze) HTML stránky volá tento parser
 * jednotlivé metody třídy ParserCallback, což nám umožňuje
 * provádět s částmi HTML stránky naše vlastní akce.
 *
 * @author Tomáš Dulík
 */
class ParserCallback extends HTMLEditorKit.ParserCallback {
    /**
     * pageURI bude obsahovat URI aktuálně parsované stránky. Budeme
     * jej využívat pro resolving všech URL, které v kódu stránky najdeme
     * - předtím, než najdené URL uložíme do foundURLs, musíme z něj udělat
     * absolutní URL!
     */
    URI pageURI;
    /**
     * depth bude obsahovat aktuální hloubku zanoření
     */
    int depth = 0, maxDepth = 0;
    /**
     * visitedURLs je množina všech URL, které jsme již navštívili
     * (parsovali). Pokud najdeme na stránce URL, které je v této množině,
     * nebudeme jej už dále parsovat
     */
    HashSet<URI> visitedURIs;
    /**
     * foundURLs jsou všechna nová (zatím nenavštívená) URL, která na stránce
     * najdeme. Poté, co projdeme celou stránku, budeme z tohoto seznamu
     * jednotlivá URL brát a zpracovávat.
     */
    LinkedList<URIinfo> foundURIs;
    /**
     * pokud debugLevel>1, budeme vypisovat debugovací hlášky na std. error
     */
    int debugLevel = 0;

    // Vyhledávací tabulka pro četnost slov
    private final HashMap<String, Integer> wordFrequency = new HashMap<>();
    // Metoda pro aktualizaci četnosti slov v tabulce
    private void updateWordFrequency(String word) {
        word = word.toLowerCase(); // Převedení na malá písmena pro jednodušší porovnání
        wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
    }

    ParserCallback(HashSet<URI> visitedURIs, LinkedList<URIinfo> foundURIs) {
        this.foundURIs = foundURIs;
        this.visitedURIs = visitedURIs;
    }


    /**
     * metoda handleSimpleTag se volá např. u značky <FRAME>
     */
    public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
        handleStartTag(t, a, pos);
    }

    public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
        URI uri;
        String href = null;
        if (debugLevel > 1)
            System.err.println("handleStartTag: " + t.toString() + ", pos=" + pos + ", attribs=" + a.toString());
        if (depth < maxDepth)
            if (t == HTML.Tag.A) href = (String) a.getAttribute(HTML.Attribute.HREF);
            else if (t == HTML.Tag.FRAME) href = (String) a.getAttribute(HTML.Attribute.SRC);
        if (href != null)
            try {
                uri = pageURI.resolve(href);
                if (!uri.isOpaque() && !visitedURIs.contains(uri)) {
                    visitedURIs.add(uri);
                    foundURIs.add(new URIinfo(uri, depth + 1));
                    if (debugLevel > 0)
                        System.err.println("Adding URI: " + uri);
                }
            } catch (Exception e) {
                System.err.println("Nalezeno nekorektní URI: " + href);
                e.printStackTrace();
            }

    }

    /******************************************************************
     * V metodě handleText bude probíhat veškerá činnost, související se
     * zjišťováním četnosti slov v textovém obsahu HTML stránek.
     * IMPLEMENTACE TÉTO METODY JE V TÉTO ÚLOZE VAŠÍM ÚKOLEM !!!!
     * Možný postup:
     * Ve třídě Parser (klidně v její metodě main) si vyrobte vyhledávací tabulku
     * =instanci třídy HashMap<String,Integer> nebo TreeMap<String,Integer>.
     * Do této tabulky si ukládejte dvojice klíč-data, kde
     * klíčem nechť jsou jednotlivá slova z textového obsahu HTML stránek,
     * data typu Integer bude dosavadní počet výskytů daného slova v
     * HTML stránkách.
     *******************************************************************/
// Implementace analýzy textu
    public void handleText(char[] data, int pos) {
        String text = String.valueOf(data);
        System.out.println("handleText: " + text + ", pos=" + pos);

        // Use regular expression to extract words and filter out non-alphabetic characters
        String[] words = text.split("[^a-zA-Z]+");
        for (String word : words) {
            updateWordFrequency(word);
        }
    }

    // Metoda pro výpis 20 nejčastěji se vyskytujících slov
    public void printTopWords() {
        System.out.println("Top 20 words:");

        // Seřazení slov podle četnosti sestupně a výpis top 20
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
        HashSet<URI> visitedURIs = new HashSet<>();
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
            ParserDelegator parser = new ParserDelegator();

            while (!foundURIs.isEmpty()) {
                URIinfo URIinfo = foundURIs.removeFirst();
                callBack.depth = URIinfo.depth;
                callBack.pageURI = uri = URIinfo.uri;
                System.err.println("Analyzing " + uri);

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(uri.toURL().openStream()));
                    parser.parse(reader, callBack, true);
                    reader.close();
                } catch (Exception e) {
                    System.err.println("Error loading page:" + e.getLocalizedMessage());
                }
            }

            // Výpis 20 nejčastěji se vyskytujících slov
            callBack.printTopWords();
        } catch (Exception e) {
            System.err.println("Zachycena neošetřená výjimka, končíme...");
            e.printStackTrace();
        }
    }
}