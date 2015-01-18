/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

/**
 *
 * @author Luckyna
 */
public enum Gender {
    M ("Male"),
    F ("Female");
    
    private String label;

    private Gender(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
    
    
}
