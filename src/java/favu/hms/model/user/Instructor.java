package favu.hms.model.user;



import favu.hms.database.DatabaseInterface;
import favu.hms.model.course.Course;
import java.util.Collection;
import java.util.Vector;

public class Instructor extends User implements Teaching {

	public static int USER_TYPE = 10;

    private String personalInfo;
    
    public Instructor(String name, int idNum, String personalInfo, String passwd) {
        super(name, idNum);
        this.personalInfo = personalInfo;
        super.passwd=passwd;
				this.userType = this.USER_TYPE;
    }

    public String getPersonalInfo() {
        return personalInfo;
    }

    public Collection<Course> getProvidedCourses() {
      return DatabaseInterface.getInstance().retrieveCoursesForInstructorWithID(this.idNum);
    }

    @Override
	public Instructor record() {
		try {
				return DatabaseInterface.getInstance().recordInstructor(this);
		} catch (Exception rnfe) {
			rnfe.printStackTrace(System.err);
		}
		return this;
	}

	public static Instructor instructorWithId(int id) {
		try {
			return DatabaseInterface.getInstance().retrieveInstructorWithId(new Integer(id));
		} catch (Exception rnfe) {
			rnfe.printStackTrace(System.err);
		}
		return null;
	}

}
