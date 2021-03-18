package services.joke;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import services.Service;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.*;

@Slf4j
public abstract class JokeService extends Service {

    OkHttpClient okHttpClient = new OkHttpClient();

    private String joke = "holdrio";

    public JokeService() {
        super();
    }

    protected CompletableFuture<String> getJoke() {

        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        Request request = new Request.Builder()
                .url("https://api.chucknorris.io/jokes/random")
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

                JsonObject element = JsonParser.parseString(response.body().string()).getAsJsonObject();
                joke = element.get("value").toString();
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

        Request request = new Request.Builder()
                .url("https://api.chucknorris.io/jokes/search?query=" + query)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                log.error("Something went wrong...");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                JsonObject jsonObject = JsonParser.parseString(response.body().string())
                        .getAsJsonObject();

                //converting them both to strings, bc you can't compare a JsonElement with an int
                //yes, it isn't recommended, but i know what i expect soooooo :))
                if (jsonObject.get("total").toString().equals(Integer.toString(0))) {
                   joke = "There are no jokes with that word larry \n" +
                            "Other than yourself :))";
                   completableFuture.complete(joke);
                } else {
                    JsonArray resultArray = jsonObject.get("result").getAsJsonArray();

                    //to spice things up :))
                    Random random = new Random();
                    JsonObject resultObject = resultArray.get(random.nextInt(resultArray.size()))
                            .getAsJsonObject();

                    joke = resultObject.get("value").toString();
                    completableFuture.complete(joke);
                }
            }
        });

        return completableFuture;
    }
}
