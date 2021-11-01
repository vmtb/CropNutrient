package com.vibe_viroma.cropnutrient.objets;

public class Response {
    String reponse, problem_key, reponse_key, from, time, link_media;

    public Response() {
    }

    public String getLink_media() {
        return link_media;
    }

    public void setLink_media(String link_media) {
        this.link_media = link_media;
    }

    public Response(String reponse, String problem_key, String reponse_key, String from, String time, String link_media) {
        this.reponse = reponse;
        this.problem_key = problem_key;
        this.reponse_key = reponse_key;
        this.from = from;
        this.time = time;
        this.link_media = link_media;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public String getProblem_key() {
        return problem_key;
    }

    public void setProblem_key(String problem_key) {
        this.problem_key = problem_key;
    }

    public String getReponse_key() {
        return reponse_key;
    }

    public void setReponse_key(String reponse_key) {
        this.reponse_key = reponse_key;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
