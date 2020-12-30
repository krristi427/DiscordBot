package services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.MessageChannel;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@Slf4j
public class JokeService {

    private static final JokeService instance = new JokeService();
    public static JokeService getInstance() {
        return instance;
    }
    OkHttpClient okHttpClient = new OkHttpClient();

    private JokeService() {

    }

    public void getJoke(MessageChannel channel) {

        Request request = new Request.Builder()
                .url("https://api.chucknorris.io/jokes/random")
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                channel.sendMessage("Something went wrong with the request...").queue();
                log.error("Something went wrong with the request...");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                JsonObject element = JsonParser.parseString(response.body().string()).getAsJsonObject();
                String joke = element.get("value").toString();
                channel.sendMessage(joke).queue();
            }
        });
    }
}
