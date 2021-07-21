package services.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import services.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public abstract class SoundService extends Service {

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
                exception.printStackTrace();
                channelFeedback = "Could not play: " + exception.getMessage();
                completableFuture.complete(channelFeedback);
            }
        });

        return completableFuture;
    }

    private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track) {
        connectToVoiceChannel(guild.getAudioManager());
        musicManager.getScheduler().queue(track);
    }

    protected void playFolder(String path, TextChannel textChannel) {

        try (Stream<Path> walk = Files.walk(Path.of(path))){

            walk.filter(Files::isRegularFile)
                    .map(Path::toString)
                    .forEach(s -> loadAndPlay(textChannel, s));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CompletableFuture<String> pauseTrack(TextChannel channel) {

        completableFuture = new CompletableFuture<>();

        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        AudioPlayer player = musicManager.getPlayer();

        if (player.getPlayingTrack() == null) {
            channelFeedback = "Cannot pause or resume player because no track is loaded for playing.";
            completableFuture.complete(channelFeedback);
            return completableFuture;
        }

        player.setPaused(!player.isPaused());

        channelFeedback = player.isPaused() ? "The player has been paused." : "The player has resumed playing.";
        completableFuture.complete(channelFeedback);
        return completableFuture;
    }

    public String resumeTrack(TextChannel channel) {

        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        AudioPlayer player = musicManager.getPlayer();
        AudioTrack playingTrack = player.getPlayingTrack();

        player.setPaused(!player.isPaused());

        return "Resuming: " + playingTrack.getInfo().title;
    }

    public String stopPlaying(TextChannel channel) {

        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        AudioPlayer player = musicManager.getPlayer();

        musicManager.getScheduler().getQueue().clear();
        player.stopTrack();
        player.setPaused(false);
        channel.getGuild().getAudioManager().closeAudioConnection();
        return "Playback has been completely stopped and the queue has been cleared.";
    }

    public String currentQueue(TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        return musicManager.getScheduler().printQueue();
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

    public void changeVolume(TextChannel channel, int newVolume) {

        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        AudioPlayer player = musicManager.getPlayer();
        int playerVolume = player.getVolume();
        player.setVolume(playerVolume + newVolume);
    }

    public String skip(TextChannel channel) {

        //just get the current track and stop the boi
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        AudioPlayer player = musicManager.getPlayer();
        AudioTrackInfo currentTrackInfo = player.getPlayingTrack().getInfo();
        String trackInfo = currentTrackInfo.author + "-" + currentTrackInfo.title;

        //and just move on to the next track:
        musicManager.getScheduler().nextTrack();
        return "I just yeeted this kid: " + trackInfo;
    }

    public String skip(TextChannel channel, int offset) {

        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        return musicManager.getScheduler().nextTrack(offset) ? "I just yeeted " + offset + " kids": "That's too big of an offset larry";
    }

    public void exitChannel(TextChannel channel) {
        stopPlaying(channel);
        channel.getGuild().getAudioManager().closeAudioConnection();
    }

    public void setId(String id) {
        this.id = id;
    }
}
