package UIElements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class UserBubblePanel extends JPanel {

    private List<String> users;
    private List<UserBubble> bubbles;
    private UserRemovalListener removalListener;
    private JLabel emptyLabel;

    public interface UserRemovalListener {
        void onUserRemoved(String userName);
    }

    public UserBubblePanel() {
        users = new ArrayList<>();
        bubbles = new ArrayList<>();

        setFocusable(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, UITheme.SPACING_SM, UITheme.SPACING_SM));
        setOpaque(false);
        setBorder(UITheme.createEmptyBorder(new Insets(UITheme.SPACING_SM, 0, UITheme.SPACING_SM, 0)));

        // Create empty state label
        emptyLabel = new JLabel("No CAC's Scanned Yet");
        emptyLabel.setFont(UITheme.FONT_INPUT);
        emptyLabel.setForeground(UITheme.TEXT_LIGHT);
        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        showEmptyState();
    }

    private void showEmptyState() {
        removeAll();
        setLayout(new BorderLayout());
        add(emptyLabel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showBubbles() {
        if (getLayout() instanceof BorderLayout) {
            removeAll();
            setLayout(new FlowLayout(FlowLayout.LEFT, UITheme.SPACING_SM, UITheme.SPACING_SM));
            setBorder(UITheme.createEmptyBorder(new Insets(UITheme.SPACING_SM, 0, UITheme.SPACING_SM, 0)));

            // Re-add all bubbles
            for (UserBubble bubble : bubbles) {
                add(bubble);
            }
            revalidate();
            repaint();
        }
    }

    public boolean isEmpty() {
        return bubbles.isEmpty();
    }

    /**
     * Add a user to the panel
     */
    public void addUser(String userName) {
        if (!users.contains(userName)) {
            users.add(userName);
            UserBubble bubble = new UserBubble(userName);
            bubbles.add(bubble);

            if (bubbles.size() == 1) {
                showBubbles();
            }

            add(bubble);
            revalidate();
            repaint();
        }
    }

    /**
     * Remove a user from the panel
     */
    public void removeUser(String userName) {
        int index = users.indexOf(userName);
        if (index >= 0) {
            users.remove(index);
            UserBubble bubble = bubbles.remove(index);
            remove(bubble);

            if (bubbles.isEmpty()) {
                showEmptyState();
            } else {
                revalidate();
                repaint();
            }

            if (removalListener != null) {
                removalListener.onUserRemoved(userName);
            }
        }
    }

    /**
     * Clear all users
     */
    public void clearAll() {
        users.clear();
        bubbles.clear();
        showEmptyState();
    }

    /**
     * Get list of all current users
     */
    public List<String> getUsers() {
        return new ArrayList<>(users);
    }

    /**
     * Check if panel has users
     */
    public boolean hasUsers() {
        return !users.isEmpty();
    }

    /**
     * Get user count
     */
    public int getUserCount() {
        return users.size();
    }

    /**
     * Set listener for user removal events
     */
    public void setRemovalListener(UserRemovalListener listener) {
        this.removalListener = listener;
    }

    /**
     * Individual user bubble component
     */
    private class UserBubble extends JPanel {
        private String userName;
        private JLabel nameLabel;
        private RemoveButton removeButton;
        private boolean hovered = false;

        public UserBubble(String userName) {
            this.userName = userName;

            setLayout(new BorderLayout(UITheme.SPACING_SM, 0));
            setOpaque(false);
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            setFocusable(false);

            // Name label
            nameLabel = new JLabel(userName);
            nameLabel.setFont(UITheme.FONT_INPUT);
            nameLabel.setForeground(UITheme.TEXT_PRIMARY);
            nameLabel.setBorder(UITheme.createEmptyBorder(new Insets(
                    UITheme.SPACING_SM,
                    UITheme.SPACING_MD,
                    UITheme.SPACING_SM,
                    UITheme.SPACING_XS
            )));
            nameLabel.setFocusable(false);

            // Remove button
            removeButton = new RemoveButton();
            removeButton.addActionListener(e -> removeUser(userName));
            removeButton.setFocusable(false);
            add(nameLabel, BorderLayout.CENTER);
            add(removeButton, BorderLayout.EAST);

            // Mouse hover effects
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });

            FontMetrics fm = nameLabel.getFontMetrics(nameLabel.getFont());
            int width = fm.stringWidth(userName) + UITheme.SPACING_MD + UITheme.SPACING_XS + 24 + UITheme.SPACING_SM;
            int height = Math.max(fm.getHeight() + UITheme.SPACING_SM * 2, 32);
            setPreferredSize(new Dimension(width, height));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background color with hover effect
            Color bgColor = hovered ? UITheme.HOVER_BACKGROUND : UITheme.SELECTION_BACKGROUND;
            g2d.setColor(bgColor);
            g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                    20, 20)); // Full rounded pill shape

            // Border
            g2d.setColor(UITheme.SELECTION_BORDER);
            g2d.setStroke(UITheme.BORDER_STROKE_DEFAULT);
            g2d.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1,
                    20, 20));

            g2d.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Custom remove button (X icon)
     */
    private class RemoveButton extends JButton {
        private boolean hovered = false;

        public RemoveButton() {
            setPreferredSize(new Dimension(22, 22));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            int size = Math.min(getWidth(), getHeight());
            int padding = 6;
            int offsetY = 5;
            int offsetX = -4;

            // Draw X
            Color xColor = hovered || getModel().isPressed() ?
                    UITheme.ACCENT_RED : UITheme.TEXT_MUTED;
            g2d.setColor(xColor);
            g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int x1 = padding + offsetX;
            int y1 = padding + offsetY;
            int x2 = size - padding + offsetX;
            int y2 = size - padding + offsetY;

            g2d.drawLine(x1, y1, x2, y2);
            g2d.drawLine(x2, y1, x1, y2);

            g2d.dispose();
        }
    }
}