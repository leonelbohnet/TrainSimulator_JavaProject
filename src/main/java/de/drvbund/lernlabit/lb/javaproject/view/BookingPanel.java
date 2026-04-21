package de.drvbund.lernlabit.lb.javaproject.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import de.drvbund.lernlabit.lb.javaproject.dataAccess.RouteOptionDAO;
import de.drvbund.lernlabit.lb.javaproject.model.RotatableIconLabel;
import de.drvbund.lernlabit.lb.javaproject.model.RouteOption;
import de.drvbund.lernlabit.lb.javaproject.controller.SimulationController;
import de.drvbund.lernlabit.lb.javaproject.model.Station;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for searching and displaying train route options between stations.
 * Provides a user interface with station selection dropdowns, a search button,
 * and a results table showing available routes with times, prices, and transfers.
 * 
 * The search is performed asynchronously using SwingWorker to keep the UI responsive,
 * with a rotating loading indicator during the search process.
 * 
 * @author Leonel Bohnet
 * @version 1.0
 */
public class BookingPanel extends JPanel {
    /** Dropdown for selecting the departure station */
    private JComboBox<Station> startCombo;
    
    /** Dropdown for selecting the arrival station */
    private JComboBox<Station> endCombo;
    
    /** Table displaying the search results */
    private JTable resultTable;
    
    /** Table model managing the result data */
    private DefaultTableModel tableModel;
    
    /** DAO for querying route options from the database */
    private RouteOptionDAO routeOptionDAO = new RouteOptionDAO();
    
    /** Animated loading indicator shown during searches */
    private RotatableIconLabel loadingLabel;
    
    /** Timer controlling the loading animation rotation */
    private Timer rotationTimer;
    
    /** List of all available stations for route display */
    private List<Station> allStations;

