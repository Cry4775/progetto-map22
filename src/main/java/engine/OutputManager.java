package engine;

import java.util.Objects;
import gui.MainFrame;

public class OutputManager {
    private static MainFrame gui;
    private static StringBuilder output = new StringBuilder();

    public static void registerGUI(MainFrame mainFrame) {
        Objects.requireNonNull(mainFrame);

        gui = mainFrame;
    }

    public static boolean isOutputEmpty() {
        return output.toString().isEmpty() ? true : false;
    }

    public static void append(StringBuilder stringBuilder) {
        append(stringBuilder.toString());
    }

    public static void append(String string) {
        if (string != null && !string.isEmpty()) {
            if (output.length() > 0) {
                if (output.length() > 1) {
                    if (output.charAt(output.length() - 1) == '\n'
                            && output.charAt(output.length() - 2) == '\n') {
                        output.append(getCleanString(string));
                    } else if (output.charAt(output.length() - 1) == '\n') {
                        output.append("\n");
                        output.append(getCleanString(string));
                    } else {
                        output.append("\n\n");
                        output.append(getCleanString(string));
                    }
                } else if (output.charAt(output.length() - 1) == '\n') {
                    output.append("\n");
                    output.append(getCleanString(string));
                } else {
                    output.append("\n\n");
                    output.append(getCleanString(string));
                }
            } else {
                output.append(getCleanString(string));
            }
        }
    }

    public static String getCleanString(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) != '\n') {
                return string.substring(i, string.length());
            }
        }
        return string;
    }

    public static void print() {
        Objects.requireNonNull(gui);

        gui.appendText(output.toString());

        output.setLength(0);
    }
}
