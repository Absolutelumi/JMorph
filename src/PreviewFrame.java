import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PreviewFrame extends JFrame
{
    public PreviewFrame(BufferedImage image)
    {
        this.setTitle("Preview");
        this.setResizable(false);
        this.setContentPane(new PreviewPanel(image));
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);

        this.pack();
        this.setVisible(true);
    }

    public void UpdateImage(BufferedImage image) { ((PreviewPanel)this.getContentPane()).UpdateImage(image); }
}

class PreviewPanel extends JPanel
{
    private final List<BufferedImage> morphFrames;

    private final JLabel MorphImageLabel;

    public PreviewPanel(BufferedImage image)
    {
        morphFrames = new ArrayList<>();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JButton ExportButton = new JButton();
        ExportButton.setText("Export");
        ExportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Wipe previous images/videos
                File imageDirectory = new File("outimages/");
                for (File file : imageDirectory.listFiles()) file.delete();

                for (int i = 0; i < morphFrames.size(); i++)
                {
                    try { ImageIO.write(morphFrames.get(i), "jpg", new File("outimages/morphImage" + i + ".jpeg")); } catch (IOException error) {}
                }

                String ffmpegCommand = "ffmpeg -framerate 30 -i outimages/morphImage%d.jpeg outimages/morph.mp4";
                try { Runtime.getRuntime().exec(ffmpegCommand); } catch (IOException error) { System.out.print(error.toString()); }
            }
        });

        MorphImageLabel = new JLabel();
        MorphImageLabel.setIcon(new ImageIcon(image));

        this.add(ExportButton);
        this.add(MorphImageLabel);
    }

    public void UpdateImage(BufferedImage image)
    {
        MorphImageLabel.setIcon(new ImageIcon(image));

        BufferedImage frame = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        frame.getGraphics().drawImage(image, 0, 0, null);
        morphFrames.add(frame);
    }
}