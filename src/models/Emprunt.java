package models;

import java.util.Date;

public class Emprunt {
    private int idEmprunt;
    private String isbn;
    private String cne;
    private Date dateEmprunt;
    private Date dateRetourPrevue;
    private Date dateRetourEffective;
    private double penalite;
    private String statut; // EN_COURS, RETOURNE, EN_RETARD

    // Informations supplémentaires (pour affichage)
    private String titreLivre;
    private String nomEtudiant;

    public Emprunt() {}

    public Emprunt(String isbn, String cne, Date dateEmprunt, Date dateRetourPrevue) {
        this.isbn = isbn;
        this.cne = cne;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourPrevue = dateRetourPrevue;
        this.statut = "EN_COURS";
        this.penalite = 0.0;
    }

    // Getters et Setters
    public int getIdEmprunt() { return idEmprunt; }
    public void setIdEmprunt(int idEmprunt) { this.idEmprunt = idEmprunt; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getCne() { return cne; }
    public void setCne(String cne) { this.cne = cne; }

    public Date getDateEmprunt() { return dateEmprunt; }
    public void setDateEmprunt(Date dateEmprunt) { this.dateEmprunt = dateEmprunt; }

    public Date getDateRetourPrevue() { return dateRetourPrevue; }
    public void setDateRetourPrevue(Date dateRetourPrevue) { this.dateRetourPrevue = dateRetourPrevue; }

    public Date getDateRetourEffective() { return dateRetourEffective; }
    public void setDateRetourEffective(Date dateRetourEffective) {
        this.dateRetourEffective = dateRetourEffective;
    }

    public double getPenalite() { return penalite; }
    public void setPenalite(double penalite) { this.penalite = penalite; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getTitreLivre() { return titreLivre; }
    public void setTitreLivre(String titreLivre) { this.titreLivre = titreLivre; }

    public String getNomEtudiant() { return nomEtudiant; }
    public void setNomEtudiant(String nomEtudiant) { this.nomEtudiant = nomEtudiant; }

    public long getJoursRetard() {
        if (dateRetourEffective != null) {
            long diff = dateRetourEffective.getTime() - dateRetourPrevue.getTime();
            return Math.max(0, diff / (1000 * 60 * 60 * 24));
        } else {
            long diff = new Date().getTime() - dateRetourPrevue.getTime();
            return Math.max(0, diff / (1000 * 60 * 60 * 24));
        }
    }

    public boolean estEnRetard() {
        return getJoursRetard() > 0 && dateRetourEffective == null;
    }
}