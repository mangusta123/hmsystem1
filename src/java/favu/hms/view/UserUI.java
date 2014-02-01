/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package favu.hms.view;

/**
 * Warning: This class is likely to be replaced by the homework.jsp page
 * It is advisable not to use it!
 * 
 */
public class UserUI {
    /**
     * Gives the page that displays the homepage that contains various information for a user. 
     * It determines the type of user (student, teaching assistant or instructor) to generate
     * the page accordingly.
     * @param userID
     * @return
     */
    public String viewUser(int userID) {
        return null;
    }

    /**
     * Generate the page representation of a student’s page.
     * @param userID
     * @return
     */
    public String viewStudent(int userID) {
        return null;
    }

    /**
     * Generate the page representation of a teaching assistant’s page.
     * @param userID
     * @return
     */
    public String viewTA(int userID) {
        return null;
    }

    /**
     * Generate the page representation of an instructor’s page
     */
    public String viewInstructor(int userid) {
        return null;
    }

    /**
     * Generate the page representation of a anonymous user’s page (hence the login page)
     * @return
     */
    public String viewLoginPage() {
        return null;
    }

    /**
     * Update the representation accordingly to changes in the object that it displays.
     */
    public void update() {
    
    }
}
