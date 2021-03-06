

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="favu.hms.model.course.*, favu.hms.model.user.*, favu.hms.database.DatabaseInterface,java.util.Collection" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Course List </h1>
        <%
        if(request.getParameter("status").equals("OK")){
            out.print("<br>The course has been added </br>");
        }else if( request.getParameter("status").equals("NOK") ){
            out.print("<br>Error : The course has not been added </br>");
        }
        Collection<String> depList = Course.getAllDepartments();
        Course.getAllDepartments();
        for(String dep : depList){
            out.print("<br> Departement : ");
            out.print(dep+"<br>");
            Collection<Course> courseList = Course.getCourseForDepartment(dep);
            for(Course c: courseList){
                out.print("<li> <a href=\"JoinCourse?courseID=\"");
                out.print(c.getCourseID());
                out.print("\"> Join "+c.getCourseName()+"</a></li>");
            }
            out.print("<br>");
        }

        %>
    </body>
</html>
