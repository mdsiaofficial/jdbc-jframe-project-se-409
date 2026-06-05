<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Manager</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background: #f6f7fb; color: #222; }
        .card { background: #fff; padding: 20px; border-radius: 12px; box-shadow: 0 8px 24px rgba(0,0,0,.08); margin-bottom: 24px; }
        input, button { padding: 10px; margin: 6px 0; width: 100%; box-sizing: border-box; }
        button { cursor: pointer; background: #2d6cdf; color: #fff; border: 0; border-radius: 8px; }
        table { width: 100%; border-collapse: collapse; background: #fff; }
        th, td { padding: 12px; border-bottom: 1px solid #e5e7eb; text-align: left; }
        .delete-btn { background: #dc2626; width: auto; }
        .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 12px; }
    </style>
</head>
<body>
<h1>Student Manager</h1>
<div class="card">
    <h2>Add Student</h2>
    <form method="post" action="${pageContext.request.contextPath}/students">
        <input type="hidden" name="action" value="add">
        <div class="grid">
            <input type="text" name="name" placeholder="Name" required>
            <input type="email" name="email" placeholder="Email" required>
            <input type="text" name="course" placeholder="Course" required>
        </div>
        <button type="submit">Save Student</button>
    </form>
</div>

<div class="card">
    <h2>Saved Students</h2>
    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Course</th>
            <th>Action</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="student" items="${students}">
            <tr>
                <td>${student.id}</td>
                <td>${student.name}</td>
                <td>${student.email}</td>
                <td>${student.course}</td>
                <td>
                    <form method="post" action="${pageContext.request.contextPath}/students" style="display:inline;">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" value="${student.id}">
                        <button class="delete-btn" type="submit">Delete</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>
