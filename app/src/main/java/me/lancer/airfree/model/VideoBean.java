package me.lancer.airfree.model;

public class VideoBean {
    /**
     * 文件夹的第一张图片路径
     */
    private String videoPath;
    /**
     * 文件夹名
     */
    private String videoName;

    private String thumbPath;

    public VideoBean(){

    }

    public VideoBean(String videoPath, String videoName, String thumbPath){
        this.videoPath = videoPath;
        this.videoName = videoName;
        this.thumbPath = thumbPath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }
}
