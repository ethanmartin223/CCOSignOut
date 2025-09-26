
package UIElements;

import UIElements.SignOutRecord;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class SignOutRoster extends JList<SignOutRecord> {

    private DefaultListModel<SignOutRecord> model;

    public SignOutRoster() {
        model = new DefaultListModel<>();
        setModel(model);

        setCellRenderer(new ModernRosterCellRenderer());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setBackground(UITheme.PANEL_BACKGROUND);
        setBorder(UITheme.createEmptyBorder(new Insets(UITheme.SPACING_MD, 0, UITheme.SPACING_MD, 0)));

        // Sample data
        signOut(new SignOutRecord("Bodden, Martin, Gallagher", "Troop Store", "717-330-3661"));
        signOut(new SignOutRecord("Gonzalez, Drose", "Off Post/Mall", "198-986-8987"));
        signOut(new SignOutRecord("Augstin, Ponstein", "Qudoba", "823-812-1239"));
        signOut(new SignOutRecord("Petrilli, Siso, Wall, Garza", "Off post/Buffalo wild Wings", "782-178-1287"));
        signOut(new SignOutRecord("Johnson, Padrone", "Dominos", "123-123-1234"));
        signOut(new SignOutRecord("Ray, Combs", "PX", "823-812-1239"));
        signOut(new SignOutRecord("Byam, Bus", "Dominos", "999-999-9999"));
        signOut(new SignOutRecord("McDonnell, Charles", "Personal PT", "128-984-0182"));
    }

    public void signOut(SignOutRecord record) {
        model.addElement(record);
    }

    public void signIn(SignOutRecord record) {
        model.removeElement(record);
    }

    public SignOutRecord getSelectedRecord() {
        return getSelectedValue();
    }

    private static class ModernRosterCellRenderer extends JPanel implements ListCellRenderer<SignOutRecord> {
        private JLabel nameLabel;
        private JLabel locationLabel;
        private JLabel phoneLabel;
        private JPanel statusIndicator;
        private boolean isSelected;
        private boolean cellHasFocus;

        public ModernRosterCellRenderer() {
            setLayout(new BorderLayout(UITheme.SPACING_LG, 0));
            setOpaque(false);
            setBorder(UITheme.createEmptyBorder(new Insets(UITheme.SPACING_LG, UITheme.SPACING_XL, UITheme.SPACING_LG, UITheme.SPACING_XL)));

            statusIndicator = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    Color indicatorColor = isSelected ? UITheme.STATUS_SELECTED : UITheme.STATUS_SIGNED_OUT;
                    g2d.setColor(indicatorColor);
                    g2d.fillOval(2, getHeight() / 2 - 4, 8, 8);

                    g2d.dispose();
                }
            };
            statusIndicator.setPreferredSize(UITheme.STATUS_INDICATOR_SIZE);
            statusIndicator.setOpaque(false);

            // Main content panel with horizontal layout
            JPanel contentPanel = new JPanel(new BorderLayout(UITheme.SPACING_MD, 0));
            contentPanel.setOpaque(false);

            // Name label (left side)
            nameLabel = new JLabel();
            nameLabel.setFont(UITheme.FONT_LIST_ITEM_NAME);
            nameLabel.setForeground(UITheme.TEXT_PRIMARY);
            nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
            nameLabel.setPreferredSize(new Dimension(200, 0)); // Fixed width for consistent alignment

            // Location label (center)
            locationLabel = new JLabel();
            locationLabel.setFont(UITheme.FONT_LIST_ITEM_DETAIL);
            locationLabel.setForeground(UITheme.TEXT_MUTED);
            locationLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Phone label (right side)
            phoneLabel = new JLabel();
            phoneLabel.setFont(UITheme.FONT_LIST_ITEM_DETAIL);
            phoneLabel.setForeground(UITheme.TEXT_MUTED);
            phoneLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            phoneLabel.setPreferredSize(new Dimension(120, 0)); // Fixed width for consistent alignment

            contentPanel.add(nameLabel, BorderLayout.WEST);
            contentPanel.add(locationLabel, BorderLayout.CENTER);
            contentPanel.add(phoneLabel, BorderLayout.EAST);

            add(statusIndicator, BorderLayout.WEST);
            add(contentPanel, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends SignOutRecord> list,
                SignOutRecord record,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            this.isSelected = isSelected;
            this.cellHasFocus = cellHasFocus;

            if (record != null) {
                nameLabel.setText(record.getName());
                locationLabel.setText(record.getLocation());
                phoneLabel.setText(record.getPhone());
            }

            if (isSelected) {
                setBackground(UITheme.SELECTION_BACKGROUND);
                nameLabel.setForeground(UITheme.ACCENT_BLUE.darker());
                locationLabel.setForeground(UITheme.ACCENT_BLUE);
                phoneLabel.setForeground(UITheme.TEXT_SECONDARY);
            } else {
                setBackground(index % 2 == 0 ? UITheme.ALTERNATING_ROW : UITheme.PANEL_BACKGROUND);
                nameLabel.setForeground(UITheme.TEXT_PRIMARY);
                locationLabel.setForeground(UITheme.TEXT_SECONDARY);
                phoneLabel.setForeground(UITheme.TEXT_MUTED);
            }

            // Subtle hover effect for focus
            if (cellHasFocus && !isSelected) {
                setBackground(UITheme.HOVER_BACKGROUND);
            }

            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (getBackground() != null) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(getBackground());
                g2d.fill(new RoundRectangle2D.Float(UITheme.SPACING_SM, UITheme.SPACING_XS,
                        getWidth() - UITheme.SPACING_MD, getHeight() - UITheme.SPACING_XS * 2,
                        UITheme.BORDER_RADIUS_MEDIUM, UITheme.BORDER_RADIUS_MEDIUM));

                // Subtle border for selected items
                if (isSelected) {
                    g2d.setColor(UITheme.SELECTION_BORDER);
                    g2d.setStroke(UITheme.BORDER_STROKE_DEFAULT);
                    g2d.draw(new RoundRectangle2D.Float(UITheme.SPACING_SM, UITheme.SPACING_XS,
                            getWidth() - UITheme.SPACING_MD, getHeight() - UITheme.SPACING_XS * 2,
                            UITheme.BORDER_RADIUS_MEDIUM, UITheme.BORDER_RADIUS_MEDIUM));
                }

                g2d.dispose();
            }
            super.paintComponent(g);
        }
    }
}