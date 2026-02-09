package dao;

import DB.DatabaseConnection;
import models.Categorie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object pour la gestion des catégories
 */
public class CategorieDAO {

    /**
     * Ajouter une nouvelle catégorie
     */
    public boolean ajouterCategorie(Categorie categorie) {
        String sql = "INSERT INTO categories (nom_categorie, description) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, categorie.getNomCategorie());
            pstmt.setString(2, categorie.getDescription());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    categorie.setIdCategorie(generatedKeys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la catégorie: " + e.getMessage());
        }
        return false;
    }

    /**
     * Modifier une catégorie existante
     */
    public boolean modifierCategorie(Categorie categorie) {
        String sql = "UPDATE categories SET nom_categorie = ?, description = ? WHERE id_categorie = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, categorie.getNomCategorie());
            pstmt.setString(2, categorie.getDescription());
            pstmt.setInt(3, categorie.getIdCategorie());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la catégorie: " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprimer une catégorie
     */
    public boolean supprimerCategorie(int idCategorie) {
        // Vérifier d'abord s'il y a des livres dans cette catégorie
        if (getNombreLivresParCategorie(idCategorie) > 0) {
            System.err.println("Impossible de supprimer cette catégorie car elle contient des livres.");
            return false;
        }

        String sql = "DELETE FROM categories WHERE id_categorie = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCategorie);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la catégorie: " + e.getMessage());
            return false;
        }
    }

    /**
     * Récupérer une catégorie par ID
     */
    public Categorie getCategorieParId(int idCategorie) {
        String sql = "SELECT * FROM categories WHERE id_categorie = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCategorie);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extraireCategorie(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la catégorie: " + e.getMessage());
        }
        return null;
    }

    /**
     * Récupérer toutes les catégories
     */
    public List<Categorie> getAllCategories() {
        List<Categorie> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY nom_categorie";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(extraireCategorie(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des catégories: " + e.getMessage());
        }
        return categories;
    }

    /**
     * Obtenir le nombre de livres dans une catégorie
     */
    public int getNombreLivresParCategorie(int idCategorie) {
        String sql = "SELECT COUNT(*) as nb FROM livres WHERE id_categorie = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCategorie);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("nb");
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des livres: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Méthode utilitaire pour extraire une catégorie du ResultSet
     */
    private Categorie extraireCategorie(ResultSet rs) throws SQLException {
        Categorie categorie = new Categorie();
        categorie.setIdCategorie(rs.getInt("id_categorie"));
        categorie.setNomCategorie(rs.getString("nom_categorie"));
        categorie.setDescription(rs.getString("description"));
        return categorie;
    }
}