import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class NumericaGUI extends JFrame {
    private JTextArea codeArea;
    private JTextArea outputArea;

    public NumericaGUI() {
        setTitle("Numerica IDE");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Color bg = new Color(24, 26, 32);
        Color panel = new Color(34, 37, 46);
        Color editor = new Color(18, 20, 26);
        Color text = new Color(230, 230, 230);
        Color accent = new Color(94, 129, 244);
        Color buttonText = Color.WHITE;

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(bg);
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        setContentPane(root);

        JLabel title = new JLabel("Numerica IDE");
        title.setForeground(text);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JLabel subtitle = new JLabel("Write, lex, parse, and run Numerica programs");
        subtitle.setForeground(new Color(170, 175, 190));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(bg);
        header.add(title);
        header.add(subtitle);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(bg);

        JButton lexButton = styledButton("Lex", accent, buttonText);
        JButton parseButton = styledButton("Parse", accent, buttonText);
        JButton runButton = styledButton("Run", new Color(60, 180, 120), buttonText);
        JButton clearButton = styledButton("Clear", new Color(190, 80, 80), buttonText);

        buttonPanel.add(lexButton);
        buttonPanel.add(parseButton);
        buttonPanel.add(runButton);
        buttonPanel.add(clearButton);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(bg);
        top.setBorder(new EmptyBorder(0, 0, 12, 0));
        top.add(header, BorderLayout.WEST);
        top.add(buttonPanel, BorderLayout.EAST);

        root.add(top, BorderLayout.NORTH);

        codeArea = new JTextArea();
        outputArea = new JTextArea();

        styleTextArea(codeArea, editor, text);
        styleTextArea(outputArea, editor, text);

        outputArea.setEditable(false);

        JScrollPane codeScroll = wrapWithTitle("Code Editor", codeArea, panel, text);
        JScrollPane outputScroll = wrapWithTitle("Output", outputArea, panel, text);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, codeScroll, outputScroll);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(null);
        splitPane.setBackground(bg);

        root.add(splitPane, BorderLayout.CENTER);

        lexButton.addActionListener(e -> runLex());
        parseButton.addActionListener(e -> runParse());
        runButton.addActionListener(e -> runProgram());
        clearButton.addActionListener(e -> outputArea.setText(""));

        codeArea.setText("""
# Begin Code Below!
""");
    }

    private JButton styledButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(95, 36));
        return button;
    }

    private void styleTextArea(JTextArea area, Color bg, Color fg) {
        area.setBackground(bg);
        area.setForeground(fg);
        area.setCaretColor(fg);
        area.setFont(new Font("Consolas", Font.PLAIN, 16));
        area.setLineWrap(false);
        area.setTabSize(4);
        area.setBorder(new EmptyBorder(12, 12, 12, 12));
    }

    private JScrollPane wrapWithTitle(String title, JTextArea area, Color panel, Color text) {
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(65, 70, 85)),
                title,
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 14),
                text
        ));
        scrollPane.getViewport().setBackground(panel);
        return scrollPane;
    }

    private void runLex() {
        outputArea.setText("");

        try {
            Lexer lexer = new Lexer(codeArea.getText());
            List<Token> tokens = lexer.tokenize();

            for (Token token : tokens) {
                outputArea.append(token.toString());
                outputArea.append("\n");
            }

        } catch (RuntimeException ex) {
            outputArea.setText(ex.getMessage());
        }
    }

    private void runParse() {
        outputArea.setText("");

        try {
            Lexer lexer = new Lexer(codeArea.getText());
            List<Token> tokens = lexer.tokenize();

            Parser parser = new Parser(tokens);
            Program program = parser.parse();

            outputArea.setText(program.toString());

        } catch (RuntimeException ex) {
            outputArea.setText(ex.getMessage());
        }
    }

    private void runProgram() {
        outputArea.setText("");

        try {
            Lexer lexer = new Lexer(codeArea.getText());
            List<Token> tokens = lexer.tokenize();

            Parser parser = new Parser(tokens);
            Program program = parser.parse();

            GUIOutput.start(outputArea);

            Evaluator evaluator = new Evaluator();
            evaluator.execute(program);

            GUIOutput.stop();

        } catch (RuntimeException ex) {
            GUIOutput.stop();
            outputArea.setText(ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NumericaGUI gui = new NumericaGUI();
            gui.setVisible(true);
        });
    }
}