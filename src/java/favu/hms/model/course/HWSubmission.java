/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package favu.hms.model.course;

import favu.hms.database.DatabaseInterface;
import favu.hms.database.RecordNotFoundException;
import java.sql.SQLException;
import java.util.Date;


public class HWSubmission {

    private int HWSubmissionID = -1;
    private int homeworkID;
    private int submitterID;
    private int submitterType;
    private Date submissionDate;
    private Date updateDate = null;
    private String message;
    private String file;
    private String grade;
    private String gradeComment;

    public int getHWSubmissionID() {
        return HWSubmissionID;
    }

    public String getFile() {
        return file;
    }

    public String getGrade() {
      if (this.grade==null)
				return "";
			return this.grade;
    }

    public String getGradeComment() {
      if (this.gradeComment==null)
				return "";
			return this.gradeComment;
    }

    public int getHomeworkID() {
        return homeworkID;
    }

    public String getMessage() {
        return message;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public int getSubmitterID() {
        return submitterID;
    }

    public int getSubmitterType() {
        return submitterType;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setHWSubmissionID(int HWSubmissionID) {
        this.HWSubmissionID = HWSubmissionID;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setGradeComment(String gradeComment) {
        this.gradeComment = gradeComment;
    }

    public void setHomeworkID(int homeworkID) {
        this.homeworkID = homeworkID;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public void setSubmitterID(int submitterID) {
        this.submitterID = submitterID;
    }

    public void setSubmitterType(int submitterType) {
        this.submitterType = submitterType;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public HWSubmission(int homeworkID, int submitterID, int submitterType, String message, String file) {
        this.homeworkID = homeworkID;
        this.submitterID = submitterID;
        this.submitterType = submitterType;
        this.message = message;
        this.file = file;
				this.submissionDate = new Date();
    }

		public HWSubmission() {
			this.submissionDate = new Date();
		}

    public boolean hasBeenUpdated(){
        return !this.submissionDate.equals(this.updateDate);
    }

    public Homework getHomework() throws RecordNotFoundException{
      return Homework.homeworkWithId(this.homeworkID);
    }

    public Course getCourse() throws RecordNotFoundException{
      return Course.courseWithId(this.getHomework().getCourseID());
    }

    public void remove() throws SQLException, RecordNotFoundException{
        DatabaseInterface.getInstance().removeHomeworkSubmission(this);
    }

	public HWSubmission record() throws RecordNotFoundException, SQLException {
		return DatabaseInterface.getInstance().recordHomeworkSubmission(this);
	}

	public static HWSubmission homeworkSubmissionWithId(int id) throws RecordNotFoundException {
		return DatabaseInterface.getInstance().retrieveHomeworkSubmissionWithId(new Integer(id));
	}

    public static int FROM_STUDENT = 1 ;
    public static int FROM_GROUP = 2;

}
