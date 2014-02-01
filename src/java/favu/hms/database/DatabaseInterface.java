package favu.hms.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.util.Map;
import java.util.Hashtable;
import java.util.Set;
import java.util.Collection;
import java.util.Vector;
import java.util.Iterator;
import java.util.Properties;
import java.util.Date;

import favu.hms.model.user.*;
import favu.hms.model.course.*;


public class DatabaseInterface {

	private String framework = "derbyclient";
	private String driver = "org.apache.derby.jdbc.ClientDriver";
	private String protocol = "jdbc:derby://localhost:1527/";
	private String dbName = "hmsDB"; // the name of the database

	private Connection conn = null;
	private Properties props;
	private Map<Statement,Date> statements;

	private Map<String,DatabaseTable> tables;

	private static DatabaseInterface instance = new DatabaseInterface();

	private DatabaseInterface() {
		this.tables = DatabaseStructure.getTables();
		this.statements = new Hashtable();

		loadDriver();

		this.props = new Properties(); // connection properties
		this.props.put("user", "favu");
		this.props.put("password", "nited");
		//this.props.put("derby.system.home", "/Users/scallion/insa/KAIST/");

		this.openConnection();
	}

	public static DatabaseInterface getInstance() {
		return instance;
	}

	private Statement getStatement() throws SQLException {
		// closes old statements
		Statement[] states = this.statements.keySet().toArray(new Statement[0]);
		for (Statement st: states) {
			Date d = this.statements.get(st);
			if ( (new Date().getTime() - d.getTime()) > 5*1000 ) {
				st.close();
				this.statements.remove(st);
			}
		}
		
		// create new statement and return it
		Statement s = this.conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		this.statements.put(s, new Date());

		return s;
	}

	public boolean userExists(int id, String password) {
		try {
			ResultSet rs = null;
			rs = this.getStatement().executeQuery("SELECT * FROM Users WHERE idNum=" + id + " AND password='" + password + "'");
			if (rs.next()) {
				rs.close();
				return true;
			}
		} catch (SQLException ex) {
			return false;
		}
		return false;
	}

	public User retrieveUserWithId(Integer id)
	throws RecordNotFoundException {
		User u = null;
		Map<String, Object> fields;

		try {
			fields = this.retrieveEntityFieldsWithId("Users", id);
			u = new User(	(String)fields.get("name"),
										(Integer)fields.get("idNum"));
			u.setPasswd((String)fields.get("password"));
			u.setUserType((Integer)fields.get("type"));

		} catch (SQLException sqle) {
      this.printSQLException(sqle);
		}

		return u;
	}

	public Student retrieveStudentWithId(Integer id)
	throws RecordNotFoundException {
		Student u = null;
		Map<String, Object> fields;

		try {
			fields = this.retrieveEntityFieldsWithId("Users", id);
			if ( ((Integer)fields.get("type")) != Student.USER_TYPE ) {
				throw new RecordNotFoundException();
			}
			u = new Student(	(String)fields.get("name"),
												(Integer)fields.get("idNum"),
												(String)fields.get("password"));
			u.setUserType(Student.USER_TYPE);

		} catch (SQLException sqle) {
      this.printSQLException(sqle);
		}

		return u;
	}

	public Instructor retrieveInstructorWithId(Integer id)
	throws RecordNotFoundException {
		Instructor u = null;
		Map<String, Object> fieldsUser;
		Map<String, Object> fieldsTeacher;

		try {
			fieldsUser = this.retrieveEntityFieldsWithId("Users", id);
			fieldsTeacher = this.retrieveEntityFieldsWithId("Teacher", id);

			if ( ((Integer)fieldsUser.get("type")) != Instructor.USER_TYPE ) {
				throw new RecordNotFoundException();
			}

			// courses of this instructor
			DatabaseTable courseTable = this.tables.get("Course");
			Collection<Course> courses = new Vector();
			ResultSet rs = null;
			rs = this.getStatement().executeQuery(courseTable.getSelectFieldsWithConstraintSQL("*", "instructorId", id));
			Collection<String> courseIds = new Vector();
			while (rs.next()) {
				courseIds.add((String)this.getJavaFromSQL(courseTable.getColumns().get("courseId"), rs));
			}
			rs.close();
			Iterator ite = courseIds.iterator();
			while (ite.hasNext()) {
				courses.add(this.retrieveCourseWithId((String)ite.next()));
			}
			Course[] coursesArray = courses.toArray(new Course[0]);

			u = new Instructor(	(String)fieldsUser.get("name"),
													(Integer)fieldsUser.get("idNum"),
													(String)fieldsTeacher.get("personalInfo"),
													(String)fieldsUser.get("password"));
			u.setUserType(Instructor.USER_TYPE);
			rs.close();

		} catch (SQLException sqle) {
      this.printSQLException(sqle);
		}

		return u;
	}

	public TeachingAssistant retrieveTeachingAssistantWithId(Integer id)
	throws RecordNotFoundException {
		TeachingAssistant u = null;
		Map<String, Object> fieldsUser;
		Map<String, Object> fieldsTeacher;

		try {
			fieldsUser = this.retrieveEntityFieldsWithId("Users", id);
			fieldsTeacher = this.retrieveEntityFieldsWithId("Teacher", id);

			if ( ((Integer)fieldsUser.get("type")) != TeachingAssistant.USER_TYPE ) {
				throw new RecordNotFoundException();
			}

			u = new TeachingAssistant((String)fieldsUser.get("name"),
																(Integer)fieldsUser.get("idNum"),
																(String)fieldsTeacher.get("personalInfo"),
																(String)fieldsUser.get("password"));
			u.setUserType(TeachingAssistant.USER_TYPE);
		} catch (SQLException sqle) {
      this.printSQLException(sqle);
		}

		return u;
	}

