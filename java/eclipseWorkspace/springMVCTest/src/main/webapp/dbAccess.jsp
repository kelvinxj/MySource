<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.*" %>
<%@ page import="javax.sql.*" %>
<%@ page import="javax.naming.*" %>
<%@ page import="java.util.Date" %>
<%--@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Show world cities</title>
</head>
<body>
<%
Date myDate = new Date();
String showType = "";
int top = 20;
String countryName = request.getParameter("countryName");
if(countryName == null)
	showType = "world";
else
	showType = countryName;
%>
<h1>Current Date:<%=myDate%></h1>
<form method="post" action="dbAccess.jsp">
	<span>Please input country name:</span>
	<input type="text" name="countryName"/>
	<input type="submit" value="Show City"/>
	<br/>
	<span>Show top <%=top %> <%=showType %>'s City</span>
	<%
	Connection conn = null;
	//out.println(jndiName);
	Statement stmt = null;
	ResultSet rs = null;
	try{
		Context initCtx = new InitialContext();
		DataSource ds = (DataSource)initCtx.lookup("java:comp/env/jdbc/world");
		conn = ds.getConnection();
		stmt = conn.createStatement();
		String sql = "";
		if(countryName == null){
			sql = "select t1.name as cityName, t2.name as countryName  " 
					+" from city t1 inner join country t2 " 
					+" on t1.countryCode = t2.code";
		}
		else{
			sql = "select t1.name as cityName, t2.name as countryName  " 
					+" from city t1 inner join country t2 " 
					+" on t1.countryCode = t2.code where t2.name like '"+countryName + "%'";
		}
		rs = stmt.executeQuery(sql);
		int i = 0;
	%>
	<table border="1">
	<tr>
		<td>City Name</td>
		<td>Country Name</td>
	</tr>
	<%
		while(rs.next()){
			if(i >= top)
				break;
	%>
	<tr>
		<td><%= rs.getString(1)%></td>
		<td><%= rs.getString(2)%></td>
	</tr>
	<%
			//out.println("ciytName: " + rs.getString(1) + ", country name: " + rs.getString(2) + "<br/>");
			i++;
		}
	}
	catch(Exception ex){
		out.println(ex);
	}
	finally{
		stmt.close();
		rs.close();
		conn.close();
	}
	%>
	</table>
</form>
</body>
</html>