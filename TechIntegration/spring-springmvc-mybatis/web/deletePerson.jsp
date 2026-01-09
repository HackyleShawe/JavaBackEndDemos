
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>deletePerson</title>
</head>
<body>
    <h3>deletePerson</h3>

    <form action="${pageContext.request.contextPath}/person/deletePerson" method="post">
        Person ID:<input type="number" name="id"> <br/>
        <input type="submit" value="SUBMIT">
    </form>
</body>
</html>
