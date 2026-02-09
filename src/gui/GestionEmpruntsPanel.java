package gui;

import dao.EmpruntDAO;
import dao.EtudiantDAO;
import dao.LivreDAO;
import models.Emprunt;
import models.Etudiant;
import models.Livre;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Panel pour la gestion des emprunts
 */
public class GestionEmpruntsPanel extends JPanel {

    private EmpruntDAO empruntDAO;
    private EtudiantDAO etudiantDAO;
    private LivreDAO livreDAO;
    private JTable tableEmprunts;
    private DefaultTableModel tableModel;
    private JComboBox<String> filtreCombo;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public GestionEmpruntsPanel() {
        empruntDAO = new EmpruntDAO();
        etudiantDAO = new EtudiantDAO();
        livreDAO = new LivreDAO();
        initializePanel();
        chargerEmprunts();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel supérieur
        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Gestion des Emprunts");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.WEST);

        // Panel de filtres
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filtreCombo = new JComboBox<>(new String[]{
                "Tous les emprunts",
                "En cours",
                "En retard",
                "Retournés"
        });
        JButton btnFiltrer = new JButton("Filtrer");

        btnFiltrer.addActionListener(e -> chargerEmprunts());
        filtreCombo.addActionListener(e -> chargerEmprunts());

        filterPanel.add(new JLabel("Afficher:"));
        filterPanel.add(filtreCombo);
        filterPanel.add(btnFiltrer);

        topPanel.add(filterPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Table des emprunts
        String[] colonnes = {"ID", "Livre", "Étudiant", "Date Emprunt",
                "Retour Prévu", "Retour Effectif", "Statut", "Pénalité"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableEmprunts = new JTable(tableModel);
        tableEmprunts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tableEmprunts);
        add(scrollPane, BorderLayout.CENTER);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnNouvel = new JButton("Nouvel Emprunt");
        JButton btnRetour = new JButton("Retourner Livre");
        JButton btnRappel = new JButton("Envoyer Rappel");
        JButton btnDetails = new JButton("Détails");

        btnNouvel.setBackground(new Color(46, 204, 113));
        btnNouvel.setForeground(Color.WHITE);
        btnRetour.setBackground(new Color(52, 152, 219));
        btnRetour.setForeground(Color.WHITE);
        btnRappel.setBackground(new Color(243, 156, 18));
        btnRappel.setForeground(Color.WHITE);
        btnDetails.setBackground(new Color(149, 165, 166));
        btnDetails.setForeground(Color.WHITE);

        btnNouvel.addActionListener(e -> nouvelEmprunt());
        btnRetour.addActionListener(e -> retournerLivre());
        btnRappel.addActionListener(e -> envoyerRappel());
        btnDetails.addActionListener(e -> afficherDetails());

        buttonPanel.add(btnNouvel);
        buttonPanel.add(btnRetour);
        buttonPanel.add(btnRappel);
        buttonPanel.add(btnDetails);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void chargerEmprunts() {
        tableModel.setRowCount(0);
        List<Emprunt> emprunts;

        String filtre = (String) filtreCombo.getSelectedItem();
        switch (filtre) {
            case "En cours":
                emprunts = empruntDAO.getEmpruntsEnCours();
                break;
            case "En retard":
                emprunts = empruntDAO.getEmpruntsEnRetard();
                break;
            default:
                emprunts = empruntDAO.getAllEmprunts();
                break;
        }

        for (Emprunt emprunt : emprunts) {
            String dateRetourEffective = emprunt.getDateRetourEffective() != null ?
                    dateFormat.format(emprunt.getDateRetourEffective()) : "-";

            String statut = emprunt.getStatut();
            if (emprunt.estEnRetard()) {
                statut = "EN RETARD (" + emprunt.getJoursRetard() + " jours)";
            }

            Object[] row = {
                    emprunt.getIdEmprunt(),
                    emprunt.getTitreLivre(),
                    emprunt.getNomEtudiant(),
                    dateFormat.format(emprunt.getDateEmprunt()),
                    dateFormat.format(emprunt.getDateRetourPrevue()),
                    dateRetourEffective,
                    statut,
                    String.format("%.2f DH", emprunt.getPenalite())
            };
            tableModel.addRow(row);
        }
    }

    private void nouvelEmprunt() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Nouvel Emprunt", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Champs de saisie
        JTextField cneField = new JTextField(20);
        JTextField isbnField = new JTextField(20);
        JSpinner dureeSpinner = new JSpinner(new SpinnerNumberModel(14, 1, 30, 1));

        JButton btnVerifEtudiant = new JButton("Vérifier");
        JButton btnVerifLivre = new JButton("Vérifier");

        JLabel etudiantLabel = new JLabel("Non vérifié");
        JLabel livreLabel = new JLabel("Non vérifié");

        // Vérification étudiant
        btnVerifEtudiant.addActionListener(e -> {
            String cne = cneField.getText().trim();
            if (!cne.isEmpty()) {
                Etudiant etudiant = etudiantDAO.getEtudiantParCne(cne);
                if (etudiant != null && etudiant.isActif()) {
                    if (etudiantDAO.peutEmprunter(cne, 3)) {
                        etudiantLabel.setText("✓ " + etudiant.getNomComplet());
                        etudiantLabel.setForeground(new Color(46, 204, 113));
                    } else {
                        etudiantLabel.setText("✗ Limite d'emprunts atteinte");
                        etudiantLabel.setForeground(Color.RED);
                    }
                } else {
                    etudiantLabel.setText("✗ Étudiant non trouvé ou inactif");
                    etudiantLabel.setForeground(Color.RED);
                }
            }
        });

        // Vérification livre
        btnVerifLivre.addActionListener(e -> {
            String isbn = isbnField.getText().trim();
            if (!isbn.isEmpty()) {
                Livre livre = livreDAO.getLivreParIsbn(isbn);
                if (livre != null && livre.estDisponible()) {
                    livreLabel.setText("✓ " + livre.getTitre() + " (Disponibles: " +
                            livre.getExemplairesDisponibles() + ")");
                    livreLabel.setForeground(new Color(46, 204, 113));
                } else if (livre != null) {
                    livreLabel.setText("✗ Aucun exemplaire disponible");
                    livreLabel.setForeground(Color.RED);
                } else {
                    livreLabel.setText("✗ Livre non trouvé");
                    livreLabel.setForeground(Color.RED);
                }
            }
        });

        // Ajouter les composants
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("CNE Étudiant:"), gbc);
        gbc.gridx = 1;
        panel.add(cneField, gbc);
        gbc.gridx = 2;
        panel.add(btnVerifEtudiant, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(etudiantLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        panel.add(new JLabel("ISBN Livre:"), gbc);
        gbc.gridx = 1;
        panel.add(isbnField, gbc);
        gbc.gridx = 2;
        panel.add(btnVerifLivre, gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(livreLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        panel.add(new JLabel("Durée (jours):"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        panel.add(dureeSpinner, gbc);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCreer = new JButton("Créer l'emprunt");
        JButton btnAnnuler = new JButton("Annuler");

        btnCreer.addActionListener(e -> {
            String cne = cneField.getText().trim();
            String isbn = isbnField.getText().trim();

            if (cne.isEmpty() || isbn.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Veuillez remplir tous les champs!",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Vérifications finales
            Etudiant etudiant = etudiantDAO.getEtudiantParCne(cne);
            Livre livre = livreDAO.getLivreParIsbn(isbn);

            if (etudiant == null || !etudiant.isActif()) {
                JOptionPane.showMessageDialog(dialog,
                        "Étudiant non valide!",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (livre == null || !livre.estDisponible()) {
                JOptionPane.showMessageDialog(dialog,
                        "Livre non disponible!",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!etudiantDAO.peutEmprunter(cne, 3)) {
                JOptionPane.showMessageDialog(dialog,
                        "L'étudiant a atteint la limite d'emprunts!",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Créer l'emprunt
            Date dateEmprunt = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateEmprunt);
            cal.add(Calendar.DAY_OF_MONTH, (Integer) dureeSpinner.getValue());
            Date dateRetourPrevue = cal.getTime();

            Emprunt emprunt = new Emprunt(isbn, cne, dateEmprunt, dateRetourPrevue);

            if (empruntDAO.creerEmprunt(emprunt)) {
                JOptionPane.showMessageDialog(dialog,
                        "Emprunt créé avec succès!\n" +
                                "Date de retour prévue: " + dateFormat.format(dateRetourPrevue),
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                chargerEmprunts();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Erreur lors de la création de l'emprunt.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        btnAnnuler.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnCreer);
        buttonPanel.add(btnAnnuler);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void retournerLivre() {
        int selectedRow = tableEmprunts.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un emprunt.",
                    "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idEmprunt = (Integer) tableModel.getValueAt(selectedRow, 0);
        String statut = (String) tableModel.getValueAt(selectedRow, 6);

        if (statut.equals("RETOURNE")) {
            JOptionPane.showMessageDialog(this,
                    "Ce livre a déjà été retourné.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Emprunt emprunt = empruntDAO.getEmpruntParId(idEmprunt);
        if (emprunt == null) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la récupération de l'emprunt.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String message = "Confirmer le retour du livre:\n" +
                emprunt.getTitreLivre() + "\n" +
                "Emprunté par: " + emprunt.getNomEtudiant();

        if (emprunt.estEnRetard()) {
            double penalite = emprunt.getJoursRetard() * 2.0;
            message += "\n\nATTENTION: Retard de " + emprunt.getJoursRetard() + " jour(s)" +
                    "\nPénalité: " + String.format("%.2f DH", penalite);
        }

        int confirm = JOptionPane.showConfirmDialog(this, message,
                "Confirmation de retour", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (empruntDAO.retournerLivre(idEmprunt)) {
                JOptionPane.showMessageDialog(this,
                        "Livre retourné avec succès!",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                chargerEmprunts();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors du retour du livre.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void envoyerRappel() {
        int selectedRow = tableEmprunts.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un emprunt.",
                    "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String etudiant = (String) tableModel.getValueAt(selectedRow, 2);
        String livre = (String) tableModel.getValueAt(selectedRow, 1);

        JOptionPane.showMessageDialog(this,
                "Rappel envoyé à: " + etudiant +
                        "\nPour le livre: " + livre +
                        "\n\n(Fonctionnalité d'envoi d'email à implémenter)",
                "Rappel envoyé",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void afficherDetails() {
        int selectedRow = tableEmprunts.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un emprunt.",
                    "Aucune sélection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idEmprunt = (Integer) tableModel.getValueAt(selectedRow, 0);
        Emprunt emprunt = empruntDAO.getEmpruntParId(idEmprunt);

        if (emprunt != null) {
            String details = String.format(
                    "ID Emprunt: %d\n" +
                            "Livre: %s\n" +
                            "Étudiant: %s\n" +
                            "Date d'emprunt: %s\n" +
                            "Retour prévu: %s\n" +
                            "Retour effectif: %s\n" +
                            "Statut: %s\n" +
                            "Jours de retard: %d\n" +
                            "Pénalité: %.2f DH",
                    emprunt.getIdEmprunt(),
                    emprunt.getTitreLivre(),
                    emprunt.getNomEtudiant(),
                    dateFormat.format(emprunt.getDateEmprunt()),
                    dateFormat.format(emprunt.getDateRetourPrevue()),
                    emprunt.getDateRetourEffective() != null ?
                            dateFormat.format(emprunt.getDateRetourEffective()) : "En cours",
                    emprunt.getStatut(),
                    emprunt.getJoursRetard(),
                    emprunt.getPenalite()
            );

            JOptionPane.showMessageDialog(this, details,
                    "Détails de l'emprunt", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}