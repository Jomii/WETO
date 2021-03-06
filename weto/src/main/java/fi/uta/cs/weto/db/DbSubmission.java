package fi.uta.cs.weto.db;

import fi.uta.cs.sqldatamodel.InvalidValueException;
import fi.uta.cs.sqldatamodel.NoSuchItemException;
import fi.uta.cs.sqldatamodel.ObjectNotValidException;
import fi.uta.cs.sqldatamodel.SqlAssignableObject;
import fi.uta.cs.sqldatamodel.SqlSelectionIterator;
import fi.uta.cs.sqldatamodel.TooManyItemsException;
import fi.uta.cs.sqldatatypes.SqlInteger;
import fi.uta.cs.sqldatatypes.SqlLongvarchar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Generated database access class for table Submission.
 *
 */
public class DbSubmission extends SqlAssignableObject implements Cloneable
{
  private SqlInteger idData;
  private SqlInteger idKC;
  private SqlInteger userIdData;
  private SqlInteger timeStampData;
  private SqlInteger autoGradeMarkData;
  private SqlInteger statusData;
  private SqlInteger taskIdData;
  private SqlLongvarchar messageData;
  private SqlInteger errorData;
  private SqlInteger fileCountData;

  /**
   * Default constructor.
   */
  public DbSubmission()
  {
    super();
    idData = new SqlInteger();
    idKC = new SqlInteger();
    userIdData = new SqlInteger();
    timeStampData = new SqlInteger();
    autoGradeMarkData = new SqlInteger();
    statusData = new SqlInteger();
    taskIdData = new SqlInteger();
    messageData = new SqlLongvarchar();
    // Contains an error code
    errorData = new SqlInteger();
    // The number of files in the submission
    fileCountData = new SqlInteger();
    idData.setPrime(true);
  }

