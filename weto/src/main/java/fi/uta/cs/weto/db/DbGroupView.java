package fi.uta.cs.weto.db;

import java.sql.*;
import java.util.Iterator;
import fi.uta.cs.sqldatamodel.*;
import fi.uta.cs.sqldatatypes.*;

/**
 * Generated database access class for view GroupView.
 * 
 */
public class DbGroupView extends SqlAssignableObject implements Cloneable {
    private SqlInteger userIdData;
    private SqlInteger idData;
    private SqlLongvarchar nameData;
    private SqlInteger typeData;
    private SqlInteger taskIdData;

    /**
     * Default constructor.
     */
    public DbGroupView() {
        super();
        userIdData = new SqlInteger();
        idData = new SqlInteger();
        nameData = new SqlLongvarchar();
        typeData = new SqlInteger();
        taskIdData = new SqlInteger();
    }

    /**
     * Updates the data from the given ResultSet object.
     * @param resultSet ResultSet object containing the data.
     * @param baseIndex Base index of the columns in the ResultSet (exclusive).
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     */
    public void setFromResultSet(ResultSet resultSet, int baseIndex) throws SQLException, InvalidValueException {
        userIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+1) );
        idData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+2) );
        nameData.jdbcSetValue( (java.lang.String) resultSet.getObject(baseIndex+3) );
        typeData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+4) );
        taskIdData.jdbcSetValue( (java.lang.Integer) resultSet.getObject(baseIndex+5) );
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("GroupView\n");
        sb.append("userId:" +  userIdData.toString() + "\n");
        sb.append("id:" +  idData.toString() + "\n");
        sb.append("name:" +  nameData.toString() + "\n");
        sb.append("type:" +  typeData.toString() + "\n");
        sb.append("taskId:" +  taskIdData.toString() + "\n");
        sb.append("\n");
        return(sb.toString());
    }

    public boolean equals( Object obj ) {
        if( obj == null ) return false;
        if( !(obj instanceof DbGroupView) ) return false;
        DbGroupView dbObj = (DbGroupView)obj;
        if( !userIdData.equals( dbObj.userIdData ) ) return false;
        if( !idData.equals( dbObj.idData ) ) return false;
        if( !nameData.equals( dbObj.nameData ) ) return false;
        if( !typeData.equals( dbObj.typeData ) ) return false;
        if( !taskIdData.equals( dbObj.taskIdData ) ) return false;
        return true;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Gets the raw data object for userId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getUserIdData() {
        return userIdData;
    }

    /**
     * Gets the raw data object for id attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getIdData() {
        return idData;
    }

    /**
     * Gets the raw data object for name attribute.
     * @return Data object as SqlLongvarchar.
     */
    public SqlLongvarchar getNameData() {
        return nameData;
    }

    /**
     * Gets the raw data object for type attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getTypeData() {
        return typeData;
    }

    /**
     * Gets the raw data object for taskId attribute.
     * @return Data object as SqlInteger.
     */
    public SqlInteger getTaskIdData() {
        return taskIdData;
    }

    /**
     * Selects the row of this object from the database view GroupView and updates the attributes accordingly.
     * @param con Open and active connection to the database.
     * @throws SQLException if the JDBC operation fails.
     * @throws InvalidValueException if the attributes are invalid.
     * @throws NoSuchItemException if the row to be selected does not exist or is not unique.
     */
    public void select(Connection con) throws SQLException, InvalidValueException, NoSuchItemException {
        String prepareString = "select * from GroupView where userId = ? AND id = ? AND name = ? AND type = ? AND taskId = ?";
        PreparedStatement ps = con.prepareStatement(prepareString);
        ps.setObject(1, userIdData.jdbcGetValue());
        ps.setObject(2, idData.jdbcGetValue());
        ps.setObject(3, nameData.jdbcGetValue());
        ps.setObject(4, typeData.jdbcGetValue());
        ps.setObject(5, taskIdData.jdbcGetValue());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            setFromResultSet(rs,0);
            rs.close();
            ps.close();
        } else {
            rs.close();
            ps.close();
            throw new NoSuchItemException();
        }
    }

    /**
     * Constructs and returns a selection iterator for rows in database view GroupView.
     * 
     * @param con Open and active connection to the database.
     * @param whereClause Optional where clause. If null is given, all the rows are selected.
     * 
     * @return Newly constructed iterator that returns objects of type GroupView. The iterator is closed when hasNext() returns false or the iterator is finalized.
     * 
     * @throws SQLException if the JDBC operation fails.
     * 
     * Note that the iterator may throw SqlSelectionIteratorException (which is a runtime exception) when its methods are called.
     */
    public static Iterator selectionIterator( Connection con, String whereClause ) throws SQLException {
        String prepareString;
        if( whereClause == null ) whereClause = "";
        if( whereClause.equals("") )
            prepareString = "select * from GroupView";
        else
            prepareString = "select * from GroupView where " + whereClause;
        PreparedStatement ps = con.prepareStatement(prepareString);
        return new SqlSelectionIterator( ps, GroupView.class );
    }
}

// End of file.
