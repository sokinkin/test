package com.jagrosh.jmusicbot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.jagrosh.jmusicbot.audio.RequestMetadata;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import com.jagrosh.jmusicbot.utils.TimeUtil;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Message;

public class PlayResultHandler implements AudioLoadResultHandler {
    private final Bot bot;
    private final CommandEvent event;
    private final Message message;

    public PlayResultHandler(Bot bot, CommandEvent event, Message message) {
        this.bot = bot;
        this.event = event;
        this.message = message;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        int pos = handler.addTrack(new QueuedTrack(track, RequestMetadata.fromResultHandler(track, event))) + 1;
        message.editMessage(FormatUtil.filter(event.getClient().getSuccess() + " Added **" +
            track.getInfo().title + "** (`" + TimeUtil.formatTime(track.getDuration()) + "`) " +
            (pos == 0 ? "to begin playing" : " to the queue at position " + pos))).queue();
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        // Similar to trackLoaded but handling playlists
    }

    @Override
    public void noMatches() {
        message.editMessage(FormatUtil.filter(event.getClient().getWarning() + " No results found for `" + event.getArgs() + "`.")).queue();
    }

    @Override
    public void loadFailed(FriendlyException throwable) {
        message.editMessage(event.getClient().getError() + " Error loading: " + throwable.getMessage()).queue();
    }
}