	public Course retrieveCourseWithId(String id)
	throws RecordNotFoundException {
		//System.err.println("retrieveCourseWithId method: IN");

		Course c = null;
		Map<String, Object> fields;

		try {
			fields = this.retrieveEntityFieldsWithId("Course", id);

			c = new Course(	(String)fields.get("courseDept"),
											(String)fields.get("courseName"),
											(String)fields.get("courseId"),
											(Integer)fields.get("limitUp"),
											(Boolean)fields.get("available"),
											(String)fields.get("courseInfo"),
											(Integer)fields.get("instructorId"));

		} catch (SQLException sqle) {
      this.printSQLException(sqle);
		}

		return c;
	}

	public Collection<TeachingAssistant> retrieveTAsForCourseWithId(String courseId) {
		Collection<TeachingAssistant> tas = new Vector();
		try {
			ResultSet rs = null;
			rs = this.getStatement().executeQuery(this.tables.get("TeachingAssistant_Courses").getSelectFieldsWithConstraintSQL("taId", "courseId", courseId));
			while (rs.next()) {
				tas.add(this.retrieveTeachingAssistantWithId(rs.getInt("taId")));
			}
			
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return tas;
	}

	public Collection<Student> retrieveStudentsForCourseWithId(String courseId) {
		Collection<Student> students = new Vector();
		try {
			ResultSet rs = null;
			rs = this.getStatement().executeQuery(this.tables.get("Course_Students").getSelectFieldsWithConstraintSQL("studentId", "courseId", courseId));
			while (rs.next()) {
				students.add(this.retrieveStudentWithId(rs.getInt("studentId")));
			}

		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return students;
	}

	public Homework retrieveHomeworkWithId(Integer id)
	throws RecordNotFoundException {
		Homework hw = null;
		Map<String, Object> fields;

		try {
			fields = this.retrieveEntityFieldsWithId("Homework", id);
			
			Collection files = new Vector();
			ResultSet rs = null;
			rs = this.getStatement().executeQuery(this.tables.get("Homework_Files").getSelectFieldsWithConstraintSQL("filePath", "homeworkId", id));
			while (rs.next()) {
				files.add(new String(rs.getString("filePath")));
			}

			hw = new Homework((Integer)fields.get("homeworkId"),
												(String)fields.get("courseId"),
												(String)fields.get("name"),
												(Date)fields.get("assignedDate"),
												(Date)fields.get("dueDate"),
												(String)fields.get("description"),
												(String)fields.get("gradingPolicy"));
			hw.setInstructorID((Integer)fields.get("instructorId"));
			hw.setUpdatable((Boolean)fields.get("updatable"));
			hw.setFiles(files);

			// TAs for this Homework
			Collection tas = new Vector();
			rs = null;
			rs = this.getStatement().executeQuery(this.tables.get("Homework_TAs").getSelectFieldsWithConstraintSQL("taId", "homeworkId", id));
			while (rs.next()) {
				hw.addTeachingAssistant(this.retrieveTeachingAssistantWithId(rs.getInt("taId")));
			}
			
			rs.close();

		} catch (SQLException sqle) {
      this.printSQLException(sqle);
		}

		return hw;
	}

	public HWSubmission retrieveHomeworkSubmissionWithId(Integer id)
	throws RecordNotFoundException {
		HWSubmission hws = null;
		Map<String, Object> fields;

		try {
			fields = this.retrieveEntityFieldsWithId("HomeworkSubmission", id);

			hws = new HWSubmission(	(Integer)fields.get("homeworkId"),
															(Integer)fields.get("submitterId"),
															(Integer)fields.get("submitterType"),
															(String)fields.get("message"),
															(String)fields.get("file"));
			hws.setGrade((String)fields.get("grade"));
			hws.setGradeComment((String)fields.get("gradeComment"));
			hws.setHWSubmissionID((Integer)fields.get("homeworkSubmissionId"));
			hws.setSubmissionDate((Date)fields.get("submissionDate"));
			hws.setUpdateDate((Date)fields.get("updateDate"));

		} catch (SQLException sqle) {
      this.printSQLException(sqle);
		}

		return hws;
	}

	public Group retrieveGroupWithId(Integer id)
	throws RecordNotFoundException {
		Group g = null;
		Map<String, Object> fields;

		try {
			fields = this.retrieveEntityFieldsWithId("Groups", id);

			g = new Group((String)fields.get("courseId"),
										(String)fields.get("name"),
										(String)fields.get("description"),
										(Integer)fields.get("maximumMembers"));
			g.setGroupID((Integer)fields.get("groupId"));
			g.setCreationDate((Timestamp)fields.get("creationDate"));
			// members of the group
			ResultSet rs = null;
			rs = this.getStatement().executeQuery(this.tables.get("Group_Members").getSelectFieldsWithConstraintSQL("memberId", "groupId", id));
			while (rs.next()) {
				g.addMember(this.retrieveStudentWithId(rs.getInt("memberId")));
			}
			rs.close();

		} catch (SQLException sqle) {
      this.printSQLException(sqle);
		}

		return g;
	}

	public Message retrieveMessageWithId(Integer id)
	throws RecordNotFoundException {
		Message m = null;
		Map<String, Object> fields;

		try {
			fields = this.retrieveEntityFieldsWithId("Message", id);

			m = new Message((Integer)fields.get("homeworkId"),
											(Integer)fields.get("senderId"),
											(String)fields.get("content"));
			m.setMessageID((Integer)fields.get("messageId"));
			m.setCreationDate((Date)fields.get("creationDate"));

		} catch (SQLException sqle) {
      this.printSQLException(sqle);
		}

		return m;
	}

	private Map<String, Object> retrieveEntityFieldsWithFieldValue(String tableName, String field, Object value)
	throws RecordNotFoundException, SQLException {

		DatabaseTable table = this.tables.get(tableName);
		Map<String, Object> fields = new Hashtable();

		ResultSet rs = null;
		rs = this.getStatement().executeQuery(table.getSelectFieldsWithConstraintSQL("*", field, value));

		if (!rs.next()) {
			throw new RecordNotFoundException("No "+tableName+" found in DB with "+field+"="+value);
		}

		Collection columns = table.getColumns().values();
		Iterator i = columns.iterator();
		while (i.hasNext()) {
			DatabaseColumn c = (DatabaseColumn)i.next();
			if (this.getJavaFromSQL(c, rs)!=null)
				fields.put(c.getName(), this.getJavaFromSQL(c, rs));
		}

		rs.close();

		return fields;
	}

	private Map<String, Object> retrieveEntityFieldsWithId(String tableName, Object id)
	throws RecordNotFoundException, SQLException {
		return this.retrieveEntityFieldsWithFieldValue(tableName, this.tables.get(tableName).getIdColumn(), id);
	}

	public User recordUser(User o)
	throws RecordNotFoundException, SQLException {
		//System.err.println("recordUser method: IN");

		Map<String, Object> fields = new Hashtable(4);
		fields.put("idNum", new Integer(o.getIdNum()));
		fields.put("name", o.getName());
		fields.put("password", o.getPasswd());
		fields.put("type", o.getUserType());

		DatabaseTable table = this.tables.get("Users");
		ResultSet rs = null;
		rs = this.getStatement().executeQuery(table.getSelectFieldsWithIdSQL("*", fields.get(table.getIdColumn())));
		if (!rs.next()) {
			this.insertEntityWithResultSet("Users", fields, rs);
		} else {
			this.updateEntityWithResultSet("Users", fields, rs);
		}
		
		rs.close();
		this.conn.commit();
		return o;
	}

	private Teaching recordTeacher(Teaching o)
	throws RecordNotFoundException, SQLException {
		//System.err.println("recordTeacher method: IN");

		Map<String, Object> fields = new Hashtable(3);
		fields.put("idNum", new Integer(o.getIdNum()));
		fields.put("personalInfo", o.getPersonalInfo());
		//this.updateEntity("Teacher", fields);

		DatabaseTable table = this.tables.get("Teacher");
		ResultSet rs = null;
		rs = this.getStatement().executeQuery(table.getSelectFieldsWithIdSQL("*", fields.get(table.getIdColumn())));
		if (!rs.next()) {
			this.insertEntityWithResultSet("Teacher", fields, rs);
		} else {
			this.updateEntityWithResultSet("Teacher", fields, rs);
		}

		rs.close();
		this.conn.commit();
		return o;
	}

	public Student recordStudent(Student o)
	throws RecordNotFoundException, SQLException {
		this.recordUser(o);

		this.conn.commit();
		return o;
	}

	public Instructor recordInstructor(Instructor o)
	throws RecordNotFoundException, SQLException {
		this.recordUser(o);
		this.recordTeacher(o);

		this.conn.commit();
		return o;
	}

	public TeachingAssistant recordTeachingAssistant(TeachingAssistant o)
	throws RecordNotFoundException, SQLException {
		this.recordStudent(o);
		this.recordTeacher(o);

		this.conn.commit();
		return o;
	}

	public Course recordCourse(Course o)
	throws RecordNotFoundException, SQLException {
		Map<String, Object> fields = new Hashtable(8);
		fields.put("courseId",	o.getCourseID());
		fields.put("courseDept",	o.getCourseDept());
		fields.put("courseName",	o.getCourseName());
		fields.put("limitUp",			new Integer(o.getLimitUp()));
		fields.put("available",		new Boolean(o.isAvailable()));
		fields.put("courseInfo",	o.getCourseInfo());
		fields.put("instructorId",new Integer(o.getCourseInstr()));
		fields.put("numOfStud",		new Integer(o.getNumOfStud()));

		DatabaseTable table = this.tables.get("Course");
		ResultSet rs = null;
		//System.err.println(table.getSelectFieldsWithIdSQL("*", fields.get(table.getIdColumn())));
		rs = this.getStatement().executeQuery(table.getSelectFieldsWithIdSQL("*", fields.get(table.getIdColumn())));
		if (!rs.next()) {
			this.insertEntityWithResultSet("Course", fields, rs);
		} else {
			this.updateEntityWithResultSet("Course", fields, rs);
		}

		//TODO record groups of a course

		//record TAs of a course
		TeachingAssistant[] newTAs = o.getTaList().toArray(new TeachingAssistant[0]);
		Integer[] newTAIds = null;
		if (newTAs!=null) {
			newTAIds = new Integer[newTAs.length];
			for (int i=0; i<newTAs.length; i++) {
				newTAIds[i] = new Integer(newTAs[i].getIdNum());
			}
		}
		table = this.tables.get("TeachingAssistant_Courses");
		this.updateAssociativeTable(table, table.getColumns().get("courseId"), o.getCourseID(), table.getColumns().get("taId"), newTAIds);

		//record students of a course
		Student[] newStudents = o.getStudentList().toArray(new Student[0]);
		Integer[] newStudentIds = null;
		if (newTAs!=null) {
			newStudentIds = new Integer[newStudents.length];
			for (int i=0; i<newStudents.length; i++) {
				newStudentIds[i] = new Integer(newStudents[i].getIdNum());
			}
		}
		table = this.tables.get("Course_Students");
		this.updateAssociativeTable(table, table.getColumns().get("courseId"), o.getCourseID(), table.getColumns().get("studentId"), newStudentIds);

		rs.close();
		this.conn.commit();
		return o;
	}

	public Homework recordHomework(Homework o)
	throws RecordNotFoundException, SQLException {
		Map<String, Object> fields = new Hashtable(9);
		fields.put("courseId",			o.getCourseID());
		fields.put("name",					o.getName());
		fields.put("assignedDate",	new Timestamp(o.getAssignedDate().getTime()));
		fields.put("dueDate",				new Timestamp(o.getDueDate().getTime()));
		fields.put("description",		o.getDescription());
		fields.put("instructorId",	new Integer(o.getInstructorID()));
		fields.put("updatable",			new Boolean(o.isUpdatable()));
		fields.put("gradingPolicy",	o.getGradingPolicy());

		if (o.getHomeworkID()>-1) {
			// update
			fields.put("homeworkId",		new Integer(o.getHomeworkID()));
			this.updateEntity("Homework", fields);

		} else {
			// creation
			Integer newId = (Integer)this.insertEntity("Homework", fields);
			o.setHomeworkID(newId.intValue());
		}

		// files of a HW
		String[] newFiles = o.getFiles().toArray(new String[0]);
		DatabaseTable table = this.tables.get("Homework_Files");
		this.updateAssociativeTable(table, table.getColumns().get("homeworkId"), new Integer(o.getHomeworkID()), table.getColumns().get("filePath"), newFiles);

		// TAs assigned to this HW
		Integer[] newTAs = o.getTeachingAssistantIDs().toArray(new Integer[0]);
		table = this.tables.get("Homework_TAs");
		this.updateAssociativeTable(table, table.getColumns().get("homeworkId"), new Integer(o.getHomeworkID()), table.getColumns().get("taId"), newTAs);

		this.conn.commit();
		return o;
	}

	public HWSubmission recordHomeworkSubmission(HWSubmission o)
	throws RecordNotFoundException, SQLException {

		Map<String, Object> fields = new Hashtable();
		fields.put("homeworkId",							new Integer(o.getHomeworkID()));
		fields.put("submitterId",							new Integer(o.getSubmitterID()));
		fields.put("submitterType",						new Integer(o.getSubmitterType()));
		fields.put("submissionDate",					new Timestamp(o.getSubmissionDate().getTime()));
		if (o.getUpdateDate()!=null)
			fields.put("updateDate",						new Timestamp(o.getUpdateDate().getTime()));
		fields.put("message",									o.getMessage());
		fields.put("file",										o.getFile());
		if (o.getGrade()!=null)
			fields.put("grade",										o.getGrade());
		if (o.getGradeComment()!=null)
			fields.put("gradeComment",						o.getGradeComment());

		if (o.getHWSubmissionID()>-1) {
			// update
			fields.put("homeworkSubmissionId",		new Integer(o.getHWSubmissionID()));
			this.updateEntity("HomeworkSubmission", fields);

		} else {
			// creation
			Integer newId = (Integer)this.insertEntity("HomeworkSubmission", fields);
			o.setHWSubmissionID(newId.intValue());
		}

		this.conn.commit();
		return o;
	}

	public Group recordGroup(Group o)
	throws RecordNotFoundException, SQLException {
		Map<String, Object> fields = new Hashtable();
		fields.put("courseId",			o.getCourseID());
		fields.put("name",					o.getName());
		fields.put("creationDate",	new Timestamp(o.getCreationDate().getTime()));
		fields.put("description",		o.getDescription());
		if (o.getPassword()!=null)
			fields.put("password",			o.getPassword());
		fields.put("maximumMembers",new Integer(o.getMaximumMembersNumber()));

		if (o.getGroupID()>-1) {
			// update
			fields.put("groupId", new Integer(o.getGroupID()));
			this.updateEntity("Groups", fields);

		} else {
			// creation
			Integer newId = (Integer)this.insertEntity("Groups", fields);
			o.setGroupID(newId.intValue());
		}

		// members of this Group
		Integer[] newMembers = o.getMembersIDs().toArray(new Integer[0]);
		DatabaseTable table = this.tables.get("Group_Members");
		this.updateAssociativeTable(table, table.getColumns().get("groupId"), new Integer(o.getGroupID()), table.getColumns().get("memberId"), newMembers);

		this.conn.commit();
		return o;
	}

	public Message recordMessage(Message o)
	throws RecordNotFoundException, SQLException {
		Map<String, Object> fields = new Hashtable();
		fields.put("homeworkId",			new Integer(o.getHomeworkID()));
		fields.put("creationDate",	new Timestamp(o.getCreationDate().getTime()));
		fields.put("content",				o.getContent());
		fields.put("senderId",			new Integer(o.getSendID()));

		if (o.getMessageID()>-1) {
			// update
			fields.put("messageId",				new Integer(o.getMessageID()));
			this.updateEntity("Message", fields);

		} else {
			// creation
			Integer newId = (Integer)this.insertEntity("Message", fields);
			o.setMessageID(newId.intValue());
		}

		this.conn.commit();
		return o;
	}

	private void updateEntity(String tableName, Map<String, Object> fields)
	throws RecordNotFoundException, SQLException {
		//System.err.println("updateEntity method: IN");

		DatabaseTable table = this.tables.get(tableName);
		ResultSet rs = null;
		rs = this.getStatement().executeQuery(table.getSelectFieldsWithIdSQL("*", fields.get(table.getIdColumn())));
		if (rs.next()) {
			this.updateEntityWithResultSet(tableName, fields, rs);
		}
			
		rs.close();
	}

	private Object insertEntity(String tableName, Map<String, Object> fields)
	throws RecordNotFoundException, SQLException {
		//System.err.println("insertEntity method: IN");

		DatabaseTable table = this.tables.get(tableName);

		Integer newId = table.insertFields(this.conn, fields);

		return newId;
	}

	private void updateEntityWithResultSet(String tableName, Map<String, Object> fields, ResultSet rs)
	throws RecordNotFoundException, SQLException {
		//System.err.println("updateEntityWithResultSet method: IN");

		this.changeEntityWithResultSet(tableName, fields, rs);
		rs.updateRow();
	}

	private void insertEntityWithResultSet(String tableName, Map<String, Object> fields, ResultSet rs)
	throws RecordNotFoundException, SQLException {
		//System.err.println("insertEntityWithResultSet method: IN");

		rs.moveToInsertRow();
		this.changeEntityWithResultSet(tableName, fields, rs);
		rs.insertRow();
	}

	private void changeEntityWithResultSet(String tableName, Map<String, Object> fields, ResultSet rs)
	throws RecordNotFoundException, SQLException {
		//System.err.println("changeEntityWithResultSet method: IN");

		DatabaseTable table = this.tables.get(tableName);
		Set keys = fields.keySet();
		Iterator i = keys.iterator();
		while (i.hasNext()) {
			String key = (String)i.next();
			if ( table.getColumns().containsKey(key) ) {
				this.updateSQLFromJava(table.getColumns().get(key), fields.get(key), rs);
			}
		}
	}

	public Collection<HWSubmission> retrieveSubmissionsForHomeworkWithID(Integer id) {
		return this.retrieveSubmissionsWithWhereClause("homeworkId="+id);
	}

	public Collection<HWSubmission> retrieveSubmissionsForGroupWithID(Integer id) {
		return this.retrieveSubmissionsWithWhereClause(	"submitterType="+HWSubmission.FROM_GROUP+" " +
																										"AND submitterId="+id);
	}

	public Collection<HWSubmission> retrieveSubmissionsForStudentWithID(Integer id) {
		return this.retrieveSubmissionsWithWhereClause(	"submitterType="+HWSubmission.FROM_STUDENT+" " +
																										"AND submitterId="+id);
	}

	private Collection<HWSubmission> retrieveSubmissionsWithWhereClause(String clause) {
		Collection<HWSubmission> hws = new Vector();

		try {
			ResultSet rs = null;
			rs = this.getStatement().executeQuery("SELECT homeworkSubmissionId FROM HomeworkSubmission WHERE "+clause);
			while (rs.next()) {
				try {
					hws.add(this.retrieveHomeworkSubmissionWithId(rs.getInt("homeworkSubmissionId")));
				} catch (RecordNotFoundException rnfe) {
					System.err.println(rnfe.getMessage());
				}
			}
			rs.close();
		} catch (SQLException sqle) {
      this.printSQLException(sqle);
		}

		return hws;
	}

	public Collection<Message> retrieveMessagesForUserWithID(Integer id) {
		return this.retrieveMessagesWithWhereClause("senderId="+id);
	}

	public Collection<Message> retrieveMessagesForHomeworkWithID(Integer id) {
		return this.retrieveMessagesWithWhereClause("homeworkId="+id);
	}

	private Collection<Message> retrieveMessagesWithWhereClause(String clause) {
		Collection<Message> messages = new Vector();

		try {
			ResultSet rs = null;
			rs = this.getStatement().executeQuery("SELECT messageId FROM Message WHERE "+clause);
			while (rs.next()) {
				try {
					messages.add(this.retrieveMessageWithId(rs.getInt("messageId")));
				} catch (RecordNotFoundException rnfe) {
					System.err.println(rnfe.getMessage());
				}
			}
			rs.close();
		} catch (SQLException sqle) {
      this.printSQLException(sqle);
		}

		return messages;
	}

	public Collection<Homework> retrieveHomeworksForCourseWithID(String id) {
		return this.retrieveHomeworksWithWhereClause("courseId=\'"+id+"\'");
	}
	
	private Collection<Homework> retrieveHomeworksForInstructorWithID(Integer id) {
		return this.retrieveHomeworksWithWhereClause("instructorId="+id);
	}

	private Collection<Homework> retrieveHomeworksWithWhereClause(String clause) {
		Collection<Homework> homeworks = new Vector();

		try {
			ResultSet rs = null;
			rs = this.getStatement().executeQuery("SELECT homeworkId FROM Homework WHERE "+clause);
			while (rs.next()) {
				try {
					homeworks.add(this.retrieveHomeworkWithId(rs.getInt("homeworkId")));
				} catch (RecordNotFoundException rnfe) {
					System.err.println(rnfe.getMessage());
				}
			}
			rs.close();
		} catch (SQLException sqle) {
      this.printSQLException(sqle);
		}

		return homeworks;
	}

	public Collection<Group> retrieveGroupsForStudentWithID(int id)
	throws SQLException, RecordNotFoundException {
		Collection<Group> groups = new Vector();
		ResultSet rs = null;
		rs = this.getStatement().executeQuery(this.tables.get("Group_Members").getSelectFieldsWithConstraintSQL("groupId", "memberId", new Integer(id)));
		while (rs.next()) {
			groups.add(this.retrieveGroupWithId(rs.getInt("groupId")));
		}
		rs.close();
		return groups;
	}

	public Collection<Group> retrieveGroupsForCourseWithID(String id) {
		return this.retrieveGroupsWithWhereClause("courseId=\'"+id+"\'");
	}

	private Collection<Group> retrieveGroupsWithWhereClause(String clause) {
		Collection<Group> groups = new Vector();

		try {
			ResultSet rs = null;
			rs = this.getStatement().executeQuery("SELECT groupId FROM Groups WHERE "+clause);
			while (rs.next()) {
				try {
					groups.add(this.retrieveGroupWithId(rs.getInt("groupId")));
				} catch (RecordNotFoundException rnfe) {
					System.err.println(rnfe.getMessage());
				}
			}
			rs.close();
		} catch (SQLException sqle) {
      this.printSQLException(sqle);
		}

		return groups;
	}

	public Collection<String> retrieveDepartmentList() {
		Collection<String> list = new Vector();
		try {
		ResultSet rs = null;
			rs = this.getStatement().executeQuery("SELECT DISTINCT courseDept FROM Course");
			while (rs.next()) {
				list.add(rs.getString("courseDept"));
			}
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return list;
	}

	public Collection<Course> retrieveCoursesForDepartment(String dept) {
		return this.retrieveCoursesWithWhereClause("courseDept="+dept);
	}

	public Collection<Course> retrieveCoursesForInstructorWithID(Integer id) {
		return this.retrieveCoursesWithWhereClause("instructorId="+id);
	}

	public Collection<Course> retrieveCoursesForTAWithID(Integer id) {
		Collection<Course> courses = new Vector();
		try {
			ResultSet rs = null;
			rs = this.getStatement().executeQuery(this.tables.get("TeachingAssistant_Courses").getSelectFieldsWithConstraintSQL("courseId", "taId", id));
			while (rs.next()) {
				courses.add(this.retrieveCourseWithId(rs.getString("courseId")));
			}
			rs.close();
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return courses;
	}

	public Collection<Course> retrieveCoursesForStudentWithID(Integer id) {
		return this.retrieveCoursesWithFromWhereClause("Course, Course_Students", "Course_Students.courseId=Course.courseId AND Course_Students.studentId="+id);
	}

	private Collection<Course> retrieveCoursesWithWhereClause(String clause) {
		return this.retrieveCoursesWithFromWhereClause("Course", clause);
	}

	private Collection<Course> retrieveCoursesWithFromWhereClause(String from, String where) {
		Collection<Course> courses = new Vector();
		Collection<String> courseIds = new Vector();
		try {
			ResultSet rst = null;
			rst = this.getStatement().executeQuery("SELECT Course.courseId FROM "+from+" WHERE "+where);
			while (rst.next()) {
				try {
					courseIds.add(rst.getString("courseId"));
					//courses.add(this.retrieveCourseWithId(rst.getString("courseId")));
				} catch (Exception rnfe) {
					System.err.println(rnfe.getMessage());
				}
			}
			rst.close();
			System.err.println(courseIds);

			String[] courseIdsArray = courseIds.toArray(new String[0]);
			for	(String id : courseIdsArray) {
				try {
					courses.add(this.retrieveCourseWithId(id));
				} catch (RecordNotFoundException ex) {
					ex.printStackTrace(System.err);
				}
			}

		} catch (SQLException sqle) {
      this.printSQLException(sqle);
		}

		return courses;
	}

	public void removeUser(User o)
	throws SQLException, RecordNotFoundException {
		Integer id = new Integer(o.getIdNum());
		o = this.retrieveUserWithId(id);

		this.tables.get("Message").deleteWithConstraint(this.conn, "senderId", id);
		this.tables.get("Users").deleteWithId(this.conn, o.getIdNum());
	}

	private void removeTeacher(Teaching o)
	throws SQLException {
		this.tables.get("Teacher").deleteWithId(this.conn, o.getIdNum());
	}

	public void removeInstructor(Instructor o)
	throws SQLException, RecordNotFoundException {
		Integer id = new Integer(o.getIdNum());
		o = this.retrieveInstructorWithId(o.getIdNum());

		// Homeworks
		Collection<Homework> h = this.retrieveHomeworksForInstructorWithID(id);
		Iterator i = h.iterator();
		while (i.hasNext()) {
			this.removeHomework((Homework)i.next());
		}

		// Courses
		Collection<Course> c = this.retrieveCoursesForInstructorWithID(id);
		i = c.iterator();
		while (i.hasNext()) {
			this.removeCourse((Course)i.next());
		}

		this.removeTeacher(o);
		this.removeUser(o);
	}

	public void removeStudent(Student o)
	throws SQLException, RecordNotFoundException {
		Integer id = new Integer(o.getIdNum());
		o = this.retrieveStudentWithId(id);

		this.tables.get("Group_Members").deleteWithConstraint(this.conn, "memberId", id);
		this.tables.get("Course_Students").deleteWithConstraint(this.conn, "studentId", id);

		// deletion of HWSubmissions associated to a Student
		Map<String, Object> constraints = new Hashtable(1);
		constraints.put("submitterId", id);
		constraints.put("submitterType", HWSubmission.FROM_STUDENT);
		this.tables.get("HomeworkSubmission").deleteWithConstraints(this.conn, constraints);
		
		this.removeUser(o);
	}

	public void removeTeachingAssistant(TeachingAssistant o)
	throws SQLException, RecordNotFoundException {
		Integer id = new Integer(o.getIdNum());
		o = this.retrieveTeachingAssistantWithId(id);

		this.tables.get("TeachingAssistant_Courses").deleteWithConstraint(this.conn, "taId", id);
		this.tables.get("Homework_TAs").deleteWithConstraint(this.conn, "taId", id);

		this.removeTeacher(o);
		this.removeStudent(o);
	}

	public void removeCourse(Course o)
	throws SQLException, RecordNotFoundException {
		String id = o.getCourseID();
		o = this.retrieveCourseWithId(id);

		// Homeworks
		Collection<Homework> h = this.retrieveHomeworksForCourseWithID(id);
		Iterator i = h.iterator();
		while (i.hasNext()) {
			this.removeHomework((Homework)i.next());
		}

		// Groups
		Collection<Group> g = this.retrieveGroupsForCourseWithID(id);
		i = h.iterator();
		while (i.hasNext()) {
			this.recordGroup((Group)i.next());
		}

		this.tables.get("Course_Students").deleteWithConstraint(this.conn, "courseId", id);
		this.tables.get("TeachingAssistant_Courses").deleteWithConstraint(this.conn, "courseId", id);
		this.tables.get("Course").deleteWithId(this.conn, id);
	}

	public void removeGroup(Group o)
	throws SQLException, RecordNotFoundException {
		Integer id = new Integer(o.getGroupID());
		o = this.retrieveGroupWithId(id);

		// deletion of HWSubmissions associated to a Group
		Map<String, Object> constraints = new Hashtable(1);
		constraints.put("submitterId", id);
		constraints.put("submitterType", HWSubmission.FROM_GROUP);
		this.tables.get("HomeworkSubmission").deleteWithConstraints(this.conn, constraints);

		this.tables.get("Group_Members").deleteWithConstraint(this.conn, "groupId", id);
		this.tables.get("Groups").deleteWithId(this.conn, id);
	}

	public void removeHomework(Homework o)
	throws SQLException, RecordNotFoundException {
		Integer id = new Integer(o.getHomeworkID());
		o = this.retrieveHomeworkWithId(id);
		this.tables.get("Message").deleteWithConstraint(this.conn, "homeworkId", id);
		this.tables.get("HomeworkSubmission").deleteWithConstraint(this.conn, "homeworkId", id);
		this.tables.get("Homework_TAs").deleteWithConstraint(this.conn, "homeworkId", id);
		this.tables.get("Homework_Files").deleteWithConstraint(this.conn, "homeworkId", id);
		this.tables.get("Homework").deleteWithId(this.conn, id);
	}

	public void removeHomeworkSubmission(HWSubmission o)
	throws SQLException, RecordNotFoundException {
		o = this.retrieveHomeworkSubmissionWithId(o.getHWSubmissionID());
		this.tables.get("HomeworkSubmission").deleteWithId(this.conn, new Integer(o.getHWSubmissionID()));
	}

	public void removeMessage(Message o)
	throws SQLException, RecordNotFoundException {
		o = this.retrieveMessageWithId(o.getMessageID());
		this.tables.get("Message").deleteWithId(this.conn, o.getMessageID());
	}

	public void initializeDatabase() {
		try {
			
			Iterator i = DatabaseStructure.getTableOrder().iterator();
			while (i.hasNext()) {
				DatabaseTable t = this.tables.get(i.next());
				//System.err.println(t.getCreateSQL());
				this.getStatement().execute(t.getCreateSQL());
				System.err.println(t.getName()+" table creation: DONE");
			}
			this.conn.commit();

		} catch (SQLException sqle) {
      this.printSQLException(sqle);
		}
		System.err.println("Database initialization complete. All tables created with success.");
	}

	public void eraseDatabase() {
		try {

			Object[] tableNames = DatabaseStructure.getTableOrder().toArray();
			for (int i=tableNames.length-1; i>-1; i--) {
				DatabaseTable t = this.tables.get((String)tableNames[i]);
				this.getStatement().execute("DROP TABLE "+t.getName());
				System.out.println(t.getName()+" table destruction: DONE");
			}
			this.conn.commit();

		} catch (SQLException sqle) {
      this.printSQLException(sqle);
		}
		System.err.println("Database destruction complete. All tables dropped with success.");
	}

	/**
	 * Loads the appropriate JDBC driver for this environment/framework.
	 */
	private void loadDriver() {
		try {

			Class.forName(this.driver).newInstance();
			System.out.println("Loaded the appropriate driver");

		} catch (ClassNotFoundException cnfe) {
			System.err.println("\nUnable to load the JDBC driver " + this.driver);
			System.err.println("Please check your CLASSPATH.");
			cnfe.printStackTrace(System.err);
		} catch (InstantiationException ie) {
			System.err.println(
				"\nUnable to instantiate the JDBC driver " + this.driver);
			ie.printStackTrace(System.err);
		} catch (IllegalAccessException iae) {
			System.err.println(
				"\nNot allowed to access the JDBC driver " + this.driver);
			iae.printStackTrace(System.err);
		}
	}

	/**
	 * Prints details of an SQLException chain to <code>System.err</code>.
	 * Details included are SQL State, Error code, Exception message.
	 *
	 * @param e the SQLException from which to print details.
	 */
		private void printSQLException(SQLException e)
	{
		// Unwraps the entire exception chain to unveil the real cause of the
		// Exception.
		while (e != null)
		{
			System.err.println("\n----- SQLException -----");
			System.err.println("  SQL State:  " + e.getSQLState());
			System.err.println("  Error Code: " + e.getErrorCode());
			System.err.println("  Message:    " + e.getMessage());
			// for stack traces, refer to derby.log or uncomment this:
			e.printStackTrace(System.err);
			e = e.getNextException();
		}
	}

	public void closeConnection() {
		try {
			this.conn.close();
		} catch (SQLException sqle) {
      this.printSQLException(sqle);
		}
	}

	public void openConnection() {
		try {
			this.conn = DriverManager.getConnection(this.protocol + this.dbName + ";create=true", this.props);
			// We want to control transactions manually. Autocommit is on by
      // default in JDBC.
      this.conn.setAutoCommit(false);
		} catch (SQLException sqle) {
      this.printSQLException(sqle);
		}
	}

	private Object getJavaFromSQL(DatabaseColumn column, ResultSet rs) 
	throws SQLException {
		Class javaClass = column.getJavaClass();

		if (javaClass.equals(String.class)) {
			return rs.getString(column.getName());

		} else if (javaClass.equals(Integer.class)) {
			return new Integer(rs.getInt(column.getName()));

		} else if (javaClass.equals(Boolean.class)) {
			int bool = rs.getInt(column.getName());
			return new Boolean(bool!=0);
			
		} else if (javaClass.equals(Timestamp.class)) {
			return rs.getTimestamp(column.getName());
		}

		throw new SQLException("javaClass corresponds to nothing: "+javaClass.toString());
	}

	private void updateSQLFromJava(DatabaseColumn column, Object value, ResultSet rs)
	throws SQLException {
		if (value != null) {
			Class javaClass = column.getJavaClass();

			if (javaClass.equals(String.class)) {
				rs.updateString(column.getName(), (String)value);

			} else if (javaClass.equals(Integer.class)) {
				rs.updateInt(column.getName(), (Integer)value);

			} else if (javaClass.equals(Boolean.class)) {
				rs.updateBoolean(column.getName(), (Boolean)value);

			} else if (javaClass.equals(Timestamp.class)) {
				rs.updateTimestamp(column.getName(), (Timestamp)value);

			} else {
				throw new SQLException("javaClass corresponds to nothing: "+javaClass.toString());
			}

		} else {
			rs.updateNull(column.getName());
		}
	}

	private void updateAssociativeTable(DatabaseTable table, DatabaseColumn referenceColumn, Object referenceValue, DatabaseColumn changingColumn, Object[] newValues)
	throws SQLException {

		Object[] news = null;
		if (newValues!=null) {
			news = newValues.clone();
		} else {
			news = new Object[0];
		}

		ResultSet rs = null;
		boolean still;

		// deletion of unwanted values
		StringBuffer sql = new StringBuffer("DELETE FROM "+table.getName()+" WHERE "+referenceColumn.getName()+"=");
		if (referenceValue.getClass().equals(Integer.class)) {
			sql.append(referenceValue+"");
		} else if (referenceValue.getClass().equals(String.class)) {
			sql.append("\'"+referenceValue+"\'");
		}
		this.getStatement().executeUpdate(sql.toString());

		rs = this.getStatement().executeQuery(table.getSelectFieldsWithConstraintSQL("*", referenceColumn.getName(), referenceValue));

		// addition of new values
		for (int i=0; i<news.length; i++) {
			if (news[i]!=null) {
				rs.moveToInsertRow();
				this.updateSQLFromJava(referenceColumn, referenceValue, rs);
				this.updateSQLFromJava(changingColumn, news[i], rs);
				rs.insertRow();
			}
		}// for
		
		rs.close();
	}

	private boolean compareObjectWithField(Object reference, DatabaseColumn column, ResultSet rs)
	throws SQLException {
		Class javaClass = column.getJavaClass();

		if (javaClass.equals(String.class)) {
			String value = rs.getString(column.getName());
			return (value.contentEquals((String)reference));

		} else if (javaClass.equals(Integer.class)) {
			Integer value = new Integer(rs.getInt(column.getName()));
			System.err.println(value.intValue()+"|"+reference);
			return (value.intValue() == ((Integer)reference).intValue());

		} else if (javaClass.equals(Boolean.class)) {
			Boolean bool = new Boolean(rs.getInt(column.getName())!=0);
			return ( ((Boolean)reference) == bool );
		}

		return false;
	}

	public static void printFields(Map<String, Object> fields) {
		String[] keys = new String[0];
		keys = fields.keySet().toArray(keys);

		System.err.println("Fields Content :");
		for (int i=0; i<keys.length; i++) {
			System.err.println("\t"+keys[i]+" ==> "+fields.get(keys[i]).toString());
		}
		System.err.println();
	}

}
