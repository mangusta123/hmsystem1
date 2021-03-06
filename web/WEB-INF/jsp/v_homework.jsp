

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@page import="favu.hms.model.course.*" %>
<%@page import="favu.hms.model.user.*" %>
<%@page import="favu.hms.database.*" %>
<%@page import="java.util.*" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>View homework</title>
    </head>
    <body>
        <%
        /*
         * There are 2 ways v_homework.jsp can be accessed
         * - from request dispatcher, then homeworkID is an attribute
         * - from direct link, then homeworkID is a parameter
         */
        String homeworkID = request.getParameter("homeworkID");

        if (homeworkID == null || homeworkID.equals("-1")) {
            homeworkID = ((Integer) request.getAttribute("homeworkID")).toString();
        } else {
            // access by user - should not be allowed actually
        }

        Homework hw = Homework.homeworkWithId(Integer.parseInt(homeworkID));
        %>
        <h1><%= hw.getName()%></h1>
        <table border="0" cellspacing="1" cellpadding="1">
            <tbody>
                <tr>
                    <td><b>Description</b></td>
                    <td><%= hw.getDescription()%></td>
                </tr>
                <tr>
                    <td><b>Attached files</b></td>
                    <td>
                        <%
                        Collection<String> files = hw.getFiles();
                        Iterator ite = files.iterator();
                        while (ite.hasNext()) {
                            String file = (String) ite.next();
                            out.print("<a href=\"uploads/" + file + "\">");
                            out.print(file);
                            out.print("</a>");
                        }
                        %>
                    </td>
                </tr>
                <tr>
                    <td><b>Assigned date</b></td>
                    <td><%= hw.getAssignedDate()%></td>
                </tr>
                <tr>
                    <td><b>Due date</b></td>
                    <td><%= hw.getDueDate()%></td>
                </tr>
                <tr>
                    <td><b>Re-submit enabled</b></td>
                    <td><%= hw.isUpdatable()%></td>
                </tr>
            </tbody>
        </table>

        <%
        /* submissions */
        if (request.getSession().getAttribute("instructorID") != null) {
          out.print("<h3><a href=\"submissions.jsp?homeworkID="+hw.getHomeworkID()+"\">View submissions</a></h3>");
        
        } else if (request.getSession().getAttribute("studentID") != null) {
          Student student = Student.studentWithId((Integer)request.getSession().getAttribute("studentID"));
          HWSubmission sub = student.getSubmissionForHomework(hw);
          if (sub!=null) {
            out.print("<h3><a href=\"viewSubmission.cgi?submissionID="+sub.getHWSubmissionID()+"\">View submission</a></h3>");
          } else {
        %>
        <h2>Submit</h2>
        <form action="submitSubmission.cgi" method="post" enctype="multipart/form-data" name="form1" id="form1">
          <table width="380" border="0">
            <tr>
              <th width="62" scope="row">File</th>
              <td width="308"><input name="submissionFile" type="file" id="submissionFile" /></td>
            </tr>
            <tr>
              <th scope="row">Message</th>
              <td><textarea name="submissionMessage" cols="50" rows="7" id="txtMessage"></textarea></td>
            </tr>
            <tr>
              <th scope="row">&nbsp;</th>
              <td><input name="btnSubmit" type="submit" id="btnSubmit" value="Submit" />
              <input type="hidden" name="homeworkID" value="<%= hw.getHomeworkID() %>">
              <input name="btnCancel" type="reset" id="btnCancel" value="Cancel" /></td>
            </tr>
          </table>
        </form>
        <%
          }
        }
        %>

      <%@include file="v_messages.jsp" %>

    </body>
</html>
