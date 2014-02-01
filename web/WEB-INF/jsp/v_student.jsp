

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Student homepage</title>
    </head>
    <body>
        <div>
            <div class="left" style="width:30%;">
                <%@include file="course_list_student.jsp" %>
            </div>
            <div class="right">
                <%@include file="v_course.jsp" %>
            </div>
        </div>
    </body>
</html>
