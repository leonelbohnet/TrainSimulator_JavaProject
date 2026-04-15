package de.drvbund.lernlabit.lb.javaproject;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
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

        animationTimer = new Timer(16, e -> repaint());
        animationTimer.start();

        dataRefreshTimer = new Timer(1000, e -> {
            this.activeTrains = tripDAO.getCurrentlyRunningTrips(stationMap);
        });
        dataRefreshTimer.start();

        setPreferredSize(new Dimension(800, 600));
        setBackground(new Color(240, 240, 240));
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
        Iterator<AnimatedTrain> iter = activeTrains.iterator();
        while (iter.hasNext()) {
            AnimatedTrain train = iter.next();
            double progress = train.getProgress();

            // Wenn der skalierte Fortschritt 1.0 erreicht hat -> Zug entfernen
            if (progress >= 1.0) {
                iter.remove();
                continue; // Springe zum nächsten Zug, zeichne diesen nicht mehr
            }

            // Nur wenn progress < 1.0 ist, wird der Zug gezeichnet
            Point2D.Double pos = train.getCurrentPosition();
            g.setColor(Color.RED);
            g.fillOval((int) pos.x - 5, (int) pos.y - 5, 10, 10);
            g.drawString(train.getName(), (int) pos.x + 10, (int) pos.y);
        }
    }

}
