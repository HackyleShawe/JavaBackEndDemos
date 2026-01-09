<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>updatePerson</title>
</head>
<body>
    <h3>updatePerson</h3>

    <form action="${pageContext.request.contextPath}/person/updatePerson" method="post">
        id:<input type="text" name="id"> <br/>

        name:<input type="text" name="name"> <br/>
        age:<input type="number" name="age"> <br/>

        sex:<input type="sex" name="sex"> <br/>
        birthday:<input type="date" name="birthday"> <br/>
        address:<input type="text" name="address" > <br/>
        tel:<input type="text" name="tel"> <br/>
        email:<input type="text" name="email"> <br/>
        <input type="submit" value="SUBMIT">
    </form>

</body>
</html>
