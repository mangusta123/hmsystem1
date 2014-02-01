

<%--
Given a course id, display the content of a course.
The content is a little different for a student and instructor.
It contains description about the course and list of homeworks.
If an instructor is viewing this course, he has the chance to
add a new homework.
--%>
<%@page import="favu.hms.model.course.*" %>
<%@page import="favu.hms.model.user.*" %>
<%@page import="java.util.Collection" %>
<%
        String courseID = request.getParameter("courseID");
        Course course = null;
        if (courseID == null) {
            out.println("Please select a course");
        } else {
            course = Course.courseWithId(courseID);
%>

<h2><%= course.getCourseName()%></h2>
<b>Instructor: </b> <%= course.getCourseInstr()%> <br />
<%= course.getCourseInfo()%><br />
<b>List of homeworks </b>
<ul>
    <%
            Collection<Homework> hws = course.getHwList();
            for (Homework hw : hws) {
                out.print("<li>");
                out.print("<a href=\"homework.jsp?courseID=" + courseID + "&" + "homeworkID=" + hw.getHomeworkID());
                out.print("\">");
                out.print(hw.getName());
                out.print("</a>");
                out.println("</li>");
            }

            // add the add new course link for instructor
            if (request.getSession().getAttribute("instructorID") != null) {
                out.print("<a href=\"addHomework.jsp?courseID=" + courseID);
                out.print("\">");
                out.print("Add new homework");
                out.print("</a>");
            }

        } // end-else
%>
</ul>
