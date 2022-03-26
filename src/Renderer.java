import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.ArrayList;
import java.util.List;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class Renderer
{
    private final List<Point> StartPoints;
    private final List<Point> EndPoints;
    private final List<Point> PreviewPoints;
    private int PointDim; // Size of (square) dimension for points

    private BufferedImage StartOriginal;
    private BufferedImage EndOriginal;

    private BufferedImage StartImage;
    private BufferedImage EndImage;

    private int TweenCount;

    private boolean isMovingPoint;
    private ImageType movedImage;
    private Point movedPoint;
    private Polygon constraintPoly;
    private final int[][] constraintOffsets = { {-1, 0} , {-1, -1} , {0, -1} , {1, 0} , {1, 1} , {0, 1} };

    private BufferedImage previewImage;
    private BufferedImage previewStartImage;
    private BufferedImage previewEndImage;

    private AlphaComposite startAlpha;

    private Morpher startMorpher;
    private Morpher endMorpher;

    private PreviewFrame previewFrame;
    private List<Triangle> startTriangles;
    private List<Triangle> endTriangles;
    private boolean previewing;
    private int tweenCounter;
    private Timer previewTimer;

    public Renderer(BufferedImage startImage, BufferedImage endImage)
    {
        StartPoints = new ArrayList<>();
        EndPoints = new ArrayList<>();
        PreviewPoints = new ArrayList<>();

        StartOriginal = startImage;
        StartImage = startImage;

        EndOriginal = endImage;
        EndImage = endImage;

        TweenCount = Constants.DefaultTweenCount;
        previewing = false;

        PointDim = 10;
        ResetPoints();
    }

    public void ChangeGridSize(int size)
    {
        PointDim = size;
        ResetPoints();
    }

    public void ChangeBrightness(ImageType image, float percent)
    {
        RescaleOp op = new RescaleOp(percent, 0, null);
        if (image == ImageType.Start) StartImage = op.filter(StartOriginal, null);
        else EndImage = op.filter(EndOriginal, null);
    }

    public void ChangeImage(ImageType type, BufferedImage image)
    {
        if (type == ImageType.Start)
        {
            StartOriginal = image;
            StartImage = image;
        }
        else
        {
            EndOriginal = image;
            EndImage = image;
        }

        ResetPoints();
    }

    public void ResetPoints()
    {
        StartPoints.clear();
        EndPoints.clear();

        for (int x = 0; x < PointDim; x++)
        {
            for (int y = 0; y < PointDim; y++)
            {
                StartPoints.add(new Point((double)StartImage.getWidth() / (PointDim - 1) * x, (double)StartImage.getHeight() / (PointDim - 1)  * y));
                EndPoints.add(new Point((double)EndImage.getWidth() / (PointDim - 1) * x, (double)EndImage.getHeight() / (PointDim - 1) * y));
            }
        }
    }

    public void SetTweenCount(int count) { TweenCount = count; }

    public void OnMousePressed(Point clickedPoint, ImageType image)
    {
        if (previewing) return;

        for (Point point : (image == ImageType.Start ? StartPoints : EndPoints))
        {
            // Check if point is within radius of point
            if (Math.abs(point.X - clickedPoint.X) <= Constants.PointRadius
             && Math.abs(point.Y - clickedPoint.Y) <= Constants.PointRadius)
            {
                // Return if point clicked is a border point - these are immovable
                int index = (image == ImageType.Start ? StartPoints : EndPoints).indexOf(point);
                if (index < PointDim || index >= PointDim * PointDim - PointDim || index % PointDim == 0 || index % PointDim == PointDim - 1) return;

                isMovingPoint = true;
                movedImage = image;
                movedPoint = point; // Save point to know which point is being moved

                List<Point> points = image == ImageType.Start ? StartPoints : EndPoints;
                int originIndex = points.indexOf(movedPoint);

                // Build constraint polygon - this prevents overlapping triangles, as the point must be in the poly
                constraintPoly = new Polygon();
                for (int[] offset : constraintOffsets)
                {
                    int xOff = offset[0];
                    int yOff = offset[1];

                    int offsetIndex = (originIndex + yOff) + (xOff * PointDim);
                    if (offsetIndex < 0 || offsetIndex > points.size()) continue;

                    int x = (int)points.get(offsetIndex).X;
                    int y = (int)points.get(offsetIndex).Y;

                    constraintPoly.addPoint(x, y);
                }

                break;
            }
        }
    }

    public void OnMouseDragged(Point point, ImageType image)
    {
        if (!isMovingPoint) return;
        if (!constraintPoly.contains(point.X, point.Y)) return;

        if (image == ImageType.Start) StartPoints.set(StartPoints.indexOf(movedPoint), point);
        else EndPoints.set(EndPoints.indexOf(movedPoint), point);

        movedPoint = point;
    }

    public void OnMouseReleased() { if (isMovingPoint) isMovingPoint = false; }

    public ImageIcon Render(ImageType image)
    {
        BufferedImage currentImage = image == ImageType.Start ? StartImage : EndImage;
        BufferedImage currentImageCopy = new BufferedImage(currentImage.getWidth(), currentImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        currentImageCopy.getGraphics().drawImage(currentImage, 0, 0, null);

        Graphics2D graphics = (Graphics2D)currentImageCopy.getGraphics();
        List<Point> points = previewing ? PreviewPoints : image == ImageType.Start ? StartPoints : EndPoints;

        graphics.setStroke(new BasicStroke(1));

        // Draw Grid
        graphics.setColor(Color.GRAY);
        for (int current = 0; current < points.size(); current++)
        {
            // next = next row value
            int next = current + 1;
            if (next < points.size() && current % PointDim < next % PointDim)
                graphics.drawLine((int)points.get(current).X, (int)points.get(current).Y, (int)points.get(next).X, (int)points.get(next).Y);

            // next = next column value
            next = current + PointDim;
            if (next < points.size())
                graphics.drawLine((int)points.get(current).X, (int)points.get(current).Y, (int)points.get(next).X, (int)points.get(next).Y);

            // next = next column value + 1
            next = current + PointDim + 1;
            if (next < points.size() && current % PointDim < next % PointDim)
                graphics.drawLine((int)points.get(current).X, (int)points.get(current).Y, (int)points.get(next).X, (int)points.get(next).Y);
        }

        // Draw points
        graphics.setColor(Color.BLACK);
        for (Point point : points)
        {
            // Make point the center of circle - so displace draw coords by radius of point
            // Width/Height of circle should be diameter of circle, so d = r*2
            if (isMovingPoint
             && movedImage != image
             && points.indexOf(point) == (image == ImageType.Start ? EndPoints.indexOf(movedPoint) : StartPoints.indexOf(movedPoint)))
            {
                // If moving point, making opposite image's corresponding point red
                graphics.setColor(Color.RED);
                graphics.fillOval((int)point.X - Constants.PointRadius, (int)point.Y - Constants.PointRadius, Constants.PointRadius * 2, Constants.PointRadius * 2);
                graphics.setColor(Color.BLACK);
            }
            else
            {
                graphics.fillOval((int)point.X - Constants.PointRadius, (int)point.Y - Constants.PointRadius, Constants.PointRadius * 2, Constants.PointRadius * 2);
            }
        }

        // Render() is called 2 times per update interval, so only update preview on end image updates
        if (previewing && image == ImageType.End)
        {
            RenderPreviewImages();
            Graphics2D g = previewImage.createGraphics();

            g.drawImage(previewEndImage, 0, 0, null);
            g.setComposite(startAlpha);
            g.drawImage(previewStartImage, 0, 0, null);

            previewFrame.UpdateImage(previewImage);
        }

        return new ImageIcon(currentImageCopy);
    }

    private void RenderPreviewImages()
    {
        // Generate new points' triangles
        List<Triangle> previewTriangles = new ArrayList<>();
        for (int i = 0; i < PointDim * PointDim; i += i % PointDim == PointDim - 2 ? 2 : 1)
        {
            if (i / PointDim == PointDim - 1) break;

            previewTriangles.add(new Triangle(PreviewPoints.get(i), PreviewPoints.get(i + PointDim), PreviewPoints.get(i + PointDim + 1)));
            previewTriangles.add(new Triangle(PreviewPoints.get(i), PreviewPoints.get(i + 1), PreviewPoints.get(i + PointDim + 1)));
        }

        for (int i = 0; i < startTriangles.size(); i++)
        {
            startMorpher.WarpTriangle(startTriangles.get(i), previewTriangles.get(i));
            endMorpher.WarpTriangle(endTriangles.get(i), previewTriangles.get(i));
        }

        float alpha = (float)tweenCounter / TweenCount;
        if (alpha > 1) alpha = 1;
        startAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1 - alpha);
    }

    public void Preview()
    {
        previewImage = new BufferedImage(Constants.ImageDisplaySize, Constants.ImageDisplaySize, TYPE_INT_ARGB);

        previewStartImage = new BufferedImage(Constants.ImageDisplaySize, Constants.ImageDisplaySize, TYPE_INT_ARGB);
        previewStartImage.getGraphics().drawImage(StartImage, 0, 0, null);

        previewEndImage = new BufferedImage(Constants.ImageDisplaySize, Constants.ImageDisplaySize, TYPE_INT_ARGB);
        previewEndImage.getGraphics().drawImage(EndImage, 0, 0, null);

        startMorpher = new Morpher(StartImage, previewStartImage);
        endMorpher = new Morpher(EndImage, previewEndImage);

        if (previewFrame != null) previewFrame.dispose();
        previewFrame = new PreviewFrame(StartImage);
        previewing = true;
        tweenCounter = 0;

        PreviewPoints.clear();
        PreviewPoints.addAll(StartPoints);

        // Generate start image's triangles
        startTriangles = new ArrayList<>();
        for (int i = 0; i < StartPoints.size(); i += i % PointDim == PointDim - 2 ? 2 : 1)
        {
            if (i / PointDim == PointDim - 1) break;

            startTriangles.add(new Triangle(StartPoints.get(i), StartPoints.get(i + PointDim), StartPoints.get(i + PointDim + 1)));
            startTriangles.add(new Triangle(StartPoints.get(i), StartPoints.get(i + 1), StartPoints.get(i + PointDim + 1)));
        }

        // Generate end image's triangles
        endTriangles = new ArrayList<>();
        for (int i = 0; i < EndPoints.size(); i += i % PointDim == PointDim - 2 ? 2 : 1)
        {
            if (i / PointDim == PointDim - 1) break;

            endTriangles.add(new Triangle(EndPoints.get(i), EndPoints.get(i + PointDim), EndPoints.get(i + PointDim + 1)));
            endTriangles.add(new Triangle(EndPoints.get(i), EndPoints.get(i + 1), EndPoints.get(i + PointDim + 1)));
        }

        previewTimer = new Timer(Constants.UpdateInterval, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (tweenCounter == TweenCount + 1)
                {
                    previewing = false;
                    previewTimer.stop();
                    return;
                }

                for (int i = 0; i < PreviewPoints.size(); i++)
                {
                    double xDelta = EndPoints.get(i).X - StartPoints.get(i).X;
                    double yDelta = EndPoints.get(i).Y - StartPoints.get(i).Y;

                    double x = PreviewPoints.get(i).X + xDelta / TweenCount;
                    double y = PreviewPoints.get(i).Y + yDelta / TweenCount;

                    PreviewPoints.set(i, new Point(x, y));
                }

                tweenCounter++;
            }
        });
        previewTimer.start();
    }
}