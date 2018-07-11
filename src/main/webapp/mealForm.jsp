<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Meal</title>
</head>
<body>
<jsp:useBean id="meal" type="ru.javawebinar.topjava.model.Meal" scope="request"/>
<h3>${param.action=='create'?'Creating':'Updating'} meal</h3>
<form method="post" action="meals">
    <input type="hidden" name="id" value="${meal.id}">
    <input type="datetime-local" value="${meal.dateTime}" name="dateTime" required><br><br>
    <input type="text" value="${meal.description}" name="description" required placeholder="еда"><br><br>
    <input type="number" value="${meal.calories}" name="calories" required placeholder="калории" min="1"><br><br>
    <button>${param.action}</button>
    &#8195&#8195&#8195
    <button type="reset" onclick="window.history.back()">cancel</button>
</form>
</body>
</html>
