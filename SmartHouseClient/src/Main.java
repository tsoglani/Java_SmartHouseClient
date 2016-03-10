
import java.util.UUID;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author tsoglani
 */
public class Main {
      public static String UNIQUE_USER_ID = null;
    private static String UNIQUE_USER_ID_SPLIT = "!!!!!";

    public static void main(String[] args) {
        
        MenuFrame menuFrame = new MenuFrame();
        generateUniqueUserID();
    }
    
      public static void generateUniqueUserID() {
        if (UNIQUE_USER_ID == null) {
            UNIQUE_USER_ID = "userUniqueID:" + UUID.randomUUID().toString() + UNIQUE_USER_ID_SPLIT;
        }
    }
}
