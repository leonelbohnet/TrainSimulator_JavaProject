package de.drvbund.lernlabit.lb.javaproject.view;

import de.drvbund.lernlabit.lb.javaproject.model.AnimatedTrain;
import de.drvbund.lernlabit.lb.javaproject.dataAccess.TripDAO;
import de.drvbund.lernlabit.lb.javaproject.controller.SimulationController;
import de.drvbund.lernlabit.lb.javaproject.model.Station;
import de.drvbund.lernlabit.lb.javaproject.model.TrackSegment;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class MapPanel extends JPanel {
    private List<TrackSegment> segments;
    private Map<Integer, Station> stationMap;
    private List<AnimatedTrain> activeTrains = new ArrayList<>();
    private Timer animationTimer;
    private Timer dataRefreshTimer;
    private TripDAO tripDAO = new TripDAO();



    public MapPanel(List<Station> stations, List<TrackSegment> segments) {
        this.segments = segments;
        this.stationMap = stations.stream().collect(Collectors.toMap(Station::getId, s -> s));
        this.activeTrains = tripDAO.getCurrentlyRunningTrips(stationMap);

        animationTimer = new Timer(16, e -> {
            SimulationController.getInstance().tick(16);
            repaint();
        });
        animationTimer.start();

        dataRefreshTimer = new Timer(300, e -> {
            List<AnimatedTrain> sqlTrains = tripDAO.getCurrentlyRunningTrips(stationMap);

            // 1. Neue Segmente in bestehende Züge einspielen
            for (AnimatedTrain nt : sqlTrains) {
                boolean found = false;
                for (AnimatedTrain existing : activeTrains) {
                    if (existing.getName().equals(nt.getName())) {
                        existing.updateSegment(nt.getStartStation(), nt.getEndStation(), nt.getDepartureTime(), nt.getArrivalTime());
                        found = true;
                        break;
                    }
                }
                if (!found) activeTrains.add(nt);
            }

            // 2. NUR löschen, wenn der Zug im SQL fehlt UND am Ziel ist
            activeTrains.removeIf(train -> {
                boolean inSql = sqlTrains.stream().anyMatch(nt -> nt.getName().equals(train.getName()));
                // Wenn er nicht im SQL ist, aber noch nicht am Endbahnhof, behalten wir ihn!
                // So überbrücken wir die "Lücke" im SQL beim Umspringen.
                return !inSql && train.isAtFinalDestination();
            });
        });

        dataRefreshTimer.start();

//        setPreferredSize(new Dimension(800, 600));
//        setBackground(new Color(240, 240, 240));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 300, SimulationController.getInstance().getSpeedFactor());
        speedSlider.setMajorTickSpacing(50);
        speedSlider.setMinorTickSpacing(25);
        speedSlider.setPaintTicks(true);
        JLabel speedLabel = new JLabel("Geschwindigkeit: " + SimulationController.getInstance().getSpeedFactor() + "x");
        speedSlider.addChangeListener(e -> {
            int factor = speedSlider.getValue();
            SimulationController.getInstance().setSpeedFactor(factor);
            speedLabel.setText("Geschwindigkeit: " + factor + "x");
        });

        controlPanel.add(speedSlider);
        controlPanel.add(speedLabel);
        add(controlPanel, BorderLayout.SOUTH);
    }

    @Override
    public Dimension getPreferredSize(){
// Finde die maximalen Koordinaten deiner Stationen
        int maxX = stationMap.values().stream().mapToInt(s -> (int)s.getX_coordinate()).max().orElse(800);
        int maxY = stationMap.values().stream().mapToInt(s -> (int)s.getY_coordinate()).max().orElse(600);

        // + 80 für das Padding (40px an jeder Seite)
        return new Dimension(maxX + 100, maxY + 100);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (TrackSegment segment : segments) {
            Station stationA = stationMap.get(segment.getStationAID());
            Station stationB = stationMap.get(segment.getStationBID());

            if (stationA != null && stationB != null) {
                if (segment.getIsIceAllowed()) {
                    g2d.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.setColor(new Color(0, 128, 255));
                } else {
                    g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.setColor(new Color(255, 0, 0));
                }
                g2d.drawLine((int) stationA.getX_coordinate(), (int) stationA.getY_coordinate(),
                        (int) stationB.getX_coordinate(), (int) stationB.getY_coordinate());
            }
        }

        for (Station station : stationMap.values()) {
            int x = (int) station.getX_coordinate();
            int y = (int) station.getY_coordinate();

            g2d.setColor(Color.BLUE);
            g2d.fillOval(x - 5, y - 5, 10, 10);

            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Aptos", Font.PLAIN, 12));
            g2d.drawString(station.getName(), x + 8, y + 5);


        }
        for (AnimatedTrain train : activeTrains){
            double progress = train.getProgress();

            Point2D.Double pos = train.getCurrentPosition();
            g.setColor(Color.RED);
            g.fillOval((int) pos.x - 5, (int) pos.y - 5, 10, 10);
            g.drawString(train.getName(), (int) pos.x + 10, (int) pos.y);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String timeStr = SimulationController.getInstance().getVirtualTime().format(formatter);
        g2d.drawString("Sim-Zeit: " + timeStr, getWidth() - 150, 20);
    }

}
