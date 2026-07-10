package co.edu.unal.poo.actividad5;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * Ventana principal de la aplicación.
 * Formulario con campos Nombre / Número y botones:
 * Crear, Leer, Actualizar, Eliminar.
 */
public class ContactManagerGUI extends JFrame {

    private final FriendFileDAO dao = new FriendFileDAO("friendsContact.txt");

    private JTextField txtName;
    private JTextField txtNumber;
    private JTable table;
    private DefaultTableModel tableModel;

    public ContactManagerGUI() {
        setTitle("Gestión de Amigos - CRUD con archivo (Actividad 5)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        refreshTable();
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        panel.add(new JLabel("Nombre:"));
        txtName = new JTextField();
        panel.add(txtName);

        panel.add(new JLabel("Número:"));
        txtNumber = new JTextField();
        panel.add(txtNumber);

        return panel;
    }

    private JScrollPane buildTablePanel() {
        tableModel = new DefaultTableModel(new Object[]{"Nombre", "Número"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);

        // Al hacer clic en una fila, se cargan los datos en el formulario
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtName.setText(tableModel.getValueAt(row, 0).toString());
                txtNumber.setText(tableModel.getValueAt(row, 1).toString());
            }
        });

        return new JScrollPane(table);
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JButton btnCreate = new JButton("Crear (Create)");
        JButton btnRead = new JButton("Leer (Read)");
        JButton btnUpdate = new JButton("Actualizar (Update)");
        JButton btnDelete = new JButton("Eliminar (Delete)");
        JButton btnClear = new JButton("Limpiar");

        btnCreate.addActionListener(e -> onCreate());
        btnRead.addActionListener(e -> refreshTable());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnClear.addActionListener(e -> clearForm());

        panel.add(btnCreate);
        panel.add(btnRead);
        panel.add(btnUpdate);
        panel.add(btnDelete);
        panel.add(btnClear);

        return panel;
    }

    // -------- lógica de los botones --------

    private void onCreate() {
        try {
            Friend friend = readFormOrShowError();
            if (friend == null) return;

            boolean created = dao.create(friend);
            if (created) {
                showInfo("Contacto agregado correctamente.");
                clearForm();
                refreshTable();
            } else {
                showWarning("Ya existe un contacto con ese nombre.");
            }
        } catch (IOException ex) {
            showError("Error al escribir en el archivo: " + ex.getMessage());
        }
    }

    private void onUpdate() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            showWarning("Selecciona o escribe el nombre del contacto a actualizar.");
            return;
        }
        try {
            long newNumber = Long.parseLong(txtNumber.getText().trim());
            boolean updated = dao.update(name, newNumber);
            if (updated) {
                showInfo("Contacto actualizado correctamente.");
                clearForm();
                refreshTable();
            } else {
                showWarning("No se encontró un contacto con ese nombre.");
            }
        } catch (NumberFormatException ex) {
            showWarning("El número debe contener solo dígitos.");
        } catch (IOException ex) {
            showError("Error al actualizar el archivo: " + ex.getMessage());
        }
    }

    private void onDelete() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            showWarning("Selecciona o escribe el nombre del contacto a eliminar.");
            return;
        }
        try {
            boolean deleted = dao.delete(name);
            if (deleted) {
                showInfo("Contacto eliminado correctamente.");
                clearForm();
                refreshTable();
            } else {
                showWarning("No se encontró un contacto con ese nombre.");
            }
        } catch (IOException ex) {
            showError("Error al eliminar del archivo: " + ex.getMessage());
        }
    }

    private void refreshTable() {
        try {
            List<Friend> friends = dao.readAll();
            tableModel.setRowCount(0);
            for (Friend f : friends) {
                tableModel.addRow(new Object[]{f.getName(), f.getNumber()});
            }
        } catch (IOException ex) {
            showError("Error al leer el archivo: " + ex.getMessage());
        }
    }

    // -------- utilidades --------

    private Friend readFormOrShowError() {
        String name = txtName.getText().trim();
        String numberText = txtNumber.getText().trim();

        if (name.isEmpty() || numberText.isEmpty()) {
            showWarning("Debes llenar nombre y número.");
            return null;
        }
        try {
            long number = Long.parseLong(numberText);
            return new Friend(name, number);
        } catch (NumberFormatException ex) {
            showWarning("El número debe contener solo dígitos.");
            return null;
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtNumber.setText("");
        table.clearSelection();
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarning(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Atención", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
