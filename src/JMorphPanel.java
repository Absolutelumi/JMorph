import javax.swing.*;

public class JMorphPanel extends JPanel
{
    public JMorphPanel()
    {
        DisplayPanel displayPanel = new DisplayPanel();
        ControlPanel controlPanel = new ControlPanel(new ControlPanel.Listener() {
            @Override
            public void OnTweenUpdate(int count) {
                displayPanel.SetTweenCount(count);
            }
            @Override
            public void OnPreview() {
                displayPanel.Preview();
            }
            @Override
            public void OnReset() {
                displayPanel.Reset();
            }
            @Override
            public void OnImageChange(ImageType image) {
                displayPanel.ChangeImage(image);
            }
            @Override
            public void OnGridSizeUpdate(int size) {
                displayPanel.SetGridSize(size);
            }
            @Override
            public void OnBrightnessChange(ImageType image, float percent) {
                displayPanel.ChangeBrightness(image, percent);
            }
        });

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(controlPanel);
        this.add(displayPanel);
    }
}
