/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package favu.hms.model.user;

import favu.hms.database.DatabaseInterface;
import favu.hms.database.RecordNotFoundException;
import favu.hms.model.course.HWSubmission;
import favu.hms.model.course.Homework;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;


public class User {

	public static int USER_TYPE = 1;

    protected String name;
    protected int idNum = -1;
    protected String passwd = "";
		protected int userType;

    public User(String name, int idNum) {
        this.idNum = idNum;
        this.name = name;
				this.userType = USER_TYPE;
    }

    public int getIdNum() {
        return idNum;
    }

	public void setIdNum(int idNum) {
		this.idNum = idNum;
	}

    public String getName() {
        return name;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public Collection<HWSubmission> getSubmissions() {
		return DatabaseInterface.getInstance().retrieveSubmissionsForStudentWithID(idNum);
	}

	public HWSubmission getSubmissionForHomework(Homework hw) {
		Iterator i = this.getSubmissions().iterator();
		while (i.hasNext()) {
			HWSubmission sub = (HWSubmission)i.next();
			if (sub.getHomeworkID()==hw.getHomeworkID())
				return sub;
		}
		return null;
	}
		
	public User record() throws RecordNotFoundException, SQLException {
		return DatabaseInterface.getInstance().recordUser(this);
	}

	public static User userWithId(int id) throws RecordNotFoundException {
		return DatabaseInterface.getInstance().retrieveUserWithId(new Integer(id));
	}

	public static boolean userExists(int id, String password) {
		return DatabaseInterface.getInstance().userExists(id, password);
	}
}
