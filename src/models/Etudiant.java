package models;

import java.util.Date;

public class Etudiant {
    private String cne;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String filiere;
    private Date dateInscription;
    private boolean actif;

    public Etudiant() {}

    public Etudiant(String cne, String nom, String prenom, String email) {
        this.cne = cne;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.actif = true;
    }

    // Getters et Setters
    public String getCne() { return cne; }
    public void setCne(String cne) { this.cne = cne; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getFiliere() { return filiere; }
    public void setFiliere(String filiere) { this.filiere = filiere; }

    public Date getDateInscription() { return dateInscription; }
    public void setDateInscription(Date dateInscription) { this.dateInscription = dateInscription; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public String getNomComplet() {
        return prenom + " " + nom;
    }
}