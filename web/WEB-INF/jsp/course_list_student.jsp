

<%-- display a list of courses for student --%>

<%@page import="favu.hms.model.course.*, favu.hms.model.user.*, favu.hms.database.DatabaseInterface,java.util.Collection" %>
<%
        /*
        This is NOT where the enrollment is supposed to be!
        This page is just a list of course for students in his homepage.
        if(request.getParameter("status").equals("OK")){
            out.print("<br> Your course has been dropped <br>");
        }else if (request.getParameter("status").equals("NOK")){
            out.print("<br> Error :  Your course has been dropped <br>");

          if (request.getParameter("status")!=null) {
            if(request.getParameter("status").equals("OK")){
                out.print("<br> Your course has been dropped <br>");
            } else if (request.getParameter("status").equals("NOK")){
                out.print("<br> Error :  Your course has been dropped <br>");
            }
        */
                
        Integer id;
        Student student = null;
        DatabaseInterface db = DatabaseInterface.getInstance();

        id = (Integer)request.getSession().getAttribute("studentID");
        student = db.retrieveStudentWithId(id);

        Collection<Course> courses;
        courses = student.getCoursetakenCourses();

        out.println("<ul>");
        for (Course c : courses) {
            out.print("<li><a href=\"studentHome.jsp?courseID=");
            out.print(c.getCourseID() + "\">");
            out.print(c.getCourseName());
            /* again, not in this place
            out.print("</a>  ");
            out.print("  <a href=\"DropCourse?courseID=");
            out.print(c.getCourseID() + "\">");
            out.print(c.getCourseName());
           */
            out.println("</a></li>");
 
        }
        out.println("</ul>");
%>
