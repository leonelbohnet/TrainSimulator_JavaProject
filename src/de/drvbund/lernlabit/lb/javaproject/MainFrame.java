package de.drvbund.lernlabit.lb.javaproject;

import javax.swing.*;
import java.util.List;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("TrainSimulator - Live Map");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        StationDAO stationDAO = new StationDAO();
        List<Station> stations = stationDAO.getAllStations();
        TrackSegmentDAO trackSegmentDAO = new TrackSegmentDAO();
        List<TrackSegment> segments = trackSegmentDAO.getAllSegment();

        MapPanel mapPanel = new MapPanel(stations, segments);
        add(mapPanel);
    }
}
