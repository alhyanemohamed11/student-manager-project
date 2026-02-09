package dao;


import DB.DatabaseConnection;
import models.Etudiant;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object pour la gestion des étudiants
 */
public class EtudiantDAO {

    /**
     * Ajouter un nouvel étudiant
     */
    public boolean ajouterEtudiant(Etudiant etudiant) {
        String sql = "INSERT INTO etudiants (cne, nom, prenom, email, telephone, filiere) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, etudiant.getCne());
            pstmt.setString(2, etudiant.getNom());
            pstmt.setString(3, etudiant.getPrenom());
            pstmt.setString(4, etudiant.getEmail());
            pstmt.setString(5, etudiant.getTelephone());
            pstmt.setString(6, etudiant.getFiliere());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'étudiant: " + e.getMessage());
            return false;
        }
    }

    /**
     * Modifier un étudiant existant
     */
    public boolean modifierEtudiant(Etudiant etudiant) {
        String sql = "UPDATE etudiants SET nom = ?, prenom = ?, email = ?, " +
                "telephone = ?, filiere = ?, actif = ? WHERE cne = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, etudiant.getNom());
            pstmt.setString(2, etudiant.getPrenom());
            pstmt.setString(3, etudiant.getEmail());
            pstmt.setString(4, etudiant.getTelephone());
            pstmt.setString(5, etudiant.getFiliere());
            pstmt.setBoolean(6, etudiant.isActif());
            pstmt.setString(7, etudiant.getCne());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de l'étudiant: " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprimer un étudiant
     */
    public boolean supprimerEtudiant(String cne) {
        String sql = "DELETE FROM etudiants WHERE cne = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cne);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'étudiant: " + e.getMessage());
            return false;
        }
    }

    /**
     * Désactiver un étudiant (au lieu de le supprimer)
     */
    public boolean desactiverEtudiant(String cne) {
        String sql = "UPDATE etudiants SET actif = false WHERE cne = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cne);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la désactivation de l'étudiant: " + e.getMessage());
            return false;
        }
    }

    /**
     * Récupérer un étudiant par CNE
     */
    public Etudiant getEtudiantParCne(String cne) {
        String sql = "SELECT * FROM etudiants WHERE cne = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cne);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extraireEtudiant(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'étudiant: " + e.getMessage());
        }
        return null;
    }

    /**
     * Récupérer tous les étudiants
     */
    public List<Etudiant> getAllEtudiants() {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT * FROM etudiants ORDER BY nom, prenom";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                etudiants.add(extraireEtudiant(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des étudiants: " + e.getMessage());
        }
        return etudiants;
    }

    /**
     * Récupérer uniquement les étudiants actifs
     */
    public List<Etudiant> getEtudiantsActifs() {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT * FROM etudiants WHERE actif = true ORDER BY nom, prenom";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                etudiants.add(extraireEtudiant(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des étudiants actifs: " + e.getMessage());
        }
        return etudiants;
    }

    /**
     * Rechercher des étudiants
     */
    public List<Etudiant> rechercherEtudiants(String recherche) {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT * FROM etudiants WHERE nom LIKE ? OR prenom LIKE ? " +
                "OR cne LIKE ? OR email LIKE ? OR filiere LIKE ? " +
                "ORDER BY nom, prenom";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String pattern = "%" + recherche + "%";
            for (int i = 1; i <= 5; i++) {
                pstmt.setString(i, pattern);
            }

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                etudiants.add(extraireEtudiant(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche d'étudiants: " + e.getMessage());
        }
        return etudiants;
    }

    /**
     * Récupérer les étudiants par filière
     */
    public List<Etudiant> getEtudiantsParFiliere(String filiere) {
        List<Etudiant> etudiants = new ArrayList<>();
        String sql = "SELECT * FROM etudiants WHERE filiere = ? AND actif = true " +
                "ORDER BY nom, prenom";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, filiere);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                etudiants.add(extraireEtudiant(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des étudiants par filière: " + e.getMessage());
        }
        return etudiants;
    }

    /**
     * Vérifier si un étudiant peut emprunter (pas trop d'emprunts en cours)
     */
    public boolean peutEmprunter(String cne, int maxEmprunts) {
        String sql = "SELECT COUNT(*) as nb FROM emprunts " +
                "WHERE cne = ? AND statut = 'EN_COURS'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cne);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int nbEmprunts = rs.getInt("nb");
                return nbEmprunts < maxEmprunts;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification des emprunts: " + e.getMessage());
        }
        return false;
    }

    /**
     * Obtenir le nombre d'emprunts en cours pour un étudiant
     */
    public int getNombreEmpruntsEnCours(String cne) {
        String sql = "SELECT COUNT(*) as nb FROM emprunts " +
                "WHERE cne = ? AND statut = 'EN_COURS'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cne);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("nb");
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des emprunts: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Méthode utilitaire pour extraire un étudiant du ResultSet
     */
    private Etudiant extraireEtudiant(ResultSet rs) throws SQLException {
        Etudiant etudiant = new Etudiant();
        etudiant.setCne(rs.getString("cne"));
        etudiant.setNom(rs.getString("nom"));
        etudiant.setPrenom(rs.getString("prenom"));
        etudiant.setEmail(rs.getString("email"));
        etudiant.setTelephone(rs.getString("telephone"));
        etudiant.setFiliere(rs.getString("filiere"));
        etudiant.setDateInscription(rs.getTimestamp("date_inscription"));
        etudiant.setActif(rs.getBoolean("actif"));
        return etudiant;
    }
}