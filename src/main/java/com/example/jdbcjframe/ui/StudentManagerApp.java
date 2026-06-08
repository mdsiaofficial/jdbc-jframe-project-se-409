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
    private JLabel totalStudentsLabel;
    private JButton saveButton;
    private int editingId = -1;

    public StudentManagerApp() {
        super("Student Manager");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);

        Font titleFont = new Font(Font.SANS_SERIF, Font.BOLD, 30);
        Font headerFont = new Font(Font.SANS_SERIF, Font.BOLD, 18);
        Font bodyFont = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
        setUIFont(bodyFont);

        JPanel content = new JPanel(new BorderLayout(16, 16));
        content.setBorder(new EmptyBorder(18, 18, 18, 18));
        content.setBackground(new Color(240, 245, 250));
        setContentPane(content);

        JLabel titleLabel = new JLabel("Student Manager");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(new Color(22, 93, 132));

        JLabel subtitleLabel = new JLabel("Add, search, edit and organize student records with ease.");
        subtitleLabel.setFont(headerFont);
        subtitleLabel.setForeground(new Color(80, 100, 120));

        JPanel headerPanel = new JPanel(new BorderLayout(0, 8));
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.BOTH;

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 220), 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        formPanel.setBackground(Color.WHITE);

        JLabel formTitle = new JLabel("Student Details");
        formTitle.setFont(headerFont);
        formTitle.setForeground(new Color(24, 87, 129));

        nameField = new JTextField(36);
        emailField = new JTextField(36);
        courseField = new JTextField(36);

        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(10, 10, 10, 10);
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.gridx = 0;
        formGbc.gridy = 0;
        formGbc.gridwidth = 2;
        formPanel.add(formTitle, formGbc);

        formGbc.gridwidth = 1;
        formGbc.gridy++;
        formGbc.gridx = 0;
        formPanel.add(new JLabel("Name"), formGbc);
        formGbc.gridx = 1;
        formPanel.add(nameField, formGbc);

        formGbc.gridy++;
        formGbc.gridx = 0;
        formPanel.add(new JLabel("Email"), formGbc);
        formGbc.gridx = 1;
        formPanel.add(emailField, formGbc);

        formGbc.gridy++;
        formGbc.gridx = 0;
        formPanel.add(new JLabel("Course"), formGbc);
        formGbc.gridx = 1;
        formPanel.add(courseField, formGbc);

        saveButton = createButton("Save Student", new Color(22, 93, 132), Color.WHITE);
        saveButton.addActionListener(e -> addStudent());
        formGbc.gridy++;
        formGbc.gridx = 0;
        formGbc.gridwidth = 2;
        formGbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(saveButton, formGbc);

        JPanel tableCard = new JPanel(new BorderLayout(12, 12));
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 220), 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(Color.WHITE);
        searchField = new JTextField(28);
        JButton searchButton = createButton("Search", new Color(0, 120, 145), Color.WHITE);
        searchButton.addActionListener(e -> searchStudents());
        JButton clearSearchButton = createButton("Clear", new Color(190, 200, 210), new Color(45, 60, 75));
        clearSearchButton.addActionListener(e -> {
            searchField.setText("");
            loadStudents();
        });

        GridBagConstraints searchGbc = new GridBagConstraints();
        searchGbc.insets = new Insets(6, 6, 6, 6);
        searchGbc.fill = GridBagConstraints.HORIZONTAL;
        searchGbc.gridx = 0;
        searchPanel.add(new JLabel("Search by name"), searchGbc);
        searchGbc.gridx = 1;
        searchPanel.add(searchField, searchGbc);
        searchGbc.gridx = 2;
        searchGbc.weightx = 0;
        searchPanel.add(searchButton, searchGbc);
        searchGbc.gridx = 3;
        searchPanel.add(clearSearchButton, searchGbc);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Email", "Course"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setRowHeight(28);
        studentTable.setAutoCreateRowSorter(true);
        studentTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        studentTable.getTableHeader().setBackground(new Color(22, 93, 132));
        studentTable.getTableHeader().setForeground(Color.WHITE);
        studentTable.getTableHeader().setFont(bodyFont.deriveFont(Font.BOLD, 16f));
        studentTable.setFont(bodyFont);

        JScrollPane tableScrollPane = new JScrollPane(studentTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableScrollPane.setPreferredSize(new Dimension(0, 360));

        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        tableHeaderPanel.setOpaque(false);
        JLabel tableTitle = new JLabel("Saved Students");
        tableTitle.setFont(headerFont);
        tableTitle.setForeground(new Color(24, 87, 129));
        tableHeaderPanel.add(tableTitle, BorderLayout.WEST);

        JLabel totalStudentsLabel = new JLabel("Total students: 0");
        totalStudentsLabel.setFont(bodyFont);
        totalStudentsLabel.setForeground(new Color(80, 100, 120));
        tableHeaderPanel.add(totalStudentsLabel, BorderLayout.EAST);

        JPanel tableTopPanel = new JPanel(new BorderLayout(0, 8));
        tableTopPanel.setOpaque(false);
        tableTopPanel.add(tableHeaderPanel, BorderLayout.NORTH);
        tableTopPanel.add(searchPanel, BorderLayout.SOUTH);

        tableCard.add(tableTopPanel, BorderLayout.NORTH);
        tableCard.add(tableScrollPane, BorderLayout.CENTER);

        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 10));
        actionButtonsPanel.setOpaque(false);
        JButton editButton = createButton("Edit", new Color(18, 123, 167), Color.WHITE);
        editButton.addActionListener(e -> editSelectedStudent());
        JButton deleteButton = createButton("Delete", new Color(219, 57, 57), Color.WHITE);
        deleteButton.addActionListener(e -> deleteSelectedStudent());
        JButton refreshButton = createButton("Refresh", new Color(22, 93, 132), Color.WHITE);
        refreshButton.addActionListener(e -> loadStudents());
        JButton clearFormButton = createButton("Clear", new Color(190, 200, 210), new Color(45, 60, 75));
        clearFormButton.addActionListener(e -> clearForm());
        actionButtonsPanel.add(editButton);
        actionButtonsPanel.add(deleteButton);
        actionButtonsPanel.add(refreshButton);
        actionButtonsPanel.add(clearFormButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.33;
        gbc.weighty = 1.0;
        mainPanel.add(formPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.67;
        mainPanel.add(tableCard, gbc);

        content.add(headerPanel, BorderLayout.NORTH);
        content.add(mainPanel, BorderLayout.CENTER);
        content.add(actionButtonsPanel, BorderLayout.SOUTH);

        this.totalStudentsLabel = totalStudentsLabel;
        loadStudents();
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        return button;
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
            if (totalStudentsLabel != null) {
                totalStudentsLabel.setText("Total students: " + students.size());
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
