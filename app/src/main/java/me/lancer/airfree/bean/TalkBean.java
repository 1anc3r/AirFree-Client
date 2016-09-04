package me.lancer.airfree.bean;

public class TalkBean {

    private String type;

    private String id;

    private String content;

    public TalkBean(String type, String id, String content) {
        this.type = type;
        this.id = id;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
