package com.vibe_viroma.cropnutrient.objets;

public class Problem {
    String title, desc, link_media, link_audio, from, key, time;

    public Problem() {
    }

    public Problem (String title, String desc, String link_media, String link_audio, String from, String key, String time) {
        this.title = title;
        this.desc = desc;
        this.link_media = link_media;
        this.link_audio = link_audio;
        this.from = from;
        this.key = key;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLink_media() {
        return link_media;
    }

    public void setLink_media(String link_media) {
        this.link_media = link_media;
    }

    public String getLink_audio() {
        return link_audio;
    }

    public void setLink_audio(String link_audio) {
        this.link_audio = link_audio;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
