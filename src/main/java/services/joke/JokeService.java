package services.joke;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import services.Observer;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;

@Slf4j
public class JokeService implements Observer {

    private static final JokeService instance = new JokeService();
    public static JokeService getInstance() {
        return instance;
    }
    OkHttpClient okHttpClient = new OkHttpClient();

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

    public void getJokeFromQuery(MessageChannel channel, String query) {

        //bc appearently they have this restriction
        if ((query.length() < 3) || (query.length() > 120)) {

            channel.sendMessage("Sorry bud that was an either too short, or too long query").queue();
            log.info("Somebody entered a short/long query");
            return;
        }

        Request request = new Request.Builder()
                .url("https://api.chucknorris.io/jokes/search?query=" + query)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                channel.sendMessage("Something went wrong...").queue();
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
                    channel.sendMessage("There are no jokes with that word larry \n" +
                            "Other than yourself :))").queue();
                } else {
                    JsonArray resultArray = jsonObject.get("result").getAsJsonArray();

                    //to spice things up :))
                    Random random = new Random();
                    JsonObject resultObject = resultArray.get(random.nextInt(resultArray.size()))
                            .getAsJsonObject();

                    channel.sendMessage(resultObject.get("value").toString()).queue();
                }
            }
        });
    }

    @Override
    public void update(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();
        String[] content = message.getContentRaw().split(" ");
        String command = content[0];

        if(command.startsWith("!")) {
            command = command.toLowerCase(Locale.ROOT).replace("!", "");

            //TODO make use of other endpoints from the API
            switch (command) {
                case ("joke") -> getJoke(channel);
                case ("jokewith") -> getJokeFromQuery(channel, content[1]);
            }
        }
    }

    @Override
    public void update(GuildMessageReceivedEvent event) {

    }

    @Override
    public void update(MessageReactionAddEvent event) {

    }

    @Override
    public void update(MessageReactionRemoveEvent event) {

    }
}
