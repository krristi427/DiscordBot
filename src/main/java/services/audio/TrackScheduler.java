package services.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

    @Getter
    private final AudioPlayer player;

    @Getter
    final BlockingQueue<AudioTrack> queue;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }


    public void queue(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }


    public void nextTrack() {

        //noInterrupt should be false, as this method is also used for skipping, in which case
        // you DO want to interrupt the current track
        player.startTrack(queue.poll(), false);
    }

    /**
     * Does the same as nextTrack(), just uses the offset to skip that number of songs
     * @param offset: indicates how many places should be skipped
     * @return true or false, whether or not the operation was successful
     */
    public boolean nextTrack(int offset) {

        if (offset >= queue.size()) {
            return false;
        }

        for (int i = 0; i < offset; i++) {
            queue.poll();
        }

        nextTrack();
        return true;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    public String printQueue() {

        StringBuilder queuedTracks = new StringBuilder();

        for (AudioTrack audioTrack : queue) {
            queuedTracks.append(audioTrack.getInfo().title).append(";\n");
        }

        return "Current tracks in the queue are: \n" + queuedTracks;
    }
}
