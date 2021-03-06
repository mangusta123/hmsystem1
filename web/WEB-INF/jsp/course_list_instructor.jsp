

<%-- display a list of courses for instructors --%>

<%@page import="favu.hms.model.course.*, favu.hms.model.user.*, favu.hms.database.DatabaseInterface,java.util.Collection" %>
<%
        Integer id;
        Instructor instructor = null;
        DatabaseInterface db = DatabaseInterface.getInstance();

        id = (Integer)request.getSession().getAttribute("instructorID");
        System.err.println("ID="+id);
        instructor = db.retrieveInstructorWithId(id);

        Collection<Course> courses = instructor.getProvidedCourses();

        out.println("<ul>");
        for (Course c : courses) {
            out.print("<li><a href=\"instructorHome.jsp?courseID=");
            out.print(c.getCourseID() + "\">");
            out.print(c.getCourseName());
            out.println("</a></li>");
        }
        out.println("</ul>");
%>
