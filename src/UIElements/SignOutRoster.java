package UIElements;

import UIElements.SignOutRecord;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class SignOutRoster extends JList<SignOutRecord> {

    public SignOutOptions signOutOptions;
    private DefaultListModel<SignOutRecord> model;
    private File csvFile;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy_MM_dd");
    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
    private Timer dayRolloverTimer;
    private LocalDate currentTrackedDate;

    public SignOutRoster(SignOutOptions op) {
        model = new DefaultListModel<>();
        setModel(model);

        signOutOptions = op;

        setupCSVFile();
        loadFromCSV();

        // Initialize day tracking and start rollover monitoring
        currentTrackedDate = LocalDate.now();
        startDayRolloverTimer();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                signOutOptions.grabFocus();
            }
        });

        setCellRenderer(new ModernRosterCellRenderer());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setBackground(UITheme.PANEL_BACKGROUND);
        setBorder(UITheme.createEmptyBorder(new Insets(UITheme.SPACING_MD, 0, UITheme.SPACING_MD, 0)));

        // Apply custom scrollbar styling when this component is added to a scroll pane
        SwingUtilities.invokeLater(() -> {
            Container parent = getParent();
            while (parent != null && !(parent instanceof JScrollPane)) {
                parent = parent.getParent();
            }
            if (parent instanceof JScrollPane) {
                applyModernScrollbarStyling((JScrollPane) parent);
            }
        });
    }

    /** Starts the timer that checks for day rollover every minute */
    private void startDayRolloverTimer() {
        // Check every 60 seconds for day rollover
        dayRolloverTimer = new Timer(60000, e -> checkForDayRollover());
        dayRolloverTimer.start();

        System.out.println("Day rollover monitoring started - checking every minute");
    }

    /** Checks if a new day has started and handles the transition */
    private void checkForDayRollover() {
        LocalDate today = LocalDate.now();

        if (!today.equals(currentTrackedDate)) {
            System.out.println("Day rollover detected: " + currentTrackedDate + " -> " + today);
            handleDayRollover(today);
            currentTrackedDate = today;
        }
    }

    /** Handles the transition to a new day */
    private void handleDayRollover(LocalDate newDate) {
        // Get all currently signed out people
        List<SignOutRecord> currentlyOut = getCurrentlySignedOutPeople();

        if (!currentlyOut.isEmpty()) {
            System.out.println("Found " + currentlyOut.size() + " people still signed out, transferring to new day");

            // Create new CSV file for the new day
            String newDateStr = newDate.format(DateTimeFormatter.ofPattern("yyyy_MM_dd"));
            String newFilename = "CCO_SIGNOUTS_" + newDateStr + ".csv";
            File newCsvFile = new File(newFilename);

            // Create the new CSV file with header
            createNewCSVFile(newCsvFile);

            // Add all currently signed out people to the new CSV
            transferSignedOutPeopleToNewDay(currentlyOut, newCsvFile, newDate);

            // Update the current CSV file reference
            csvFile = newCsvFile;

            // Show notification in the UI
            SwingUtilities.invokeLater(() -> {
                if (signOutOptions != null) {
                    JOptionPane.showMessageDialog(signOutOptions,
                            "New day detected! " + currentlyOut.size() + " people transferred to " + newDateStr + " records.",
                            "Day Rollover", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        } else {
            // No one currently signed out, just create new file for new day
            setupCSVFile();
            System.out.println("New day started with no one signed out");
        }
    }

    /** Gets all people currently signed out from the model */
    private List<SignOutRecord> getCurrentlySignedOutPeople() {
        List<SignOutRecord> currentlyOut = new ArrayList<>();

        for (int i = 0; i < model.getSize(); i++) {
            SignOutRecord record = model.getElementAt(i);
            // People in the model are currently signed out (no sign-in time)
            if (record.getTimeSignedIn() == null || record.getTimeSignedIn().isEmpty()) {
                currentlyOut.add(record);
            }
        }

        return currentlyOut;
    }

    /** Creates a new CSV file with the proper header */
    private void createNewCSVFile(File newCsvFile) {
        try {
            newCsvFile.createNewFile();
            try (FileWriter writer = new FileWriter(newCsvFile, false)) {
                writer.write("PeopleSignedOut,Location,TimeOut,TimeIn,Phone\n");
            }
        } catch (IOException e) {
            System.err.println("Error creating new CSV file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Transfers currently signed out people to the new day's CSV file */
    private void transferSignedOutPeopleToNewDay(List<SignOutRecord> signedOutPeople, File newCsvFile, LocalDate newDate) {
        String rolloverTime = timeFormatter.format(new Date());

        try (FileWriter writer = new FileWriter(newCsvFile, true)) {
            for (SignOutRecord record : signedOutPeople) {
                // Write to new CSV with rollover time as sign-out time
                writer.write(
                        record.getName().replace(", ", "%") + "," +
                                record.getLocation() + " (Carried over)" + "," +
                                rolloverTime + "," +
                                "" + "," + // Empty sign-in time
                                record.getPhone() + "\n"
                );

                // Update the record's sign-out time to reflect the rollover
                record.setTimeSignedOut(rolloverTime);

                System.out.println("Transferred: " + record.getName() + " to new day");
            }
        } catch (IOException e) {
            System.err.println("Error transferring people to new day: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Prepares today's CSV file */
    private void setupCSVFile() {
        String todayStr = dateFormatter.format(new Date());
        String filename = "CCO_SIGNOUTS_" + todayStr + ".csv";
        csvFile = new File(filename);

        if (!csvFile.exists()) {
            createNewCSVFile(csvFile);
        }
    }

    /** Loads the roster from today's CSV file if it exists */
    private void loadFromCSV() {
        if (!csvFile.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 5) {
                    String names = parts[0].replace("%", ", "); // Display nicely
                    String location = parts[1];
                    String timeout = parts[2];
                    String timein = parts[3];
                    String phone = parts[4];

                    if (timein.equals(""))
                        model.addElement(new SignOutRecord(names, location, phone, timeout, null));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void signOut(SignOutRecord record) {
        model.addElement(record);
        appendToCSV(record, timeFormatter.format(new Date()), "");
    }

    public void signIn(SignOutRecord record, String time) {
        record.setTimeSignedIn(time);
        model.removeElement(record);
        updateTimeInCSV(record);
    }

    public SignOutRecord getSelectedRecord() {
        return getSelectedValue();
    }

    private void appendToCSV(SignOutRecord record, String timeOut, String timeIn) {
        try (FileWriter writer = new FileWriter(csvFile, true)) {
            writer.write(
                    record.getName().replace(", ", "%") + "," +
                            record.getLocation() + "," +
                            timeOut + "," +
                            timeIn + "," +
                            record.getPhone() + "\n"
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Update TimeIn column for the record when signed in */
    private void updateTimeInCSV(SignOutRecord record) {
        try {
            java.util.List<String> lines = Files.readAllLines(csvFile.toPath());
            for (int i = 1; i < lines.size(); i++) { // Skip header
                String[] parts = lines.get(i).split(",", -1);
                if (parts.length >= 5) {
                    String names = parts[0].replace("%", ", ");
                    String phone = parts[4];

                    // Match by name & phone
                    if (names.equals(record.getName()) && phone.equals(record.getPhone())) {
                        parts[3] = timeFormatter.format(new Date());
                        lines.set(i, String.join(",", parts));
                        break;
                    }
                }
            }
            Files.write(csvFile.toPath(), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Stops the day rollover timer when the component is no longer needed */
    public void stopDayRolloverTimer() {
        if (dayRolloverTimer != null) {
            dayRolloverTimer.stop();
            System.out.println("Day rollover monitoring stopped");
        }
    }

    /** Gets the current CSV file being used */
    public File getCurrentCSVFile() {
        return csvFile;
    }

    /** Gets the current tracked date */
    public LocalDate getCurrentTrackedDate() {
        return currentTrackedDate;
    }

    /** Applies modern scrollbar styling to match the UITheme */
    private void applyModernScrollbarStyling(JScrollPane scrollPane) {
        // Style the vertical scrollbar
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUI(new ModernScrollBarUI());
        verticalScrollBar.setPreferredSize(new Dimension(12, 0));

        // Style the horizontal scrollbar
        JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
        horizontalScrollBar.setUI(new ModernScrollBarUI());
        horizontalScrollBar.setPreferredSize(new Dimension(0, 12));

        // Remove scrollpane borders and styling
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(UITheme.PANEL_BACKGROUND);
        scrollPane.getViewport().setBackground(UITheme.PANEL_BACKGROUND);
    }

    /** Custom ScrollBar UI that matches the modern theme */
    static class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {

        @Override
        protected void configureScrollBarColors() {
            // Set colors to match theme
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

            // Paint track background
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

            // Determine thumb color based on state
            Color thumbColor;
            if (isDragging) {
                thumbColor = UITheme.BORDER_FOCUS;
            } else if (isThumbRollover()) {
                thumbColor = UITheme.BORDER_DEFAULT;
            } else {
                thumbColor = UITheme.BORDER_LIGHT;
            }

            int margin = 2;
            int x = thumbBounds.x + margin;
            int y = thumbBounds.y + margin;
            int width = thumbBounds.width - (margin * 2);
            int height = thumbBounds.height - (margin * 2);

            g2d.setColor(thumbColor);
            g2d.fill(new RoundRectangle2D.Float(x, y, width, height, 6, 6));

            g2d.dispose();
        }

        @Override
        protected void setThumbRollover(boolean active) {
            if (thumbRollover != active) {
                scrollbar.repaint(getThumbBounds());
            }
            thumbRollover = active;
        }

        private boolean thumbRollover = false;

        @Override
        protected TrackListener createTrackListener() {
            return new TrackListener() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    setThumbRollover(true);
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    setThumbRollover(false);
                }
            };
        }
    }

    // ---------------------------------------------------
    // --------- Modern Roster Cell Renderer ------------
    // ---------------------------------------------------
    private static class ModernRosterCellRenderer extends JPanel implements ListCellRenderer<SignOutRecord> {
        private JLabel nameLabel;
        private JLabel locationLabel;
        private JLabel phoneLabel;
        private boolean isSelected;
        private boolean cellHasFocus;

        public ModernRosterCellRenderer() {
            setLayout(new BorderLayout(UITheme.SPACING_LG, 0));
            setOpaque(false);
            setBorder(UITheme.createEmptyBorder(new Insets(UITheme.SPACING_LG, UITheme.SPACING_XL, UITheme.SPACING_LG, UITheme.SPACING_XL)));

            JPanel contentPanel = new JPanel(new BorderLayout(UITheme.SPACING_MD, 0));
            contentPanel.setOpaque(false);


            nameLabel = new JLabel();
            nameLabel.setFont(UITheme.FONT_LIST_ITEM_NAME);
            nameLabel.setForeground(UITheme.TEXT_PRIMARY);
            nameLabel.setHorizontalAlignment(SwingConstants.LEFT);

            locationLabel = new JLabel();
            locationLabel.setFont(UITheme.FONT_LIST_ITEM_DETAIL);
            locationLabel.setForeground(UITheme.TEXT_MUTED);
            locationLabel.setHorizontalAlignment(SwingConstants.CENTER);

            phoneLabel = new JLabel();
            phoneLabel.setFont(UITheme.FONT_LIST_ITEM_DETAIL);
            phoneLabel.setForeground(UITheme.TEXT_MUTED);
            phoneLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            phoneLabel.setPreferredSize(new Dimension(120, 0));

            add(nameLabel, BorderLayout.WEST);
            contentPanel.add(locationLabel, BorderLayout.WEST);
            contentPanel.add(phoneLabel, BorderLayout.EAST);
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

