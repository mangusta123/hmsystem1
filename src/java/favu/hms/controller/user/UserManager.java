/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package favu.hms.controller.user;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import favu.hms.model.user.*;
import favu.hms.database.*;
import javax.servlet.RequestDispatcher;


public class UserManager extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int usern = Integer.parseInt(request.getParameter("userID"));
        String passw = request.getParameter("password");
        User usr;
        int i = 0, j = 0;
        boolean b = User.userExists(usern, passw);

				//forget the users who used the system before on the same computer (auto-log off)
				request.getSession().removeAttribute("instructorID");
				request.getSession().removeAttribute("studentID");

        if (b) {
            try {
                usr = User.userWithId(usern);
                i = usr.getUserType();
                j = usr.getIdNum();

                if (i == Instructor.USER_TYPE) {
                    request.getSession().setAttribute("instructorID", j);
                    request.setAttribute("instructorID", j);
                    RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/v_instructor.jsp");
                    rd.forward(request, response);
                } else if (i == Student.USER_TYPE || i == TeachingAssistant.USER_TYPE) {
                    request.getSession().setAttribute("studentID", j);
                    request.setAttribute("studentID", j);
                    RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/v_student.jsp");
                    rd.forward(request, response);
                }

            } catch (RecordNotFoundException ex) {
                System.err.println(ex.getMessage());
            }
        } else {
            response.sendRedirect("index.jsp");
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
