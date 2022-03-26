import javax.swing.*;

public class JMorphFrame extends JFrame
{
    public JMorphFrame()
    {
        this.setTitle("JMorph");
        this.setResizable(false);
        this.setContentPane(new JMorphPanel());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        this.pack();
        this.setVisible(true);
    }

    public static void main(String[] args) { new JMorphFrame(); }
}
