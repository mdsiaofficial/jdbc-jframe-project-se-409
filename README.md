# Basic JDBC Student Manager

A simple Java desktop application built with:

- Swing for the interface
- JDBC for database operations
- MySQL for the database

## Features

- Add a student
- View all students
- Delete a student

## Project Structure

- `src/main/java` contains Java source code
- `pom.xml` configures the Maven desktop application

## How to Run

1. Install Java 11+, Maven, and MySQL.
2. Start MySQL in XAMPP.
3. Create a MySQL database named `student_management_jdbc_db` in phpMyAdmin.
4. Check `src/main/resources/db.properties` for your MySQL settings.
5. Build the project:
   ```bash
   mvn clean package
   ```
6. Run the desktop app:
   ```bash
   mvn exec:java
   ```

## Notes

- The `students` table is created automatically when the app first connects.
- Default connection settings assume XAMPP MySQL running on `localhost:3306` with user `root` and an empty password.

## Easier Local Setup

- `src/main/resources/db.properties` stores database settings in one place.
- Environment variables `DB_URL`, `DB_USER`, and `DB_PASSWORD` can override the file.
- IntelliJ can open the Maven project directly from the root folder.

## IntelliJ

1. Open the project root in IntelliJ.
2. Let IntelliJ import it as a Maven project.
3. Run `com.example.jdbcjframe.ui.StudentManagerApp`.

## UI Flow

- Enter a name, email, and course.
- Click `Save Student` to add a record.
- Select a row and click `Delete Selected` to remove it.
- Click `Refresh` to reload the table from the database.

## Eclipse

1. Use `File > Import > Existing Maven Projects`.
2. Select this project root.
3. Start Tomcat from the Servers view.
4. Run the project on Tomcat.
