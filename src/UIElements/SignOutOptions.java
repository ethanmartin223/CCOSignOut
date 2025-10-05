package UIElements;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignOutOptions extends JPanel {

    private SignOutRoster roster;
    private UserBubblePanel nameField;
    private JTextField locationField;
    private JTextField phoneField;
    private JComboBox<String> userDropdown;
    private JPasswordField pinField;
    private JCheckBox noCacCheckbox;
    private JCheckBox familyLeaveCheckbox;
    private JCheckBox overnightLeaveCheckbox;

    private Map<String, String> userPins;
    private boolean pinFieldError = false;
    private boolean locationError = false;
    private boolean nameError = false;
    private boolean phoneError = false;

    public static HashMap<String, String> loadUserPins(File file) {
        HashMap<String, String> userPins = new HashMap<>();

        if (!file.exists()) {
            System.out.println("PIN file not found: " + file.getAbsolutePath());
            return userPins;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    String pin = parts[1].trim();
                    userPins.put(name, pin);
                } else {
                    System.out.println("⚠️ Invalid line in PIN file: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userPins;
    }

    public SignOutOptions(SignOutRoster roster) {
        this.roster = roster;
        roster.signOutOptions = this;

        userPins = loadUserPins(new File("user_pins.txt"));

        setLayout(new BorderLayout());
        setPreferredSize(UITheme.OPTIONS_PANEL_SIZE);
        setOpaque(false);

        JPanel mainPanel = createModernPanel();
        mainPanel.setLayout(new BorderLayout(0, UITheme.SPACING_XL));
        mainPanel.setBorder(UITheme.createEmptyBorder(UITheme.PANEL_PADDING));

        JPanel titleSection = createTitleSection();
        JPanel formSection = createFormSection();
        JPanel authSection = createAuthSection();
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
        titlePanel.setBorder(UITheme.createEmptyBorder(new Insets(0, 0, UITheme.SPACING_SM, 0)));

        JLabel titleLabel = new JLabel("Sign Out");
        titleLabel.setFont(UITheme.FONT_SECTION_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);

        JPanel titleContent = new JPanel();
        titleContent.setLayout(new BoxLayout(titleContent, BoxLayout.Y_AXIS));
        titleContent.setOpaque(false);
        titleContent.add(titleLabel);

        titlePanel.add(titleContent, BorderLayout.WEST);
        return titlePanel;
    }

    private JPanel createFormSection() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        nameField = new UserBubblePanel();
        JScrollPane scrollPane = new JScrollPane(nameField);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(UITheme.PANEL_BACKGROUND);
        scrollPane.setPreferredSize(new Dimension(50, UITheme.INPUT_HEIGHT+15));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(6);
        scrollPane.getHorizontalScrollBar().setFocusable(false);
        scrollPane.getVerticalScrollBar().setFocusable(false);
        SwingUtilities.invokeLater(() -> applyModernScrollbarStyling(scrollPane));
        scrollPane.getViewport().setBackground(UITheme.PANEL_BACKGROUND);
        nameField.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                System.out.println("TEST");
                JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
                horizontalScrollBar.setValue(horizontalScrollBar.getMaximum());
            }
        });

        CardScannerField scannerField = new CardScannerField(nameField);
        scannerField.setMaximumSize(new Dimension(400, UITheme.INPUT_HEIGHT));
        formPanel.add(scannerField);

        formPanel.add(Box.createVerticalStrut(UITheme.SPACING_XS));

        formPanel.add(scrollPane);
        formPanel.add(Box.createVerticalStrut(UITheme.SPACING_MD));

        formPanel.add(createFieldGroup("Location", locationField = createModernTextField("Where are you going?")));
        formPanel.add(Box.createVerticalStrut(UITheme.SPACING_SM));
        formPanel.add(createCheckboxPanel());
        formPanel.add(Box.createVerticalStrut(UITheme.SPACING_LG));

        formPanel.add(createFieldGroup("Phone Number", phoneField = createModernTextField("Contact number")));

        return formPanel;
    }

    private void applyModernScrollbarStyling(JScrollPane scrollPane) {
        // Style the vertical scrollbar
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUI(new SignOutRoster.ModernScrollBarUI());
        verticalScrollBar.setPreferredSize(new Dimension(12, 0));

        // Style the horizontal scrollbar
        JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
        horizontalScrollBar.setUI(new SignOutRoster.ModernScrollBarUI());
        horizontalScrollBar.setPreferredSize(new Dimension(0, 12));

        // Remove scrollpane borders and styling
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(UITheme.PANEL_BACKGROUND);
        scrollPane.getViewport().setBackground(UITheme.PANEL_BACKGROUND);
    }

    private JPanel createCheckboxPanel() {
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, UITheme.SPACING_MD, 0));
        checkboxPanel.setOpaque(false);

        familyLeaveCheckbox = createModernCheckbox("Family Leave");
        overnightLeaveCheckbox = createModernCheckbox("Overnight Leave");


        checkboxPanel.add(familyLeaveCheckbox);
        checkboxPanel.add(overnightLeaveCheckbox);

        return checkboxPanel;
    }

    private JCheckBox createModernCheckbox(String text) {
        JCheckBox checkbox = new JCheckBox(text) {
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw custom checkbox
                int boxSize = 18;
                int boxX = 0;
                int boxY = (getHeight() - boxSize) / 2;

                // Draw checkbox background
                g2d.setColor(UITheme.INPUT_BACKGROUND);
                g2d.fill(new RoundRectangle2D.Float(boxX, boxY, boxSize, boxSize,
                        UITheme.BORDER_RADIUS_SMALL, UITheme.BORDER_RADIUS_SMALL));

                // Draw checkbox border
                if (isSelected()) {
                    g2d.setColor(UITheme.ACCENT_BLUE);
                    g2d.setStroke(UITheme.BORDER_STROKE_FOCUS);
                } else {
                    g2d.setColor(UITheme.BORDER_LIGHT);
                    g2d.setStroke(UITheme.BORDER_STROKE_DEFAULT);
                }
                g2d.draw(new RoundRectangle2D.Float(boxX + 0.5f, boxY + 0.5f, boxSize - 1, boxSize - 1,
                        UITheme.BORDER_RADIUS_SMALL, UITheme.BORDER_RADIUS_SMALL));

                // Draw checkmark if selected
                if (isSelected()) {
                    g2d.setColor(UITheme.ACCENT_BLUE);
                    g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                    int checkX = boxX + 4;
                    int checkY = boxY + 9;
                    g2d.drawLine(checkX, checkY, checkX + 3, checkY + 3);
                    g2d.drawLine(checkX + 3, checkY + 3, checkX + 9, checkY - 3);
                }

                // Draw text
                g2d.setFont(getFont());
                g2d.setColor(getForeground());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = boxX + boxSize + 8;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), textX, textY);

                g2d.dispose();
            }
        };

        checkbox.setFont(UITheme.FONT_INPUT);
        checkbox.setForeground(UITheme.TEXT_LIGHT);
        checkbox.setOpaque(false);
        checkbox.setFocusPainted(false);
        checkbox.setFocusable(false);
        checkbox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkbox.setContentAreaFilled(false);
        checkbox.setBorderPainted(false);

        // Calculate preferred size based on text
        FontMetrics fm = checkbox.getFontMetrics(UITheme.FONT_INPUT);
        int textWidth = fm.stringWidth(text);
        checkbox.setPreferredSize(new Dimension(textWidth + 30, 28));

        return checkbox;
    }

    private JPanel createAuthSection() {
        JPanel authPanel = new JPanel();

        authPanel.setLayout(new BoxLayout(authPanel, BoxLayout.Y_AXIS));
        authPanel.setOpaque(false);
        authPanel.setBorder(UITheme.createSectionBorder());

        JLabel titleLabel = new JLabel("Drill SGT Options");
        titleLabel.setFont(UITheme.FONT_SECTION_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);

        JPanel titleContent = new JPanel();
        titleContent.setLayout(new BorderLayout());
        titleContent.setOpaque(false);
        titleContent.add(titleLabel);
        titleContent.setBorder(UITheme.createEmptyBorder(new Insets(0, 0, UITheme.SPACING_XL, 0)));
        authPanel.add(titleContent);

        authPanel.add(createFieldGroup("User", userDropdown = createModernDropdown()));
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
        field.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                roster.clearSelection();
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });

        setupPlaceholder(field, placeholder);
        return field;
    }

    private JPasswordField createModernPasswordField() {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor = pinFieldError ? new Color(255, 240, 240) : getBackground();
                g2d.setColor(bgColor);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                        UITheme.BORDER_RADIUS_MEDIUM, UITheme.BORDER_RADIUS_MEDIUM));

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

        field.setPreferredSize(new Dimension(0, UITheme.INPUT_HEIGHT-40));
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

        dropdown.setPreferredSize(new Dimension(0, UITheme.INPUT_HEIGHT-40));
        dropdown.setFont(UITheme.FONT_INPUT);
        dropdown.setBackground(UITheme.INPUT_BACKGROUND);
        dropdown.setForeground(UITheme.TEXT_PRIMARY);
        dropdown.setBorder(UITheme.createEmptyBorder(UITheme.INPUT_PADDING));
        dropdown.setFocusable(false);
        dropdown.setOpaque(false);

        dropdown.setRenderer(new ModernComboBoxRenderer());
        dropdown.setUI(new ModernComboBoxUI());

        return dropdown;
    }

    private static class ModernComboBoxUI extends javax.swing.plaf.basic.BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton() {
                @Override
                public void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

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

        @Override
        protected ComboPopup createPopup() {
            return new ModernComboPopup(comboBox);
        }
    }

    private static class ModernComboPopup extends javax.swing.plaf.basic.BasicComboPopup {
        public ModernComboPopup(JComboBox combo) {
            super(combo);
        }

        @Override
        protected void configurePopup() {
            super.configurePopup();

            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        }

        @Override
        protected void configureScroller() {
            super.configureScroller();

            if (scroller != null) {
                scroller.setOpaque(false);
                scroller.setBorder(BorderFactory.createEmptyBorder());
                scroller.getViewport().setOpaque(false);

                JScrollBar verticalScrollBar = scroller.getVerticalScrollBar();
                verticalScrollBar.setUI(new ModernScrollBarUI());
                verticalScrollBar.setPreferredSize(new Dimension(8, 0));
            }
        }

        @Override
        protected JList createList() {
            JList list = super.createList();
            list.setOpaque(false);
            list.setBackground(UITheme.CARD_BACKGROUND);
            list.setSelectionBackground(UITheme.SELECTION_BACKGROUND);
            list.setSelectionForeground(UITheme.TEXT_PRIMARY);
            list.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            return list;
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(UITheme.CARD_BACKGROUND);
            g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                    UITheme.BORDER_RADIUS_MEDIUM, UITheme.BORDER_RADIUS_MEDIUM));

            g2d.setColor(UITheme.BORDER_DEFAULT);
            g2d.setStroke(UITheme.BORDER_STROKE_DEFAULT);
            g2d.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2,
                    UITheme.BORDER_RADIUS_MEDIUM, UITheme.BORDER_RADIUS_MEDIUM));

            g2d.setColor(new Color(0, 0, 0, 20));
            g2d.fill(new RoundRectangle2D.Float(2, 2, getWidth(), getHeight(),
                    UITheme.BORDER_RADIUS_MEDIUM, UITheme.BORDER_RADIUS_MEDIUM));

            g2d.dispose();
            super.paintComponent(g);
        }
    }

    private static class ModernComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {

            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            setFont(UITheme.FONT_INPUT);
            setBorder(UITheme.createEmptyBorder(new Insets(10, 15, 10, 15)));
            setOpaque(true);

            if (isSelected) {
                setBackground(UITheme.SELECTION_BACKGROUND);
                setForeground(UITheme.TEXT_PRIMARY);
            } else {
                setBackground(UITheme.CARD_BACKGROUND);
                setForeground(UITheme.TEXT_PRIMARY);
            }

            if (cellHasFocus && !isSelected) {
                setBackground(UITheme.HOVER_BACKGROUND);
            }

            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(getBackground());
            g2d.fill(new RoundRectangle2D.Float(2, 1, getWidth() - 4, getHeight() - 2,
                    UITheme.BORDER_RADIUS_SMALL, UITheme.BORDER_RADIUS_SMALL));

            g2d.dispose();
            super.paintComponent(g);
        }
    }

    private static class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {

        @Override
        protected void configureScrollBarColors() {
            trackColor = UITheme.PANEL_BACKGROUND;
            thumbColor = UITheme.BORDER_LIGHT;
            thumbDarkShadowColor = UITheme.BORDER_DEFAULT;
            thumbHighlightColor = UITheme.BORDER_FOCUS;
            thumbLightShadowColor = UITheme.BORDER_LIGHT;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createInvisibleButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createInvisibleButton();
        }

        private JButton createInvisibleButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(UITheme.PANEL_BACKGROUND);
            g2d.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);

            g2d.dispose();
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }

            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color thumbColor;
            if (isDragging) {
                thumbColor = UITheme.BORDER_FOCUS;
            } else if (isThumbRollover()) {
                thumbColor = UITheme.BORDER_DEFAULT;
            } else {
                thumbColor = UITheme.BORDER_LIGHT;
            }

            int margin = 1;
            int x = thumbBounds.x + margin;
            int y = thumbBounds.y + margin;
            int width = thumbBounds.width - (margin * 2);
            int height = thumbBounds.height - (margin * 2);

            g2d.setColor(thumbColor);
            g2d.fill(new RoundRectangle2D.Float(x, y, width, height, 4, 4));

            g2d.dispose();
        }

        private boolean thumbRollover = false;

        public boolean isThumbRollover() {
            return thumbRollover;
        }
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
            repaint();
            return;
        }

        SignOutRecord record = new SignOutRecord(name, location, phone,
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), null);
        roster.signOut(record);

        nameField.removeAll();
        clearField(locationField, "Where are you going?");
        clearField(phoneField, "Contact number");
        pinField.setText("");

        // Clear checkboxes
        noCacCheckbox.setSelected(false);
        familyLeaveCheckbox.setSelected(false);
        overnightLeaveCheckbox.setSelected(false);

        nameField.grabFocus();
        nameField.setForeground(UITheme.TEXT_PRIMARY);
    }

    private void handleSignIn() {
        if (!authenticateUser()) return;

        SignOutRecord selected = roster.getSelectedRecord();
        if (selected != null) {
            roster.signIn(selected, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
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

    private String getFieldText(UserBubblePanel field) {
        String text = "";
        ArrayList<String> users = (ArrayList<String>) field.getUsers();
        for (int i=0; i< field.getUsers().size(); i++) {
            text += users.get(i)+(i!=field.getUsers().size()?", ":"");
        };
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

    private void setErrorBorder(UserBubblePanel pinField) {
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
            repaint();

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
            repaint();

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
            repaint();

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