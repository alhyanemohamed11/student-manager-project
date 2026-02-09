package dao;


import DB.DatabaseConnection;
import models.Livre;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivreDAO {

    // Ajouter un livre
    public boolean ajouterLivre(Livre livre) {
        String sql = "INSERT INTO livres (isbn, titre, auteur, id_categorie, " +
                "annee_publication, nombre_exemplaires, exemplaires_disponibles) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, livre.getIsbn());
            pstmt.setString(2, livre.getTitre());
            pstmt.setString(3, livre.getAuteur());
            pstmt.setInt(4, livre.getIdCategorie());
            pstmt.setInt(5, livre.getAnneePublication());
            pstmt.setInt(6, livre.getNombreExemplaires());
            pstmt.setInt(7, livre.getExemplairesDisponibles());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du livre: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Modifier un livre
    public boolean modifierLivre(Livre livre) {
        String sql = "UPDATE livres SET titre = ?, auteur = ?, id_categorie = ?, " +
                "annee_publication = ?, nombre_exemplaires = ?, exemplaires_disponibles = ? " +
                "WHERE isbn = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, livre.getTitre());
            pstmt.setString(2, livre.getAuteur());
            pstmt.setInt(3, livre.getIdCategorie());
            pstmt.setInt(4, livre.getAnneePublication());
            pstmt.setInt(5, livre.getNombreExemplaires());
            pstmt.setInt(6, livre.getExemplairesDisponibles());
            pstmt.setString(7, livre.getIsbn());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du livre: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Supprimer un livre
    public boolean supprimerLivre(String isbn) {
        String sql = "DELETE FROM livres WHERE isbn = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, isbn);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du livre: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Récupérer un livre par ISBN
    public Livre getLivreParIsbn(String isbn) {
        String sql = "SELECT l.*, c.nom_categorie FROM livres l " +
                "LEFT JOIN categories c ON l.id_categorie = c.id_categorie " +
                "WHERE l.isbn = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extraireLivre(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du livre: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Récupérer tous les livres
    public List<Livre> getAllLivres() {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT l.*, c.nom_categorie FROM livres l " +
                "LEFT JOIN categories c ON l.id_categorie = c.id_categorie " +
                "ORDER BY l.titre";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                livres.add(extraireLivre(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des livres: " + e.getMessage());
            e.printStackTrace();
        }
        return livres;
    }

    // Rechercher des livres
    public List<Livre> rechercherLivres(String recherche) {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT l.*, c.nom_categorie FROM livres l " +
                "LEFT JOIN categories c ON l.id_categorie = c.id_categorie " +
                "WHERE l.titre LIKE ? OR l.auteur LIKE ? OR l.isbn LIKE ? " +
                "ORDER BY l.titre";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String pattern = "%" + recherche + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                livres.add(extraireLivre(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des livres: " + e.getMessage());
            e.printStackTrace();
        }
        return livres;
    }

    // Récupérer les livres par catégorie
    public List<Livre> getLivresParCategorie(int idCategorie) {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT l.*, c.nom_categorie FROM livres l " +
                "LEFT JOIN categories c ON l.id_categorie = c.id_categorie " +
                "WHERE l.id_categorie = ? ORDER BY l.titre";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCategorie);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                livres.add(extraireLivre(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des livres par catégorie: " + e.getMessage());
            e.printStackTrace();
        }
        return livres;
    }

    // Mettre à jour la disponibilité d'un livre
    public boolean mettreAJourDisponibilite(String isbn, int changement) {
        String sql = "UPDATE livres SET exemplaires_disponibles = exemplaires_disponibles + ? " +
                "WHERE isbn = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, changement);
            pstmt.setString(2, isbn);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la disponibilité: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Méthode utilitaire pour extraire un livre du ResultSet
    private Livre extraireLivre(ResultSet rs) throws SQLException {
        Livre livre = new Livre();
        livre.setIsbn(rs.getString("isbn"));
        livre.setTitre(rs.getString("titre"));
        livre.setAuteur(rs.getString("auteur"));
        livre.setIdCategorie(rs.getInt("id_categorie"));
        livre.setNomCategorie(rs.getString("nom_categorie"));
        livre.setAnneePublication(rs.getInt("annee_publication"));
        livre.setNombreExemplaires(rs.getInt("nombre_exemplaires"));
        livre.setExemplairesDisponibles(rs.getInt("exemplaires_disponibles"));
        livre.setDateAjout(rs.getTimestamp("date_ajout"));
        return livre;
    }
}