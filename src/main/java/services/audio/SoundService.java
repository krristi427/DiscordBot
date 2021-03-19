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
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import services.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class SoundService extends Service {

    //TODO add functions for making the audio louder/silent
    //TODO chances to jump to a specific place in the queue/skip songs

    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;
    private String id;
    private String channelFeedback;
    private CompletableFuture<String> completableFuture;

    public SoundService() {
        super();
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

    public CompletableFuture<String> loadAndPlay(TextChannel channel, String trackUrl) {

        completableFuture = new CompletableFuture<>();

        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                channelFeedback = "Adding to queue: " + track.getInfo().title;
                completableFuture.complete(channelFeedback);
                play(channel.getGuild(), musicManager, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                channelFeedback = "Adding to queue "
                        + firstTrack.getInfo().title
                        + " (first track of playlist " + playlist.getName() + ")";
                completableFuture.complete(channelFeedback);
                play(channel.getGuild(), musicManager, firstTrack);
            }

            @Override
            public void noMatches() {
                channelFeedback = "Nothing found by " + trackUrl;
                completableFuture.complete(channelFeedback);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channelFeedback = "Could not play: " + exception.getMessage();
                completableFuture.complete(channelFeedback);
            }
        });

        return completableFuture;
    }

    private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track) {
        connectToVoiceChannel(guild.getAudioManager());
        musicManager.scheduler.queue(track);
    }

    public CompletableFuture<String> pauseTrack(TextChannel channel) {

        completableFuture = new CompletableFuture<>();

        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        AudioPlayer player = musicManager.player;

        if (player.getPlayingTrack() == null) {
            channelFeedback = "Cannot pause or resume player because no track is loaded for playing.";
            completableFuture.complete(channelFeedback);
            return completableFuture;
        }

        player.setPaused(!player.isPaused());

        if (player.isPaused()) {
            channelFeedback = "The player has been paused.";
            completableFuture.complete(channelFeedback);
        }
        else {
            channelFeedback = "The player has resumed playing.";
            completableFuture.complete(channelFeedback);
        }
        return completableFuture;
    }

    public String resumeTrack(TextChannel channel) {

        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        AudioPlayer player = musicManager.player;
        AudioTrack playingTrack = player.getPlayingTrack();

        player.setPaused(!player.isPaused());

        return "Resuming: " + playingTrack.getInfo().title;
    }

    public String stopPlaying(TextChannel channel) {

        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        AudioPlayer player = musicManager.player;

        musicManager.scheduler.queue.clear();
        player.stopTrack();
        player.setPaused(false);
        channel.getGuild().getAudioManager().closeAudioConnection();
        return "Playback has been completely stopped and the queue has been cleared.";
    }

    public void currentQueue(TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.printQueue(channel);
    }

    private void connectToVoiceChannel(AudioManager audioManager) {
        if (!audioManager.isConnected()) {

            //get the member and the voice channel they are in, so that you can join them
            Member member = audioManager.getGuild().getMemberById(id);
            if (member != null) {
                VoiceChannel voiceChannel = member.getVoiceState().getChannel();

                if (voiceChannel != null) {
                    audioManager.openAudioConnection(voiceChannel);

                } else {

                    //if the person isn't in a voice channel, connect to the first you find
                    for (VoiceChannel channel : audioManager.getGuild().getVoiceChannelCache()) {
                        audioManager.openAudioConnection(channel);
                        break;
                    }
                }
            }
        }
    }

    public void exitChannel(TextChannel channel) {
        channel.getGuild().getAudioManager().closeAudioConnection();
    }

    public void setId(String id) {
        this.id = id;
    }
}
