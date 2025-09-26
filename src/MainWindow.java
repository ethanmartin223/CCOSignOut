import UIElements.SignOutOptions;
import UIElements.SignOutRecord;
import UIElements.SignOutRoster;
import UIElements.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class MainWindow extends JFrame {

    private SignOutRoster roster;
    private SignOutOptions options;

    public MainWindow() {
        super();

//        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        GraphicsDevice gd = ge.getDefaultScreenDevice();
//        gd.setFullScreenWindow(this);


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setSize((int) (getToolkit().getScreenSize().width*.8), (int) (getToolkit().getScreenSize().height*.8));

        setLayout(new BorderLayout());

        getContentPane().setBackground(UITheme.MAIN_BACKGROUND);

//        JPanel titleBar = createTitleBar();
//        add(titleBar, BorderLayout.NORTH);

        // --- Roster + Search Panel ---
        roster = new SignOutRoster();

        JPanel rosterPanel = createModernPanel();
        rosterPanel.setLayout(new BorderLayout(0, UITheme.SPACING_LG));
        rosterPanel.setBorder(UITheme.createEmptyBorder(UITheme.PANEL_PADDING));

        JTextField searchField = createModernTextField("Search by name...");
        JButton searchButton = createModernButton("Search", UITheme.ACCENT_BLUE);

        JPanel searchPanel = new JPanel(new BorderLayout(UITheme.SPACING_MD, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(roster);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UITheme.PANEL_BACKGROUND);
        scrollPane.setBackground(UITheme.PANEL_BACKGROUND);

        rosterPanel.add(searchPanel, BorderLayout.NORTH);
        rosterPanel.add(scrollPane, BorderLayout.CENTER);

        // Search action
        Runnable doSearch = () -> {
            String query = searchField.getText().trim().toLowerCase();
            if (query.isEmpty()) return;

            ListModel<SignOutRecord> model = roster.getModel();
            for (int i = 0; i < model.getSize(); i++) {
                SignOutRecord rec = model.getElementAt(i);
                if (rec.getName().toLowerCase().contains(query)) {
                    roster.setSelectedIndex(i);
                    roster.ensureIndexIsVisible(i);
                    return;
                }
            }

            showModernDialog("No matching user found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
        };

        searchButton.addActionListener(e -> doSearch.run());
        searchField.addActionListener(e -> doSearch.run());

        // --- Options panel ---
        options = new SignOutOptions(roster);

        JPanel mainContent = new JPanel(new BorderLayout(UITheme.SPACING_XL, 0));
        mainContent.setBackground(UITheme.MAIN_BACKGROUND);
        mainContent.setBorder(UITheme.createEmptyBorder(UITheme.MAIN_CONTENT_PADDING));

        mainContent.add(rosterPanel, BorderLayout.CENTER);
        mainContent.add(options, BorderLayout.EAST);

        add(mainContent, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(UITheme.PANEL_BACKGROUND);
        titleBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, UITheme.STROKE_THIN, 0, UITheme.BORDER_DEFAULT),
                UITheme.createEmptyBorder(UITheme.TITLE_BAR_PADDING)
        ));

        JLabel titleLabel = new JLabel("Charlie CO - EWO's Sign Out");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);


        JPanel titleContent = new JPanel();
        titleContent.setLayout(new BoxLayout(titleContent, BoxLayout.Y_AXIS));
        titleContent.setOpaque(false);
        titleContent.add(titleLabel);
        titleContent.add(Box.createVerticalStrut(UITheme.SPACING_XS));

        titleBar.add(titleContent, BorderLayout.WEST);
        return titleBar;
    }

    private JPanel createModernPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(UITheme.CARD_BACKGROUND);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                        UITheme.BORDER_RADIUS_LARGE, UITheme.BORDER_RADIUS_LARGE));

                g2d.setColor(UITheme.BORDER_DEFAULT);
                g2d.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1,
                        UITheme.BORDER_RADIUS_LARGE, UITheme.BORDER_RADIUS_LARGE));

                g2d.dispose();
            }
        };
    }

    private JTextField createModernTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background
                g2d.setColor(getBackground());
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                        UITheme.BORDER_RADIUS_MEDIUM, UITheme.BORDER_RADIUS_MEDIUM));

                // Border
                if (hasFocus()) {
                    g2d.setColor(UITheme.BORDER_FOCUS);
                    g2d.setStroke(UITheme.BORDER_STROKE_FOCUS);
                } else {
                    g2d.setColor(UITheme.BORDER_LIGHT);
                    g2d.setStroke(UITheme.BORDER_STROKE_DEFAULT);
                }
                g2d.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2,
                        UITheme.BORDER_RADIUS_SMALL, UITheme.BORDER_RADIUS_SMALL));

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        field.setPreferredSize(UITheme.SEARCH_FIELD_SIZE);
        field.setFont(UITheme.FONT_INPUT);
        field.setBorder(UITheme.createEmptyBorder(UITheme.INPUT_PADDING));
        field.setBackground(UITheme.MAIN_BACKGROUND);
        field.setForeground(UITheme.TEXT_PRIMARY);

        // Placeholder effect
        field.setText(placeholder);
        field.setForeground(UITheme.TEXT_LIGHT);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(UITheme.TEXT_PRIMARY);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(UITheme.TEXT_LIGHT);
                }
            }
        });

        return field;
    }

    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color currentBg = getModel().isPressed() ? UITheme.getDarkerColor(bgColor) :
                        getModel().isRollover() ? UITheme.getBrighterColor(bgColor) : bgColor;

                g2d.setColor(currentBg);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                        UITheme.BORDER_RADIUS_MEDIUM, UITheme.BORDER_RADIUS_MEDIUM));

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        button.setPreferredSize(UITheme.SEARCH_BUTTON_SIZE);
        button.setFont(UITheme.FONT_BUTTON);
        button.setForeground(UITheme.TEXT_ON_DARK);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void showModernDialog(String message, String title, int messageType) {
        JOptionPane optionPane = new JOptionPane(message, messageType);
        JDialog dialog = optionPane.createDialog(this, title);
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(MainWindow::new);
    }
}