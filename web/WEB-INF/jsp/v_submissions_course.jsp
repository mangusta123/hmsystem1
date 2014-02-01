

<%@page import="favu.hms.model.course.*, java.util.Collection, java.util.Iterator" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%
        // this should be set by some other page
        Course course = (Course) request.getAttribute("course");
        Collection<Homework> hwList = course.getHwList();
        Iterator<Homework> iter = hwList.iterator();
        String selectedHW = request.getParameter("selectedHW");
        // if this is the first time this page is accessed
        // selectedHW is set to the first homework
        if (selectedHW == null) {
            if (iter.hasNext()) {
                selectedHW = String.valueOf(iter.next().getHomeworkID());
            }
        }
        // now set the homeworkID attribute
        request.setAttribute("homeworkID", selectedHW);
        int hwno = 1;
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Course homework submission</title>
    </head>
    <body>
        <h1>Homework submission list</h1>
        <form action="v_submissions_course.jsp" method="POST">
            Select a homework:
            <select name="selectedHW">
                <%
                    while (iter.hasNext()) {
                        Integer hwID = iter.next().getHomeworkID();
                        out.print("<option value=\"" + hwID + "\">");
                        out.print(hwno + "</option>");
                        hwno++;
                    }
                %>
            </select>
            <input type="submit" value="Go" />
        </form>

        <%@include file="v_submissions_by_hw.jsp" %>
    </body>
</html>
