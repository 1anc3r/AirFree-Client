package me.lancer.airfree.model;

public class LetterBean {

    private String filePath;

    private String fileName;

    private String fileParentPath;

    private LetterBean fileParent;

    public LetterBean(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public LetterBean(String filePath, String fileName, String fileParentPath, LetterBean fileParent) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileParentPath = fileParentPath;
        this.fileParent = fileParent;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileParentPath() {
        return fileParentPath;
    }

    public void setFileParentPath(String fileParentPath) {
        this.fileParentPath = fileParentPath;
    }

    public LetterBean getFileParent() {
        return fileParent;
    }

    public void setFileParent(LetterBean fileParent) {
        this.fileParent = fileParent;
    }
}
