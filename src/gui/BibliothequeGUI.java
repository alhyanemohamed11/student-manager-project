package gui;

import DB.DatabaseConnection;
import javax.swing.*;
import java.awt.*;

/**
 * Interface graphique principale de l'application de gestion de bibliothèque
 */
public class BibliothequeGUI extends JFrame {

    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Panels pour chaque section
    private GestionLivresPanel livresPanel;
    private GestionEtudiantsPanel etudiantsPanel;
    private GestionEmpruntsPanel empruntsPanel;
    private StatistiquesPanel statistiquesPanel;

    public BibliothequeGUI() {
        initializeGUI();
        testDatabaseConnection();
    }

    private void initializeGUI() {
        setTitle("Système de Gestion de Bibliothèque");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        // Configuration du layout principal
        setLayout(new BorderLayout());

        // Barre de menu
        createMenuBar();

        // Panel de navigation latéral
        JPanel sidePanel = createSidePanel();
        add(sidePanel, BorderLayout.WEST);

        // Panel principal avec CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialisation des panels
        livresPanel = new GestionLivresPanel();
        etudiantsPanel = new GestionEtudiantsPanel();
        empruntsPanel = new GestionEmpruntsPanel();
        statistiquesPanel = new StatistiquesPanel();

        // Ajout des panels au CardLayout
        mainPanel.add(createAccueilPanel(), "accueil");
        mainPanel.add(livresPanel, "livres");
        mainPanel.add(etudiantsPanel, "etudiants");
        mainPanel.add(empruntsPanel, "emprunts");
        mainPanel.add(statistiquesPanel, "statistiques");

        add(mainPanel, BorderLayout.CENTER);

        // Afficher le panel d'accueil par défaut
        cardLayout.show(mainPanel, "accueil");
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu Fichier
        JMenu fichierMenu = new JMenu("Fichier");
        JMenuItem quitterItem = new JMenuItem("Quitter");
        quitterItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Êtes-vous sûr de vouloir quitter ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                DatabaseConnection.closeConnection();
                System.exit(0);
            }
        });
        fichierMenu.add(quitterItem);

        // Menu Gestion
        JMenu gestionMenu = new JMenu("Gestion");
        JMenuItem livresItem = new JMenuItem("Livres");
        JMenuItem etudiantsItem = new JMenuItem("Étudiants");
        JMenuItem empruntsItem = new JMenuItem("Emprunts");

        livresItem.addActionListener(e -> cardLayout.show(mainPanel, "livres"));
        etudiantsItem.addActionListener(e -> cardLayout.show(mainPanel, "etudiants"));
        empruntsItem.addActionListener(e -> cardLayout.show(mainPanel, "emprunts"));

        gestionMenu.add(livresItem);
        gestionMenu.add(etudiantsItem);
        gestionMenu.add(empruntsItem);

        // Menu Rapports
        JMenu rapportsMenu = new JMenu("Rapports");
        JMenuItem statistiquesItem = new JMenuItem("Statistiques");
        statistiquesItem.addActionListener(e -> {
            cardLayout.show(mainPanel, "statistiques");
            statistiquesPanel.rafraichirStatistiques();
        });
        rapportsMenu.add(statistiquesItem);

        // Menu Aide
        JMenu aideMenu = new JMenu("Aide");
        JMenuItem aProposItem = new JMenuItem("À propos");
        aProposItem.addActionListener(e -> showAboutDialog());
        aideMenu.add(aProposItem);

        menuBar.add(fichierMenu);
        menuBar.add(gestionMenu);
        menuBar.add(rapportsMenu);
        menuBar.add(aideMenu);

        setJMenuBar(menuBar);
    }

    private JPanel createSidePanel() {
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(new Color(52, 73, 94));
        sidePanel.setPreferredSize(new Dimension(200, getHeight()));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Logo/Titre
        JLabel titleLabel = new JLabel("Bibliothèque");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidePanel.add(titleLabel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Boutons de navigation
        addNavButton(sidePanel, "🏠 Accueil", "accueil");
        addNavButton(sidePanel, "📚 Livres", "livres");
        addNavButton(sidePanel, "👥 Étudiants", "etudiants");
        addNavButton(sidePanel, "📋 Emprunts", "emprunts");
        addNavButton(sidePanel, "📊 Statistiques", "statistiques");

        sidePanel.add(Box.createVerticalGlue());

        return sidePanel;
    }

    private void addNavButton(JPanel panel, String text, String cardName) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setBackground(new Color(41, 128, 185));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));

        button.addActionListener(e -> {
            cardLayout.show(mainPanel, cardName);
            if (cardName.equals("statistiques")) {
                statistiquesPanel.rafraichirStatistiques();
            }
        });

        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private JPanel createAccueilPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Panel central avec informations
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel welcomeLabel = new JLabel("Bienvenue dans le Système de Gestion de Bibliothèque");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Gérez facilement vos livres, étudiants et emprunts");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(welcomeLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(subtitleLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Quick actions
        JPanel quickActionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        quickActionsPanel.setBackground(Color.WHITE);

        quickActionsPanel.add(createQuickActionButton("Nouveau Livre", "livres"));
        quickActionsPanel.add(createQuickActionButton("Nouvel Étudiant", "etudiants"));
        quickActionsPanel.add(createQuickActionButton("Nouvel Emprunt", "emprunts"));
        quickActionsPanel.add(createQuickActionButton("Voir Statistiques", "statistiques"));

        centerPanel.add(quickActionsPanel);

        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private JButton createQuickActionButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(180, 50));
        button.setBackground(new Color(41, 128, 185));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);

        button.addActionListener(e -> {
            cardLayout.show(mainPanel, cardName);
            if (cardName.equals("statistiques")) {
                statistiquesPanel.rafraichirStatistiques();
            }
        });

        return button;
    }

    private void testDatabaseConnection() {
        if (!DatabaseConnection.testConnection()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erreur de connexion à la base de données.\nVérifiez votre configuration.",
                    "Erreur de connexion",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(
                this,
                "Système de Gestion de Bibliothèque\n" +
                        "Version 1.0\n\n" +
                        "Développé pour la gestion des livres, étudiants et emprunts\n" +
                        "© 2025",
                "À propos",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Utiliser le look and feel du système
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            BibliothequeGUI gui = new BibliothequeGUI();
            gui.setVisible(true);
        });
    }
}