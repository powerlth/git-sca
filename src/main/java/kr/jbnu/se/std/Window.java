package kr.jbnu.se.std;

import java.awt.Frame;
import javax.swing.*;


public class Window extends JFrame{
        
    private Window()
    {
        // Sets the title for this frame.2
        this.setTitle("Shoot the duck");
        
        // Sets size of the frame.
        if(false) // Full screen mode
        {
            // Disables decorations for this frame.
            this.setUndecorated(true);
            // Puts the frame to full screen.
            this.setExtendedState(Frame.MAXIMIZED_BOTH);
        }
        else // kr.jbnu.se.std.Window mode
        {
            // Size of the frame.
            this.setSize(800, 600);
            // Puts frame to center of the screen.
            this.setLocationRelativeTo(null);
            // So that frame cannot be resizable by the user.
            this.setResizable(true);
        }
        
        // Exit the application when user close frame.
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        this.setContentPane(new Framework());
        
        this.setVisible(true);
    }

    public static void main(String[] args)
    {
        Framework framework = new Framework();
        MainMenu mainMenu = new MainMenu(framework);
        mainMenu.setFramework(framework);
        // Use the event dispatch thread to build the UI for thread-safety.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Window();
            }
        });
    }
}
