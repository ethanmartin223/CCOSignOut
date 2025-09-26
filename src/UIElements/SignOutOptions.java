package UIElements;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

public class SignOutOptions extends JPanel {

    private SignOutRoster roster;
    private JTextField nameField;
    private JTextField locationField;
    private JTextField phoneField;
    private JComboBox<String> userDropdown;
    private JPasswordField pinField;

    private Map<String, String> userPins;

    public SignOutOptions(SignOutRoster roster) {
        this.roster = roster;

        userPins = new HashMap<>();
        userPins.put("DS Wright", "1234");
        userPins.put("DS Koch", "1234");
        userPins.put("DS Brody", "1234");
        userPins.put("DS Kuipers", "1234");

        setLayout(new BorderLayout());
        setPreferredSize(UITheme.OPTIONS_PANEL_SIZE);
        setOpaque(false);

        JPanel mainPanel = createModernPanel();
        mainPanel.setLayout(new BorderLayout(0, UITheme.SPACING_XL));
        mainPanel.setBorder(UITheme.createEmptyBorder(UITheme.PANEL_PADDING));

        // Title section
        JPanel titleSection = createTitleSection();

        // Form section
        JPanel formSection = createFormSection();

        // Auth section
        JPanel authSection = createAuthSection();

        // Button section
        JPanel buttonSection = createButtonSection();

        mainPanel.add(titleSection, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(0, UITheme.SPACING_XL));
        contentPanel.setOpaque(false);
        contentPanel.add(formSection, BorderLayout.NORTH);
        contentPanel.add(authSection, BorderLayout.CENTER);
        contentPanel.add(buttonSection, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createTitleSection() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(UITheme.createEmptyBorder(new Insets(0, 0, UITheme.SPACING_XL, 0)));

        JLabel titleLabel = new JLabel("Sign Out");
        titleLabel.setFont(UITheme.FONT_SECTION_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);


        JPanel titleContent = new JPanel();
        titleContent.setLayout(new BoxLayout(titleContent, BoxLayout.Y_AXIS));
        titleContent.setOpaque(false);
        titleContent.add(titleLabel);
        titleContent.add(Box.createVerticalStrut(UITheme.SPACING_SM));

        titlePanel.add(titleContent, BorderLayout.WEST);
        return titlePanel;
    }

    private JPanel createFormSection() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // Name field
        formPanel.add(createFieldGroup("Names", nameField = createModernTextField("Scan CAC")));
        formPanel.add(Box.createVerticalStrut(UITheme.SPACING_LG));

        // Location field
        formPanel.add(createFieldGroup("Location", locationField = createModernTextField("Where are you going?")));
        formPanel.add(Box.createVerticalStrut(UITheme.SPACING_LG));

        // Phone field
        formPanel.add(createFieldGroup("Phone Number", phoneField = createModernTextField("Contact number")));

        return formPanel;
    }

    private JPanel createAuthSection() {
        JPanel authPanel = new JPanel();
        authPanel.setLayout(new BoxLayout(authPanel, BoxLayout.Y_AXIS));
        authPanel.setOpaque(false);
        authPanel.setBorder(UITheme.createSectionBorder());

        // User dropdown
        authPanel.add(createFieldGroup("User", userDropdown = createModernDropdown()));
        authPanel.add(Box.createVerticalStrut(UITheme.SPACING_LG));

        // PIN field
        authPanel.add(createFieldGroup("PIN", pinField = createModernPasswordField()));

        return authPanel;
    }

