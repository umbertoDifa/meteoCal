/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

/**
 *
 * @author Francesco
 */
public enum ControlMessages {

    USER_NOT_FOUND(0,"Incorrect Username"),
    WRONG_PASSWORD(1,"Incorrect Password");
    
    //static?
    private final int index;
    private final String message;

    ControlMessages(int index, String message) {
        this.index = index;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    
}
