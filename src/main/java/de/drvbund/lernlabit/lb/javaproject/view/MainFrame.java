package de.drvbund.lernlabit.lb.javaproject.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import de.drvbund.lernlabit.lb.javaproject.dataAccess.StationDAO;
import de.drvbund.lernlabit.lb.javaproject.dataAccess.TrackSegmentDAO;
import de.drvbund.lernlabit.lb.javaproject.model.Station;
import de.drvbund.lernlabit.lb.javaproject.model.TrackSegment;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("TrainSimulator - Live Map");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        FlatSVGIcon icon = new FlatSVGIcon("icons/train.svg");
        icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.LIGHT_GRAY));

        setIconImage(icon.getImage());


        StationDAO stationDAO = new StationDAO();
        List<Station> stations = stationDAO.getAllStations();
        TrackSegmentDAO trackSegmentDAO = new TrackSegmentDAO();
        List<TrackSegment> segments = trackSegmentDAO.getAllSegment();

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setMinimumSize(new Dimension(800, 600));


        MapPanel mapPanel = new MapPanel(stations, segments);
        mapPanel.setBackground(Color.LIGHT_GRAY);
        mapPanel.setMinimumSize(new Dimension(800, 600));
        BookingPanel bookingPanel = new BookingPanel(stations);
        bookingPanel.setMinimumSize(new Dimension(400, 600));

        wrapper.add(mapPanel, new GridBagConstraints());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, wrapper, bookingPanel);
        splitPane.setMinimumSize(new Dimension(1200, 600));
        splitPane.setDividerLocation(800);
        splitPane.setDividerSize(0);
        splitPane.setEnabled(false);
        splitPane.setResizeWeight(0);
        add(splitPane);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                splitPane.revalidate();
                splitPane.repaint();
            }
        });
    }
}
