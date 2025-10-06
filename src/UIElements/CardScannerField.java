package UIElements;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardScannerField extends JComponent implements FocusListener, KeyListener {
    private StringBuilder buffer = new StringBuilder();
    private boolean scanning = false;
    private int scanTimeoutMs = 100;
    private long lastKeyTime = 0;
    private String placeholderText = "Scan CAC";
    private boolean hasScannedData = false;
    private String displayText = "";
    private JCheckBox noCacCheckbox;

    private JTextField noCacTextField;
    private UserBubblePanel bubblePanel;
    private boolean manualEntryMode = false;

    public CardScannerField(UserBubblePanel ubp) {
        setFocusable(true);
        setPreferredSize(new Dimension(0, UITheme.INPUT_HEIGHT));
        setOpaque(false);
        setLayout(null); // Use absolute positioning

        addFocusListener(this);
        addKeyListener(this);

        setCursor(new Cursor(Cursor.TEXT_CURSOR));

        bubblePanel = ubp;

        noCacTextField = createModernTextField("Enter Name");
        noCacTextField.setBorder(new EmptyBorder(0,0,0,0));
        noCacTextField.setVisible(false);
        noCacTextField.addActionListener(e -> {
            String text = noCacTextField.getText().trim();
            if (!text.isEmpty() && !text.equals("Enter Name")) {
                bubblePanel.addUser(text);
                noCacTextField.setText("");
                noCacCheckbox.setSelected(false);
                manualEntryMode = noCacCheckbox.isSelected();
                updateInputMode();
                updateInputMode();
            }
        });
        add(noCacTextField);

        // Create the checkbox
        noCacCheckbox = createModernCheckbox("No CAC");
        noCacCheckbox.addActionListener(e -> {
            manualEntryMode = noCacCheckbox.isSelected();
            updateInputMode();
        });
        add(noCacCheckbox);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!manualEntryMode) {
                    CardScannerField.this.grabFocus();
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                layoutComponents();
            }
        });
    }

    private void layoutComponents() {
        int width = getWidth();
        int height = getHeight();

        // Position checkbox on the right side
        Dimension checkboxSize = noCacCheckbox.getPreferredSize();
        int checkboxX = width - checkboxSize.width - UITheme.SPACING_LG;
        int checkboxY = (height - checkboxSize.height) / 2;
        noCacCheckbox.setBounds(checkboxX, checkboxY, checkboxSize.width, checkboxSize.height);

        // Position text field to the right of the checkbox, but keep some margin
        int textFieldWidth = 180;
        int leftMargin = 10;
        int textFieldX = Math.max(leftMargin, checkboxX - textFieldWidth - UITheme.SPACING_MD);

        int textFieldY = UITheme.SPACING_SM;
        int textFieldHeight = height - (UITheme.SPACING_SM * 2);
        noCacTextField.setBounds(textFieldX, textFieldY, textFieldWidth, textFieldHeight);
    }

    private void updateInputMode() {
        if (manualEntryMode) {
            // Switch to manual entry mode
            noCacTextField.setVisible(true);
            noCacTextField.grabFocus();
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            setFocusable(false);
        } else {
            // Switch back to scanner mode
            noCacTextField.setVisible(false);
            noCacTextField.setText("");
            setCursor(new Cursor(Cursor.TEXT_CURSOR));
            setFocusable(true);
            grabFocus();
        }
        repaint();
    }

    protected void onCardScanned(String data) {
        hasScannedData = true;
        String r = formatDataFromCac(data);
        if (r != null) {
            bubblePanel.addUser(r);
        }
        repaint();
        clear();
    }

    private String formatDataFromCac(String data) {
        String[] splitdata = data.split("\\.");
        if (splitdata.length < 3) return null;
        System.out.println(Arrays.toString(splitdata));
        String lastname = capitalize(splitdata[0]);
        String firstname = capitalize(splitdata[1]);
        String middlename = capitalize(splitdata[2]);

        return lastname + " " + firstname.charAt(0);
    }

    private String capitalize(String inp) {
        return inp.substring(0, 1).toUpperCase() + inp.toLowerCase().substring(1);
    }

    public String getScannedData() {
        return displayText;
    }

    public void clear() {
        displayText = "";
        hasScannedData = false;
        buffer.setLength(0);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw background
        g2d.setColor(UITheme.INPUT_BACKGROUND);
        g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                UITheme.BORDER_RADIUS_MEDIUM, UITheme.BORDER_RADIUS_MEDIUM));

        if (hasFocus() && !manualEntryMode) {
            g2d.setColor(UITheme.BORDER_FOCUS);
            g2d.setStroke(UITheme.BORDER_STROKE_FOCUS);
        } else {
            g2d.setColor(UITheme.BORDER_LIGHT);
            g2d.setStroke(UITheme.BORDER_STROKE_DEFAULT);
        }
        g2d.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2,
                UITheme.BORDER_RADIUS_SMALL, UITheme.BORDER_RADIUS_SMALL));

        // Only draw scanner text if not in manual entry mode
        if (!manualEntryMode) {
            g2d.setFont(UITheme.FONT_INPUT);
            FontMetrics fm = g2d.getFontMetrics();

            String text;
            Color textColor;

            if (hasScannedData) {
                text = displayText;
                textColor = UITheme.TEXT_PRIMARY;
            } else {
                text = placeholderText;
                textColor = UITheme.TEXT_LIGHT;
            }

            g2d.setColor(textColor);

            int textX = UITheme.SPACING_LG;
            int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

            // Calculate max width considering the checkbox space
            int checkboxSpace = noCacCheckbox.getPreferredSize().width + UITheme.SPACING_LG * 2;
            int maxWidth = getWidth() - (UITheme.SPACING_LG * 2) - checkboxSpace;

            String displayedText = text;
            if (fm.stringWidth(text) > maxWidth) {
                while (fm.stringWidth(displayedText + "...") > maxWidth && displayedText.length() > 0) {
                    displayedText = displayedText.substring(0, displayedText.length() - 1);
                }
                displayedText += "...";
            }

            g2d.drawString(displayedText, textX, textY);

            if (scanning && hasFocus()) {
                g2d.setColor(UITheme.ACCENT_BLUE);
                int indicatorSize = 8;
                int indicatorX = getWidth() - checkboxSpace - UITheme.SPACING_LG - indicatorSize;
                int indicatorY = (getHeight() - indicatorSize) / 2;
                g2d.fillOval(indicatorX, indicatorY, indicatorSize, indicatorSize);
            }
        }

        g2d.dispose();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (manualEntryMode) {
            return;
        }

        char c = e.getKeyChar();
        long now = System.currentTimeMillis();

        if (now - lastKeyTime > scanTimeoutMs) {
            buffer.setLength(0);
            scanning = true;
            repaint();
        }

        lastKeyTime = now;

        if (c == '\n' || c == '\t') {
            scanning = false;
            String scannedData = buffer.toString();
            buffer.setLength(0);

            if (!scannedData.isEmpty()) {
                onCardScanned(scannedData);
            }
            repaint();
        } else if (Character.isLetterOrDigit(c) || c == ';' || c == '%' ||
                c == '=' || c == '?' || c == '^' || c == ' ' || c == '.') {
            buffer.append(c);
        }

        e.consume();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (manualEntryMode) {
            return;
        }

        if ((e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) && !scanning) {
            clear();
            e.consume();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void focusGained(FocusEvent e) {
        repaint();
    }

    @Override
    public void focusLost(FocusEvent e) {
        buffer.setLength(0);
        scanning = false;
        repaint();
    }

    public void setPlaceholder(String placeholder) {
        this.placeholderText = placeholder;
        repaint();
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

        field.setFont(UITheme.FONT_INPUT);
        field.setBorder(UITheme.createEmptyBorder(UITheme.INPUT_PADDING));
        field.setBackground(UITheme.INPUT_BACKGROUND);
        field.setForeground(UITheme.TEXT_PRIMARY);

        setupPlaceholder(field, placeholder);
        return field;
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
}