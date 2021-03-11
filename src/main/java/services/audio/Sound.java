package services.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import services.Observer;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Sound implements Observer {

    public static final Sound instance = new Sound();
    public static final Sound getInstance() {
        return instance;
    }

    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    //TODO add functionality to disconnect from voice channel
    public Sound() {
        this.musicManagers = new HashMap<>();

        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
        return musicManager;
    }

    public void loadAndPlay(TextChannel channel, String trackUrl) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                channel.sendMessage("Adding to queue: " + track.getInfo().title).queue();
                play(channel.getGuild(), musicManager, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                channel.sendMessage("Adding to queue "
                        + firstTrack.getInfo().title
                        + " (first track of playlist " + playlist.getName() + ")").queue();
                play(channel.getGuild(), musicManager, firstTrack);
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Nothing found by " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });
    }

    private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track) {
        connectToFirstVoiceChannel(guild.getAudioManager());
        musicManager.scheduler.queue(track);
    }

    public void pauseTrack(TextChannel channel) {

        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        AudioPlayer player = musicManager.player;

        if (player.getPlayingTrack() == null) {
            channel.sendMessage("Cannot pause or resume player because no track is loaded for playing.").queue();
            return;
        }

        player.setPaused(!player.isPaused());

        if (player.isPaused()) {
            channel.sendMessage("The player has been paused.").queue();
        }
        else {
            channel.sendMessage("The player has resumed playing.").queue();
        }
    }

    public void resumeTrack(TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        AudioPlayer player = musicManager.player;
        AudioTrack playingTrack = player.getPlayingTrack();

        player.setPaused(!player.isPaused());

        channel.sendMessage("Resuming: " + playingTrack.getInfo().title).queue();
    }

    public void stopPlaying(TextChannel channel) {

        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        AudioPlayer player = musicManager.player;

        musicManager.scheduler.queue.clear();
        player.stopTrack();
        player.setPaused(false);
        channel.sendMessage("Playback has been completely stopped and the queue has been cleared.").queue();
    }

    public void currentQueue(TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.printQueue(channel);
    }

    private void connectToFirstVoiceChannel(AudioManager audioManager) {
        if (!audioManager.isConnected()) {
            for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
                audioManager.openAudioConnection(voiceChannel);
                break;
            }
        }
    }

    @Override
    public void update(MessageReceivedEvent event) {

    }

    @Override
    public void update(GuildMessageReceivedEvent event) {

        String[] content = event.getMessage().getContentRaw().split(" ");
        String command = content[0];
        if(command.startsWith("!")) {
            command = command.toLowerCase(Locale.ROOT).replace("!", "");

            switch (command) {
                case ("play") -> loadAndPlay(event.getChannel(), content[1]);
                case ("pause") -> pauseTrack(event.getChannel());
                case ("resume") -> resumeTrack(event.getChannel());
                case ("stop") -> stopPlaying(event.getChannel());
                case ("currentQueue") -> currentQueue(event.getChannel());
            }

        }
    }

    @Override
    public void update(MessageReactionAddEvent event) {

    }

    @Override
    public void update(MessageReactionRemoveEvent event) {

    }
}
