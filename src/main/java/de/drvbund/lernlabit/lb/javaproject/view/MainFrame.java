package de.drvbund.lernlabit.lb.javaproject.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import de.drvbund.lernlabit.lb.javaproject.dataAccess.StationDAO;
import de.drvbund.lernlabit.lb.javaproject.dataAccess.TrackSegmentDAO;
import de.drvbund.lernlabit.lb.javaproject.model.Station;
import de.drvbund.lernlabit.lb.javaproject.model.TrackSegment;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Main application window for the Train Simulator.
 * Displays a split-pane layout with a live train map on the left
 * and a booking/route search panel on the right.
 * 
 * The window initializes maximized and loads station and track data
 * from the database to populate both panels.
 * 
 * @author Leonel Bohnet
 * @version 1.0
 */
public class MainFrame extends JFrame {
    
    /**
     * Creates and configures the main application window.
     * Sets up the window properties, loads data from the database,
     * creates the map and booking panels, and arranges them in a split pane layout.
     */
    public MainFrame() {
        // Configure window properties
        setTitle("TrainSimulator - Live Map");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Start maximized

        // Load and set application icon
        FlatSVGIcon icon = new FlatSVGIcon("icons/train.svg");
        icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.LIGHT_GRAY));
        setIconImage(icon.getImage());


        // Load station and track segment data from database
        StationDAO stationDAO = new StationDAO();
        List<Station> stations = stationDAO.getAllStations();
        TrackSegmentDAO trackSegmentDAO = new TrackSegmentDAO();
        List<TrackSegment> segments = trackSegmentDAO.getAllSegment();

        // Create wrapper panel for map with centered layout
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setMinimumSize(new Dimension(800, 600));

        // Create map panel showing live train positions
        MapPanel mapPanel = new MapPanel(stations, segments);
        mapPanel.setBackground(Color.LIGHT_GRAY);
        mapPanel.setMinimumSize(new Dimension(800, 600));
        
        // Create booking panel for route searches
        BookingPanel bookingPanel = new BookingPanel(stations);
        bookingPanel.setMinimumSize(new Dimension(400, 600));

        // Center the map panel within the wrapper
        wrapper.add(mapPanel, new GridBagConstraints());

        // Create split pane with map on left, booking panel on right
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, wrapper, bookingPanel);
        splitPane.setMinimumSize(new Dimension(1200, 600));
        splitPane.setDividerLocation(800); // Initial divider position
        splitPane.setDividerSize(0); // Hide divider (non-resizable)
        splitPane.setEnabled(false); // Prevent user from moving divider
        splitPane.setResizeWeight(0); // Left panel gets extra space on resize
        add(splitPane);

        // Add resize listener to refresh split pane on window resize
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                splitPane.revalidate();
                splitPane.repaint();
            }
        });
    }
}
