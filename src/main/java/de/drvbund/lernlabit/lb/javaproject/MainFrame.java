package de.drvbund.lernlabit.lb.javaproject;

import javax.swing.*;
import java.util.List;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("TrainSimulator - Live Map");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        StationDAO stationDAO = new StationDAO();
        List<Station> stations = stationDAO.getAllStations();
        TrackSegmentDAO trackSegmentDAO = new TrackSegmentDAO();
        List<TrackSegment> segments = trackSegmentDAO.getAllSegment();

        MapPanel mapPanel = new MapPanel(stations, segments);
        BookingPanel bookingPanel = new BookingPanel(stations);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapPanel, bookingPanel);
        splitPane.setDividerLocation(800);
        add(splitPane);
    }
}
