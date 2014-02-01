

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Add new homework</title>
    </head>
    <body>
        <h2>Add new homework</h2>
        <form action="addHomework.cgi" method="POST" enctype="multipart/form-data">
            <input name="homeworkID" type="hidden" value="-1" />
            <% out.print("<input name=\"courseID\" type=\"hidden\" value=\"" + request.getParameter("courseID") + "\" />"); %>
            <table border="0" cellspacing="1" cellpadding="1">
                <tbody>
                    <tr>
                        <td>Homework Name</td>
                        <td><input type="text" name="name"  /></td>
                    </tr>
                    <tr>
                        <td>Homework Description</td>
                        <td><textarea name="description" rows="4" cols="20"></textarea></td>
                    </tr>
                    <tr>
                        <td>Attach files</td>
                        <td><input type="file" name="files" value="" /></td>
                    </tr>
                    <tr>
                        <td>Due date</td>
                        <td><input type="text" name="dueDate" value="" /></td>
                    </tr>
                    <tr>
                        <td>Enable re-submit?</td>
                        <td>
                            Yes <input type="radio" name="updatable" value="Yes" />
                            No <input type="radio" name="updatable" value="No" checked/>
                        </td>
                    </tr>
                    <tr>
                        <td>Grading policy</td>
                        <td><input type="text" name="gradingPolicy" value="" /></td>
                    </tr>
                </tbody>
                <tr>
                    <td></td>
                    <td>
                        <input type="submit" value="Add homework" />
                        <input type="reset" value="Cancel" />
                    </td>
                </tr>
            </table>
        </form>
    </body>
</html>
