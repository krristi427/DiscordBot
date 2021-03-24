package services.audio;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import services.Wrapper;

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

        String result = loadAndPlay((TextChannel) channel, content[1]).get();
        bot.sendInfoMessage(result, channel);
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
