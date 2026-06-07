package com.example.jdbcjframe.dao;

import com.example.jdbcjframe.model.Student;
import com.example.jdbcjframe.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT id, name, email, course FROM students ORDER BY id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                students.add(new Student(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("email"),
                    resultSet.getString("course")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load students", e);
        }
        return students;
    }

    public void insert(Student student) {
        String sql = "INSERT INTO students(name, email, course) VALUES(?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, student.getName());
            statement.setString(2, student.getEmail());
            statement.setString(3, student.getCourse());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add student", e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM students WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete student", e);
        }
    }
}
