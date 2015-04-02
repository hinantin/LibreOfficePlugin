 package qhichwa.openoffice;
 
 import javax.swing.JOptionPane;
 
 class DialogThread
   extends Thread
 {
   private final String text;
   
   DialogThread(String text)
   {
     this.text = text;
   }
   
   public void run()
   {
     JOptionPane.showMessageDialog(null, this.text);
   }
 }
