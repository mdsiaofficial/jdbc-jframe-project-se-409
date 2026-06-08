package com.example.jdbcjframe.ui;

import com.example.jdbcjframe.dao.StudentDAO;
import com.example.jdbcjframe.model.Student;

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
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
    private final JTextField searchField;
    private JButton saveButton;
    private int editingId = -1;

    public StudentManagerApp() {
        super("Basic JDBC Student Manager by Shoriful 1010");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout(28, 28));
        content.setBorder(new EmptyBorder(24, 24, 24, 24));
        setContentPane(content);

        Font appFont = new Font(Font.SANS_SERIF, Font.PLAIN, 28);
        setUIFont(appFont);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add Student"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(18, 18, 18, 18);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField(30);
        emailField = new JTextField(30);
        courseField = new JTextField(30);

        addRow(formPanel, gbc, 0, "Name", nameField);
        addRow(formPanel, gbc, 1, "Email", emailField);
        addRow(formPanel, gbc, 2, "Course", courseField);

        saveButton = new JButton("Save Student");
        saveButton.addActionListener(e -> addStudent());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(22, 18, 18, 18);
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

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 28, 20));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadStudents());
        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(e -> deleteSelectedStudent());
        JButton editButton = new JButton("Edit Selected");
        editButton.addActionListener(e -> editSelectedStudent());
        JButton clearFormButton = new JButton("Clear Form");
        clearFormButton.addActionListener(e -> clearForm());
        actionsPanel.add(refreshButton);
        actionsPanel.add(deleteButton);
        actionsPanel.add(editButton);
        actionsPanel.add(clearFormButton);

        // Search panel above the form
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 28, 20));
        searchField = new JTextField(30);
        JButton searchButton = new JButton("Search by Name");
        searchButton.addActionListener(e -> searchStudents());
        JButton clearSearchButton = new JButton("Clear Search");
        clearSearchButton.addActionListener(e -> { searchField.setText(""); loadStudents(); });
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearSearchButton);

        studentTable.setRowHeight(42);
        studentTable.getTableHeader().setFont(appFont.deriveFont(Font.BOLD, 26));
        studentTable.getTableHeader().setPreferredSize(new Dimension(0, 56));
        studentTable.getTableHeader().setBackground(Color.RED);
        studentTable.getTableHeader().setForeground(Color.WHITE);
        studentTable.getTableHeader().setOpaque(true);

        applyFontToComponent(content, appFont);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(searchPanel, BorderLayout.NORTH);
        northPanel.add(formPanel, BorderLayout.CENTER);

        content.add(northPanel, BorderLayout.NORTH);
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

    private void setUIFont(Font font) {
        UIManager.put("Label.font", font);
        UIManager.put("Button.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("Table.font", font);
        UIManager.put("TableHeader.font", font.deriveFont(Font.BOLD, font.getSize()));
        UIManager.put("TitledBorder.font", font.deriveFont(Font.BOLD, font.getSize()));
        UIManager.put("Panel.font", font);
        UIManager.put("OptionPane.messageFont", font);
        UIManager.put("OptionPane.buttonFont", font);
    }

    private void applyFontToComponent(java.awt.Component component, Font font) {
        component.setFont(font);
        if (component instanceof java.awt.Container) {
            for (java.awt.Component child : ((java.awt.Container) component).getComponents()) {
                applyFontToComponent(child, font);
            }
        }
    }

    private void addStudent() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String course = courseField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || course.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // simple email validation
        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
        Matcher matcher = emailPattern.matcher(email);
        if (!matcher.matches()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (editingId > 0) {
                studentDAO.update(new Student(editingId, name, email, course));
                JOptionPane.showMessageDialog(this, "Student updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                studentDAO.insert(new Student(0, name, email, course));
                JOptionPane.showMessageDialog(this, "Student saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            clearForm();
            loadStudents();
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
        editingId = -1;
        if (saveButton != null) {
            saveButton.setText("Save Student");
        }
    }

    private void editSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);
        String email = (String) tableModel.getValueAt(selectedRow, 2);
        String course = (String) tableModel.getValueAt(selectedRow, 3);

        editingId = id;
        nameField.setText(name);
        emailField.setText(email);
        courseField.setText(course);
        if (saveButton != null) {
            saveButton.setText("Update Student");
        }
    }

    private void searchStudents() {
        String q = searchField.getText().trim();
        if (q.isEmpty()) {
            loadStudents();
            return;
        }

        try {
            List<Student> students = studentDAO.findByName(q);
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
