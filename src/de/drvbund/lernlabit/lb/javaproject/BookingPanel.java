package de.drvbund.lernlabit.lb.javaproject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
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

            ;
        };
        resultTable = new JTable(tableModel);
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
                        String trains = option.getParts().stream()
                                .map(RoutePart::getTrain_name)
                                .reduce((a, b) -> a + " ➔ " + b)
                                .orElse("-");

                        String routes = option.getParts().stream()
                                .map(RoutePart::getRoute_name)
                                .distinct()
                                .reduce((a, b) -> a + " , " + b)
                                .orElse("-");

                        tableModel.addRow(new Object[]{
                                routes,
                                trains,
                                option.getStartTime(),
                                option.getEndTime(),
                                option.getTotalDuration() + " Min",
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
        });
    }
}