    private JPanel createButtonSection() {
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, UITheme.SPACING_MD));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(UITheme.createEmptyBorder(new Insets(UITheme.SPACING_XL, 0, 0, 0)));

        JButton signOutButton = createModernButton("Sign Out", UITheme.ACCENT_GREEN, UITheme.TEXT_ON_DARK);
        JButton signInButton = createModernButton("Sign In Selected", UITheme.ACCENT_BLUE, UITheme.TEXT_ON_DARK);

        buttonPanel.add(signOutButton);
        buttonPanel.add(signInButton);

        // Button actions
        signOutButton.addActionListener(e -> handleSignOut());
        signInButton.addActionListener(e -> handleSignIn());

        return buttonPanel;
    }

    private JPanel createFieldGroup(String label, JComponent field) {
        JPanel group = new JPanel(new BorderLayout(0, UITheme.SPACING_MD));
        group.setOpaque(false);

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(UITheme.FONT_LABEL);
        fieldLabel.setForeground(UITheme.TEXT_SECONDARY);

        group.add(fieldLabel, BorderLayout.NORTH);
        group.add(field, BorderLayout.CENTER);
        return group;
    }

    private JPanel createModernPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // White background with rounded corners
                g2d.setColor(UITheme.CARD_BACKGROUND);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                        UITheme.BORDER_RADIUS_LARGE, UITheme.BORDER_RADIUS_LARGE));

                // Subtle border
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

                g2d.setColor(getBackground());
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                        UITheme.BORDER_RADIUS_MEDIUM, UITheme.BORDER_RADIUS_MEDIUM));

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

        field.setPreferredSize(new Dimension(0, UITheme.INPUT_HEIGHT));
        field.setFont(UITheme.FONT_INPUT);
        field.setBorder(UITheme.createEmptyBorder(UITheme.INPUT_PADDING));
        field.setBackground(UITheme.INPUT_BACKGROUND);
        field.setForeground(UITheme.TEXT_PRIMARY);

        // Placeholder functionality
        setupPlaceholder(field, placeholder);
        return field;
    }

    private JPasswordField createModernPasswordField() {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(getBackground());
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                        UITheme.BORDER_RADIUS_MEDIUM, UITheme.BORDER_RADIUS_MEDIUM));

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

        field.setPreferredSize(new Dimension(0, UITheme.INPUT_HEIGHT));
        field.setFont(UITheme.FONT_INPUT);
        field.setBorder(UITheme.createEmptyBorder(UITheme.INPUT_PADDING));
        field.setBackground(UITheme.INPUT_BACKGROUND);
        field.setForeground(UITheme.TEXT_PRIMARY);

        return field;
    }

    private JComboBox<String> createModernDropdown() {
        JComboBox<String> dropdown = new JComboBox<>(userPins.keySet().toArray(new String[0])) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(getBackground());
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                        UITheme.BORDER_RADIUS_MEDIUM, UITheme.BORDER_RADIUS_MEDIUM));

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

        dropdown.setPreferredSize(new Dimension(0, UITheme.INPUT_HEIGHT));
        dropdown.setFont(UITheme.FONT_INPUT);
        dropdown.setBackground(UITheme.INPUT_BACKGROUND);
        dropdown.setForeground(UITheme.TEXT_PRIMARY);
        dropdown.setBorder(UITheme.createEmptyBorder(UITheme.INPUT_PADDING));
        dropdown.setFocusable(false);
        dropdown.setOpaque(false);

        dropdown.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton() {
                    @Override
                    public void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        // Draw arrow
                        g2d.setColor(UITheme.TEXT_MUTED);
                        int[] xPoints = {getWidth()/2 - 4, getWidth()/2 + 4, getWidth()/2};
                        int[] yPoints = {getHeight()/2 - 2, getHeight()/2 - 2, getHeight()/2 + 3};
                        g2d.fillPolygon(xPoints, yPoints, 3);

                        g2d.dispose();
                    }
                };
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(false);
                return button;
            }
        });

        return dropdown;
    }

    private JButton createModernButton(String text, Color bgColor, Color textColor) {
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

        button.setPreferredSize(new Dimension(0, UITheme.BUTTON_HEIGHT));
        button.setFont(UITheme.FONT_BUTTON);
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void setupPlaceholder(JTextField field, String placeholder) {
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
    }

    private void handleSignOut() {
        if (!authenticateUser()) return;

        String name = getFieldText(nameField);
        String location = getFieldText(locationField);
        String phone = getFieldText(phoneField);

        if (name.isEmpty() || location.isEmpty() || phone.isEmpty()) {
            showModernDialog(
                    "All fields (Name, Location, Phone) must be filled before signing out.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        SignOutRecord record = new SignOutRecord(name, location, phone);
        roster.signOut(record);

        // Reset fields
        clearField(nameField, "Enter full name");
        clearField(locationField, "Where are you going?");
        clearField(phoneField, "Contact number");
        pinField.setText("");

        nameField.grabFocus();
        nameField.setText("");
    }

    private void handleSignIn() {
        if (!authenticateUser()) return;

        SignOutRecord selected = roster.getSelectedRecord();
        if (selected != null) {
            roster.signIn(selected);
        }
        pinField.setText("");
    }

    private String getFieldText(JTextField field) {
        String text = field.getText().trim();
        if (text.equals("Enter full name") || text.equals("Where are you going?") || text.equals("Contact number")) {
            return "";
        }
        return text;
    }

    private void clearField(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(UITheme.TEXT_LIGHT);
    }

    private boolean authenticateUser() {
        String user = (String) userDropdown.getSelectedItem();
        String enteredPin = new String(pinField.getPassword());

        if (user == null || enteredPin.isEmpty()) {
            showModernDialog(
                    "Please select a user and enter their PIN.",
                    "Authentication Required",
                    JOptionPane.WARNING_MESSAGE
            );
            pinField.setText("");
            return false;
        }

        String correctPin = userPins.get(user);
        if (!enteredPin.equals(correctPin)) {
            showModernDialog(
                    "Invalid PIN for user " + user + ".",
                    "Authentication Failed",
                    JOptionPane.ERROR_MESSAGE
            );
            pinField.setText("");
            return false;
        }

        pinField.setText("");
        return true;
    }

    private void showModernDialog(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}