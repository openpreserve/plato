<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
   <head>
     <title>Character encoding test page</title>
   </head>
   <body>
     <p>TEST: Data posted to this form was:
     <%
       request.setCharacterEncoding("UTF-8");
       out.print(request.getParameter("mydata"));
     %>

     </p>
     <form method="POST" action="encoding_test.jsp">
       <input type="text" name="mydata">
       <input type="submit" value="Submit" />
       <input type="reset" value="Reset" />
     </form>
   </body>
</html>