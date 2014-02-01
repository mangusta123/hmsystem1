package favu.hms.model.user;



import favu.hms.database.DatabaseInterface;
import favu.hms.database.RecordNotFoundException;
import favu.hms.model.course.Course;
import favu.hms.model.course.Group;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

public class Student extends User {

	public static int USER_TYPE = 20;

       public Collection<Course> getCoursetakenCourses() 
		throws SQLException, RecordNotFoundException {
        return DatabaseInterface.getInstance().retrieveCoursesForStudentWithID(this.idNum);
    }

    public Student(String name, int idNum, String passwd) {
        super(name, idNum);
        super.passwd=passwd;
				this.userType = this.USER_TYPE;
		}


    public Collection<Group> getMGroups() 
		throws SQLException, RecordNotFoundException {
        return DatabaseInterface.getInstance().retrieveGroupsForStudentWithID(this.getIdNum());
    }

    public void addCourse(Course c) throws RecordNotFoundException, SQLException{
        c.addStudent(this);
        c.record();
    }

    public void remCourse(Course c) throws RecordNotFoundException, SQLException{
        c.remStudent(this);
        c.record();
    }

    public void joinGroup(Group grp) throws RecordNotFoundException, SQLException{
        grp.addMember(this);
        grp.record();
    }
    
    public void leaveGroup(Group grp) throws RecordNotFoundException, SQLException{
        grp.removeMember(this);
        grp.record();
    }

	@Override
	public Student record() throws RecordNotFoundException, SQLException, RecordNotFoundException, SQLException {
		return DatabaseInterface.getInstance().recordStudent(this);
	}

	public static Student studentWithId(int id) throws RecordNotFoundException {
		return DatabaseInterface.getInstance().retrieveStudentWithId(new Integer(id));
	}
}
