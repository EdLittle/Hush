/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import com.sun.xml.internal.ws.util.StringUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author FREAK
 */
public class DatabaseManager {
    static File file;

    public DatabaseManager() throws Exception{
        System.out.println("Hey soul sister");
        
        file = new File("data/users.txt");
        
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    public String[] getNames() throws Exception{
        FileInputStream fiStream = new FileInputStream("data/users.txt");
        DataInputStream in = new DataInputStream(fiStream);
        
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = "";
        String[] users = null;
        String names = "";
        //System.out.println("Baby");
        while((line = br.readLine()) != null){
            //System.out.println(line);
            names = names + line + "\n";
            
        }
        //System.out.println("4MEN");
        users = names.split("\n");
        return users;
    }
    
    public static void storeNames(String[] users) throws FileNotFoundException, IOException{
        int counter = 0;
        int length = users.length;
        BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
        
        for(counter = 0 ; counter < length; counter++){
            bw.write(users[counter]);
            bw.newLine();
            bw.flush();
        }
        bw.close();
    }
    
    public static void setUsernames(String[] usernames) throws FileNotFoundException, IOException{
        System.out.println("Yo. Im here.");
        for(int i = 0; i < 8; i++){
            System.out.println(usernames[i]);
        }
        storeNames(usernames);
    }
}
