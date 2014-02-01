

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="favu.hms.model.course.*" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%--
TODO: Map updateSubmissions.cgi to some servlet, most likely hwsubmission manager
--%>
<%-- This page displays a single homework submission viewed by instructor
and/or teaching assistant. The page viewed by student should not enable
editting of grade and grade comment field--%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Homework submission</title>
    </head>
    <body>
        <% HWSubmission submission = (HWSubmission)request.getAttribute("submission"); %>
        <form action="gradeSubmission.cgi" method="get">
            <table border="0" width="1" cellspacing="1" cellpadding="1">
                <tbody>
                    <tr>
                        <td><b>Course</b></td>
                        <td><%= submission.getCourse().getCourseName()%></td>
                    </tr>
                    <tr>
                        <td><b>Homework</b></td>
                        <td>
                            <%
                                favu.hms.model.course.Homework hw = favu.hms.model.course.Homework.homeworkWithId(submission.getHomeworkID());
                                out.print(hw.getName());
                            %>
                        </td>
                    </tr>
                    <tr>
                        <td><b>Submitted by</b></td>
                        <td><%= submission.getSubmitterID() %></td>
                    </tr>
                    <tr>
                        <td><b>Submitted file</b></td>
                        <td><a href="uploads/<%= submission.getFile() %>">Download</a></td>
                    </tr>
                    <tr>
                        <td><b>Submitted date</b></td>
                        <td><%= submission.getSubmissionDate() %></td>
                    </tr>
                    <tr>
                        <td><b>Last update</b></td>
                        <td><%= submission.getUpdateDate() %></td>
                    </tr>
                    <tr>
                        <td><b>Message</b></td>
                        <td><%= submission.getMessage() %></td>
                    </tr>
                    <tr>
                        <td><b>Grade</b></td>
                        <td>
                            <input type="text" name="grade" value="<%= submission.getGrade() %>" />
                        </td>
                    </tr>
                    <tr>
                        <td><b>Comment</b></td>
                        <td><input type="text" name="gradeComment" value="<%= submission.getGradeComment() %>" /></td>
                    </tr>
                    <tr>
                        <td>
                            <input type="submit" name="Save" />
                            <input type="hidden" name="submissionID" value="<%= submission.getHWSubmissionID() %>"/>
                        </td>
                        <td></td>
                    </tr>
                </tbody>
            </table>
        </form>
    </body>
</html>
