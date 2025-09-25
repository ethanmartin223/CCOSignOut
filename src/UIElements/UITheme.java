package UIElements;

import java.awt.*;

public class UITheme {

    // Primary Background Colors
    public static final Color MAIN_BACKGROUND = new Color(248, 250, 252);
    public static final Color PANEL_BACKGROUND = Color.WHITE;
    public static final Color CARD_BACKGROUND = Color.WHITE;
    public static final Color INPUT_BACKGROUND = new Color(249, 250, 251);

    // Text Colors
    public static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    public static final Color TEXT_SECONDARY = new Color(55, 65, 81);
    public static final Color TEXT_MUTED = new Color(107, 114, 128);
    public static final Color TEXT_LIGHT = new Color(156, 163, 175);
    public static final Color TEXT_ON_DARK = Color.WHITE;

    // Accent Colors
    public static final Color ACCENT_BLUE = new Color(59, 130, 246);
    public static final Color ACCENT_GREEN = new Color(16, 185, 129);
    public static final Color ACCENT_RED = new Color(239, 68, 68);
    public static final Color ACCENT_ORANGE = new Color(245, 158, 11);

    // Border Colors
    public static final Color BORDER_DEFAULT = new Color(229, 231, 235);
    public static final Color BORDER_LIGHT = new Color(209, 213, 219);
    public static final Color BORDER_FOCUS = ACCENT_BLUE;

    // Selection and Hover Colors
    public static final Color SELECTION_BACKGROUND = new Color(239, 246, 255);
    public static final Color SELECTION_BORDER = new Color(191, 219, 254);
    public static final Color HOVER_BACKGROUND = new Color(243, 244, 246);
    public static final Color ALTERNATING_ROW = new Color(249, 250, 251);

    // Status Colors
    public static final Color STATUS_SIGNED_OUT = ACCENT_RED;
    public static final Color STATUS_SELECTED = ACCENT_BLUE;
    public static final Color STATUS_SUCCESS = ACCENT_GREEN;
    public static final Color STATUS_WARNING = ACCENT_ORANGE;

    // Font Family
    public static final String FONT_FAMILY = "Inter";

    // Font Sizes
    public static final int FONT_SIZE_SMALL = 12;
    public static final int FONT_SIZE_NORMAL = 13;
    public static final int FONT_SIZE_MEDIUM = 14;
    public static final int FONT_SIZE_LARGE = 16;
    public static final int FONT_SIZE_XL = 18;
    public static final int FONT_SIZE_XXL = 20;

    // Font Weights
    public static final int FONT_WEIGHT_NORMAL = Font.PLAIN;
    public static final int FONT_WEIGHT_SEMIBOLD = Font.BOLD;
    public static final int FONT_WEIGHT_BOLD = Font.BOLD;

    // Font Instances
    public static final Font FONT_TITLE = new Font(FONT_FAMILY, FONT_WEIGHT_BOLD, FONT_SIZE_XXL);
    public static final Font FONT_SUBTITLE = new Font(FONT_FAMILY, FONT_WEIGHT_NORMAL, FONT_SIZE_MEDIUM);
    public static final Font FONT_SECTION_TITLE = new Font(FONT_FAMILY, FONT_WEIGHT_BOLD, FONT_SIZE_XL);
    public static final Font FONT_LABEL = new Font(FONT_FAMILY, FONT_WEIGHT_BOLD, FONT_SIZE_MEDIUM);
    public static final Font FONT_INPUT = new Font(FONT_FAMILY, FONT_WEIGHT_NORMAL, FONT_SIZE_MEDIUM);
    public static final Font FONT_BUTTON = new Font(FONT_FAMILY, FONT_WEIGHT_BOLD, FONT_SIZE_MEDIUM);
    public static final Font FONT_LIST_ITEM_NAME = new Font(FONT_FAMILY, FONT_WEIGHT_SEMIBOLD, FONT_SIZE_LARGE);
    public static final Font FONT_LIST_ITEM_DETAIL = new Font(FONT_FAMILY, FONT_WEIGHT_NORMAL, FONT_SIZE_NORMAL);

