package de.drvbund.lernlabit.lb.javaproject;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class BookingPanel extends JPanel {
    private JComboBox<Station> startCombo;
    private JComboBox<Station> endCombo;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private RouteOptionDAO routeOptionDAO = new RouteOptionDAO();

    public BookingPanel(List<Station> stations) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        startCombo = new JComboBox<>(stations.toArray(new Station[0]));
        endCombo = new JComboBox<>(stations.toArray(new Station[0]));
        JButton searchBtn = new JButton("Verbindungen suchen");

        searchBar.add(new JLabel("Von:"));
        searchBar.add(startCombo);
        searchBar.add(new JLabel("Nach:"));
        searchBar.add(endCombo);
        searchBar.add(searchBtn);

        add(searchBar, BorderLayout.NORTH);

        String[] columns = {"Route", "Zug", "Abfahrt", "Ankunft", "Dauer", "Umstiege", "Preis"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultTable = new JTable(tableModel);

        setColumnAlignment(2, SwingConstants.RIGHT, resultTable);
        setColumnAlignment(3, SwingConstants.RIGHT, resultTable);
        setColumnAlignment(4, SwingConstants.RIGHT, resultTable);
        setColumnAlignment(5, SwingConstants.RIGHT, resultTable);
        setColumnAlignment(6, SwingConstants.RIGHT, resultTable);

        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        searchBtn.addActionListener(e -> {
            Station start = (Station) startCombo.getSelectedItem();
            Station end = (Station) endCombo.getSelectedItem();

            if ((start != null) && (end != null) && (start != end)) {
                List<RouteOption> options = routeOptionDAO.findAvailableRoutes(start.getId(), end.getId(), SimulationController.getInstance().getVirtualTime());
                tableModel.setRowCount(0);

                if (options.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Keine Verbindungen gefunden!", "Info", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    for (RouteOption option : options) {
                        List<String> distinctTrains = new ArrayList<>();
                        distinctTrains.add(option.getParts().get(0).getTrain_name());

                        for (int i = 1; i < option.getParts().size(); i++) {
                            String currentTrain = option.getParts().get(i).getTrain_name();
                            if (!currentTrain.equals(option.getParts().get(i - 1).getTrain_name())) {
                                distinctTrains.add(currentTrain);
                            }
                        }
                        String trainChain = String.join(" ➔ ", distinctTrains);

                        tableModel.addRow(new Object[]{
                                option.getStationPath(stations),
                                trainChain,
                                option.getStartTime(),
                                option.getEndTime(),
                                option.getFormattedDuration(),
                                option.getTransferCount(),
                                String.format("%.2f €", option.getTotalPrice())
                        });
                    }
                }
            } else {
                if (start == end) {
                    JOptionPane.showMessageDialog(this, "Start- und Endpunkt dürfen nicht gleich sein!", "Info", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Bitte wählen Sie einen Start- und Endpunkt aus!", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            resizeColumnWidth(resultTable);
        });
    }

    public void resizeColumnWidth(JTable table){
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++){
            int width = 15;
            for (int row = 0; row < table.getRowCount(); row++){
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width +1, width);
            }
            if (width > 400) width = 400;
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

    public void setColumnAlignment(int colIndex, int alignment, JTable table){
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(alignment);
        table.getColumnModel().getColumn(colIndex).setCellRenderer(renderer);
    }
}