    /**
     * Creates a new booking panel with station selection and results display.
     * Initializes the UI components including dropdowns, search button, loading indicator,
     * and results table with custom styling and cell renderers.
     * 
     * @param stations the list of all available stations for the dropdowns
     */
    public BookingPanel(List<Station> stations) {
        this.allStations = stations;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create search bar with station selection and search button
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        startCombo = new JComboBox<>(stations.toArray(new Station[0]));
        endCombo = new JComboBox<>(stations.toArray(new Station[0]));
        JButton searchBtn = new JButton("Verbindungen suchen");

        // Create animated loading indicator (initially hidden)
        FlatSVGIcon svgIcon = new FlatSVGIcon("icons/Rolling@1x-1.0s-25px-25px.svg", 24, 24);
        loadingLabel = new RotatableIconLabel(svgIcon);
        loadingLabel.setVisible(false);

        // Add components to search bar
        searchBar.add(new JLabel("Von:"));
        searchBar.add(startCombo);
        searchBar.add(new JLabel("Nach:"));
        searchBar.add(endCombo);
        searchBar.add(searchBtn);
        searchBar.add(loadingLabel);

        // Timer for rotating the loading indicator (10 degrees every 30ms)
        rotationTimer = new Timer(30, e -> {
            loadingLabel.setAngle(loadingLabel.getAngle() + 10);
        });

        add(searchBar, BorderLayout.NORTH);

        // Create table with columns for route information
        String[] columns = {"Route", "Zug", "Abfahrt", "Ankunft", "Dauer", "Umstiege", "Preis"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        resultTable = new JTable(tableModel);

        // Configure table appearance
        resultTable.getTableHeader().setReorderingAllowed(false);
        resultTable.getTableHeader().setResizingAllowed(false);
        resultTable.setRowHeight(resultTable.getFont().getSize() + 20);

        // Create alternating row color renderer for better readability
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    // Alternate between two dark colors for row backgrounds
                    comp.setBackground(row % 2 == 0 ? new Color(60, 63, 65) : new Color(45, 47, 49));
                }
                return comp;
            }
        };

        // Apply renderer to all columns
        for (int i = 0; i < resultTable.getColumnCount(); i++) {
            resultTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        // Right-align time, duration, transfer, and price columns for better readability
        setColumnAlignment(2, SwingConstants.RIGHT, resultTable); // Abfahrt
        setColumnAlignment(3, SwingConstants.RIGHT, resultTable); // Ankunft
        setColumnAlignment(4, SwingConstants.RIGHT, resultTable); // Dauer
        setColumnAlignment(5, SwingConstants.RIGHT, resultTable); // Umstiege
        setColumnAlignment(6, SwingConstants.RIGHT, resultTable); // Preis

        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        // Attach search action listener
        searchBtn.addActionListener(e -> performSearch(searchBtn));
    }

    /**
     * Performs an asynchronous search for route options between selected stations.
     * Validates the selection, displays a loading indicator, and populates the results table.
     * Uses SwingWorker to perform the database query on a background thread.
     * 
     * @param searchBtn the search button to disable during the search operation
     */
    public void performSearch(JButton searchBtn) {
        Station start = (Station) startCombo.getSelectedItem();
        Station end = (Station) endCombo.getSelectedItem();

        // Validate station selection
        if (start == null || end == null || start.equals(end)) {
            JOptionPane.showMessageDialog(this, "Bitte gültige Stationen wählen!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Show loading indicator and disable search button
        loadingLabel.setVisible(true);
        rotationTimer.start();
        searchBtn.setEnabled(false);
        tableModel.setRowCount(0); // Clear previous results

        // Perform search asynchronously
        new SwingWorker<List<RouteOption>, Void>() {
            @Override
            protected List<RouteOption> doInBackground() throws Exception {
                // Query database for available routes (runs on background thread)
                return routeOptionDAO.findAvailableRoutes(start.getId(), end.getId(), SimulationController.getInstance().getVirtualTime());
            }

            @Override
            protected void done() {
                try {
                    List<RouteOption> options = get();
                    if (options.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Keine Verbindungen gefunden!", "Info", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        // Populate table with search results
                        for (RouteOption option : options) {
                            tableModel.addRow(new Object[]{
                                  option.getStationPath(allStations),
                                  formatTrainChain(option),
                                  option.getStartTime(),
                                  option.getEndTime(),
                                  option.getFormattedDuration(),
                                  option.getTransferCount(),
                                  String.format("%.2f €", option.getTotalPrice())
                          });
                        }
                        // Auto-resize columns to fit content
                        resizeColumnWidth(resultTable);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Fehler beim Laden der Daten!", "Fehler", JOptionPane.ERROR_MESSAGE);
                } finally {
                    // Hide loading indicator and re-enable search button
                    loadingLabel.setVisible(false);
                    rotationTimer.stop();
                    searchBtn.setEnabled(true);
                }
            }
        }.execute();
    }

    /**
     * Formats the train sequence for a route option, showing only transfers.
     * Consecutive segments with the same train are merged, and only distinct trains are shown.
     * 
     * @param option the route option to format
     * @return a formatted string like "ICE 123 ➔ RE 456" showing the train sequence
     */
    private String formatTrainChain(RouteOption option) {
        List<String> distinctTrains = new ArrayList<>();
        distinctTrains.add(option.getParts().get(0).getTrain_name());
        
        // Add only trains that differ from the previous one (transfers)
        for (int i = 1; i < option.getParts().size(); i++) {
            String currentTrain = option.getParts().get(i).getTrain_name();
            if (!currentTrain.equals(option.getParts().get(i - 1).getTrain_name())) {
                distinctTrains.add(currentTrain);
            }
        }
        return String.join(" ➔ ", distinctTrains);
    }

    /**
     * Automatically resizes table columns to fit their content.
     * Calculates the maximum width needed for each column based on cell content,
     * with a maximum width limit of 400 pixels to prevent excessive column widths.
     * 
     * @param table the JTable to resize
     */
    public void resizeColumnWidth(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 15; // Minimum width
            
            // Find the maximum width needed by any cell in this column
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 1, width);
            }
            
            // Cap maximum width to prevent excessive column sizes
            if (width > 400) width = 400;
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

    /**
     * Sets the horizontal alignment for a specific table column.
     * Creates a custom cell renderer that maintains alternating row colors
     * while applying the specified text alignment.
     * 
     * @param colIndex the column index to align
     * @param alignment the alignment constant (e.g., SwingConstants.RIGHT)
     * @param table the JTable to apply the alignment to
     */
    public void setColumnAlignment(int colIndex, int alignment, JTable table) {
        // Alternative approach (commented out):
        // TableCellRenderer existingRenderer = resultTable.getColumnModel().getColumn(colIndex).getCellRenderer();
        // if (existingRenderer instanceof DefaultTableCellRenderer) {
        //     ((DefaultTableCellRenderer) existingRenderer).setHorizontalAlignment(alignment);
        // }
        
        // Create custom renderer with alignment and alternating row colors
        DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Apply alternating row colors when not selected
                if (!isSelected) {
                    comp.setBackground(row % 2 == 0 ? new Color(60, 63, 65) : new Color(45, 47, 49));
                }
                return comp;
            }
        };
        customRenderer.setHorizontalAlignment(alignment);
        resultTable.getColumnModel().getColumn(colIndex).setCellRenderer(customRenderer);
    }
}
