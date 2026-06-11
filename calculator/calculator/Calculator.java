import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// CSC426 calculator. Swing GUI that shows the whole expression you type,
// for example 2+4-93, and prints the answer after = is pressed (2+4-93=-87).
// Supports + - * / \ ^ % with normal operator precedence, plus Clear and backspace.
public class Calculator extends JFrame {

    private String expr = "";        // the expression currently on screen
    private String lastResult = "";
    private boolean evaluated = false;

    private final JTextField display = new JTextField("0");

    // light teal palette
    private static final Color FRAME_BG  = new Color(0xF2, 0xF5, 0xF4);
    private static final Color SURFACE   = Color.WHITE;
    private static final Color BORDER    = new Color(0xD3, 0xDD, 0xDB);
    private static final Color TEXT      = new Color(0x1F, 0x2A, 0x37);
    private static final Color MUTED     = new Color(0x60, 0x6B, 0x66);
    private static final Color OP_BG     = new Color(0xDE, 0xF0, 0xEE);
    private static final Color OP_TEXT   = new Color(0x0F, 0x76, 0x6E);
    private static final Color FUNC_BG   = new Color(0xED, 0xF1, 0xF0);
    private static final Color EQ_BG     = new Color(0x0F, 0x76, 0x6E);
    private static final Color CLEAR_TXT = new Color(0xB3, 0x26, 0x1E);

