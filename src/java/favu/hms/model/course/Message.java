/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package favu.hms.model.course;

import favu.hms.database.DatabaseInterface;
import favu.hms.database.RecordNotFoundException;
import java.sql.SQLException;
import java.util.Date;


public class Message {

    private int messageID = -1;
    private int homeworkID;
    private int sendID;
    private Date creationDate;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public int getHomeworkID() {
        return homeworkID;
    }

    public void setHomeworkID(int homeworkID) {
        this.homeworkID = homeworkID;
    }

    public int getMessageID() {
        return messageID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public int getSendID() {
        return sendID;
    }

    public void setSendID(int sendID) {
        this.sendID = sendID;
    }

    public Message(int homeworkID, int sendID, String content) {
        this.homeworkID = homeworkID;
        this.sendID = sendID;
        this.content = content;
				this.creationDate = new Date();
    }

    public void remove() throws SQLException, RecordNotFoundException{
        DatabaseInterface.getInstance().removeMessage(this);
    }

    public static Message getMessage(int id) throws RecordNotFoundException{
      return DatabaseInterface.getInstance().retrieveMessageWithId(new Integer(id));
    }
		
	public Message record() throws RecordNotFoundException, SQLException {
		return DatabaseInterface.getInstance().recordMessage(this);
	}

	public static Message messageWithId(int id) throws RecordNotFoundException {
		return DatabaseInterface.getInstance().retrieveMessageWithId(new Integer(id));
	}
    
    

}
