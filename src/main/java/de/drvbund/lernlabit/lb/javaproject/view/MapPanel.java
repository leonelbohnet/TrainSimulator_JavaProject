package de.drvbund.lernlabit.lb.javaproject.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;
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

/**
 * Panel displaying the live train map with animated train movements.
 * Shows stations as nodes, track segments as lines, and trains as moving dots.
 * Includes controls for simulation speed and play/pause functionality.
 * 
 * The map uses two timers:
 * - Animation timer (16ms) for smooth visual updates and simulation time progression
 * - Data refresh timer (300ms) for querying new train positions from the database
 * 
 * @author Leonel Bohnet
 * @version 1.0
 */
public class MapPanel extends JPanel {
    /** List of all track segments connecting stations */
    private List<TrackSegment> segments;
    
    /** Map of station IDs to Station objects for quick lookup */
    private Map<Integer, Station> stationMap;
    
    /** List of currently active trains being animated on the map */
    private List<AnimatedTrain> activeTrains = new ArrayList<>();
    
    /** Timer for animation updates (16ms = ~60 FPS) */
    private Timer animationTimer;
    
    /** Timer for refreshing train data from database (300ms) */
    private Timer dataRefreshTimer;
    
    /** DAO for querying currently running trips */
    private TripDAO tripDAO = new TripDAO();
    
    /** Label displaying the current simulation time */
    private JLabel timeLabel = new JLabel("Sim-Zeit: 00:00:00");
    
    /** Button for play/pause control */
    private JButton playBtn;
    
    /** Flag indicating whether simulation is paused */
    private boolean isPause = false;
    
    /** Icon for play button (unused field, but kept for compatibility) */
    private ImageIcon playIcon;
    
    /** Icon for pause button (unused field, but kept for compatibility) */
    private ImageIcon pauseIcon;



    /**
     * Creates a new map panel with stations, tracks, and train animations.
     * Initializes timers for animation and data refresh, sets up simulation callbacks,
     * and creates the control panel with speed slider and play/pause button.
     * 
     * @param stations list of all stations to display on the map
     * @param segments list of all track segments connecting stations
     */
    public MapPanel(List<Station> stations, List<TrackSegment> segments) {
        this.segments = segments;
        // Create station lookup map for fast access by ID
        this.stationMap = stations.stream().collect(Collectors.toMap(Station::getId, s -> s));
        // Load initial set of active trains
        this.activeTrains = tripDAO.getCurrentlyRunningTrips(stationMap);

        // Register callback to update time label on each simulation tick
        SimulationController.getInstance().setOnTickListener(() -> {
            updateTimeLabel();
        });

        // Animation timer: updates simulation time and triggers repaints (~60 FPS)
        animationTimer = new Timer(16, e -> {
            SimulationController.getInstance().tick(16);
            repaint();
        });
        animationTimer.start();

        // Data refresh timer: periodically queries database for train updates
        dataRefreshTimer = new Timer(300, e -> {
            List<AnimatedTrain> sqlTrains = tripDAO.getCurrentlyRunningTrips(stationMap);

            // Step 1: Update existing trains with new segment data or add new trains
            for (AnimatedTrain nt : sqlTrains) {
                boolean found = false;
                for (AnimatedTrain existing : activeTrains) {
                    if (existing.getName().equals(nt.getName())) {
                        // Update segment for existing train (handles multi-segment journeys)
                        existing.updateSegment(nt.getStartStation(), nt.getEndStation(), nt.getDepartureTime(), nt.getArrivalTime());
                        found = true;
                        break;
                    }
                }
                // Add new trains that weren't previously active
                if (!found) activeTrains.add(nt);
            }

            // Step 2: Remove trains only if they're not in SQL AND have reached their final destination
            // This prevents trains from disappearing during segment transitions
            activeTrains.removeIf(train -> {
                boolean inSql = sqlTrains.stream().anyMatch(nt -> nt.getName().equals(train.getName()));
                // Keep trains that are still in transit even if not currently in SQL results
                // This bridges the "gap" when trains switch between segments
                return !inSql && train.isAtFinalDestination();
            });
        });

        dataRefreshTimer.start();

        // Load SVG icons for play/pause button
        FlatSVGIcon playIcon = new FlatSVGIcon("icons/play.svg", 30, 30);
        FlatSVGIcon pauseIcon = new FlatSVGIcon("icons/pause.svg", 30, 30);

        // Alternative PNG icon loading code (commented out, kept for reference)
        /*     playIcon = new ImageIcon(getClass().getResource("/icons/play-button.png"));
        // pauseIcon = new ImageIcon(getClass().getResource("/icons/pause.png"));
        Image img = playIcon.getImage();
        Image newImg = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        playIcon = new ImageIcon(newImg);

        img = pauseIcon.getImage();
        newImg = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        pauseIcon = new ImageIcon(newImg);
        */

        // Create control panel with simulation controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 5));
        controlPanel.setPreferredSize(new Dimension(800, 50));

        // Speed slider: controls simulation speed multiplier (0-300x)
        JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 300, SimulationController.getInstance().getSpeedFactor());
        speedSlider.setMajorTickSpacing(50);
        speedSlider.setMinorTickSpacing(10);
        speedSlider.setSnapToTicks(true);
        speedSlider.setPaintTicks(true);
        
