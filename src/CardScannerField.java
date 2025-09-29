
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CardScannerField extends JComponent implements FocusListener, KeyListener {
    private StringBuilder buffer = new StringBuilder();
    private boolean scanning = false;
    private int scanTimeoutMs = 100; // If delay between chars >100ms, assume not scanning
    private long lastKeyTime = 0;

    public CardScannerField() {
        setFocusable(true);
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        setPreferredSize(new Dimension(200, 40));

        addFocusListener(this);
        addKeyListener(this);
    }

    /** Called when a complete card scan is detected */
    protected void onCardScanned(String data) {
        JOptionPane.showMessageDialog(this, "Scanned: " + data);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Visual cue
        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        String text = hasFocus() ? "Scan card..." : "Click to focus";
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = (getHeight() + fm.getAscent()) / 2 - 3;
        g2.drawString(text, x, y);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        long now = System.currentTimeMillis();

        // Reset buffer if delay between keys is too long
        if (now - lastKeyTime > scanTimeoutMs) {
            buffer.setLength(0);
            scanning = true;
        }

        lastKeyTime = now;

        // If Enter or Tab, consider scan complete
        if (c == '\n' || c == '\t') {
            scanning = false;
            String scannedData = buffer.toString();
            buffer.setLength(0);

            if (!scannedData.isEmpty()) {
                onCardScanned(scannedData);
            }
        } else if (Character.isLetterOrDigit(c) || c == ';' || c == '%') {
            // Accept typical magnetic stripe characters
            buffer.append(c);
        }

        e.consume(); // prevent normal typing
    }

    @Override public void keyPressed(KeyEvent e) { /* no-op */ }
    @Override public void keyReleased(KeyEvent e) { /* no-op */ }

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

    // Test the component
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Card Scanner Field");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new FlowLayout());
            frame.add(new JLabel("Card Scanner Input:"));

            CardScannerField scannerField = new CardScannerField();
            frame.add(scannerField);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Give initial focus
            scannerField.requestFocusInWindow();
        });
    }
}
