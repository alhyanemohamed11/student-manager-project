package gui;

import dao.EtudiantDAO;
import models.Etudiant;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel pour la gestion des étudiants
 */
public class GestionEtudiantsPanel extends JPanel {

    private EtudiantDAO etudiantDAO;
    private JTable tableEtudiants;
    private DefaultTableModel tableModel;
    private JTextField rechercheField;
    private JCheckBox actifCheckBox;

    public GestionEtudiantsPanel() {
        etudiantDAO = new EtudiantDAO();
        initializePanel();
        chargerEtudiants();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel supérieur
        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Gestion des Étudiants");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.WEST);

        // Panel de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rechercheField = new JTextField(20);
        actifCheckBox = new JCheckBox("Actifs seulement", true);
        JButton btnRechercher = new JButton("Rechercher");
        JButton btnTous = new JButton("Tous");

        btnRechercher.addActionListener(e -> rechercherEtudiants());
        btnTous.addActionListener(e -> chargerEtudiants());
        actifCheckBox.addActionListener(e -> chargerEtudiants());

        searchPanel.add(new JLabel("Recherche:"));
        searchPanel.add(rechercheField);
        searchPanel.add(actifCheckBox);
        searchPanel.add(btnRechercher);
        searchPanel.add(btnTous);

        topPanel.add(searchPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Table des étudiants
        String[] colonnes = {"CNE", "Nom", "Prénom", "Email", "Téléphone",
                "Filière", "Actif", "Emprunts"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableEtudiants = new JTable(tableModel);
        tableEtudiants.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tableEtudiants);
        add(scrollPane, BorderLayout.CENTER);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnAjouter = new JButton("Ajouter un étudiant");
        JButton btnModifier = new JButton("Modifier");
        JButton btnDesactiver = new JButton("Désactiver");
        JButton btnHistorique = new JButton("Historique");

        btnAjouter.setBackground(new Color(46, 204, 113));
        btnAjouter.setForeground(Color.WHITE);
        btnModifier.setBackground(new Color(52, 152, 219));
        btnModifier.setForeground(Color.WHITE);
        btnDesactiver.setBackground(new Color(231, 76, 60));
        btnDesactiver.setForeground(Color.WHITE);
        btnHistorique.setBackground(new Color(149, 165, 166));
        btnHistorique.setForeground(Color.WHITE);

        btnAjouter.addActionListener(e -> ajouterEtudiant());
        btnModifier.addActionListener(e -> modifierEtudiant());
        btnDesactiver.addActionListener(e -> desactiverEtudiant());
        btnHistorique.addActionListener(e -> afficherHistorique());

        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnDesactiver);
        buttonPanel.add(btnHistorique);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void chargerEtudiants() {
        tableModel.setRowCount(0);
        List<Etudiant> etudiants;

        if (actifCheckBox.isSelected()) {
            etudiants = etudiantDAO.getEtudiantsActifs();
        } else {
            etudiants = etudiantDAO.getAllEtudiants();
        }

        for (Etudiant etudiant : etudiants) {
            int nbEmprunts = etudiantDAO.getNombreEmpruntsEnCours(etudiant.getCne());
            Object[] row = {
                    etudiant.getCne(),
                    etudiant.getNom(),
                    etudiant.getPrenom(),
                    etudiant.getEmail(),
                    etudiant.getTelephone(),
                    etudiant.getFiliere(),
                    etudiant.isActif() ? "Oui" : "Non",
                    nbEmprunts
            };
            tableModel.addRow(row);
        }
    }

    private void rechercherEtudiants() {
        String recherche = rechercheField.getText().trim();
        if (recherche.isEmpty()) {
            chargerEtudiants();
            return;
        }

        tableModel.setRowCount(0);
        List<Etudiant> etudiants = etudiantDAO.rechercherEtudiants(recherche);

        for (Etudiant etudiant : etudiants) {
            if (actifCheckBox.isSelected() && !etudiant.isActif()) {
                continue;
            }

            int nbEmprunts = etudiantDAO.getNombreEmpruntsEnCours(etudiant.getCne());
            Object[] row = {
                    etudiant.getCne(),
                    etudiant.getNom(),
                    etudiant.getPrenom(),
                    etudiant.getEmail(),
                    etudiant.getTelephone(),
                    etudiant.getFiliere(),
                    etudiant.isActif() ? "Oui" : "Non",
                    nbEmprunts
            };
            tableModel.addRow(row);
        }
    }

    private void ajouterEtudiant() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Ajouter un étudiant", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Champs de saisie
        JTextField cneField = new JTextField(20);
        JTextField nomField = new JTextField(20);
        JTextField prenomField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField telephoneField = new JTextField(20);
        JTextField filiereField = new JTextField(20);

        // Ajouter les composants
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("CNE:"), gbc);
        gbc.gridx = 1;
        panel.add(cneField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Nom:"), gbc);
        gbc.gridx = 1;
        panel.add(nomField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Prénom:"), gbc);
        gbc.gridx = 1;
        panel.add(prenomField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Téléphone:"), gbc);
        gbc.gridx = 1;
        panel.add(telephoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Filière:"), gbc);
        gbc.gridx = 1;
        panel.add(filiereField, gbc);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSauvegarder = new JButton("Sauvegarder");
        JButton btnAnnuler = new JButton("Annuler");

        btnSauvegarder.addActionListener(e -> {
            if (validerChamps(cneField, nomField, prenomField, emailField)) {
                Etudiant etudiant = new Etudiant();
                etudiant.setCne(cneField.getText().trim());
                etudiant.setNom(nomField.getText().trim());
                etudiant.setPrenom(prenomField.getText().trim());
                etudiant.setEmail(emailField.getText().trim());
                etudiant.setTelephone(telephoneField.getText().trim());
                etudiant.setFiliere(filiereField.getText().trim());
                etudiant.setActif(true);

                if (etudiantDAO.ajouterEtudiant(etudiant)) {
                    JOptionPane.showMessageDialog(dialog,
                            "Étudiant ajouté avec succès!",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                    chargerEtudiants();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Erreur lors de l'ajout de l'étudiant.\nLe CNE existe peut-être déjà.",
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

    private void modifierEtudiant() {
        int selectedRow = tableEtudiants.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un étudiant à modifier.",
                    "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cne = (String) tableModel.getValueAt(selectedRow, 0);
        Etudiant etudiant = etudiantDAO.getEtudiantParCne(cne);

        if (etudiant == null) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la récupération de l'étudiant.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Fonctionnalité en cours de développement");
    }

    private void desactiverEtudiant() {
        int selectedRow = tableEtudiants.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un étudiant.",
                    "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cne = (String) tableModel.getValueAt(selectedRow, 0);
        String nom = tableModel.getValueAt(selectedRow, 1) + " " +
                tableModel.getValueAt(selectedRow, 2);

        // Vérifier s'il y a des emprunts en cours
        int nbEmprunts = etudiantDAO.getNombreEmpruntsEnCours(cne);
        if (nbEmprunts > 0) {
            JOptionPane.showMessageDialog(this,
                    "Impossible de désactiver cet étudiant.\n" +
                            "Il a " + nbEmprunts + " emprunt(s) en cours.",
                    "Emprunts en cours",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir désactiver l'étudiant:\n" + nom + " ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (etudiantDAO.desactiverEtudiant(cne)) {
                JOptionPane.showMessageDialog(this,
                        "Étudiant désactivé avec succès!",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                chargerEtudiants();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la désactivation.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void afficherHistorique() {
        int selectedRow = tableEtudiants.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un étudiant.",
                    "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cne = (String) tableModel.getValueAt(selectedRow, 0);
        JOptionPane.showMessageDialog(this,
                "Historique des emprunts pour le CNE: " + cne +
                        "\n\nFonctionnalité à implémenter avec EmpruntDAO.getEmpruntsParEtudiant()",
                "Historique",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean validerChamps(JTextField cne, JTextField nom,
                                  JTextField prenom, JTextField email) {
        if (cne.getText().trim().isEmpty() ||
                nom.getText().trim().isEmpty() ||
                prenom.getText().trim().isEmpty() ||
                email.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Les champs CNE, Nom, Prénom et Email sont obligatoires!",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validation basique de l'email
        if (!email.getText().contains("@")) {
            JOptionPane.showMessageDialog(this,
                    "L'email n'est pas valide!",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }
}