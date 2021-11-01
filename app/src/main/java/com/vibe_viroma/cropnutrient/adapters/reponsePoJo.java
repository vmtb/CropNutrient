package com.vibe_viroma.cropnutrient.adapters;

import com.vibe_viroma.cropnutrient.objets.Response;
import com.vibe_viroma.cropnutrient.objets.User;

public class reponsePoJo {

    Response response;
    User user;
    String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public reponsePoJo(Response response, User user) {
        this.response = response;
        this.user = user;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
