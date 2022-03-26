import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlPanel extends JPanel
{
    private final JButton ChangeStartButton;
    private final JButton ChangeEndButton;
    private final JLabel FrameCountLabel;
    private final JSlider FrameCountSlider;
    private final JButton PreviewButton;
    private final JButton ResetButton;
    private final JLabel GridSizeLabel;
    private final JSlider GridSizeSlider;
    private final JLabel StartBrightnessLabel;
    private final JSlider StartBrightnessSlider;
    private final JLabel EndBrightnessLabel;
    private final JSlider EndBrightnessSlider;

    public ControlPanel(Listener listener)
    {
        ChangeStartButton = new JButton();
        ChangeStartButton.setText("Change Start Image");
        ChangeStartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.OnImageChange(ImageType.Start);
            }
        });

        ChangeEndButton = new JButton();
        ChangeEndButton.setText("Change End Image");
        ChangeEndButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.OnImageChange(ImageType.End);
            }
        });

        FrameCountSlider = new JSlider();
        FrameCountSlider.setMinimum(1);
        FrameCountSlider.setMaximum(99);
        FrameCountSlider.setValue(Constants.DefaultTweenCount);
        FrameCountSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                FrameCountLabel.setText("Tweens: " + FrameCountSlider.getValue());
                listener.OnTweenUpdate(FrameCountSlider.getValue());
            }
        });

        FrameCountLabel = new JLabel();
        FrameCountLabel.setText("Tweens: " + FrameCountSlider.getValue());

        PreviewButton = new JButton();
        PreviewButton.setText("Preview");
        PreviewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.OnPreview();
            }
        });

        ResetButton = new JButton();
        ResetButton.setText("Reset");
        ResetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.OnReset();
            }
        });

        GridSizeLabel = new JLabel();
        GridSizeLabel.setText("Grid Size: 10x10");

        GridSizeSlider = new JSlider();
        GridSizeSlider.setMinimum(5);
        GridSizeSlider.setMaximum(20);
        GridSizeSlider.setValue(10);
        GridSizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                GridSizeLabel.setText("Grid Size: " + GridSizeSlider.getValue() + "x" + GridSizeSlider.getValue());
                listener.OnGridSizeUpdate(GridSizeSlider.getValue());
            }
        });

        StartBrightnessLabel = new JLabel();
        StartBrightnessLabel.setText("Brightness: 1.0");

        StartBrightnessSlider = new JSlider();
        StartBrightnessSlider.setMinimum(0);
        StartBrightnessSlider.setMaximum(20);
        StartBrightnessSlider.setValue(10);
        StartBrightnessSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                listener.OnBrightnessChange(ImageType.Start, (float)StartBrightnessSlider.getValue() / 10);
                StartBrightnessLabel.setText("Brightness: " + (float)StartBrightnessSlider.getValue() / 10);
            }
        });

        EndBrightnessLabel = new JLabel();
        EndBrightnessLabel.setText("Brightness: 1.0");

        EndBrightnessSlider = new JSlider();
        EndBrightnessSlider.setMinimum(0);
        EndBrightnessSlider.setMaximum(20);
        EndBrightnessSlider.setValue(10);
        EndBrightnessSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                listener.OnBrightnessChange(ImageType.End, (float)EndBrightnessSlider.getValue() / 10);
                EndBrightnessLabel.setText("Brightness: " + (float)EndBrightnessSlider.getValue() / 10);
            }
        });

        this.add(StartBrightnessLabel);
        this.add(StartBrightnessSlider);
        this.add(ChangeStartButton);
        this.add(GridSizeLabel);
        this.add(GridSizeSlider);
        this.add(FrameCountLabel);
        this.add(FrameCountSlider);
        this.add(PreviewButton);
        this.add(ResetButton);
        this.add(ChangeEndButton);
        this.add(EndBrightnessSlider);
        this.add(EndBrightnessLabel);
    }

    interface Listener
    {
        void OnImageChange(ImageType image);
        void OnBrightnessChange(ImageType image, float percent);
        void OnGridSizeUpdate(int size);
        void OnTweenUpdate(int count);
        void OnPreview();
        void OnReset();
    }
}
