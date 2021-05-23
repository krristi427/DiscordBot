package services.joke;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import services.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.*;

@Slf4j
public abstract class JokeService extends Service {

    //TODO detect \n in the string text and display it properly

    OkHttpClient okHttpClient = new OkHttpClient();
    Properties properties = new Properties();
    private String joke = "holdrio";

    public JokeService() {
        super();
        try {
            properties.load(new FileInputStream("src/main/resources/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected CompletableFuture<String> getJoke() {

        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        Request request = new Request.Builder()
                .url("https://dad-jokes.p.rapidapi.com/random/joke")
                .get()
                .addHeader("x-rapidapi-key", properties.getProperty("jokeApiKey"))
                .addHeader("x-rapidapi-host", "dad-jokes.p.rapidapi.com")
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                log.error("Something went wrong with the request...");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String responseBody = Objects.requireNonNull(response.body()).string();
                JsonObject element = JsonParser.parseString(responseBody)
                        .getAsJsonObject()
                        .getAsJsonArray("body")
                        .get(0)
                        .getAsJsonObject();

                String setup = element.get("setup").toString();
                String punchline = element.get("punchline").toString();
                joke = setup + "\n" + punchline;
                completableFuture.complete(joke);
            }
        });

        return completableFuture;
    }

    protected CompletableFuture<String> getJokeFromQuery(String query) {

        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        //bc appearently they have this restriction
        if ((query.length() < 3) || (query.length() > 120)) {

            log.info("Somebody entered a short/long query");
            return CompletableFuture.completedFuture("");
        }

        //if a type was provided, that is handled in a different method
        if (query.equals("general") || query.equals("knock-knock") || query.equals("programming")) {
            return getJokeFromType(query);
        }

        Request request = new Request.Builder()
                .url("https://dad-jokes.p.rapidapi.com/joke/search?term=" + query)
                .get()
                .addHeader("x-rapidapi-key", properties.getProperty("jokeApiKey"))
                .addHeader("x-rapidapi-host", "dad-jokes.p.rapidapi.com")
                .build();

        Call call = okHttpClient.newCall(request);

        //DO NOT refactor out this callback, as when you provide the same instance,
        // the same random number will be chosen, leading to the same joke
        call.enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                log.error("Something went wrong...");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                //to spice things up :))
                Random random = new Random();

                String responseBody = Objects.requireNonNull(response.body()).string();
                JsonArray body = JsonParser.parseString(responseBody)
                        .getAsJsonObject()
                        .getAsJsonArray("body");

                if (body.size() > 0) {
                    JsonObject element = body.get(random.nextInt(body.size())).getAsJsonObject();

                    String setup = element.get("setup").toString();
                    String punchline = element.get("punchline").toString();
                    joke = setup + "\n" + punchline;
                    completableFuture.complete(joke);

                } else {
                    completableFuture.complete("There are no jokes on that word larry\nOther than yourself");
                }
            }
        });

        return completableFuture;
    }

    protected CompletableFuture<String> getJokeFromType(String type) {

        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        Request request = new Request.Builder()
                .url("https://dad-jokes.p.rapidapi.com/joke/type/" + type)
                .get()
                .addHeader("x-rapidapi-key", properties.getProperty("jokeApiKey"))
                .addHeader("x-rapidapi-host", "dad-jokes.p.rapidapi.com")
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                log.error("Something went wrong...");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                //to spice things up :))
                Random random = new Random();

                String responseBody = Objects.requireNonNull(response.body()).string();
                JsonArray body = JsonParser.parseString(responseBody)
                        .getAsJsonObject()
                        .getAsJsonArray("body");

                if (body.size() > 0) {
                    JsonObject element = body.get(random.nextInt(body.size())).getAsJsonObject();

                    String setup = element.get("setup").toString();
                    String punchline = element.get("punchline").toString();
                    joke = setup + "\n" + punchline;
                    completableFuture.complete(joke);

                } else {
                    completableFuture.complete("There are no jokes on that word larry\nOther than yourself");
                }
            }
        });
        return completableFuture;
    }
}
