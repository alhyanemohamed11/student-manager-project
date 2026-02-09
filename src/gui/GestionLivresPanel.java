package gui;

import dao.LivreDAO;
import dao.CategorieDAO;
import models.Livre;
import models.Categorie;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel pour la gestion des livres
 */
public class GestionLivresPanel extends JPanel {

    private LivreDAO livreDAO;
    private CategorieDAO categorieDAO;
    private JTable tableLivres;
    private DefaultTableModel tableModel;
    private JTextField rechercheField;
    private JComboBox<Categorie> categorieCombo;

    public GestionLivresPanel() {
        livreDAO = new LivreDAO();
        categorieDAO = new CategorieDAO();
        initializePanel();
        chargerLivres();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel supérieur avec titre et recherche
        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Gestion des Livres");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.WEST);

        // Panel de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rechercheField = new JTextField(20);
        JButton btnRechercher = new JButton("Rechercher");
        JButton btnTous = new JButton("Tous les livres");

        btnRechercher.addActionListener(e -> rechercherLivres());
        btnTous.addActionListener(e -> chargerLivres());

        searchPanel.add(new JLabel("Recherche:"));
        searchPanel.add(rechercheField);
        searchPanel.add(btnRechercher);
        searchPanel.add(btnTous);

        topPanel.add(searchPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Table des livres
        String[] colonnes = {"ISBN", "Titre", "Auteur", "Catégorie", "Année",
                "Total Ex.", "Disponibles"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableLivres = new JTable(tableModel);
        tableLivres.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableLivres.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(tableLivres);
        add(scrollPane, BorderLayout.CENTER);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnAjouter = new JButton("Ajouter un livre");
        JButton btnModifier = new JButton("Modifier");
        JButton btnSupprimer = new JButton("Supprimer");
        JButton btnDetails = new JButton("Détails");

        btnAjouter.setBackground(new Color(46, 204, 113));
        btnAjouter.setForeground(Color.WHITE);
        btnModifier.setBackground(new Color(52, 152, 219));
        btnModifier.setForeground(Color.WHITE);
        btnSupprimer.setBackground(new Color(231, 76, 60));
        btnSupprimer.setForeground(Color.WHITE);
        btnDetails.setBackground(new Color(149, 165, 166));
        btnDetails.setForeground(Color.WHITE);

        btnAjouter.addActionListener(e -> ajouterLivre());
        btnModifier.addActionListener(e -> modifierLivre());
        btnSupprimer.addActionListener(e -> supprimerLivre());
        btnDetails.addActionListener(e -> afficherDetails());

        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(btnDetails);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void chargerLivres() {
        tableModel.setRowCount(0);
        List<Livre> livres = livreDAO.getAllLivres();

        for (Livre livre : livres) {
            Object[] row = {
                    livre.getIsbn(),
                    livre.getTitre(),
                    livre.getAuteur(),
                    livre.getNomCategorie(),
                    livre.getAnneePublication(),
                    livre.getNombreExemplaires(),
                    livre.getExemplairesDisponibles()
            };
            tableModel.addRow(row);
        }
    }

    private void rechercherLivres() {
        String recherche = rechercheField.getText().trim();
        if (recherche.isEmpty()) {
            chargerLivres();
            return;
        }

        tableModel.setRowCount(0);
        List<Livre> livres = livreDAO.rechercherLivres(recherche);

        for (Livre livre : livres) {
            Object[] row = {
                    livre.getIsbn(),
                    livre.getTitre(),
                    livre.getAuteur(),
                    livre.getNomCategorie(),
                    livre.getAnneePublication(),
                    livre.getNombreExemplaires(),
                    livre.getExemplairesDisponibles()
            };
            tableModel.addRow(row);
        }

        if (livres.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Aucun livre trouvé pour cette recherche.",
                    "Résultat",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void ajouterLivre() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Ajouter un livre", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Champs de saisie
        JTextField isbnField = new JTextField(20);
        JTextField titreField = new JTextField(20);
        JTextField auteurField = new JTextField(20);
        JComboBox<Categorie> categorieCombo = new JComboBox<>();
        JSpinner anneeSpinner = new JSpinner(new SpinnerNumberModel(2025, 1900, 2100, 1));
        JSpinner exemplairesSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

        // Charger les catégories
        List<Categorie> categories = categorieDAO.getAllCategories();
        for (Categorie cat : categories) {
            categorieCombo.addItem(cat);
        }

        // Ajouter les composants
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        panel.add(isbnField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Titre:"), gbc);
        gbc.gridx = 1;
        panel.add(titreField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Auteur:"), gbc);
        gbc.gridx = 1;
        panel.add(auteurField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Catégorie:"), gbc);
        gbc.gridx = 1;
        panel.add(categorieCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Année:"), gbc);
        gbc.gridx = 1;
        panel.add(anneeSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Nombre d'exemplaires:"), gbc);
        gbc.gridx = 1;
        panel.add(exemplairesSpinner, gbc);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSauvegarder = new JButton("Sauvegarder");
        JButton btnAnnuler = new JButton("Annuler");

        btnSauvegarder.addActionListener(e -> {
            if (validerChamps(isbnField, titreField, auteurField)) {
                Livre livre = new Livre();
                livre.setIsbn(isbnField.getText().trim());
                livre.setTitre(titreField.getText().trim());
                livre.setAuteur(auteurField.getText().trim());
                livre.setIdCategorie(((Categorie) categorieCombo.getSelectedItem()).getIdCategorie());
                livre.setAnneePublication((Integer) anneeSpinner.getValue());
                livre.setNombreExemplaires((Integer) exemplairesSpinner.getValue());
                livre.setExemplairesDisponibles((Integer) exemplairesSpinner.getValue());

                if (livreDAO.ajouterLivre(livre)) {
                    JOptionPane.showMessageDialog(dialog,
                            "Livre ajouté avec succès!",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                    chargerLivres();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Erreur lors de l'ajout du livre.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnAnnuler.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnSauvegarder);
        buttonPanel.add(btnAnnuler);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void modifierLivre() {
        int selectedRow = tableLivres.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un livre à modifier.",
                    "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String isbn = (String) tableModel.getValueAt(selectedRow, 0);
        Livre livre = livreDAO.getLivreParIsbn(isbn);

        if (livre == null) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la récupération du livre.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Dialog similaire à ajouterLivre mais avec les champs pré-remplis
        // ... (code similaire avec setValue sur les champs)

        JOptionPane.showMessageDialog(this, "Fonctionnalité en cours de développement");
    }

    private void supprimerLivre() {
        int selectedRow = tableLivres.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un livre à supprimer.",
                    "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String isbn = (String) tableModel.getValueAt(selectedRow, 0);
        String titre = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer le livre:\n" + titre + " ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (livreDAO.supprimerLivre(isbn)) {
                JOptionPane.showMessageDialog(this,
                        "Livre supprimé avec succès!",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                chargerLivres();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la suppression.\nLe livre est peut-être emprunté.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void afficherDetails() {
        int selectedRow = tableLivres.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un livre.",
                    "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String isbn = (String) tableModel.getValueAt(selectedRow, 0);
        Livre livre = livreDAO.getLivreParIsbn(isbn);

        if (livre != null) {
            String details = String.format(
                    "ISBN: %s\nTitre: %s\nAuteur: %s\nCatégorie: %s\n" +
                            "Année: %d\nTotal exemplaires: %d\nDisponibles: %d\nEmpruntés: %d",
                    livre.getIsbn(), livre.getTitre(), livre.getAuteur(),
                    livre.getNomCategorie(), livre.getAnneePublication(),
                    livre.getNombreExemplaires(), livre.getExemplairesDisponibles(),
                    livre.getNombreEmpruntes()
            );

            JOptionPane.showMessageDialog(this, details,
                    "Détails du livre", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean validerChamps(JTextField isbn, JTextField titre, JTextField auteur) {
        if (isbn.getText().trim().isEmpty() ||
                titre.getText().trim().isEmpty() ||
                auteur.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Tous les champs sont obligatoires!",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}