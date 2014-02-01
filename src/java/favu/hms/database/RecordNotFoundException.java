/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package favu.hms.database;


public class RecordNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>RecordNotFoundException</code> without detail message.
     */
    public RecordNotFoundException() {
    }


    /**
     * Constructs an instance of <code>RecordNotFoundException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public RecordNotFoundException(String msg) {
        super(msg);
    }
}
