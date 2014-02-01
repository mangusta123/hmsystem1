package favu.hms.model.user;

import favu.hms.database.DatabaseInterface;
import favu.hms.database.RecordNotFoundException;
import favu.hms.model.course.Course;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;


public class TeachingAssistant extends Student implements Teaching {

	public static int USER_TYPE = 30;

    protected String personalInfo;

    public TeachingAssistant(String name, int idNum, String personalInfo, String passwd){
        super(name, idNum, passwd);
        this.personalInfo = personalInfo;
				this.userType = this.USER_TYPE;
    }

    public Collection<Course> getAssistCourses() {
        return DatabaseInterface.getInstance().retrieveCoursesForTAWithID(this.idNum);
    }

    public String getPersonalInfo() {
        return personalInfo;
    }

    public void remAssistCourse(String courseID){

    }

    public void addAssistCourse(Course c) {
			c.addTA(this);
    }

	@Override
	public TeachingAssistant record() throws RecordNotFoundException, SQLException {
		return DatabaseInterface.getInstance().recordTeachingAssistant(this);
	}

	public static TeachingAssistant teachingAssistantWithId(int id) throws RecordNotFoundException {
		return DatabaseInterface.getInstance().retrieveTeachingAssistantWithId(new Integer(id));
	}

}