    // === DIMENSIONS ===

    // Border Radius
    public static final int BORDER_RADIUS_SMALL = 6;
    public static final int BORDER_RADIUS_MEDIUM = 8;
    public static final int BORDER_RADIUS_LARGE = 12;

    // Component Heights
    public static final int INPUT_HEIGHT = 42;
    public static final int BUTTON_HEIGHT = 45;
    public static final int BUTTON_HEIGHT_SMALL = 42;

    // Spacing
    public static final int SPACING_XS = 2;
    public static final int SPACING_SM = 5;
    public static final int SPACING_MD = 10;
    public static final int SPACING_LG = 15;
    public static final int SPACING_XL = 20;
    public static final int SPACING_XXL = 25;
    public static final int SPACING_XXXL = 30;

    // Padding
    public static final Insets PADDING_SMALL = new Insets(SPACING_SM, SPACING_SM, SPACING_SM, SPACING_SM);
    public static final Insets PADDING_MEDIUM = new Insets(SPACING_MD, SPACING_MD, SPACING_MD, SPACING_MD);
    public static final Insets PADDING_LARGE = new Insets(SPACING_LG, SPACING_LG, SPACING_LG, SPACING_LG);
    public static final Insets PADDING_XL = new Insets(SPACING_XL, SPACING_XL, SPACING_XL, SPACING_XL);

    // Input padding
    public static final Insets INPUT_PADDING = new Insets(SPACING_MD, SPACING_LG, SPACING_MD, SPACING_LG);

    // Panel padding
    public static final Insets PANEL_PADDING = new Insets(SPACING_XXL, SPACING_XXL, SPACING_XXL, SPACING_XXL);
    public static final Insets CARD_PADDING = new Insets(SPACING_LG, SPACING_XL, SPACING_LG, SPACING_XL);
    public static final Insets TITLE_BAR_PADDING = new Insets(SPACING_LG, SPACING_XXXL, SPACING_LG, SPACING_XXXL);
    public static final Insets MAIN_CONTENT_PADDING = new Insets(SPACING_XL, SPACING_XXXL, SPACING_XXXL, SPACING_XXXL);

    // === STROKE WIDTHS ===

    public static final int STROKE_THIN = 1;
    public static final int STROKE_MEDIUM = 2;
    public static final int STROKE_THICK = 3;

    // Border Strokes
    public static final BasicStroke BORDER_STROKE_DEFAULT = new BasicStroke(STROKE_THIN);
    public static final BasicStroke BORDER_STROKE_FOCUS = new BasicStroke(STROKE_MEDIUM);

    // === COMPONENT SIZES ===

    public static final Dimension WINDOW_SIZE = new Dimension(1000, 650);
    public static final Dimension OPTIONS_PANEL_SIZE = new Dimension(350, 0);
    public static final Dimension SEARCH_FIELD_SIZE = new Dimension(300, INPUT_HEIGHT);
    public static final Dimension SEARCH_BUTTON_SIZE = new Dimension(100, INPUT_HEIGHT);
    public static final Dimension STATUS_INDICATOR_SIZE = new Dimension(12, 0);


    public static Color getBrighterColor(Color color) {
        return new Color(
                Math.min(255, color.getRed() + 20),
                Math.min(255, color.getGreen() + 20),
                Math.min(255, color.getBlue() + 20)
        );
    }

    public static Color getDarkerColor(Color color) {
        return color.darker();
    }

    public static javax.swing.border.Border createEmptyBorder(Insets insets) {
        return javax.swing.BorderFactory.createEmptyBorder(
                insets.top, insets.left, insets.bottom, insets.right
        );
    }

    public static javax.swing.border.Border createSectionBorder() {
        return javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createMatteBorder(STROKE_THIN, 0, 0, 0, BORDER_DEFAULT),
                createEmptyBorder(new Insets(SPACING_XL, 0, 0, 0))
        );
    }
}