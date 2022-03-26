import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.awt.Image.SCALE_DEFAULT;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class DisplayPanel extends JPanel
{
    private BufferedImage StartImage;
    private BufferedImage EndImage;

    private String StartPath;
    private String EndPath;

    private final Renderer ImageRenderer;

    public DisplayPanel()
    {
        StartPath = Constants.StartImagePath;
        EndPath = Constants.EndImagePath;

        try { GetScaledImages(StartPath, EndPath); } catch (IOException e) {}
        ImageRenderer = new Renderer(StartImage, EndImage);

        JLabel StartImageLabel = new JLabel();
        StartImageLabel.setIcon(ImageRenderer.Render(ImageType.Start));
        StartImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ImageRenderer.OnMousePressed(new Point(e.getX(), e.getY()), ImageType.Start);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                ImageRenderer.OnMouseReleased();
            }
        });
        StartImageLabel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                ImageRenderer.OnMouseDragged(new Point(e.getX(), e.getY()), ImageType.Start);
            }
        });
        this.add(StartImageLabel);

        JLabel EndImageLabel = new JLabel();
        EndImageLabel.setIcon(ImageRenderer.Render(ImageType.End));
        EndImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ImageRenderer.OnMousePressed(new Point(e.getX(), e.getY()), ImageType.End);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                ImageRenderer.OnMouseReleased();
            }
        });
        EndImageLabel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                ImageRenderer.OnMouseDragged(new Point(e.getX(), e.getY()), ImageType.End);
            }
        });
        this.add(EndImageLabel);

        new Timer(Constants.UpdateInterval, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StartImageLabel.setIcon(ImageRenderer.Render(ImageType.Start));
                EndImageLabel.setIcon(ImageRenderer.Render(ImageType.End));
            }
        }).start();
    }

    private void GetScaledImages(String startPath, String endPath) throws IOException
    {
        BufferedImage start, end;
        start = ImageIO.read(new File(startPath));
        end = ImageIO.read(new File(endPath));

        StartImage = new BufferedImage(Constants.ImageDisplaySize, Constants.ImageDisplaySize, TYPE_INT_ARGB);
        StartImage.getGraphics().drawImage(start.getScaledInstance(Constants.ImageDisplaySize, Constants.ImageDisplaySize, SCALE_DEFAULT), 0, 0, null);

        EndImage = new BufferedImage(Constants.ImageDisplaySize, Constants.ImageDisplaySize, TYPE_INT_ARGB);
        EndImage.getGraphics().drawImage(end.getScaledInstance(Constants.ImageDisplaySize, Constants.ImageDisplaySize, SCALE_DEFAULT), 0, 0, null);
    }

    public void SetGridSize(int size) { ImageRenderer.ChangeGridSize(size); }

    public void SetTweenCount(int count) { ImageRenderer.SetTweenCount(count); }

    public void Preview() { ImageRenderer.Preview(); }

    public void Reset() { ImageRenderer.ResetPoints(); }

    public void ChangeBrightness(ImageType image, float percent) { ImageRenderer.ChangeBrightness(image, percent); }

    public void ChangeImage(ImageType image)
    {
        JFileChooser jfc = new JFileChooser();
        jfc.showOpenDialog(null);

        if (image == ImageType.Start)
        {
            try { GetScaledImages(jfc.getSelectedFile().getPath(), EndPath); } catch (IOException e) {}
            ImageRenderer.ChangeImage(image, StartImage);
        }
        else
        {
            try { GetScaledImages(StartPath, jfc.getSelectedFile().getPath()); } catch (IOException e) {}
            ImageRenderer.ChangeImage(image, EndImage);
        }

        int width = Math.max(StartImage.getWidth(), EndImage.getWidth());
        int height = Math.max(StartImage.getHeight(), EndImage.getHeight());
        this.setPreferredSize(new Dimension(width, height));
    }
}
