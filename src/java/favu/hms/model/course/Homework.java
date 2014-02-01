/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package favu.hms.model.course;

import favu.hms.database.DatabaseInterface;
import favu.hms.database.RecordNotFoundException;
import java.util.Collection;
import java.util.Date;
import favu.hms.model.user.TeachingAssistant;
import java.sql.SQLException;
import java.util.Vector;


public class Homework {

    private int homeworkID = -1;
    private String courseID;
    private String name;
    private Date assignedDate;
    private Date dueDate;
    private String description;
    private Collection<String> files = new Vector();
    private int instructorID;
    private Collection<Integer> teachingAssistantIDs = new Vector();
    private boolean updatable;
    private String gradingPolicy;

    /**
     * default constructor
     */
    public Homework() {
        
    }
    
    public Homework(int homeworkID, String courseID, String name, Date assignedDate, Date dueDate, String description, String gradingPolicy) {
      this.homeworkID = homeworkID;
			this.courseID = courseID;
        this.name = name;
        this.assignedDate = assignedDate;
        this.dueDate = dueDate;
        this.description = description;
        this.gradingPolicy = gradingPolicy;
    }

    public Date getAssignedDate() {
        return assignedDate;
    }

    public String getCourseID() {
        return courseID;
    }

    public String getDescription() {
        return description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public String getGradingPolicy() {
        return gradingPolicy;
    }

    public int getHomeworkID() {
        return homeworkID;
    }

    public String getName() {
        return name;
    }

    public Collection<Integer> getTeachingAssistantIDs() {
        return teachingAssistantIDs;
    }

    public boolean isUpdatable() {
        return updatable;
    }

    public Collection<String> getFiles() {
        return files;
    }

    public int getInstructorID() {
        return instructorID;
    }

	public void setHomeworkID(int homeworkID) {
		this.homeworkID = homeworkID;
	}

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addFile(String filePath){
        files.add(filePath);
    }

    public void setFiles(Collection<String> files) {
        this.files = files;
    }

    public void setGradingPolicy(String gradingPolicy) {
        this.gradingPolicy = gradingPolicy;
    }

    public void setInstructorID(int instructorID) {
        this.instructorID = instructorID;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

    public void addTeachingAssistant(TeachingAssistant ta){
			this.teachingAssistantIDs.add(ta.getIdNum());
    }

    public void remTeachingAssistant(TeachingAssistant ta){

    }

    public Collection<HWSubmission> getSubmissions(){
			return DatabaseInterface.getInstance().retrieveSubmissionsForHomeworkWithID(homeworkID);
    }

    public Collection<Message> getMessages(){
			return DatabaseInterface.getInstance().retrieveMessagesForHomeworkWithID(homeworkID);
    }

    public void remove(){

    }

    public void update(){

    }

    public Homework homeworkWithID(int id){
			return null;
    }

	public Homework record() throws RecordNotFoundException, SQLException {
		return DatabaseInterface.getInstance().recordHomework(this);
	}

	public static Homework homeworkWithId(int id) throws RecordNotFoundException {
		return DatabaseInterface.getInstance().retrieveHomeworkWithId(new Integer(id));
	}

    /**
     * @param courseID the courseID to set
     */
    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    /**
     * @param assignedDate the assignedDate to set
     */
    public void setAssignedDate(Date assignedDate) {
        this.assignedDate = assignedDate;
    }

    /**
     * @param teachingAssistantIDs the teachingAssistantIDs to set
     */
    public void setTeachingAssistantIDs(Collection<Integer> teachingAssistantIDs) {
        this.teachingAssistantIDs = teachingAssistantIDs;
    }
    
}
