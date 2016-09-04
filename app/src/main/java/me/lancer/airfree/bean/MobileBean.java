package me.lancer.airfree.bean;

import java.util.List;

public class MobileBean {

    private String path;

    private String fileName;

    private String fileParent;

    private List<String> fileChilds;

    private String fileDate;

    public MobileBean() {

    }

    public MobileBean(String path, String fileName, String fileParent, List<String> fileChilds, String fileDate) {
        this.path = path;
        this.fileName = fileName;
        this.fileParent = fileParent;
        this.fileChilds = fileChilds;
        this.fileDate = fileDate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileParent() {
        return fileParent;
    }

    public void setFileParent(String fileParent) {
        this.fileParent = fileParent;
    }

    public List<String> getFileChilds() {
        return fileChilds;
    }

    public void setFileChilds(List<String> fileChilds) {
        this.fileChilds = fileChilds;
    }

    public String getFileDate() {
        return fileDate;
    }

    public void setFileDate(String fileDate) {
        this.fileDate = fileDate;
    }
}
