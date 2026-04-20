package de.drvbund.lernlabit.lb.javaproject.view;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import de.drvbund.lernlabit.lb.javaproject.dataAccess.RouteOptionDAO;
import de.drvbund.lernlabit.lb.javaproject.model.RotatebleIconLabel;
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


public class BookingPanel extends JPanel {
    private JComboBox<Station> startCombo;
    private JComboBox<Station> endCombo;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private RouteOptionDAO routeOptionDAO = new RouteOptionDAO();
    private RotatebleIconLabel loadingLabel;
    private Timer rotationTimer;
    private List<Station> allStations;

    public BookingPanel(List<Station> stations) {
        this.allStations = stations;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        startCombo = new JComboBox<>(stations.toArray(new Station[0]));
        endCombo = new JComboBox<>(stations.toArray(new Station[0]));
        JButton searchBtn = new JButton("Verbindungen suchen");

        FlatSVGIcon svgIcon = new FlatSVGIcon("icons/Rolling@1x-1.0s-25px-25px.svg", 24, 24);
        loadingLabel = new RotatebleIconLabel(svgIcon);
        loadingLabel.setVisible(false);


        searchBar.add(new JLabel("Von:"));
        searchBar.add(startCombo);
        searchBar.add(new JLabel("Nach:"));
        searchBar.add(endCombo);
        searchBar.add(searchBtn);
        searchBar.add(loadingLabel);

        rotationTimer = new Timer(30, e -> {
            loadingLabel.setAngle(loadingLabel.getAngle() + 10);
        });


        add(searchBar, BorderLayout.NORTH);

        String[] columns = {"Route", "Zug", "Abfahrt", "Ankunft", "Dauer", "Umstiege", "Preis"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultTable = new JTable(tableModel);

        resultTable.getTableHeader().setReorderingAllowed(false);
        resultTable.getTableHeader().setResizingAllowed(false);
        resultTable.setRowHeight(resultTable.getFont().getSize() + 20);


        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    comp.setBackground(row % 2 == 0 ? new Color(60, 63, 65) : new Color(45, 47, 49));
                }
                return comp;
            }
        };

        for (int i = 0; i < resultTable.getColumnCount(); i++) {
            resultTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        setColumnAlignment(2, SwingConstants.RIGHT, resultTable);
        setColumnAlignment(3, SwingConstants.RIGHT, resultTable);
        setColumnAlignment(4, SwingConstants.RIGHT, resultTable);
        setColumnAlignment(5, SwingConstants.RIGHT, resultTable);
        setColumnAlignment(6, SwingConstants.RIGHT, resultTable);

        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        searchBtn.addActionListener(e -> performSearch(searchBtn));
    }

    public void performSearch(JButton searchBtn){
        Station start = (Station) startCombo.getSelectedItem();
        Station end = (Station) endCombo.getSelectedItem();

        if(start == null || end == null || start.equals(end)){
            JOptionPane.showMessageDialog(this, "Bitte gültige Stationen wählen!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        loadingLabel.setVisible(true);
        rotationTimer.start();
        searchBtn.setEnabled(false);
        tableModel.setRowCount(0);

        new SwingWorker<List<RouteOption>, Void>(){
            @Override
            protected List<RouteOption> doInBackground() throws Exception {
                return routeOptionDAO.findAvailableRoutes(start.getId(), end.getId(), SimulationController.getInstance().getVirtualTime());
            }

            @Override
            protected void done() {
                try {
                    List<RouteOption> options = get();
                    if (options.isEmpty()){
                        JOptionPane.showMessageDialog(null, "Keine Verbindungen gefunden!", "Info", JOptionPane.INFORMATION_MESSAGE);
                    } else {
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
                        resizeColumnWidth(resultTable);
                    }
                } catch (Exception ex){
                    JOptionPane.showMessageDialog(null, "Fehler beim Laden der Daten!", "Fehler", JOptionPane.ERROR_MESSAGE);
                } finally {
                    loadingLabel.setVisible(false);
                    rotationTimer.stop();
                    searchBtn.setEnabled(true);
                }
            }
        }.execute();
    }

    private String formatTrainChain(RouteOption option){
        List<String> distinctTrains = new ArrayList<>();
        distinctTrains.add(option.getParts().get(0).getTrain_name());
        for (int i = 1; i < option.getParts().size(); i++) {
            String currentTrain = option.getParts().get(i).getTrain_name();
            if (!currentTrain.equals(option.getParts().get(i - 1).getTrain_name())) {
                distinctTrains.add(currentTrain);
            }
        }
        return String.join(" ➔ ", distinctTrains);
    }

    public void resizeColumnWidth(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 15;
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 1, width);
            }
            if (width > 400) width = 400;
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

    public void setColumnAlignment(int colIndex, int alignment, JTable table) {
//        TableCellRenderer existingRenderer = resultTable.getColumnModel().getColumn(colIndex).getCellRenderer();
//        if (existingRenderer instanceof DefaultTableCellRenderer) {
//            ((DefaultTableCellRenderer) existingRenderer).setHorizontalAlignment(alignment);
//        }
        DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if(!isSelected){
                    comp.setBackground(row % 2 == 0 ? new Color(60, 63, 65) : new Color(45, 47, 49));
                }
                return comp;
            }
        };
        customRenderer.setHorizontalAlignment(alignment);
        resultTable.getColumnModel().getColumn(colIndex).setCellRenderer(customRenderer);
    }
}
