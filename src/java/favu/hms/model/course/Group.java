/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package favu.hms.model.course;

import favu.hms.database.DatabaseInterface;
import favu.hms.database.RecordNotFoundException;
import favu.hms.model.*;
import favu.hms.model.user.Student;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.Vector;


public class Group {

    private int groupID = -1;
    private String courseID;
    private Date creationDate;
    private String name;
    private String password;
    private String description;
    private Collection<Integer> membersIDs = new Vector();
    private int maximumMembersNumber;

    public String getCourseID() {
        return courseID;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getDescription() {
        return description;
    }

    public int getGroupID() {
        return groupID;
    }

    public int getMaximumMembersNumber() {
        return maximumMembersNumber;
    }

    public Collection<Integer> getMembersIDs() {
        return membersIDs;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public void setMaximumMembersNumber(int maximumMembersNumber) {
        this.maximumMembersNumber = maximumMembersNumber;
    }

    public void setMembersIDs(Collection<Integer> membersIDs) {
        this.membersIDs = membersIDs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /*to do*/

    public Collection getMembers(){
        Collection temp = this.membersIDs;
        return temp;
    }

    public void addMember(Student member){
       this.membersIDs.add(member.getIdNum());
    }

    public void removeMember(Student member){
       this.membersIDs.remove(member.getIdNum());
        
    }

    public Collection<HWSubmission> getHWSubmissions(){
        return DatabaseInterface.getInstance().retrieveSubmissionsForGroupWithID(this.groupID);
    }

    public void remove() throws SQLException, RecordNotFoundException{
        DatabaseInterface.getInstance().removeGroup(this);        
    }

    public static Group GroupWithID(int id) throws RecordNotFoundException{
      return DatabaseInterface.getInstance().retrieveGroupWithId(new Integer(id));
    }
    /*/to do*/
    
    public boolean isFull(){
        return (this.maximumMembersNumber >= this.membersIDs.size());
    }

    public boolean isPasswordCorrect(String password){
        return password.equals(this.password);
    }

    public Group(String courseID, String name, String description, int maximumMembersNumber) {
			this.courseID = courseID;
			this.name = name;
			this.description = description;
			this.maximumMembersNumber = maximumMembersNumber;
			this.creationDate = new Date();
			this.membersIDs = new Vector();
    }

	public Group record() throws RecordNotFoundException, SQLException {
		return DatabaseInterface.getInstance().recordGroup(this);
	}

	public static Group groupWithId(int id) throws RecordNotFoundException {
		return DatabaseInterface.getInstance().retrieveGroupWithId(new Integer(id));
	}
    
}
