package me.lancer.airfree.bean;

public class ComputerBean {

    private String filePath;

    private String fileName;

    private String fileParentPath;

    private ComputerBean fileParent;

    public ComputerBean(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public ComputerBean(String filePath, String fileName, String fileParentPath, ComputerBean fileParent) {
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

    public ComputerBean getFileParent() {
        return fileParent;
    }

    public void setFileParent(ComputerBean fileParent) {
        this.fileParent = fileParent;
    }
}
