package presentation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

abstract public class AbstractUI<T> extends JFrame {
    private JTable table;
    DefaultTableModel tableModel;
    Class<T> instanceClass;
    JPanel inputPanel;
    List<T> dataList;

    public AbstractUI(List<T> dataList, Class<T> instanceClass) {
        this.dataList = dataList;
        this.instanceClass = instanceClass;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddDialog();
            }
        });

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    try {
                        editInstance();
                    } catch (IllegalAccessException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(AbstractUI.this, "Please select a row to edit.");
                }
            }
        });

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    deleteInstance();
                } else {
                    JOptionPane.showMessageDialog(AbstractUI.this, "Please select a row to delete.");
                }
            }
        });

        JButton previousButton = new JButton("Previous");
        previousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMainMenu();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(previousButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        populateTable();

        add(panel);
    }

    private void populateTable() {
        if (dataList.isEmpty()) {
            return;
        }

        Field[] fields = instanceClass.getDeclaredFields();

        for (Field field : fields) {
            tableModel.addColumn(field.getName());
        }

        for (T data : dataList) {
            Object[] rowData = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                try {
                    rowData[i] = fields[i].get(data);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            tableModel.addRow(rowData);
        }
    }

    private Object parseFieldValue(Class<?> fieldType, String value) {
        if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
            return Integer.parseInt(value);
        } else {
            return value;
        }
    }

    //ADD ACTION
    public void addInstance(T instance) {
        if (!add(instance)) {
            return;
        }
        dataList.add(instance);

        Field[] fields = instanceClass.getDeclaredFields();

        Object[] rowData = new Object[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            try {
                rowData[i] = fields[i].get(instance);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        tableModel.addRow(rowData);
    }

    private void showAddDialog() {
        if (instanceClass == null) {
            return;
        }
        List<JTextField> textFields = new ArrayList<>();
        Field[] fields = instanceClass.getDeclaredFields();

        inputPanel = new JPanel(new GridLayout(fields.length - 1, 2));

        for (int i = 1; i < fields.length; i++) {
            fields[i].setAccessible(true);
            JLabel label = new JLabel(fields[i].getName());
            JTextField textField = new JTextField();
            inputPanel.add(label);
            inputPanel.add(textField);
            textFields.add(textField);
        }

        int result = JOptionPane.showConfirmDialog(AbstractUI.this, inputPanel,
                "Add Instance", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Constructor<?> constructor = instanceClass.getConstructor();
                T newInstance = (T) constructor.newInstance();

                for (int i = 1; i < fields.length; i++) {
                    fields[i].setAccessible(true);
                    Object fieldValue = parseFieldValue(fields[i].getType(), textFields.get(i - 1).getText());
                    fields[i].set(newInstance, fieldValue);
                }

                addInstance(newInstance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //EDIT ACTION
    public void editInstance() throws IllegalAccessException {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            // Get the selected client
            T selectedInstance = dataList.get(selectedRow);
            List<JTextField> textFields = new ArrayList<>();
            // Show the edit dialog to modify the client data
            boolean isConfirmed = showEditDialog(selectedInstance, textFields);
            if (isConfirmed) {
                // Update the selected client with the modified field values
                updateInstanceFields(selectedInstance, selectedRow, textFields);
                // Refresh the table
                tableModel.fireTableDataChanged();
            }
        }
    }

    public void updateInstanceFields(T instance, int row, List<JTextField> textFields) {
        Field[] fields = instanceClass.getDeclaredFields();

        for (int i = 1; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            String fieldValue = textFields.get(i - 1).getText();

            try {
                if (field.getType() == int.class) {
                    int intValue = Integer.parseInt(fieldValue);
                    field.setInt(instance, intValue);
                } else {
                    field.set(instance, fieldValue);
                }
                tableModel.setValueAt(fieldValue, row, i);
            } catch (IllegalAccessException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
        edit(instance, instance);
    }

    private boolean showEditDialog(T instance, List<JTextField> textFields) throws IllegalAccessException {
        JPanel inputPanel = new JPanel(new GridLayout(instanceClass.getDeclaredFields().length - 1, 2));

        for (int i = 1; i < instanceClass.getDeclaredFields().length; i++) {
            Field field = instanceClass.getDeclaredFields()[i];
            field.setAccessible(true);
            JLabel label = new JLabel(field.getName());
            JTextField textField = new JTextField(String.valueOf(field.get(instance)));
            inputPanel.add(label);
            inputPanel.add(textField);
            textFields.add(textField);
        }

        this.inputPanel = inputPanel;  // Store the inputPanel reference in the class variable

        int result = JOptionPane.showConfirmDialog(null, inputPanel, "Edit Client", JOptionPane.OK_CANCEL_OPTION);
        return result == JOptionPane.OK_OPTION;
    }

    //DELETE ACTION
    public void deleteInstance() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            // Get the selected client
            T selectedInstance = dataList.get(selectedRow);

            // Remove the client from the list
            dataList.remove(selectedInstance);
            delete(selectedInstance);

            // Remove the selected row from the table
            tableModel.removeRow(selectedRow);
        }
    }

    //Main Menu
    private void showMainMenu() {
        MainMenuUI mainMenuUI = new MainMenuUI();
        mainMenuUI.setVisible(true);
        dispose(); // Close the current UI
    }

    abstract boolean add(T instance);
    abstract void edit(T instance, T newInstance);
    abstract void delete(T instance);
}