  /**
   * Updates the data from the given ResultSet object.
   *
   * @param resultSet ResultSet object containing the data.
   * @param baseIndex Base index of the columns in the ResultSet (exclusive).
   * @throws SQLException if the JDBC operation fails.
   * @throws InvalidValueException if the attributes are invalid.
   */
  public void setFromResultSet(ResultSet resultSet, int baseIndex) throws
          SQLException, InvalidValueException
  {
    idData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex + 1));
    idKC.jdbcSetValue(idData.jdbcGetValue());
    userIdData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 2));
    timeStampData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 3));
    autoGradeMarkData.jdbcSetValue((java.lang.Integer) resultSet.getObject(
            baseIndex + 4));
    statusData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 5));
    taskIdData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 6));
    messageData.jdbcSetValue((java.lang.String) resultSet.getObject(baseIndex
            + 7));
    errorData.jdbcSetValue((java.lang.Integer) resultSet
            .getObject(baseIndex + 8));
    fileCountData.jdbcSetValue((java.lang.Integer) resultSet.getObject(baseIndex
            + 9));
  }

  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("Submission\n");
    sb.append("id:" + idData.toString() + "\n");
    sb.append("userId:" + userIdData.toString() + "\n");
    sb.append("timeStamp:" + timeStampData.toString() + "\n");
    sb.append("autoGradeMark:" + autoGradeMarkData.toString() + "\n");
    sb.append("status:" + statusData.toString() + "\n");
    sb.append("taskId:" + taskIdData.toString() + "\n");
    sb.append("message:" + messageData.toString() + "\n");
    sb.append("error:" + errorData.toString() + "\n");
    sb.append("fileCount:" + fileCountData.toString() + "\n");
    sb.append("\n");
    return (sb.toString());
  }

  public boolean equals(Object obj)
  {
    if(obj == null)
    {
      return false;
    }
    if(!(obj instanceof DbSubmission))
    {
      return false;
    }
    DbSubmission dbObj = (DbSubmission) obj;
    if(!idData.equals(dbObj.idData))
    {
      return false;
    }
    if(!userIdData.equals(dbObj.userIdData))
    {
      return false;
    }
    if(!timeStampData.equals(dbObj.timeStampData))
    {
      return false;
    }
    if(!autoGradeMarkData.equals(dbObj.autoGradeMarkData))
    {
      return false;
    }
    if(!statusData.equals(dbObj.statusData))
    {
      return false;
    }
    if(!taskIdData.equals(dbObj.taskIdData))
    {
      return false;
    }
    if(!messageData.equals(dbObj.messageData))
    {
      return false;
    }
    if(!errorData.equals(dbObj.errorData))
    {
      return false;
    }
    if(!fileCountData.equals(dbObj.fileCountData))
    {
      return false;
    }
    return true;
  }

  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }

  /**
   * Gets the raw data object for id attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getIdData()
  {
    return idData;
  }

  /**
   * Gets the raw data object for userId attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getUserIdData()
  {
    return userIdData;
  }

  /**
   * Gets the raw data object for timeStamp attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getTimeStampData()
  {
    return timeStampData;
  }

  /**
   * Gets the raw data object for autoGradeMark attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getAutoGradeMarkData()
  {
    return autoGradeMarkData;
  }

  /**
   * Gets the raw data object for status attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getStatusData()
  {
    return statusData;
  }

  /**
   * Gets the raw data object for taskId attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getTaskIdData()
  {
    return taskIdData;
  }

  /**
   * Gets the raw data object for message attribute.
   *
   * @return Data object as SqlLongvarchar.
   */
  public SqlLongvarchar getMessageData()
  {
    return messageData;
  }

  /**
   * Gets the raw data object for error attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getErrorData()
  {
    return errorData;
  }

  /**
   * Gets the raw data object for fileCount attribute.
   *
   * @return Data object as SqlInteger.
   */
  public SqlInteger getFileCountData()
  {
    return fileCountData;
  }

  /**
   * Inserts this object to the database table Submission.
   *
   * @param con Open and active connection to the database.
   * @throws SQLException if the JDBC operation fails.
   * @throws ObjectNotValidException if the attributes are invalid.
   */
  public void insert(Connection con) throws SQLException,
          ObjectNotValidException
  {
    if(!userIdData.isValid())
    {
      throw new ObjectNotValidException("userId");
    }
    if(!timeStampData.isValid())
    {
      throw new ObjectNotValidException("timeStamp");
    }
    if(!autoGradeMarkData.isValid())
    {
      throw new ObjectNotValidException("autoGradeMark");
    }
    if(!statusData.isValid())
    {
      throw new ObjectNotValidException("status");
    }
    if(!taskIdData.isValid())
    {
      throw new ObjectNotValidException("taskId");
    }
    if(!messageData.isValid())
    {
      throw new ObjectNotValidException("message");
    }
    if(!errorData.isValid())
    {
      throw new ObjectNotValidException("error");
    }
    if(!fileCountData.isValid())
    {
      throw new ObjectNotValidException("fileCount");
    }
    String prepareString
            = "insert into Submission (userId, timeStamp, autoGradeMark, status, taskId, message, error, fileCount) values (?, ?, ?, ?, ?, ?, ?, ?) returning id;";
    try(PreparedStatement ps = con.prepareStatement(prepareString))
    {
      ps.setObject(1, userIdData.jdbcGetValue());
      ps.setObject(2, timeStampData.jdbcGetValue());
      ps.setObject(3, autoGradeMarkData.jdbcGetValue());
      ps.setObject(4, statusData.jdbcGetValue());
      ps.setObject(5, taskIdData.jdbcGetValue());
      ps.setObject(6, messageData.jdbcGetValue());
      ps.setObject(7, errorData.jdbcGetValue());
      ps.setObject(8, fileCountData.jdbcGetValue());
      ResultSet rs = ps.executeQuery();
      if(!rs.next())
      {
        throw new SQLException("Insert did not return 1 row");
      }
      else
      {
        try
        {
          Integer id = rs.getInt(1);
          idData.jdbcSetValue(id);
          idKC.jdbcSetValue(id);
        }
        catch(Exception e)
        {
          throw new SQLException("Insert could not update id");
        }
      }
    }
  }

  /**
   * Updates the row of this object in the database table Submission. The row is
   * identified by the primary key attribute(s).
   *
   * @param con Open and active connection to the database.
   * @throws SQLException if the JDBC operation fails.
   * @throws ObjectNotValidException if the attributes are invalid.
   * @throws NoSuchItemException if the row to be updated does not exist or is
   * not unique.
   */
  public void update(Connection con) throws SQLException,
          ObjectNotValidException, NoSuchItemException
  {
    if(!idData.isValid())
    {
      throw new ObjectNotValidException("id");
    }
    if(!userIdData.isValid())
    {
      throw new ObjectNotValidException("userId");
    }
    if(!timeStampData.isValid())
    {
      throw new ObjectNotValidException("timeStamp");
    }
    if(!autoGradeMarkData.isValid())
    {
      throw new ObjectNotValidException("autoGradeMark");
    }
    if(!statusData.isValid())
    {
      throw new ObjectNotValidException("status");
    }
    if(!taskIdData.isValid())
    {
      throw new ObjectNotValidException("taskId");
    }
    if(!messageData.isValid())
    {
      throw new ObjectNotValidException("message");
    }
    if(!errorData.isValid())
    {
      throw new ObjectNotValidException("error");
    }
    if(!fileCountData.isValid())
    {
      throw new ObjectNotValidException("fileCount");
    }
    String prepareString
            = "update Submission set id = ?, userId = ?, timeStamp = ?, autoGradeMark = ?, status = ?, taskId = ?, message = ?, error = ?, fileCount = ? where id = ?";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, idData.jdbcGetValue());
    ps.setObject(2, userIdData.jdbcGetValue());
    ps.setObject(3, timeStampData.jdbcGetValue());
    ps.setObject(4, autoGradeMarkData.jdbcGetValue());
    ps.setObject(5, statusData.jdbcGetValue());
    ps.setObject(6, taskIdData.jdbcGetValue());
    ps.setObject(7, messageData.jdbcGetValue());
    ps.setObject(8, errorData.jdbcGetValue());
    ps.setObject(9, fileCountData.jdbcGetValue());
    ps.setObject(10, idKC.jdbcGetValue());
    int rows = ps.executeUpdate();
    ps.close();
    try
    {
      idKC.jdbcSetValue(idData.jdbcGetValue());
    }
    catch(InvalidValueException e)
    {
      // Ignored (isValid already called)
    }
    if(rows != 1)
    {
      throw new NoSuchItemException();
    }
  }

  /**
   * Deletes the row of this object from the database table Submission. The row
   * is identified by the primary key attribute(s).
   *
   * @param con Open and active connection to the database.
   * @throws SQLException if the JDBC operation fails.
   * @throws TooManyItemsException if the row to be deleted is not unique.
   * @throws NoSuchItemException if the row to be deleted does not exist.
   */
  public void delete(Connection con) throws SQLException, TooManyItemsException,
          NoSuchItemException
  {
    String prepareString = "delete from Submission where id = ?";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, idKC.jdbcGetValue());
    int rows = ps.executeUpdate();
    ps.close();
    if(rows > 1)
    {
      throw new TooManyItemsException();
    }
    if(rows < 1)
    {
      throw new NoSuchItemException();
    }
  }

  /**
   * Selects the row of this object from the database table Submission and
   * updates the attributes accordingly. The row is identified by the primary
   * key attribute(s).
   *
   * @param con Open and active connection to the database.
   * @throws SQLException if the JDBC operation fails.
   * @throws InvalidValueException if the attributes are invalid.
   * @throws NoSuchItemException if the row to be selected does not exist or is
   * not unique.
   */
  public void select(Connection con) throws SQLException, InvalidValueException,
          NoSuchItemException
  {
    String prepareString = "select * from Submission where id = ?";
    PreparedStatement ps = con.prepareStatement(prepareString);
    ps.setObject(1, idData.jdbcGetValue());
    ResultSet rs = ps.executeQuery();
    if(rs.next())
    {
      setFromResultSet(rs, 0);
      rs.close();
      ps.close();
    }
    else
    {
      rs.close();
      ps.close();
      throw new NoSuchItemException();
    }
  }

  /**
   * Constructs and returns a selection iterator for rows in database table
   * Submission.
   *
   * @param con Open and active connection to the database.
   * @param whereClause Optional where clause. If null is given, all the rows
   * are selected.
   *
   * @return Newly constructed iterator that returns objects of type Submission.
   * The iterator is closed when hasNext() returns false or the iterator is
   * finalized.
   *
   * @throws SQLException if the JDBC operation fails.
   *
   * Note that the iterator may throw SqlSelectionIteratorException (which is a
   * runtime exception) when its methods are called.
   */
  public static Iterator selectionIterator(Connection con, String whereClause)
          throws SQLException
  {
    String prepareString;
    if(whereClause == null)
    {
      whereClause = "";
    }
    if(whereClause.equals(""))
    {
      prepareString = "select * from Submission";
    }
    else
    {
      prepareString = "select * from Submission where " + whereClause;
    }
    PreparedStatement ps = con.prepareStatement(prepareString);
    return new SqlSelectionIterator(ps, Submission.class);
  }

}

// End of file.
