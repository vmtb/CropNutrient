package com.vibe_viroma.cropnutrient.adapters;

import com.vibe_viroma.cropnutrient.objets.Ferme;

public class champs {
    Ferme ferme;
    String ferme_id;
    String user_id;

    public champs(Ferme ferme, String ferme_id, String user_id) {
        this.ferme = ferme;
        this.ferme_id = ferme_id;
        this.user_id = user_id;
    }

    public Ferme getFerme() {
        return ferme;
    }

    public void setFerme(Ferme ferme) {
        this.ferme = ferme;
    }

    public String getFerme_id() {
        return ferme_id;
    }

    public void setFerme_id(String ferme_id) {
        this.ferme_id = ferme_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
