import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class FinalScientificCalculator extends JFrame {
    private JTextField display;
    private JPanel basicPanel, scientificPanel;
    private JButton toggleButton;
    private double memory = 0;

    public FinalScientificCalculator() {
        setTitle("Scientific Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLayout(new BorderLayout());

        display = new JTextField();
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.BOLD, 28));
        display.setPreferredSize(new Dimension(800, 80));
        display.setBackground(new Color(0xF0F0F0));
        display.setBorder(BorderFactory.createLineBorder(new Color(0xD3D3D3), 2));
        add(display, BorderLayout.NORTH);

        basicPanel = new JPanel(new GridLayout(5, 4, 10, 10));
        scientificPanel = new JPanel(new GridLayout(7, 5, 10, 10));

        setupBasicButtons();
        setupScientificButtons();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(basicPanel, BorderLayout.CENTER);
        mainPanel.add(scientificPanel, BorderLayout.EAST);
        add(mainPanel, BorderLayout.CENTER);

        toggleButton = new JButton("Scientific Mode");
        toggleButton.setFont(new Font("Arial", Font.PLAIN, 20));
        toggleButton.setPreferredSize(new Dimension(800, 80));
        toggleButton.addActionListener(e -> {
            scientificPanel.setVisible(!scientificPanel.isVisible());
            toggleButton.setText(scientificPanel.isVisible() ? "Basic Mode" : "Scientific Mode");
        });
        add(toggleButton, BorderLayout.SOUTH);

        scientificPanel.setVisible(false);
        setVisible(true);
    }

    private void setupBasicButtons() {
        String[] labels = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+",
            "C", "←", "(", ")"
        };
        addButtonsToPanel(basicPanel, labels, false);
    }

    private void setupScientificButtons() {
        String[] labels = {
            "sin", "cos", "tan", "log", "ln",
            "√", "x²", "x³", "x^y", "exp",
            "π", "!", "M+", "M-", "MR",
            "MC", "sinh", "cosh", "tanh", "mod",
            "log_x"  // New button for logarithm with arbitrary base
        };
        addButtonsToPanel(scientificPanel, labels, true);
    }

    private void addButtonsToPanel(JPanel panel, String[] labels, boolean isScientific) {
        for (String label : labels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.PLAIN, 20));
            button.setPreferredSize(new Dimension(150, 80));
            button.addActionListener(new ButtonClickListener());
            button.setBackground(isScientific ? new Color(0xFFDAB9) : new Color(0x87CEEB));
            panel.add(button);
        }
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            String currentText = display.getText().trim();
            try {
                switch (command) {
                    case "=":
                        currentText = currentText.replace("mod", "%");
                        display.setText(String.valueOf(evaluateExpression(currentText)));
                        break;
                    case "C":
                        display.setText("");
                        break;
                    case "←":
                        if (!currentText.isEmpty())
                            display.setText(currentText.substring(0, currentText.length() - 1));
                        break;
                    case "M+":
                        memory += evaluateExpression(currentText);
                        break;
                    case "M-":
                        memory -= evaluateExpression(currentText);
                        break;
                    case "MR":
                        display.setText(String.valueOf(memory));
                        break;
                    case "MC":
                        memory = 0;
                        break;
                    case "π":
                        display.setText(currentText + Math.PI);
                        break;
                    case "sin":
                        display.setText(String.valueOf(Math.sin(Math.toRadians(evaluateExpression(currentText)))));
                        break;
                    case "cos":
                        display.setText(String.valueOf(Math.cos(Math.toRadians(evaluateExpression(currentText)))));
                        break;
                    case "tan":
                        display.setText(String.valueOf(Math.tan(Math.toRadians(evaluateExpression(currentText)))));
                        break;
                    case "sinh":
                        display.setText(String.valueOf(Math.sinh(evaluateExpression(currentText))));
                        break;
                    case "cosh":
                        display.setText(String.valueOf(Math.cosh(evaluateExpression(currentText))));
                        break;
                    case "tanh":
                        display.setText(String.valueOf(Math.tanh(evaluateExpression(currentText))));
                        break;
                    case "log":
                        display.setText(String.valueOf(Math.log10(evaluateExpression(currentText))));
                        break;
                    case "ln":
                        display.setText(String.valueOf(Math.log(evaluateExpression(currentText))));
                        break;
                    case "√":
                        display.setText(String.valueOf(Math.sqrt(evaluateExpression(currentText))));
                        break;
                    case "x²":
                        display.setText(String.valueOf(Math.pow(evaluateExpression(currentText), 2)));
                        break;
                    case "x³":
                        display.setText(String.valueOf(Math.pow(evaluateExpression(currentText), 3)));
                        break;
                    case "x^y":
                        display.setText(currentText + "^");
                        break;
                    case "exp":
                        display.setText(String.valueOf(Math.exp(evaluateExpression(currentText))));
                        break;
                    case "!":
                        display.setText(String.valueOf(factorial((int) evaluateExpression(currentText))));
                        break;
                    case "log_x": {
                        double argument = evaluateExpression(currentText);
                        if (argument <= 0) {
                            JOptionPane.showMessageDialog(null, "Argument must be greater than 0 for logarithm.");
                            break;
                        }
                        String baseStr = JOptionPane.showInputDialog("Enter base value (must be > 0 and not equal to 1):");
                        if (baseStr != null && !baseStr.isEmpty()) {
                            try {
                                double base = Double.parseDouble(baseStr);
                                if (base <= 0 || base == 1) {
                                    JOptionPane.showMessageDialog(null, "Invalid base. Base must be > 0 and not equal to 1.");
                                    break;
                                }
                                display.setText(String.valueOf(Math.log(argument) / Math.log(base)));
                            } catch (NumberFormatException nfe) {
                                JOptionPane.showMessageDialog(null, "Invalid input for base. Please enter a valid number.");
                            }
                        }
                        break;
                    }
                    default:
                        display.setText(currentText + command);
                        break;
                }
            } catch (Exception ex) {
                display.setText("Error");
            }
        }
    }

    private double evaluateExpression(String expression) {
        return new ExpressionParser().evaluate(expression);
    }

    private long factorial(int n) {
        if (n < 0) throw new IllegalArgumentException("Negative factorial not defined");
        long result = 1;
        for (int i = 1; i <= n; i++) result *= i;
        return result;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FinalScientificCalculator::new);
    }

    private static class ExpressionParser {
        public double evaluate(String expr) {
            expr = expr.replace("mod", "%");  // Ensure mod works as %
            Stack<Double> values = new Stack<>();
            Stack<Character> ops = new Stack<>();

            for (int i = 0; i < expr.length(); i++) {
                char c = expr.charAt(i);
                if (Character.isDigit(c) || c == '.') {
                    StringBuilder num = new StringBuilder();
                    while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.'))
                        num.append(expr.charAt(i++));
                    values.push(Double.parseDouble(num.toString()));
                    i--;
                } else if (c == '(') {
                    ops.push(c);
                } else if (c == ')') {
                    while (ops.peek() != '(')
                        values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                    ops.pop();
                } else {
                    while (!ops.isEmpty() && precedence(ops.peek()) >= precedence(c))
                        values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                    ops.push(c);
                }
            }

            while (!ops.isEmpty())
                values.push(applyOp(ops.pop(), values.pop(), values.pop()));

            return values.pop();
        }

        private int precedence(char op) {
            if (op == '+' || op == '-') return 1;
            if (op == '*' || op == '/' || op == '%') return 2;
            if (op == '^') return 3;
            return 0;
        }
        
        private double applyOp(char op, double b, double a) {
            switch (op) {
                case '+': return a + b;
                case '-': return a - b;
                case '*': return a * b;
                case '/': return a / b;
                case '%': return a % b;
                case '^': return Math.pow(a, b);
                default: return 0;
            }
        }
    }
}
