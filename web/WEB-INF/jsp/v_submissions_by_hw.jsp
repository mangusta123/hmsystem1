<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>


<%@page import="favu.hms.model.course.*, favu.hms.database.DatabaseInterface, java.util.Collection"%>

<%-- 
Instructor's page for view submissions
This page displays all homework submission for a homework given its id.
The id is expected to be set by some manager
It will be included by other page so the html code is not completed --%>

<h3>Bulletin board</h3>

<%
        String hwid = (String)request.getAttribute("homeworkID");
        if (hwid==null) hwid = request.getParameter("homeworkID");

        Homework hw = Homework.homeworkWithId(Integer.parseInt(hwid));
        // retrieve all homework submissions for this homework from database
        Collection<HWSubmission> submissionList = hw.getSubmissions();
        int no = 1;
%>

<table border="0" width="1" cellspacing="1" cellpadding="1">
    <thead>
        <tr>
            <th>No</th>
            <th>Submitted by</th>
            <th>Content</th>
            <th>Submitted date</th>
        </tr>
    </thead>
    <tbody>
        <%
        for (HWSubmission sms : submissionList) {
        %>
      <tr>
        <td><%= no++%></td>
        <td><%= sms.getSubmitterID()%> </td>
        <td>
            <% String href = "viewSubmission.cgi?submissionID=" + sms.getHWSubmissionID(); %>
            <a href="<%=href%>">view</a>
        </td>
        <td><%= sms.getSubmissionDate()%></td>
      </tr>
        <%
        }
        %>
    </tbody>
</table>
