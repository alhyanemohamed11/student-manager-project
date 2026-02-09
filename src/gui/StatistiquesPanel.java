package gui;

import dao.EmpruntDAO;
import dao.EtudiantDAO;
import dao.LivreDAO;

import javax.swing.*;
import java.awt.*;

/**
 * Panel pour afficher les statistiques de la bibliothèque
 */
public class StatistiquesPanel extends JPanel {

    private EmpruntDAO empruntDAO;
    private EtudiantDAO etudiantDAO;
    private LivreDAO livreDAO;

    private JLabel lblTotalLivres;
    private JLabel lblLivresDisponibles;
    private JLabel lblLivresEmpruntes;
    private JLabel lblTotalEtudiants;
    private JLabel lblEtudiantsActifs;
    private JLabel lblEmpruntsEnCours;
    private JLabel lblEmpruntsEnRetard;
    private JLabel lblTotalPenalites;

    public StatistiquesPanel() {
        empruntDAO = new EmpruntDAO();
        etudiantDAO = new EtudiantDAO();
        livreDAO = new LivreDAO();
        initializePanel();
        rafraichirStatistiques();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Titre
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Statistiques de la Bibliothèque");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Panel principal avec grille
        JPanel mainPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Section Livres
        JPanel livresPanel = createSectionPanel("📚 Statistiques des Livres");
        lblTotalLivres = new JLabel("0");
        lblLivresDisponibles = new JLabel("0");
        lblLivresEmpruntes = new JLabel("0");

        addStatRow(livresPanel, "Total de livres:", lblTotalLivres);
        addStatRow(livresPanel, "Exemplaires disponibles:", lblLivresDisponibles);
        addStatRow(livresPanel, "Exemplaires empruntés:", lblLivresEmpruntes);

        // Section Étudiants
        JPanel etudiantsPanel = createSectionPanel("👥 Statistiques des Étudiants");
        lblTotalEtudiants = new JLabel("0");
        lblEtudiantsActifs = new JLabel("0");

        addStatRow(etudiantsPanel, "Total d'étudiants:", lblTotalEtudiants);
        addStatRow(etudiantsPanel, "Étudiants actifs:", lblEtudiantsActifs);

        // Section Emprunts
        JPanel empruntsPanel = createSectionPanel("📋 Statistiques des Emprunts");
        lblEmpruntsEnCours = new JLabel("0");
        lblEmpruntsEnRetard = new JLabel("0");
        lblTotalPenalites = new JLabel("0.00 DH");

        addStatRow(empruntsPanel, "Emprunts en cours:", lblEmpruntsEnCours);
        addStatRow(empruntsPanel, "Emprunts en retard:", lblEmpruntsEnRetard);
        addStatRow(empruntsPanel, "Total des pénalités:", lblTotalPenalites);

        mainPanel.add(livresPanel);
        mainPanel.add(etudiantsPanel);
        mainPanel.add(empruntsPanel);

        add(mainPanel, BorderLayout.CENTER);

        // Bouton de rafraîchissement
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnRafraichir = new JButton("Rafraîchir les statistiques");
        btnRafraichir.setBackground(new Color(52, 152, 219));
        btnRafraichir.setForeground(Color.WHITE);
        btnRafraichir.setFont(new Font("Arial", Font.BOLD, 14));
        btnRafraichir.addActionListener(e -> rafraichirStatistiques());
        bottomPanel.add(btnRafraichir);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(52, 73, 94), 2),
                        title,
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 16),
                        new Color(52, 73, 94)
                ),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    private void addStatRow(JPanel panel, String label, JLabel valueLabel) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel lblText = new JLabel(label);
        lblText.setFont(new Font("Arial", Font.PLAIN, 14));

        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        valueLabel.setForeground(new Color(41, 128, 185));

        rowPanel.add(lblText, BorderLayout.WEST);
        rowPanel.add(valueLabel, BorderLayout.EAST);

        panel.add(rowPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    public void rafraichirStatistiques() {
        // Statistiques des livres
        int totalLivres = 0;
        int disponibles = 0;
        int empruntes = 0;

        var livres = livreDAO.getAllLivres();
        for (var livre : livres) {
            totalLivres += livre.getNombreExemplaires();
            disponibles += livre.getExemplairesDisponibles();
        }
        empruntes = totalLivres - disponibles;

        lblTotalLivres.setText(String.valueOf(totalLivres));
        lblLivresDisponibles.setText(String.valueOf(disponibles));
        lblLivresEmpruntes.setText(String.valueOf(empruntes));

        // Statistiques des étudiants
        int totalEtudiants = etudiantDAO.getAllEtudiants().size();
        int etudiantsActifs = etudiantDAO.getEtudiantsActifs().size();

        lblTotalEtudiants.setText(String.valueOf(totalEtudiants));
        lblEtudiantsActifs.setText(String.valueOf(etudiantsActifs));

        // Statistiques des emprunts
        EmpruntDAO.EmpruntStatistiques stats = empruntDAO.getStatistiques();

        lblEmpruntsEnCours.setText(String.valueOf(stats.empruntsEnCours));
        lblEmpruntsEnRetard.setText(String.valueOf(stats.empruntsEnRetard));
        lblTotalPenalites.setText(String.format("%.2f DH", stats.totalPenalites));

        // Changer la couleur si des emprunts sont en retard
        if (stats.empruntsEnRetard > 0) {
            lblEmpruntsEnRetard.setForeground(Color.RED);
        } else {
            lblEmpruntsEnRetard.setForeground(new Color(46, 204, 113));
        }
    }
}