<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://topjava.com/functions" prefix="f" %>
<html>
<head>
    <title>Meals</title>
    <link href="style.css" type="text/css" rel="stylesheet">
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<section>
    <a href="meals?action=create">Create Meal</a>
    <table>
        <thead>
        <tr>
            <th>Date</th>
            <th>Description</th>
            <th>Calories</th>
            <th colspan="2">Options</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${meals}" var="meal">
            <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.MealWithExceed" scope="page"/>
            <tr class="${meal.exceed?'exceeded':'normal'}">
                <td>${f:formatDateTime(meal.dateTime)}</td>
                <td>${meal.description}</td>
                <td>${meal.calories}</td>
                <td>
                    <a href="meals?action=update&id=${meal.id}">update</a>
                </td>
                <td>
                    <a href="meals?action=delete&id=${meal.id}">delete</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</section>
</body>
</html>
