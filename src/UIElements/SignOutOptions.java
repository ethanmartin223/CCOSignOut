package UIElements;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.KeyEvent;
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
    private boolean pinFieldError = false;
    private boolean locationError = false;
    private boolean nameError = false;
    private boolean phoneError = false;

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

        nameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                clearNameError();
            }
        });
        locationField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                clearLocationError();
            }
        });
        phoneField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                clearPhoneError();
            }
        });

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

        formPanel.add(createFieldGroup("Names", nameField = createModernTextField("Scan CAC")));
        formPanel.add(Box.createVerticalStrut(UITheme.SPACING_LG));

        formPanel.add(createFieldGroup("Location", locationField = createModernTextField("Where are you going?")));
        formPanel.add(Box.createVerticalStrut(UITheme.SPACING_LG));

        formPanel.add(createFieldGroup("Phone Number", phoneField = createModernTextField("Contact number")));

        return formPanel;
    }

    private JPanel createAuthSection() {
        JPanel authPanel = new JPanel();
        authPanel.setLayout(new BoxLayout(authPanel, BoxLayout.Y_AXIS));
        authPanel.setOpaque(false);
        authPanel.setBorder(UITheme.createSectionBorder());

        authPanel.add(createFieldGroup("Drill SGT", userDropdown = createModernDropdown()));
        authPanel.add(Box.createVerticalStrut(UITheme.SPACING_LG));

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

                // Set background color based on error state
                Color bgColor = pinFieldError ? new Color(255, 240, 240) : getBackground();
                g2d.setColor(bgColor);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                        UITheme.BORDER_RADIUS_MEDIUM, UITheme.BORDER_RADIUS_MEDIUM));

                // Set border color based on error state
                Color borderColor;
                if (hasFocus()) {
                    borderColor = UITheme.BORDER_FOCUS;
                    g2d.setStroke(UITheme.BORDER_STROKE_FOCUS);
                } else {
                    borderColor = UITheme.BORDER_LIGHT;
                    g2d.setStroke(UITheme.BORDER_STROKE_DEFAULT);
                }

                g2d.setColor(borderColor);
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
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                clearPinError();
            }
        });
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (!getFieldText(phoneField).isEmpty() ||
                            !getFieldText(nameField).isEmpty() ||
                            !getFieldText(locationField).isEmpty()) {
                        handleSignOut();
                    } else if (roster.getSelectedRecord() != null) {
                        handleSignIn();
                    }
                }
            }
        });

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
            if (name.isEmpty()) triggerNameError();
            if (location.isEmpty()) triggerLocationError();
            if (phone.isEmpty()) triggerPhoneError();
            return;
        }

        SignOutRecord record = new SignOutRecord(name, location, phone);
        roster.signOut(record);

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
        if (text.equals("Scan CAC") || text.equals("Where are you going?") || text.equals("Contact number")) {
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
            triggerPinError();
            pinField.setText("");
            return false;
        }

        String correctPin = userPins.get(user);
        if (!enteredPin.equals(correctPin)) {
            triggerPinError();

            pinField.setText("");
            return false;
        }

        pinField.setText("");
        return true;
    }


    private void triggerPinError() {
        pinFieldError = true;
        pinField.repaint();

        Point originalLocation = pinField.getLocation();
        Timer shakeTimer = new Timer(50, null);
        final int[] shakeCount = {0};
        final int maxShakes = 6;
        final int shakeDistance = 5;

        shakeTimer.addActionListener(e -> {
            setErrorBorder(pinField);
            if (shakeCount[0] < maxShakes) {
                int offset = (shakeCount[0] % 2 == 0) ? shakeDistance : -shakeDistance;
                pinField.setLocation(originalLocation.x + offset, originalLocation.y);
                shakeCount[0]++;
            } else {
                pinField.setLocation(originalLocation);
                shakeTimer.stop();

                Timer clearTimer = new Timer(2000, clearEvent -> {
                    clearPinError();
                    ((Timer) clearEvent.getSource()).stop();
                });
                clearTimer.setRepeats(false);
                clearTimer.start();
            }
        });

        shakeTimer.start();
    }

    private void setErrorBorder(JTextField pinField) {
        EmptyBorder emptyBorder = new MatteBorder(3,3,3,3, UITheme.ACCENT_RED);
        EmptyBorder emptyBorder2 = new EmptyBorder(
                UITheme.INPUT_PADDING.top-3,
                UITheme.INPUT_PADDING.left-3,
                UITheme.INPUT_PADDING.bottom-3,
                UITheme.INPUT_PADDING.right-3);
        pinField.setBorder(new CompoundBorder(emptyBorder, emptyBorder2));
    }

    private void triggerLocationError() {
        locationError = true;

        setErrorBorder(locationField);
        Timer clearTimer = new Timer(2000, clearEvent -> {
            clearLocationError();
            ((Timer) clearEvent.getSource()).stop();
        });
        clearTimer.setRepeats(false);
        clearTimer.start();

    }

    private void triggerPhoneError() {
        phoneError = true;
        setErrorBorder(phoneField);
        Timer clearTimer = new Timer(2000, clearEvent -> {
            clearPhoneError();
            ((Timer) clearEvent.getSource()).stop();
        });
        clearTimer.setRepeats(false);
        clearTimer.start();
    }

    private void triggerNameError() {
        nameError = true;
        setErrorBorder(nameField);
        Timer clearTimer = new Timer(2000, clearEvent -> {
            clearNameError();
            ((Timer) clearEvent.getSource()).stop();
        });
        clearTimer.setRepeats(false);
        clearTimer.start();

    }
    private void clearPinError() {
        if (pinFieldError) {
            pinFieldError = false;
            pinField.setBorder(UITheme.createEmptyBorder(UITheme.INPUT_PADDING));
            pinField.repaint();
        }
    }
    private void clearLocationError() {
        if (locationError) {
            locationError = false;
            locationField.setBorder(UITheme.createEmptyBorder(UITheme.INPUT_PADDING));
            locationField.repaint();
        }
    }private void clearPhoneError() {
        if (phoneError) {
            phoneError = false;
            phoneField.setBorder(UITheme.createEmptyBorder(UITheme.INPUT_PADDING));
            phoneField.repaint();
        }
    }private void clearNameError() {
        if (nameError) {
            nameError = false;
            nameField.setBorder(UITheme.createEmptyBorder(UITheme.INPUT_PADDING));
            nameField.repaint();
        }
    }


    private void showModernDialog(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}