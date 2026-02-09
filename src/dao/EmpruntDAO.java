package dao;

import DB.DatabaseConnection;
import models.Emprunt;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object pour la gestion des emprunts
 */
public class EmpruntDAO {

    private static final int DUREE_EMPRUNT_JOURS = 14; // Durée standard d'un emprunt
    private static final double PENALITE_PAR_JOUR = 2.0; // Pénalité par jour de retard

    /**
     * Créer un nouvel emprunt
     */
    public boolean creerEmprunt(Emprunt emprunt) {
        String sql = "INSERT INTO emprunts (isbn, cne, date_emprunt, date_retour_prevue, statut) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, emprunt.getIsbn());
            pstmt.setString(2, emprunt.getCne());
            pstmt.setTimestamp(3, new Timestamp(emprunt.getDateEmprunt().getTime()));
            pstmt.setTimestamp(4, new Timestamp(emprunt.getDateRetourPrevue().getTime()));
            pstmt.setString(5, "EN_COURS");

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    emprunt.setIdEmprunt(generatedKeys.getInt(1));
                }

                // Mettre à jour la disponibilité du livre
                new LivreDAO().mettreAJourDisponibilite(emprunt.getIsbn(), -1);

                return true;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de l'emprunt: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retourner un livre emprunté
     */
    public boolean retournerLivre(int idEmprunt) {
        // Récupérer l'emprunt
        Emprunt emprunt = getEmpruntParId(idEmprunt);
        if (emprunt == null || !emprunt.getStatut().equals("EN_COURS")) {
            return false;
        }

        Date dateRetour = new Date();
        long joursRetard = emprunt.getJoursRetard();
        double penalite = joursRetard > 0 ? joursRetard * PENALITE_PAR_JOUR : 0.0;
        String statut = joursRetard > 0 ? "RETOURNE" : "RETOURNE";

        String sql = "UPDATE emprunts SET date_retour_effective = ?, penalite = ?, statut = ? " +
                "WHERE id_emprunt = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, new Timestamp(dateRetour.getTime()));
            pstmt.setDouble(2, penalite);
            pstmt.setString(3, statut);
            pstmt.setInt(4, idEmprunt);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // Mettre à jour la disponibilité du livre
                new LivreDAO().mettreAJourDisponibilite(emprunt.getIsbn(), 1);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors du retour du livre: " + e.getMessage());
        }
        return false;
    }

    /**
     * Récupérer un emprunt par ID
     */
    public Emprunt getEmpruntParId(int idEmprunt) {
        String sql = "SELECT e.*, l.titre as titre_livre, " +
                "CONCAT(et.prenom, ' ', et.nom) as nom_etudiant " +
                "FROM emprunts e " +
                "JOIN livres l ON e.isbn = l.isbn " +
                "JOIN etudiants et ON e.cne = et.cne " +
                "WHERE e.id_emprunt = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idEmprunt);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extraireEmprunt(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'emprunt: " + e.getMessage());
        }
        return null;
    }

    /**
     * Récupérer tous les emprunts
     */
    public List<Emprunt> getAllEmprunts() {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT e.*, l.titre as titre_livre, " +
                "CONCAT(et.prenom, ' ', et.nom) as nom_etudiant " +
                "FROM emprunts e " +
                "JOIN livres l ON e.isbn = l.isbn " +
                "JOIN etudiants et ON e.cne = et.cne " +
                "ORDER BY e.date_emprunt DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                emprunts.add(extraireEmprunt(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des emprunts: " + e.getMessage());
        }
        return emprunts;
    }

    /**
     * Récupérer les emprunts en cours
     */
    public List<Emprunt> getEmpruntsEnCours() {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT e.*, l.titre as titre_livre, " +
                "CONCAT(et.prenom, ' ', et.nom) as nom_etudiant " +
                "FROM emprunts e " +
                "JOIN livres l ON e.isbn = l.isbn " +
                "JOIN etudiants et ON e.cne = et.cne " +
                "WHERE e.statut = 'EN_COURS' " +
                "ORDER BY e.date_retour_prevue";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                emprunts.add(extraireEmprunt(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des emprunts en cours: " + e.getMessage());
        }
        return emprunts;
    }

    /**
     * Récupérer les emprunts en retard
     */
    public List<Emprunt> getEmpruntsEnRetard() {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT e.*, l.titre as titre_livre, " +
                "CONCAT(et.prenom, ' ', et.nom) as nom_etudiant " +
                "FROM emprunts e " +
                "JOIN livres l ON e.isbn = l.isbn " +
                "JOIN etudiants et ON e.cne = et.cne " +
                "WHERE e.statut = 'EN_COURS' AND e.date_retour_prevue < CURDATE() " +
                "ORDER BY e.date_retour_prevue";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                emprunts.add(extraireEmprunt(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des emprunts en retard: " + e.getMessage());
        }
        return emprunts;
    }

    /**
     * Récupérer les emprunts d'un étudiant
     */
    public List<Emprunt> getEmpruntsParEtudiant(String cne) {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT e.*, l.titre as titre_livre, " +
                "CONCAT(et.prenom, ' ', et.nom) as nom_etudiant " +
                "FROM emprunts e " +
                "JOIN livres l ON e.isbn = l.isbn " +
                "JOIN etudiants et ON e.cne = et.cne " +
                "WHERE e.cne = ? " +
                "ORDER BY e.date_emprunt DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cne);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                emprunts.add(extraireEmprunt(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des emprunts de l'étudiant: " + e.getMessage());
        }
        return emprunts;
    }

    /**
     * Récupérer les emprunts d'un livre
     */
    public List<Emprunt> getEmpruntsParLivre(String isbn) {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT e.*, l.titre as titre_livre, " +
                "CONCAT(et.prenom, ' ', et.nom) as nom_etudiant " +
                "FROM emprunts e " +
                "JOIN livres l ON e.isbn = l.isbn " +
                "JOIN etudiants et ON e.cne = et.cne " +
                "WHERE e.isbn = ? " +
                "ORDER BY e.date_emprunt DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                emprunts.add(extraireEmprunt(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des emprunts du livre: " + e.getMessage());
        }
        return emprunts;
    }

    /**
     * Mettre à jour le statut des emprunts en retard
     */
    public void mettreAJourStatutsRetard() {
        String sql = "UPDATE emprunts SET statut = 'EN_RETARD' " +
                "WHERE statut = 'EN_COURS' AND date_retour_prevue < CURDATE()";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour des statuts: " + e.getMessage());
        }
    }

    /**
     * Obtenir les statistiques des emprunts
     */
    public EmpruntStatistiques getStatistiques() {
        EmpruntStatistiques stats = new EmpruntStatistiques();

        String sql = "SELECT " +
                "(SELECT COUNT(*) FROM emprunts WHERE statut = 'EN_COURS') as en_cours, " +
                "(SELECT COUNT(*) FROM emprunts WHERE statut = 'EN_COURS' AND date_retour_prevue < CURDATE()) as en_retard, " +
                "(SELECT COUNT(*) FROM emprunts WHERE statut = 'RETOURNE') as retournes, " +
                "(SELECT SUM(penalite) FROM emprunts) as total_penalites";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                stats.empruntsEnCours = rs.getInt("en_cours");
                stats.empruntsEnRetard = rs.getInt("en_retard");
                stats.empruntsRetournes = rs.getInt("retournes");
                stats.totalPenalites = rs.getDouble("total_penalites");
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des statistiques: " + e.getMessage());
        }

        return stats;
    }

    /**
     * Méthode utilitaire pour extraire un emprunt du ResultSet
     */
    private Emprunt extraireEmprunt(ResultSet rs) throws SQLException {
        Emprunt emprunt = new Emprunt();
        emprunt.setIdEmprunt(rs.getInt("id_emprunt"));
        emprunt.setIsbn(rs.getString("isbn"));
        emprunt.setCne(rs.getString("cne"));
        emprunt.setDateEmprunt(rs.getTimestamp("date_emprunt"));
        emprunt.setDateRetourPrevue(rs.getTimestamp("date_retour_prevue"));

        Timestamp dateRetourEffective = rs.getTimestamp("date_retour_effective");
        if (dateRetourEffective != null) {
            emprunt.setDateRetourEffective(dateRetourEffective);
        }

        emprunt.setPenalite(rs.getDouble("penalite"));
        emprunt.setStatut(rs.getString("statut"));
        emprunt.setTitreLivre(rs.getString("titre_livre"));
        emprunt.setNomEtudiant(rs.getString("nom_etudiant"));

        return emprunt;
    }

    /**
     * Classe interne pour les statistiques
     */
    public static class EmpruntStatistiques {
        public int empruntsEnCours;
        public int empruntsEnRetard;
        public int empruntsRetournes;
        public double totalPenalites;
    }
}