import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class RocketGUI extends JFrame {

    private JTextField mass1Field, thrust1Field;
    private JTextField mass2Field, thrust2Field;
    private PlotPanel plotPanel;

    public RocketGUI() {
        setTitle("Rocket Trajectory Simulator");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        
        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 5, 5));

        inputPanel.add(new JLabel("Rocket 1 Mass (kg):"));
        mass1Field = new JTextField("50");
        inputPanel.add(mass1Field);

        inputPanel.add(new JLabel("Rocket 1 Thrust (N):"));
        thrust1Field = new JTextField("1500");
        inputPanel.add(thrust1Field);

        inputPanel.add(new JLabel("Rocket 2 Mass (kg):"));
        mass2Field = new JTextField("60");
        inputPanel.add(mass2Field);

        inputPanel.add(new JLabel("Rocket 2 Thrust (N):"));
        thrust2Field = new JTextField("1600");
        inputPanel.add(thrust2Field);

        JButton simulateButton = new JButton("Simulate");
        inputPanel.add(simulateButton);

        add(inputPanel, BorderLayout.NORTH);

        
        plotPanel = new PlotPanel();
        add(plotPanel, BorderLayout.CENTER);

        simulateButton.addActionListener(e -> runSimulation());
    }

    private void runSimulation() {
        double mass1 = Double.parseDouble(mass1Field.getText());
        double thrust1 = Double.parseDouble(thrust1Field.getText());

        double mass2 = Double.parseDouble(mass2Field.getText());
        double thrust2 = Double.parseDouble(thrust2Field.getText());

        ArrayList<Double> altitudes1 = simulateRocket(mass1, thrust1);
        ArrayList<Double> altitudes2 = simulateRocket(mass2, thrust2);

        plotPanel.setData(altitudes1, altitudes2);
    }

    private ArrayList<Double> simulateRocket(double mass, double thrust) {
        double dragCoefficient = 0.75;
        double airDensity = 1.225;
        double area = 0.3;
        double gravity = 9.81;

        double velocity = 0;
        double altitude = 0;
        double time = 0;
        double dt = 0.1;

        ArrayList<Double> altitudes = new ArrayList<>();

        while (altitude >= 0 && time <= 60) {
            double drag = 0.5 * airDensity * velocity * velocity
                    * dragCoefficient * area;

            if (velocity < 0) {
                drag = -drag;
            }

            double currentThrust = (time <= 10) ? thrust : 0;

            double netForce = currentThrust - (mass * gravity) - drag;
            double acceleration = netForce / mass;

            velocity += acceleration * dt;
            altitude += velocity * dt;

            altitudes.add(altitude);

            time += dt;
        }

        return altitudes;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RocketGUI gui = new RocketGUI();
            gui.setVisible(true);
        });
    }
}

class PlotPanel extends JPanel {

    private ArrayList<Double> data1 = new ArrayList<>();
    private ArrayList<Double> data2 = new ArrayList<>();

    public void setData(ArrayList<Double> d1, ArrayList<Double> d2) {
        data1 = d1;
        data2 = d2;
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (data1.isEmpty()) return;

        int width = getWidth();
        int height = getHeight();

        
        double maxAlt = 0;                                       //max altitude calculations
        for (double v : data1) if (v > maxAlt) maxAlt = v;
        for (double v : data2) if (v > maxAlt) maxAlt = v;

        int margin = 40;

        
        g.drawLine(margin, height - margin, width - margin, height - margin);
        g.drawLine(margin, margin, margin, height - margin);

        drawCurve(g, data1, Color.RED, maxAlt, margin, width, height);
        drawCurve(g, data2, Color.BLUE, maxAlt, margin, width, height);

        g.setColor(Color.RED);
        g.drawString("Rocket 1", width - 100, margin + 20);

        g.setColor(Color.BLUE);
        g.drawString("Rocket 2", width - 100, margin + 40);
    }

    private void drawCurve(Graphics g, ArrayList<Double> data,
                           Color color, double maxAlt,
                           int margin, int width, int height) {

        g.setColor(color);

        int n = data.size();
        for (int i = 1; i < n; i++) {
            int x1 = margin + (i - 1) * (width - 2 * margin) / n;
            int x2 = margin + i * (width - 2 * margin) / n;

            int y1 = (int) ((height - margin) -
                    (data.get(i - 1) / maxAlt) * (height - 2 * margin));

            int y2 = (int) ((height - margin) -
                    (data.get(i) / maxAlt) * (height - 2 * margin));

            g.drawLine(x1, y1, x2, y2);
        }
    }
}

