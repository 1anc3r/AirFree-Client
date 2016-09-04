package me.lancer.airfree.bean;

public class MusicBean {

    private long id;

    private String title;

    private String artist;

    private String album;

    private long albumId;

    private long duration;

    private long size;

    private String path;

    public MusicBean() {

    }

    public MusicBean(long id, String path, String title, long albumId, String album, String artist) {
        this.id = id;
        this.path = path;
        this.title = title;
        this.albumId = albumId;
        this.album = album;
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
