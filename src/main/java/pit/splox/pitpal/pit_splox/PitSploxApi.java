package pit.splox.pitpal.pit_splox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
import com.mojang.authlib.exceptions.AuthenticationException;

import pit.splox.pitpal.modules.snooper.SnooperMessage;
import pit.splox.pitpal.pit_events.PitEvent;

import static pit.splox.pitpal.PitPal.*;

/**
 * Non-stateful static class for making requests to the PitSplox API
 */
public class PitSploxApi {
    public static final String apiUrl = "https://us-central1-gold-pit.cloudfunctions.net/api/";
    
    private static String authId;
    private static String token;
    public static boolean isAuthenticated() {
        return token != null;
    }
    public static CompletableFuture<Void> authenticate() {
        return CompletableFuture.runAsync(() -> {
            try {
                authenticateSync();
                logger.info("Authentication success!");
            } catch (Exception e) {
                logger.error("Error authenticating");
            }
        });
    }
    private static void authenticateSync() throws AuthenticationException {
        // How PitSplox authentication works is that it pretends to be an MC server.
        // This allows us to verify your identity while never handling any of your
        // sensitive information, so you don't have to put blind trust in us. This
        // is the Pit after all.

        // Terminology:
        //   - minecraft token: allows you to sign in to minecraft, anyone with it can
        //       join a server and pretend to be you
        //   - pitsplox token: allows you to connect to PitSplox, anyone with it can pretend
        //       to be you to PitSplox's servers
        // The following code authenticates you using your MC account (so we can punish
        // you if you submit false data, scam, etc.) while not letting PitSplox ever
        // see your MC token.

        // This gets a randomly generated ID from PitSplox
        authId = pullAuthIdSync();

        // This sends our (1) Minecraft token and (2) PitSplox randomly generated ID to Mojang's servers
        mc.getSessionService().joinServer(mc.getSession().getProfile(), mc.getSession().getToken(), authId);

        // This will now send a request to PitSplox. PitSplox's servers will then ask Mojang if your
        // account joined a server with the randomly generated ID earlier, and when Mojang says that you did,
        // we'll know that you're telling the truth and aren't lying to us, and we did it without
        // checking your credentials directly.
        // Now that we have our own PitSplox-specific token, we don't have
        // to worry about dealing with this stuff again.
        token = requestTokenSync();
    }

    private static String pullAuthIdSync() {
        return postJsonSync("auth/minecraft/pullAuthId", "{\"mcUuid\":\"" + mc.getSession().getProfile().getId().toString() + "\"}").getAsJsonObject().get("authId").getAsString();
    }

    private static String requestTokenSync() {
        return postJsonSync("auth/minecraft/requestToken", "{\"username\": \"" + mc.getSession().getUsername() + "\",\"mcUuid\":\"" + mc.getSession().getProfile().getId().toString() + "\"}").getAsJsonObject().get("token").getAsString();
    }

    private static <TResponse> String postReaderSync(String path, String data) {
        logger.info("Made request to /" + path);
        try {
            if (path.charAt(0) == '/') {
                throw new RuntimeException("Path shouldn't start with /");
            }

            // Copied from the internet
            URL url = new URL(apiUrl + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setFixedLengthStreamingMode(data.getBytes(StandardCharsets.UTF_8).length);
            if (isAuthenticated()) {
                connection.setRequestProperty("Authorization", "Bearer " + token);
            }
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("accept", "application/json");
            connection.setDoOutput(true);
            connection.connect();

            try (OutputStream os = connection.getOutputStream()) {
                os.write(data.getBytes(StandardCharsets.UTF_8));
            }

            InputStream responseStream = connection.getInputStream();

            return new BufferedReader(
                new InputStreamReader(responseStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            logger.error("Bad request in postSync");
            e.printStackTrace();
            throw new RuntimeException("Bad request");
        }
    }

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
            if (isAuthenticated()) {
                connection.setRequestProperty("Authorization", "Bearer " + token);
            }
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

    private static JsonElement fetchJsonSync(String path) {
        return jsonParser.parse(fetchReaderSync(path));
    }

    private static JsonElement postJsonSync(String path, String data) {
        return jsonParser.parse(postReaderSync(path, data));
    }

    private static <TResponse> JsonElement fetchJsonTreeSync(String path) {
        String res = fetchReaderSync(path);
        return jsonParser.parse(res);
    }

    private static <TResponse> CompletableFuture<TResponse> fetch(String path, Class<TResponse> classOfObj) {
        return CompletableFuture.supplyAsync(() -> fetchSync(path, classOfObj));
    }

    public static CompletableFuture<List<PitEvent>> fetchEvents() {
        return CompletableFuture.supplyAsync(() -> fetchEventsSync());
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

    public static void postSnooperMessagesSync(List<SnooperMessage> msgs) {
        postReaderSync("snooper/logMessages", "{\"messages\":[" + String.join(",", msgs.stream().map(msg -> gson.toJson(msg)).collect(Collectors.toList())) + "]}");
    }

    public static CompletableFuture<Void> postSnooperMessages(List<SnooperMessage> msgs) {
        return CompletableFuture.runAsync(() -> postSnooperMessagesSync(msgs));
    }
}
