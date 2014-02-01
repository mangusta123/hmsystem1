/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package favu.hms.database;

import favu.hms.model.course.*;
import favu.hms.model.user.*;
import java.sql.SQLException;
import java.util.Date;

public class DatabaseInitializer {

	public static void main(String args[]) {
		
		try {
			


			DatabaseInterface db = DatabaseInterface.getInstance();
			db.eraseDatabase();
			db.initializeDatabase();


			// creation of some instructors
			Instructor i = new Instructor("Kim", 19770001, "CS professor", "password");
			i.record();

			Instructor ii = new Instructor("Moon", 19660002, "Network expert", "passofmoon");
			ii.record();

			System.err.println("Instructor creation done.");


			// creation of TAs
			TeachingAssistant t = new TeachingAssistant("Shinae", 20059666, "I love networks", "motdepasse");
			t.record();

			TeachingAssistant tt = new TeachingAssistant("John", 20069326, "I give super grades.", "dontknow");
			tt.record();

			TeachingAssistant ttt = new TeachingAssistant("Robert", 20010932, "I do great coffee.", "writeaspass");
			ttt.record();

			TeachingAssistant tttt = new TeachingAssistant("William", 19991224, "Master of PlanetLab", "whatto");
			tttt.record();

			System.err.println("TAs creation done.");


			// creation of some courses for the professors
			Course c = new Course("Computer Science", "Software Engineering", "CS350", 24, true, "A nice course about money", i.getIdNum());
			c.record();

			Course cc = new Course("Industrial Design", "Software for I.D.", "ID408", 45, true, "Make computers handsome", i.getIdNum());
			cc.record();

			Course ccc = new Course("Computer Science", "Network Architecture", "CS540", 12, true, "Read papers. Now.", ii.getIdNum());
			ccc.record();

			System.err.println("Courses creation done.");


			// we enroll the TAs for courses
			c.addTA(tt);
			c.addTA(ttt);
			c.addTA(tttt);
			c.record();

			cc.addTA(ttt);
			cc.record();

			ccc.addTA(t);
			ccc.addTA(tttt);
			ccc.record();

			System.err.println("TA enrollement done.");


			// creation of some students
			Student s = new Student("PierreElie", 20096002, "azerty");
			s.record();

			Student ss = new Student("Trung", 20074309, "uiop");
			ss.record();

			Student sss = new Student("Benjamin", 20085423, "qsdfgh");
			sss.record();

			Student ssss = new Student("Fariz", 20064658, "jklm");
			ssss.record();

			Student sssss = new Student("Jonathan", 20096001, "wxcvbn");
			sssss.record();

			System.err.println("Students creation done.");

			// lets enroll them to courses
			c.addStudent(s);
			c.addStudent(ss);
			c.addStudent(sss);
			c.addStudent(ssss);
			c.addStudent(sssss);
			c.record();

			cc.addStudent(s);
			cc.record();

			ccc.addStudent(s);
			ccc.addStudent(sss);
			ccc.addStudent(sssss);
			ccc.record();

			System.err.println("Students enrollement done.");


			// creation of homeworks
			Homework h = new Homework(-1, ccc.getCourseID(), "PlanetLab", new Date(), new Date(), "Youpi, a project of network", "grading policy 1");
			h.setInstructorID(ii.getIdNum());
			h.addTeachingAssistant(tttt);
			h.record();

			Homework hh = new Homework(-1, c.getCourseID(), "SE Project", new Date(), new Date(), "HMS or whatever you want", "grading policy 2");
			hh.setInstructorID(i.getIdNum());
			hh.record();

			System.err.println("HW creation done.");


			// creation of a group
			Group g = new Group(c.getCourseID(), "FAVU", "favunited to make HMs", 5);
			g.record();

			g.addMember(s);
			g.addMember(ss);
			g.addMember(sss);
			g.addMember(ssss);
			g.addMember(sssss);
			g.record();

			System.err.println("Group creation done.");


			// creation of homework submissions
			HWSubmission hs = new HWSubmission(h.getHomeworkID(), s.getIdNum(), HWSubmission.FROM_STUDENT, "this is my homework", "/path/to/file");
			hs = hs.record();

			HWSubmission hshs = new HWSubmission(hh.getHomeworkID(), g.getGroupID(), HWSubmission.FROM_GROUP, "this is our project", "/another/path/to/file");
			hshs.record();

			System.err.println("HWSubmission creation done.");
			

			// creation of messages
			Message m = new Message(h.getHomeworkID(), sssss.getIdNum(), "I haven't begun yet, is it a problem?");
			m.record();

			Message mm = new Message(h.getHomeworkID(), t.getIdNum(), "I don't know, I wll ask Moon");
			mm.record();

			Message mmm = new Message(h.getHomeworkID(), ii.getIdNum(), "Of course it is! HURRY UP!");
			mmm.record();

			System.err.println("Messages creation done.");


			

			System.err.println(i.getProvidedCourses());


			c.getTaList();

			s.getCoursetakenCourses();

            //testModelClass();
			

		} catch (RecordNotFoundException ex) {
			ex.printStackTrace(System.err);
		} catch (SQLException ex) {
			ex.printStackTrace(System.err);
		}

	}

    public static void testModelClass() throws RecordNotFoundException, SQLException{
        System.out.println("entering test 2");
        DatabaseInterface db = DatabaseInterface.getInstance();
        //remove Jonathan from the database
        System.out.println("looking for Jon");
        System.out.println(db.retrieveStudentWithId(20096001).getName());
        System.out.println("test 2 finished");
    }


}
