/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emaildemo;

/**
 *
 * @author Song
 */
public class EmailDemo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String[] to = {"javamit@gmail.com","donblamemepls@hotmail.com"};
        if(EmailSender.sendMail
                ("garyysliew93@gmail.com",
                        "7416389liew",
                        "message to reciepents",
                        to))System.out.println("Email send successfully");
          
                
        else System.out.println("some error occur");
    }
    
}
