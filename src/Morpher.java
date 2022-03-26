import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

public class Morpher
{
    private final BufferedImage StartImage;
    private final BufferedImage EndImage;

    public Morpher(BufferedImage start, BufferedImage end)
    {
        StartImage = start;
        EndImage = end;
    }

    public void WarpTriangle(Triangle start, Triangle end)
    {
        double[][] a = new double[3][3];

        for (int i = 0; i < 3; i++)
        {
            a[i][0] = start.GetX(i);
            a[i][1] = start.GetY(i);
            a[i][2] = 1.0;
        }

        int[] l = new int[3];
        Gauss(a, l);

        double[] b = new double[3];
        for (int i = 0; i < 3; i++) b[i] = end.GetX(i);

        double[] x = new double[3];
        Solve(a, l, b, x);

        double[] by = new double[3];
        for (int i = 0; i < 3; i++) by[i] = end.GetY(i);

        double[] y = new double[3];
        Solve(a, l, by, y);

        AffineTransform transform = new AffineTransform(x[0], y[0], x[1], y[1], x[2], y[2]);
        GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);

        path.moveTo((float)end.GetX(0), (float)end.GetY(0));
        path.lineTo((float)end.GetX(1), (float)end.GetY(1));
        path.lineTo((float)end.GetX(2), (float)end.GetY(2));
        path.lineTo((float)end.GetX(0), (float)end.GetY(0));
        Graphics2D graphics = EndImage.createGraphics();

        AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)1);
        graphics.setComposite(alpha);

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setClip(path);
        graphics.setTransform(transform);
        graphics.drawImage(StartImage, 0, 0, null);
    }

    private void Gauss(double[][] a, int[] l)
    {
        double[] s = new double[a.length];
        double r, rmax, smax, xmult;
        int j = 0;

        for (int i = 0; i < a.length; i++)
        {
            l[i] = i;
            smax = 0;

            for (j = 0; j < a.length; j++) smax = Math.max(smax, Math.abs(a[i][j]));
            s[i] = smax;
        }

        for (int k = 0; k < (a.length - 1); k++)
        {
            j--;
            rmax = 0;

            for (int i = k; i < a.length; i++)
            {
                r = Math.abs(a[l[i]][k] / s[l[i]]);
                if (r > rmax)
                {
                    rmax = r;
                    j = i;
                }
            }

            int temp = l[j];
            l[j] = l[k];
            l[k] = temp;

            for (int i = k + 1; i < a.length; i++)
            {
                xmult = a[l[i]][k] / a[l[k]][k];
                a[l[i]][k] = xmult;
                for (j = k + 1; j < a.length; j++)
                    a[l[i]][j] = a[l[i]][j] - xmult * a[l[k]][j];
            }
        }
    }

    private void Solve(double[][] a, int[] l, double[] b, double[] x)
    {
        double sum;

        for (int k = 0; k < (a.length - 1); k++)
        for (int i = k + 1; i < a.length; i++)
            b[l[i]] -= a[l[i]][k] * b[l[k]];

        x[a.length - 1] = b[l[a.length - 1]] / a[l[a.length - 1]][a.length - 1];

        for (int i = a.length - 2; i >= 0; i--)
        {
            sum = b[l[i]];
            for (int j = i + 1; j < a.length; j++) sum = sum - a[l[i]][j] * x[j];
            x[i] = sum / a[l[i]][i];
        }
    }
}