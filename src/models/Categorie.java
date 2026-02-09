package models;

/**
 * Classe représentant une catégorie de livres
 */
public class Categorie {
    private int idCategorie;
    private String nomCategorie;
    private String description;

    // Constructeur par défaut
    public Categorie() {}

    // Constructeur avec nom
    public Categorie(String nomCategorie) {
        this.nomCategorie = nomCategorie;
    }

    // Constructeur complet
    public Categorie(int idCategorie, String nomCategorie, String description) {
        this.idCategorie = idCategorie;
        this.nomCategorie = nomCategorie;
        this.description = description;
    }

    // Getters et Setters
    public int getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(int idCategorie) {
        this.idCategorie = idCategorie;
    }

    public String getNomCategorie() {
        return nomCategorie;
    }

    public void setNomCategorie(String nomCategorie) {
        this.nomCategorie = nomCategorie;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Méthodes utilitaires
    @Override
    public String toString() {
        return nomCategorie;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Categorie categorie = (Categorie) obj;
        return idCategorie == categorie.idCategorie;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idCategorie);
    }
}
