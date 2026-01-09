<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Person Management</title>
</head>
<body>

  <a href="${pageContext.request.contextPath}/addPerson.jsp">addPerson</a> <br/><br/>
  <a href="${pageContext.request.contextPath}/deletePerson.jsp">deletePerson</a> <br/><br/>
  <a href="${pageContext.request.contextPath}/updatePerson.jsp">updatePerson</a> <br/><br/>
  <a href="${pageContext.request.contextPath}/readAllPerson.jsp">readAllPerson</a> <br/><br/>

</body>
</html>
