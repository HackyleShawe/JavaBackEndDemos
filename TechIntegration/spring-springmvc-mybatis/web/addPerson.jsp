<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>addPerson</title>
</head>
<body>

    <h3>addPerson</h3>

    <form action="${pageContext.request.contextPath}/person/addPerson" method="post">
        name:<input type="text" name="name"> <br/>
        age:<input type="number" name="age"> <br/>

        sex:<input type="text" name="sex"> <br/>
        <%--birthday:<input type="text" name="birthday"> <br/>--%>
        address:<input type="text" name="address" > <br/>
        tel:<input type="text" name="tel"> <br/>
        email:<input type="text" name="email"> <br/>
        <input type="submit" value="SUBMIT">
    </form>

</body>
</html>
