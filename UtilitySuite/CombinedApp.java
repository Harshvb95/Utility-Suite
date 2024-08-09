import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.EmptyStackException;
import java.util.Stack;
import java.lang.Math;
import java.text.DecimalFormat;

public class CombinedApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame mainFrame = new JFrame("Utility Suite");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(400, 200);
            mainFrame.setLayout(new GridLayout(2, 1));

            JButton currencyConverterButton = new JButton("Currency Converter");
            JButton scientificCalculatorButton = new JButton("Scientific Calculator");

            currencyConverterButton.setFont(new Font("Arial", Font.PLAIN, 20));
            scientificCalculatorButton.setFont(new Font("Arial", Font.PLAIN, 20));

            currencyConverterButton.addActionListener(e -> CurrencyC.converter());
            scientificCalculatorButton.addActionListener(e -> SwingUtilities.invokeLater(ScientificCalculator::new));

            mainFrame.add(currencyConverterButton);
            mainFrame.add(scientificCalculatorButton);

            mainFrame.setVisible(true);
        });
    }
}

class CurrencyC {
    public static void converter() {
        JFrame f = new JFrame("Currency Converter");
        JLabel l1, l2;
        JTextField t1, t2;
        JButton b1, b2, b3;

        l1 = new JLabel("Rupees:");
        l1.setBounds(20, 40, 60, 30);
        l2 = new JLabel("Dollars:");
        l2.setBounds(170, 40, 60, 30);

        t1 = new JTextField("0");
        t1.setBounds(80, 40, 50, 30);
        t2 = new JTextField("0");
        t2.setBounds(240, 40, 50, 30);

        b1 = new JButton("INR");
        b1.setBounds(50, 80, 60, 15);
        b2 = new JButton("Dollar");
        b2.setBounds(190, 80, 60, 15);
        b3 = new JButton("Close");
        b3.setBounds(150, 150, 60, 30);

        b1.addActionListener(e -> {
            double d = Double.parseDouble(t1.getText());
            double d1 = (d / 65.25);
            t2.setText(String.valueOf(d1));
        });

        b2.addActionListener(e -> {
            double d2 = Double.parseDouble(t2.getText());
            double d3 = (d2 * 65.25);
            t1.setText(String.valueOf(d3));
        });

        b3.addActionListener(e -> f.dispose());

        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        f.add(l1);
        f.add(t1);
        f.add(l2);
        f.add(t2);
        f.add(b1);
        f.add(b2);
        f.add(b3);

        f.setLayout(null);
        f.setSize(400, 300);
        f.setVisible(true);
    }
}

class ScientificCalculator {
    private JFrame frame;
    private JTextField display;
    private String input = "";

    public ScientificCalculator() {
        frame = new JFrame("Scientific Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());

        display = new JTextField();
        display.setFont(new Font("Arial", Font.PLAIN, 20));
        display.setEditable(false);
        frame.add(display, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 5));

        String[] buttons = {
                "7", "8", "9", "/", "sqrt",
                "4", "5", "6", "*", "x^2",
                "1", "2", "3", "-", "x^y",
                "0", ".", "+/-", "+", "=",
                "sin", "cos", "tan", "log", "ln"
        };

        for (String button : buttons) {
            JButton btn = new JButton(button);
            btn.setFont(new Font("Arial", Font.PLAIN, 18));
            btn.addActionListener(new ButtonClickListener());
            buttonPanel.add(btn);
        }

        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if ("0123456789.".contains(command)) {
                input += command;
            } else if ("+-*/".contains(command)) {
                input += " " + command + " ";
            } else if ("sqrt x^2 x^y sin cos tan log ln".contains(command)) {
                input = command + "(" + input + ")";
            } else if ("=".equals(command)) {
                try {
                    input = evaluateExpression(input);
                } catch (ArithmeticException ex) {
                    input = "Error";
                }
            } else if ("+/-".equals(command)) {
                input = negateInput(input);
            }

            display.setText(input);
        }

        private String evaluateExpression(String expression) {
            String result = "";
            try {
                String[] parts = expression.split(" ");
                Stack<String> operators = new Stack<>();
                Stack<Double> values = new Stack<>();

                for (String part : parts) {
                    if ("+-*/sqrtx^2x^ysincostanlogln".contains(part)) {
                        operators.push(part);
                    } else {
                        values.push(Double.parseDouble(part));
                    }

                    while (!operators.isEmpty() && values.size() >= 2) {
                        String operator = operators.pop();
                        double b = values.pop();
                        double a = values.pop();
                        double res = calculate(a, b, operator);
                        values.push(res);
                    }
                }

                DecimalFormat df = new DecimalFormat("#.##########");
                result = df.format(values.pop());
            } catch (NumberFormatException | EmptyStackException e) {
                result = "Error";
            }
            return result;
        }

        private double calculate(double a, double b, String operator) {
            switch (operator) {
                case "+":
                    return a + b;
                case "-":
                    return a - b;
                case "*":
                    return a * b;
                case "/":
                    if (b == 0) throw new ArithmeticException("Division by zero");
                    return a / b;
                case "sqrt":
                    if (a < 0) throw new ArithmeticException("Square root of negative number");
                    return Math.sqrt(a);
                case "x^2":
                    return a * a;
                case "x^y":
                    return Math.pow(a, b);
                case "sin":
                    return Math.sin(Math.toRadians(a));
                case "cos":
                    return Math.cos(Math.toRadians(a));
                case "tan":
                    return Math.tan(Math.toRadians(a));
                case "log":
                    if (a <= 0 || b <= 0 || a == 1) throw new ArithmeticException("Invalid logarithm");
                    return Math.log(b) / Math.log(a);
                case "ln":
                    if (a <= 0) throw new ArithmeticException("Invalid natural logarithm");
                    return Math.log(a);
                default:
                    throw new IllegalArgumentException("Invalid operator: " + operator);
            }
        }

        private String negateInput(String input) {
            if (input.isEmpty()) return input;

            String[] parts = input.split(" ");
            int lastIndex = parts.length - 1;
            String lastPart = parts[lastIndex];

            if (!lastPart.isEmpty() && Character.isDigit(lastPart.charAt(0))) {
                if (lastPart.charAt(0) == '-') {
                    parts[lastIndex] = lastPart.substring(1);
                } else {
                    parts[lastIndex] = "-" + lastPart;
                }
                return String.join(" ", parts);
            } else {
                return input;
            }
        }
    }
}