        // Label showing current speed multiplier
        JLabel speedLabel = new JLabel("Geschwindigkeit: " + SimulationController.getInstance().getSpeedFactor() + "x");
        speedLabel.setPreferredSize(new Dimension(150, 20));
        speedLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Update simulation speed and label when slider changes
        speedSlider.addChangeListener(e -> {
            int factor = speedSlider.getValue();
            // Minimum speed is 1x (prevent 0x which would freeze simulation)
            if (factor == 0) {
                factor = 1;
            }
            SimulationController.getInstance().setSpeedFactor(factor);
            speedLabel.setText("Geschwindigkeit: " + factor + "x");
        });

        timeLabel.setPreferredSize(new Dimension(150, 20));
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Play/Pause button: toggles simulation time
        playBtn = new JButton(pauseIcon);
        playBtn.addActionListener(e -> {
            if (isPause) {
                // Resume simulation
                SimulationController.getInstance().resumeSimTime();
                playBtn.setIcon(pauseIcon);
                isPause = false;
            } else {
                // Pause simulation
                SimulationController.getInstance().pauseSimTime();
                playBtn.setIcon(playIcon);
                isPause = true;
            }
        });

        // Add all controls to control panel
        controlPanel.add(playBtn);
        controlPanel.add(speedLabel);
        controlPanel.add(speedSlider);
        controlPanel.add(timeLabel);
        add(controlPanel, BorderLayout.NORTH);
    }

    /**
     * Calculates the preferred size for the map panel based on station coordinates.
     * Finds the maximum X and Y coordinates among all stations and adds padding.
     * 
     * @return the preferred Dimension for the panel
     */
    @Override
    public Dimension getPreferredSize() {
        // Find the maximum coordinates of all stations
        int maxX = stationMap.values().stream().mapToInt(s -> (int) s.getX_coordinate()).max().orElse(800);
        int maxY = stationMap.values().stream().mapToInt(s -> (int) s.getY_coordinate()).max().orElse(600);

        // Add 100px padding for visual margin
        return new Dimension(maxX + 100, maxY + 100);
    }

    /**
     * Custom paint method that renders the map with stations, tracks, and trains.
     * Drawing order: track segments (lines) → stations (nodes) → trains (moving dots).
     * 
     * Track segments are color-coded:
     * - Blue (thick): ICE-allowed tracks
     * - Red (thin): Regional-only tracks
     * 
     * @param g the Graphics context
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Enable anti-aliasing for smooth graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw track segments as lines connecting stations
        for (TrackSegment segment : segments) {
            Station stationA = stationMap.get(segment.getStationAID());
            Station stationB = stationMap.get(segment.getStationBID());

            if (stationA != null && stationB != null) {
                if (segment.getIsIceAllowed()) {
                    // ICE tracks: thick blue lines
                    g2d.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.setColor(new Color(0, 128, 255));
                } else {
                    // Regional tracks: thin red lines
                    g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.setColor(new Color(255, 0, 0));
                }
                g2d.drawLine((int) stationA.getX_coordinate(), (int) stationA.getY_coordinate(),
                        (int) stationB.getX_coordinate(), (int) stationB.getY_coordinate());
            }
        }

        // Draw stations as blue circles with name labels
        for (Station station : stationMap.values()) {
            int x = (int) station.getX_coordinate();
            int y = (int) station.getY_coordinate();

            // Draw station marker (blue circle)
            g2d.setColor(Color.BLUE);
            g2d.fillOval(x - 5, y - 5, 10, 10);

            // Draw station name next to the marker
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Aptos", Font.PLAIN, 12));
            g2d.drawString(station.getName(), x + 8, y + 5);
        }
        
        // Draw trains as red circles with name labels
        for (AnimatedTrain train : activeTrains) {
            double progress = train.getProgress();

            Point2D.Double pos = train.getCurrentPosition();
            g.setColor(Color.RED);
            g.fillOval((int) pos.x - 5, (int) pos.y - 5, 10, 10);
            g.drawString(train.getName(), (int) pos.x + 10, (int) pos.y);
        }
    }

    /**
     * Updates the time label to show the current simulation time.
     * Called on each simulation tick via the registered callback.
     */
    public void updateTimeLabel() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String timeStr = SimulationController.getInstance().getVirtualTime().format(formatter);

        timeLabel.setText("Sim-Zeit: " + timeStr);
    }

}
