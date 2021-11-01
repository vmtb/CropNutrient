package com.vibe_viroma.cropnutrient.objets;

public class Ferme {
    String producteur, superficie, ferme_name, zone, region, unite="";

    public String getProducteur() {
        return producteur;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public void setProducteur(String producteur) {
        this.producteur = producteur;
    }

    public String getSuperficie() {
        return superficie;
    }

    public void setSuperficie(String superficie) {
        this.superficie = superficie;
    }

    public String getFerme_name() {
        return ferme_name;
    }

    public void setFerme_name(String ferme_name) {
        this.ferme_name = ferme_name;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Ferme(String producteur, String superficie, String ferme_name, String zone, String region, String unite) {
        this.producteur = producteur;
        this.superficie = superficie;
        this.ferme_name = ferme_name;
        this.zone = zone;
        this.region = region;
        this.unite = unite;
    }

    public Ferme() {
    }
}
