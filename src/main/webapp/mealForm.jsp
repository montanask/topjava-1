<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>create or update meal</title>
</head>
<body>
<h3>${meal.isNew()?'Creating':'Updating'} meal</h3>
<form method="post">
    <input type="number" value="${meal.isNew()?"":meal.id}" name="id" hidden>
    <input type="datetime-local" value="${meal.dateTime}" name="dateTime" required><br><br>
    <input type="text" value="${meal.description}" name="description" required placeholder="еда"><br><br>
    <input type="number" value="${meal.calories}" name="calories" required placeholder="калории" min="1"><br><br>
    <button>${meal.isNew()?'create':'update'}</button>
    &#8195&#8195&#8195
    <button type="reset" onclick="window.history.back()">cancel</button>
</form>
</body>
</html>
