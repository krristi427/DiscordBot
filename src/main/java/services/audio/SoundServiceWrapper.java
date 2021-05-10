package services.audio;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import services.Wrapper;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class SoundServiceWrapper extends SoundService implements Wrapper {

    private static final SoundServiceWrapper instance = new SoundServiceWrapper();
    public static SoundServiceWrapper getInstance() {
        return instance;
    }
    private String id;

    public SoundServiceWrapper() {
        super();
    }

    public void play(String[] content, MessageChannel channel) throws ExecutionException, InterruptedException {
        setId(bot.eventAuthor.getId());

        File file = new File(content[1]);

        //if the file is a directory, traverse the hell out of it,
        // while adding to the queue every file in your way
        if (file.isDirectory()) {

            playFolder(content[1], (TextChannel) channel);
            bot.sendInfoMessage("Conceiled File-names due to confidentiality", channel);
            return;
        }

        String result = loadAndPlay((TextChannel) channel, content[1]).get();

        if (!result.isEmpty()) {
            bot.sendInfoMessage(result, channel);
        }
    }

    public void pause(String[] content, MessageChannel channel) throws ExecutionException, InterruptedException {
        String result = pauseTrack((TextChannel) channel).get();
        bot.sendInfoMessage(result, channel);
    }

    public void resume(String[] content, MessageChannel channel) {
        String result = resumeTrack((TextChannel) channel);
        bot.sendInfoMessage(result, channel);
    }

    public void stop(String[] content, MessageChannel channel) {
        String result = stopPlaying((TextChannel) channel);
        bot.sendInfoMessage(result, channel);
    }

    public void currentqueue(String[] content, MessageChannel channel) {
        currentQueue((TextChannel) channel);
    }

    public void volume(String[] content, MessageChannel channel) {
        changeVolume((TextChannel)channel, Integer.parseInt(content[1]));
    }

    public void exit(String[] content, MessageChannel channel) {
        exitChannel((TextChannel) channel);
        channel.sendMessage("It was a pleasure serving you human").queue();
    }

}
