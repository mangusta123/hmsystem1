

<%@page import="favu.hms.model.user.*, favu.hms.model.course.*, favu.hms.database.DatabaseInterface, java.util.Collection, java.util.Iterator"%>

<h3>Bulletin board</h3>
<%
        String hwid = (String)request.getAttribute("homeworkID");
        if (hwid==null) hwid = request.getParameter("homeworkID");

        Homework hwk = Homework.homeworkWithId(Integer.parseInt(hwid));
        // retrieve all homework submissions for this homework from database
        Collection<Message> messageList = hwk.getMessages();
%>

<table border="0" cellspacing="10" cellpadding="1">
    <thead>
        <tr>
            <th>Date</th>
            <th>Content</th>
            <th>By</th>
        </tr>
    </thead>
    <tbody>
        <%
        for (Message msg : messageList) {
        %>
      <tr>
        <td><%= msg.getCreationDate() %> </td>
        <td><%= msg.getContent() %></td>
        <td><%= User.userWithId(msg.getSendID()).getName() %></td>
      </tr>
        <%
        }
        %>
    </tbody>
</table>

<form action="addMessage.cgi" method="POST">
    <label for="content">New Message</label><br/>
    <textarea cols="30" rows="3" name="content" id="content"></textarea><br/>
    <input name="btnPost" type="submit" id="btnPost" value="Submit" />
    <input name="btnCancel" type="reset" id="btnCancel" value="Cancel" />
    <input type="hidden" name="homeworkID" value="<%= hwid %>"/>
</form>