    public Calculator() {
        setTitle("CSC426 Calculator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);                       // wide and short by default
        setMinimumSize(new Dimension(520, 360));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(FRAME_BG);
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        root.add(buildDisplay(), BorderLayout.NORTH);
        root.add(buildKeypad(), BorderLayout.CENTER);
        root.add(buildEquals(), BorderLayout.SOUTH);

        setContentPane(root);
        attachKeyboardSupport(root);
    }

    private JComponent buildDisplay() {
        display.setEditable(false);
        display.setFocusable(false);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setBackground(SURFACE);
        display.setForeground(TEXT);
        display.setFont(new Font("Consolas", Font.PLAIN, 30));
        display.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER, 1, true), new EmptyBorder(16, 16, 16, 16)));
        return display;
    }

    private JComponent buildKeypad() {
        JPanel grid = new JPanel(new GridLayout(4, 5, 8, 8));
        grid.setBackground(FRAME_BG);
        String[][] rows = {
            {"Clear", "<-", "%", "\\", "^"},
            {"7", "8", "9", "/", "*"},
            {"4", "5", "6", "-", "+"},
            {"1", "2", "3", "0", "."},
        };
        for (String[] row : rows) {
            for (String label : row) {
                Color bg, fg;
                if (label.equals("Clear")) { bg = FUNC_BG; fg = CLEAR_TXT; }
                else if (label.equals("<-")) { bg = FUNC_BG; fg = MUTED; }
                else if (label.length() == 1 && isOperatorChar(label.charAt(0))) { bg = OP_BG; fg = OP_TEXT; }
                else { bg = SURFACE; fg = TEXT; }
                grid.add(makeButton(label, bg, fg, 18));
            }
        }
        return grid;
    }

    private JComponent buildEquals() {
        JButton eq = makeButton("=", EQ_BG, Color.WHITE, 20);
        eq.setPreferredSize(new Dimension(10, 56));
        return eq;
    }

    private JButton makeButton(String label, Color bg, Color fg, int size) {
        JButton b = new JButton(label);
        b.setFont(new Font("Segoe UI", Font.PLAIN, size));
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setBorder(new LineBorder(BORDER, 1, true));
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> handleInput(label));
        return b;
    }

    private void handleInput(String t) {
        if (t.equals("Clear")) clear();
        else if (t.equals("<-")) backspace();
        else if (t.equals("=") || t.equals("Enter")) equalsPressed();
        else if (t.equals(".")) dot();
        else if (t.length() == 1 && isOperatorChar(t.charAt(0))) operator(t);
        else digit(t);
    }

    private void clear() {
        expr = "";
        evaluated = false;
        display.setText("0");
    }

    private void digit(String d) {
        if (evaluated) { expr = d; evaluated = false; }
        else expr += d;
        showExpr();
    }

    private void dot() {
        if (evaluated) { expr = "0."; evaluated = false; }
        else if (expr.isEmpty() || lastCharIsOperator(expr)) expr += "0.";
        else if (!lastNumberHasDot(expr)) expr += ".";
        showExpr();
    }

    private void operator(String op) {
        if (evaluated) evaluated = false;          // keep result as the start of the next sum
        if (expr.isEmpty()) {
            if (op.equals("-")) expr = "-";         // allow a negative number to start
        } else if (lastCharIsOperator(expr)) {
            expr = expr.substring(0, expr.length() - 1) + op;   // swap the trailing operator
        } else {
            expr += op;
        }
        showExpr();
    }

    private void backspace() {
        if (evaluated) { expr = ""; evaluated = false; }
        else if (!expr.isEmpty()) expr = expr.substring(0, expr.length() - 1);
        showExpr();
    }

    private void equalsPressed() {
        if (expr.isEmpty() || lastCharIsOperator(expr)) return;
        try {
            double r = evaluate(expr);
            String rs = format(r);
            display.setText(expr + "=" + rs);
            lastResult = rs;
            expr = rs;
            evaluated = true;
        } catch (Exception ex) {
            display.setText("Error");
            expr = "";
            evaluated = false;
        }
    }

    private void showExpr() {
        display.setText(expr.isEmpty() ? "0" : expr);
    }

    private static boolean isOperatorChar(char c) {
        return "+-*/\\^%".indexOf(c) >= 0;
    }

    private static boolean lastCharIsOperator(String s) {
        return !s.isEmpty() && isOperatorChar(s.charAt(s.length() - 1));
    }

    // does the number currently being typed already contain a decimal point
    private static boolean lastNumberHasDot(String s) {
        for (int i = s.length() - 1; i >= 0; i--) {
            char c = s.charAt(i);
            if (isOperatorChar(c)) return false;
            if (c == '.') return true;
        }
        return false;
    }

    private String format(double v) {
        if (v == Math.rint(v) && !Double.isInfinite(v)) return Long.toString((long) v);
        return Double.toString(v);
    }

    // ---- expression evaluator (recursive descent, respects precedence) ----
    private static String S;
    private static int P;

    private static double evaluate(String s) {
        S = s; P = 0;
        double v = parseExpr();
        if (P < S.length()) throw new RuntimeException("unexpected input");
        if (Double.isNaN(v) || Double.isInfinite(v)) throw new ArithmeticException("undefined");
        return v;
    }

    private static char peek() { return P < S.length() ? S.charAt(P) : '\0'; }
    private static char next() { return S.charAt(P++); }

    private static double parseExpr() {           // + and -
        double x = parseTerm();
        while (peek() == '+' || peek() == '-') {
            char op = next();
            double y = parseTerm();
            x = (op == '+') ? x + y : x - y;
        }
        return x;
    }

    private static double parseTerm() {           // * / \ %
        double x = parsePower();
        while (peek() == '*' || peek() == '/' || peek() == '\\' || peek() == '%') {
            char op = next();
            double y = parsePower();
            switch (op) {
                case '*' -> x *= y;
                case '/' -> { if (y == 0) throw new ArithmeticException("divide by zero"); x /= y; }
                case '\\' -> { if (y == 0) throw new ArithmeticException("divide by zero"); x = Math.floor(x / y); }
                case '%' -> { if (y == 0) throw new ArithmeticException("mod by zero"); x %= y; }
            }
        }
        return x;
    }

    private static double parsePower() {          // ^  (right associative)
        double base = parseUnary();
        if (peek() == '^') {
            next();
            double exp = parsePower();
            return Math.pow(base, exp);
        }
        return base;
    }

    private static double parseUnary() {
        if (peek() == '-') { next(); return -parseUnary(); }
        if (peek() == '+') { next(); return parseUnary(); }
        return parseNumber();
    }

    private static double parseNumber() {
        int start = P;
        while (P < S.length() && (Character.isDigit(S.charAt(P)) || S.charAt(P) == '.')) P++;
        if (start == P) throw new RuntimeException("number expected");
        return Double.parseDouble(S.substring(start, P));
    }

    private void attachKeyboardSupport(JComponent root) {
        root.setFocusable(true);
        root.requestFocusInWindow();
        root.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                char c = e.getKeyChar();
                int code = e.getKeyCode();
                if (Character.isDigit(c)) handleInput(String.valueOf(c));
                else if (isOperatorChar(c)) handleInput(String.valueOf(c));
                else if (c == '.') handleInput(".");
                else if (code == KeyEvent.VK_ENTER || c == '=') handleInput("=");
                else if (code == KeyEvent.VK_BACK_SPACE) handleInput("<-");
                else if (code == KeyEvent.VK_DELETE || code == KeyEvent.VK_ESCAPE) handleInput("Clear");
            }
        });
    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--test")) {
            String[] tests = {"2+4-93", "2+3*4", "2^3^2", "7\\2", "10%3", "-5+2", "3.5*2"};
            for (String t : tests) System.out.println(t + " = " + evaluate(t));
            return;
        }
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) { }
            new Calculator().setVisible(true);
        });
    }
}
