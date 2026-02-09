package models;


import java.util.Date;

/**
 * Classe représentant un livre dans la bibliothèque
 */
public class Livre {
    private String isbn;
    private String titre;
    private String auteur;
    private int idCategorie;
    private String nomCategorie;
    private int anneePublication;
    private int nombreExemplaires;
    private int exemplairesDisponibles;
    private Date dateAjout;

    // Constructeur par défaut
    public Livre() {}

    // Constructeur avec paramètres principaux
    public Livre(String isbn, String titre, String auteur, int idCategorie) {
        this.isbn = isbn;
        this.titre = titre;
        this.auteur = auteur;
        this.idCategorie = idCategorie;
    }

    // Constructeur complet
    public Livre(String isbn, String titre, String auteur, int idCategorie,
                 int anneePublication, int nombreExemplaires) {
        this.isbn = isbn;
        this.titre = titre;
        this.auteur = auteur;
        this.idCategorie = idCategorie;
        this.anneePublication = anneePublication;
        this.nombreExemplaires = nombreExemplaires;
        this.exemplairesDisponibles = nombreExemplaires;
    }

    // Getters et Setters
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

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

    public int getAnneePublication() {
        return anneePublication;
    }

    public void setAnneePublication(int anneePublication) {
        this.anneePublication = anneePublication;
    }

    public int getNombreExemplaires() {
        return nombreExemplaires;
    }

    public void setNombreExemplaires(int nombreExemplaires) {
        this.nombreExemplaires = nombreExemplaires;
    }

    public int getExemplairesDisponibles() {
        return exemplairesDisponibles;
    }

    public void setExemplairesDisponibles(int exemplairesDisponibles) {
        this.exemplairesDisponibles = exemplairesDisponibles;
    }

    public Date getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(Date dateAjout) {
        this.dateAjout = dateAjout;
    }

    // Méthodes utilitaires
    public boolean estDisponible() {
        return exemplairesDisponibles > 0;
    }

    public int getNombreEmpruntes() {
        return nombreExemplaires - exemplairesDisponibles;
    }

    @Override
    public String toString() {
        return titre + " - " + auteur + " (" + isbn + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Livre livre = (Livre) obj;
        return isbn != null && isbn.equals(livre.isbn);
    }

    @Override
    public int hashCode() {
        return isbn != null ? isbn.hashCode() : 0;
    }
}