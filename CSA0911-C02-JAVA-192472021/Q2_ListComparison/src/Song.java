package src;

import java.util.Objects;

/**
 * Song model representing a track in the Music Playlist Editor.
 * Student: K VENKATA VAMSI GOUD (192472021)
 * Course: Programming in Java (CSA09) - Assignment 2 (CO2)
 */
public class Song {
    private final String songId;
    private final String title;
    private final String artist;
    private final int durationSec;

    public Song(String songId, String title, String artist, int durationSec) {
        this.songId = songId;
        this.title = title;
        this.artist = artist;
        this.durationSec = durationSec;
    }

    public String getSongId() { return songId; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public int getDurationSec() { return durationSec; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return Objects.equals(songId, song.songId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(songId);
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%ds) [%s]", title, artist, durationSec, songId);
    }
}
