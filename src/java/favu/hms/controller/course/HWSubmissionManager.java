/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package favu.hms.controller.course;

import favu.hms.database.RecordNotFoundException;
import favu.hms.model.course.HWSubmission;
import favu.hms.model.user.Instructor;
import favu.hms.model.user.Student;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;


public class HWSubmissionManager extends HttpServlet {

	protected void viewHWSubmission(HttpServletRequest request, HttpServletResponse response) 
	throws RecordNotFoundException, Exception {
		Integer studentId = (Integer)request.getSession().getAttribute("studentID");
		Integer instructorId = (Integer)request.getSession().getAttribute("instructorID");
		
		if (studentId!=null) {
			Student student = Student.studentWithId(studentId);
			Integer submissionId = Integer.parseInt(request.getParameter("submissionID"));
			HWSubmission submission = HWSubmission.homeworkSubmissionWithId(submissionId);
			
			if ( (submission.getSubmitterType() == HWSubmission.FROM_STUDENT)
							&& (submission.getSubmitterID() == student.getIdNum()) ) {
				request.setAttribute("submission", submission);
				RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/v_hwsubmission_student.jsp");
				rd.forward(request, response);
			} else {
				throw new Exception("You did not submit this submission, you can't view it.");
			}

		} else if (instructorId!=null) {
			Instructor instructor = Instructor.instructorWithId(instructorId);
			Integer submissionId = Integer.decode(request.getParameter("submissionID"));
			HWSubmission submission = HWSubmission.homeworkSubmissionWithId(submissionId);
			if (submission.getCourse().getCourseInstr() == instructor.getIdNum()) {
				request.setAttribute("submission", submission);
				RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/v_hwsubmission_instructor.jsp");
				rd.forward(request, response);
			} else {
				throw new Exception("You are not the instructor who assigned this HW, can can't view it.");
			}

		} else {
			throw new Exception("No user is logged in");
		}
	}

	protected void submitHWSubmission(HttpServletRequest request, HttpServletResponse response) 
	throws Exception {
		Integer studentId = (Integer)request.getSession().getAttribute("studentID");

		if (studentId==null) {
			throw new Exception("No user is logged in");
		}

		HWSubmission hws = new HWSubmission();
		hws.setSubmitterType(HWSubmission.FROM_STUDENT);
		hws.setSubmitterID(studentId);

		ServletFileUpload upload = new ServletFileUpload();
		try {
			FileItemIterator iter = upload.getItemIterator(request);

			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				String name = item.getFieldName();
				InputStream stream = item.openStream();

				if (item.isFormField()) {
					String fieldValue = Streams.asString(stream);
					if (name.equals("homeworkID")) {
						int id = Integer.parseInt(fieldValue);
						hws.setHomeworkID(id);
					} else if (name.equals("submissionMessage")) {
							hws.setMessage(fieldValue);
					}

				} else {
					String fileName = item.getName();
					hws.setFile(fileName);
					this.writeFileToDisk(fileName, stream);
				}
			}
		} catch (FileUploadException ex) {
				System.err.println(ex.getMessage());
		} catch (IOException ex) {
				System.err.println(ex.getMessage());
		}

		// update the database
		try {
				hws.record();
				request.setAttribute("submission", hws);
				RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/v_hwsubmission_instructor.jsp");
				rd.forward(request, response);
		} catch (Exception ex) {
				System.err.println(ex.getMessage());
		}

	}

	private void writeFileToDisk(String fileName, InputStream in) {
		String filePath = getServletContext().getRealPath("/uploads") + "/" + fileName;
		try {
			FileOutputStream out = new FileOutputStream(filePath);
			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException ex) {
				System.err.println(ex.getMessage());
		} catch (IOException ex) {
				System.err.println(ex.getMessage());
		}
  }

	protected void gradeHWSubmission(HttpServletRequest request, HttpServletResponse response) 
	throws Exception {
		Integer instructorId = (Integer)request.getSession().getAttribute("instructorID");

		if (instructorId==null) {
			throw new Exception("No instructor is logged in");
		}
		Instructor instructor = Instructor.instructorWithId(instructorId);
		Integer submissionId = Integer.decode(request.getParameter("submissionID"));
		HWSubmission submission = HWSubmission.homeworkSubmissionWithId(submissionId);

		if (submission.getCourse().getCourseInstr() != instructor.getIdNum()) {
			throw new Exception("You are not the instructor who assigned this HW, can can't view it.");
		}
		
		String grade = request.getParameter("grade");
		String gradeComment = request.getParameter("gradeComment");
		
		submission.setGrade(grade);
		submission.setGradeComment(gradeComment);
		submission.record();

		request.setAttribute("submission", submission);

		RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/v_hwsubmission_instructor.jsp");
		rd.forward(request, response);
	}
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
			try {
				String action = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/")+1);
				if (action.contentEquals("viewSubmission.cgi")) {
					this.viewHWSubmission(request, response);

				} else if (action.contentEquals("submitSubmission.cgi")) {
					this.submitHWSubmission(request, response);

				} else if (action.contentEquals("gradeSubmission.cgi")) {
					this.gradeHWSubmission(request, response);
					
				} else {
					// TODO redirect to default fail page
				}
			} catch (Exception e) {
				// TODO redirect to default fail page
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
