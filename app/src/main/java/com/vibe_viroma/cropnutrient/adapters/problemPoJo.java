package com.vibe_viroma.cropnutrient.adapters;

import com.vibe_viroma.cropnutrient.objets.Problem;
import com.vibe_viroma.cropnutrient.objets.User;

public class problemPoJo {
    Problem problem;
    User user;
    String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public problemPoJo(Problem problem, User user) {
        this.problem = problem;
        this.user = user;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }
}
