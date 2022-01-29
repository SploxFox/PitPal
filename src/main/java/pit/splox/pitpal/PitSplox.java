package pit.splox.pitpal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import com.google.common.collect.Multiset.Entry;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import pit.splox.pitpal.pit_events.PitEvent;

import static pit.splox.pitpal.PitPal.*;

public class PitSplox {
    public static final String apiUrl = "https://us-central1-gold-pit.cloudfunctions.net/api/";

    private static <TResponse> String fetchReaderSync(String path) {
        logger.info("Made request to /" + path);
        try {
            if (path.charAt(0) == '/') {
                throw new RuntimeException("Path shouldn't start with /");
            }

            // Copied from the internet
            URL url = new URL(apiUrl + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("accept", "application/json");

            // This line makes the request
            InputStream responseStream = connection.getInputStream();

            return new BufferedReader(
                new InputStreamReader(responseStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            logger.error("Bad request in fetchSync");
            e.printStackTrace();
            throw new RuntimeException("Bad request");
        }
    }

    private static <TResponse> TResponse fetchSync(String path, Class<TResponse> classOfObj) {
        return gson.fromJson(fetchReaderSync(path), classOfObj);
    }

    private static <TResponse> JsonElement fetchJsonTreeSync(String path) {
        String res = fetchReaderSync(path);
        logger.info(res);
        return jsonParser.parse(res);
    }

    private static <TResponse> CompletableFuture<TResponse> fetch(String path, Class<TResponse> classOfObj) {
        return CompletableFuture.supplyAsync(() -> fetchSync(path, classOfObj));
    }

    public static CompletableFuture<List<PitEvent>> fetchEvents() {
        return CompletableFuture.supplyAsync(PitSplox::fetchEventsSync);
    }

    public static List<PitEvent> fetchEventsSync() {
        JsonElement tree = fetchJsonTreeSync("events");
        Set<Map.Entry<String,JsonElement>> eventsSet = tree.getAsJsonObject().entrySet();
        List<PitEvent> events = eventsSet.stream()
            .map((Map.Entry<String, JsonElement> entry) -> entry.getValue().getAsJsonObject())
            .map((JsonObject obj) -> new PitEvent(obj.get("start").getAsLong(), obj.get("name").getAsString(), obj.get("type").getAsString()))
            .collect(Collectors.toList());
        
        events.sort((PitEvent a, PitEvent b) -> (int)(a.start - b.start));
        return events;
    }
}
