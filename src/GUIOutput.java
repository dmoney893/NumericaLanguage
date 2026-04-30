import javax.swing.JTextArea;

public class GUIOutput {
    private static JTextArea outputArea = null;

    public static void start(JTextArea area) {
        outputArea = area;
    }

    public static void stop() {
        outputArea = null;
    }

    public static void print(String value) {
        if (outputArea != null) {
            outputArea.append(value);
            outputArea.append("\n");
            outputArea.repaint();
        } else {
            System.out.println(value);
        }
    }
}