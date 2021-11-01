package com.vibe_viroma.cropnutrient.objets;

public class Photo {
    String name, lien, time, lieu="";
    double lat=0, lon=0;
    String key="";

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Photo(){

    }

    public Photo(String name, String lien, String time, String lieu, double lat, double lon) {
        this.name = name;
        this.lien = lien;
        this.time = time;
        this.lieu = lieu;
        this.lat = lat;
        this.lon = lon;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLien() {
        return lien;
    }

    public void setLien(String lien) {
        this.lien = lien;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
