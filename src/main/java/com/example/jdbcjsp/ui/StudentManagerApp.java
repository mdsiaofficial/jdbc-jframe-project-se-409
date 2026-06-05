package com.example.jdbcjsp.ui;

import com.example.jdbcjsp.dao.StudentDAO;
import com.example.jdbcjsp.model.Student;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

public class StudentManagerApp extends JFrame {
    private final StudentDAO studentDAO = new StudentDAO();
    private final DefaultTableModel tableModel;
    private final JTable studentTable;
    private final JTextField nameField;
    private final JTextField emailField;
    private final JTextField courseField;

    public StudentManagerApp() {
        super("Student Manager");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(860, 560);
        setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout(16, 16));
        content.setBorder(new EmptyBorder(16, 16, 16, 16));
        setContentPane(content);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add Student"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(18);
        emailField = new JTextField(18);
        courseField = new JTextField(18);

        addRow(formPanel, gbc, 0, "Name", nameField);
        addRow(formPanel, gbc, 1, "Email", emailField);
        addRow(formPanel, gbc, 2, "Course", courseField);

        JButton saveButton = new JButton("Save Student");
        saveButton.addActionListener(e -> addStudent());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 8, 8, 8);
        formPanel.add(saveButton, gbc);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Email", "Course"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(studentTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Saved Students"));

        JPanel actionsPanel = new JPanel();
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadStudents());
        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(e -> deleteSelectedStudent());
        actionsPanel.add(refreshButton);
        actionsPanel.add(deleteButton);

        content.add(formPanel, BorderLayout.NORTH);
        content.add(tableScrollPane, BorderLayout.CENTER);
        content.add(actionsPanel, BorderLayout.SOUTH);

        loadStudents();
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridwidth = 1;
        gbc.gridy = row;

        gbc.gridx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void addStudent() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String course = courseField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || course.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            studentDAO.insert(new Student(0, name, email, course));
            clearForm();
            loadStudents();
            JOptionPane.showMessageDialog(this, "Student saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        int choice = JOptionPane.showConfirmDialog(this, "Delete the selected student?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            studentDAO.delete(id);
            loadStudents();
            JOptionPane.showMessageDialog(this, "Student deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStudents() {
        try {
            List<Student> students = studentDAO.findAll();
            tableModel.setRowCount(0);
            for (Student student : students) {
                tableModel.addRow(new Object[]{
                    student.getId(),
                    student.getName(),
                    student.getEmail(),
                    student.getCourse()
                });
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        nameField.setText("");
        emailField.setText("");
        courseField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Keep the default look and feel if the system theme is unavailable.
            }
            new StudentManagerApp().setVisible(true);
        });
    }
}
