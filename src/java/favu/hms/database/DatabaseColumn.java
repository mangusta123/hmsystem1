package favu.hms.database;


public class DatabaseColumn {

	private String name;
	private Class javaClass;
	private String sqlType;
	private String sqlProperty;

	public DatabaseColumn(String name, Class javaClass, String sqlType, String sqlProperty) {
		this.name = name;
		this.javaClass = javaClass;
		this.sqlType = sqlType;
		this.sqlProperty = sqlProperty;
	}

	public Class getJavaClass() {
		return javaClass;
	}

	public String getName() {
		return name;
	}

	public String getSqlProperty() {
		return sqlProperty;
	}

	public String getSqlType() {
		return sqlType;
	}

}
