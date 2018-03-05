package Core;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.LinkedList;
import java.util.List;

import javax.media.j3d.Canvas3D;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.vecmath.Point3i;

import com.sun.j3d.utils.universe.SimpleUniverse;

import curve.Curve;
import curve.CurveType;
import curve.PointType;
import curve.PointsOperation;
import surface.CoordinateSystem;
import surface.Surface;
import surface.SurfaceOperation;
import surface.SurfaceType;
import surface.Triangle2D;

public class MainClass {

    private Curve curve = new Curve();
    private SurfaceOperation surfaceType;
    private ButtonGroup curveChoice = new ButtonGroup();
    private ButtonGroup pointChoice = new ButtonGroup();
    private ButtonGroup surfaceChoice = new ButtonGroup();
    private boolean outputASCII = false;
    private int numberOfSlices;

    public JFrame frmCseGeometric;
    public static double[] viewFrom = new double[3];
    public static double[] viewTo = new double[3];
    public static int numberOfTriangles = 0;
    public static Triangle2D[] drawableTriangles = new Triangle2D[10000];
    private JTextField fromX;
    private JTextField fromY;
    private JTextField fromZ;
    private JTextField toX;
    private JTextField toY;
    private JTextField toZ;

    /**
     * Create the application.
     */
    public MainClass() {
        this.initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        this.frmCseGeometric = new JFrame(
                "CSE 5543 Geometric Modeling - LAB 1");
        this.frmCseGeometric.setTitle("CSE 5543 Geometric Modeling - LAB 2");
        this.frmCseGeometric.setBounds(100, 100, 1264, 847);
        this.frmCseGeometric.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frmCseGeometric.getContentPane().setLayout(null);

        JTextPane txtpnViewFrom = new JTextPane();
        txtpnViewFrom.setText("View From:");
        txtpnViewFrom.setBounds(34, 655, 124, 23);
        this.frmCseGeometric.getContentPane().add(txtpnViewFrom);

        this.fromX = new JTextField();
        this.fromX.setText("10");
        this.fromX.setBounds(170, 655, 65, 21);
        this.frmCseGeometric.getContentPane().add(this.fromX);
        this.fromX.setColumns(10);

        this.fromY = new JTextField();
        this.fromY.setText("10");
        this.fromY.setColumns(10);
        this.fromY.setBounds(170, 680, 65, 21);
        this.frmCseGeometric.getContentPane().add(this.fromY);

        this.fromZ = new JTextField();
        this.fromZ.setText("10");
        this.fromZ.setColumns(10);
        this.fromZ.setBounds(170, 705, 65, 21);
        this.frmCseGeometric.getContentPane().add(this.fromZ);

        JTextPane txtpnViewTo = new JTextPane();
        txtpnViewTo.setEditable(false);
        txtpnViewTo.setText("View To:");
        txtpnViewTo.setBounds(34, 732, 124, 23);
        this.frmCseGeometric.getContentPane().add(txtpnViewTo);

        this.toX = new JTextField();
        this.toX.setText("5");
        this.toX.setColumns(10);
        this.toX.setBounds(170, 732, 65, 21);
        this.frmCseGeometric.getContentPane().add(this.toX);

        this.toY = new JTextField();
        this.toY.setText("0");
        this.toY.setColumns(10);
        this.toY.setBounds(170, 757, 65, 21);
        this.frmCseGeometric.getContentPane().add(this.toY);

        this.toZ = new JTextField();
        this.toZ.setText("0");
        this.toZ.setColumns(10);
        this.toZ.setBounds(170, 782, 65, 21);
        this.frmCseGeometric.getContentPane().add(this.toZ);

        /*
         * Set up a canvas where the points will be input and the curve will be
         * displayed.
         */
        Canvas canvas = new Canvas3D(
                SimpleUniverse.getPreferredConfiguration());

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent arg0) {
                if (MainClass.this.curve.pointType() == PointType.EDIT) {
                    MainClass.this.curve.editUpdate(
                            new Point3i(arg0.getX(), arg0.getY(), 0));

                    PointsOperation.updatePoints(
                            MainClass.this.curve.controlPoints(),
                            MainClass.this.curve.currentIndex(),
                            canvas.getWidth(), canvas.getHeight(),
                            canvas.getGraphics());
                }

                if (MainClass.this.curve.controlPoints().size() > 1) {
                    MainClass.this.curve.curveType().updateCurve(
                            MainClass.this.curve, canvas.getGraphics());
                }

            }
        });
        canvas.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent arg0) {

                MainClass.this.curve.updateIndexAndList(
                        new Point3i(arg0.getX(), arg0.getY(), 0));
                PointsOperation.updatePoints(
                        MainClass.this.curve.controlPoints(),
                        MainClass.this.curve.currentIndex(), canvas.getWidth(),
                        canvas.getHeight(), canvas.getGraphics());
                if (MainClass.this.curve.controlPoints().size() > 1) {
                    MainClass.this.curve.curveType().updateCurve(
                            MainClass.this.curve, canvas.getGraphics());
                }

            }

        });
        canvas.setBackground(
                UIManager.getColor("ComboBox.selectionForeground"));
        canvas.setBounds(364, 57, 425, 300);
        this.frmCseGeometric.getContentPane().add(canvas);

        JRadioButton add_point = new JRadioButton("Add points");
        add_point.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                PointType.setPointStatus(MainClass.this.curve, PointType.ADD);

            }
        });
        add_point.setSelected(true);
        add_point.setBounds(34, 380, 149, 23);
        this.frmCseGeometric.getContentPane().add(add_point);
        this.pointChoice.add(add_point);

        JRadioButton insert_point = new JRadioButton("Insert before selected");
        insert_point.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                PointType.setPointStatus(MainClass.this.curve,
                        PointType.INSERT);

            }
        });
        insert_point.setBounds(34, 400, 224, 23);
        this.frmCseGeometric.getContentPane().add(insert_point);
        this.pointChoice.add(insert_point);

        JRadioButton edit_point = new JRadioButton("Edit points");
        edit_point.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                PointType.setPointStatus(MainClass.this.curve, PointType.EDIT);

            }
        });
        edit_point.setBounds(34, 420, 149, 23);
        this.frmCseGeometric.getContentPane().add(edit_point);
        this.pointChoice.add(edit_point);

        JButton duplicate = new JButton("Duplicate at selected");
        duplicate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                MainClass.this.curve.duplicate();
                PointsOperation.updatePoints(
                        MainClass.this.curve.controlPoints(),
                        MainClass.this.curve.currentIndex(), canvas.getWidth(),
                        canvas.getHeight(), canvas.getGraphics());

                if (MainClass.this.curve.controlPoints().size() > 1) {
                    MainClass.this.curve.curveType().updateCurve(
                            MainClass.this.curve, canvas.getGraphics());
                }

            }
        });
        duplicate.setBounds(34, 480, 277, 25);
        this.frmCseGeometric.getContentPane().add(duplicate);

        JButton delete = new JButton("Delete selected");
        delete.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                MainClass.this.curve.delete();
                PointsOperation.updatePoints(
                        MainClass.this.curve.controlPoints(),
                        MainClass.this.curve.currentIndex(), canvas.getWidth(),
                        canvas.getHeight(), canvas.getGraphics());

                if (MainClass.this.curve.controlPoints().size() > 1) {
                    MainClass.this.curve.curveType().updateCurve(
                            MainClass.this.curve, canvas.getGraphics());
                }

            }
        });
        delete.setBounds(34, 510, 277, 25);
        this.frmCseGeometric.getContentPane().add(delete);

        JRadioButton uniform_cubic_bSpline = new JRadioButton(
                "Uniform Cubic B-Spline");
        uniform_cubic_bSpline.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                MainClass.this.curve.changeCurveStatus(CurveType.CUBICBSPLINE);

                PointsOperation.updatePoints(
                        MainClass.this.curve.controlPoints(),
                        MainClass.this.curve.currentIndex(), canvas.getWidth(),
                        canvas.getHeight(), canvas.getGraphics());
                if (MainClass.this.curve.controlPoints().size() > 1) {
                    MainClass.this.curve.curveType().updateCurve(
                            MainClass.this.curve, canvas.getGraphics());
                }

            }
        });
        uniform_cubic_bSpline.setBounds(34, 110, 324, 23);
        this.frmCseGeometric.getContentPane().add(uniform_cubic_bSpline);
        this.curveChoice.add(uniform_cubic_bSpline);

        JRadioButton bezier_curve = new JRadioButton("Bezier Curve");
        bezier_curve.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                MainClass.this.curve.changeCurveStatus(CurveType.BEZIER);

                PointsOperation.updatePoints(
                        MainClass.this.curve.controlPoints(),
                        MainClass.this.curve.currentIndex(), canvas.getWidth(),
                        canvas.getHeight(), canvas.getGraphics());
                if (MainClass.this.curve.controlPoints().size() > 1) {
                    MainClass.this.curve.curveType().updateCurve(
                            MainClass.this.curve, canvas.getGraphics());
                }

            }
        });
        bezier_curve.setSelected(true);
        bezier_curve.setBounds(34, 90, 324, 23);
        this.frmCseGeometric.getContentPane().add(bezier_curve);
        this.curveChoice.add(bezier_curve);

        /*
         * Set up a surface choice group
         */
        JRadioButton revolution = new JRadioButton("Surfaces of Revolution");
        revolution.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MainClass.this.surfaceType = SurfaceType.REVOLUTION;
            }
        });
        revolution.setEnabled(false);
        revolution.setBounds(34, 180, 324, 23);
        this.frmCseGeometric.getContentPane().add(revolution);
        this.surfaceChoice.add(revolution);

        JRadioButton extrusion = new JRadioButton("Extrusion");
        extrusion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MainClass.this.surfaceType = SurfaceType.EXTRUSION;
            }
        });
        extrusion.setEnabled(false);
        extrusion.setBounds(34, 280, 324, 23);
        this.frmCseGeometric.getContentPane().add(extrusion);
        this.surfaceChoice.add(extrusion);

        JRadioButton sweep = new JRadioButton("Sweep Operators");
        sweep.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MainClass.this.surfaceType = SurfaceType.SWEEP;
            }
        });
        sweep.setEnabled(false);
        sweep.setBounds(34, 300, 324, 23);
        this.frmCseGeometric.getContentPane().add(sweep);
        this.surfaceChoice.add(sweep);

        JCheckBox outputASCII = new JCheckBox("Output ASCII Vertex-Face File");
        outputASCII.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MainClass.this.outputASCII = !MainClass.this.outputASCII;
            }
        });
        outputASCII.setEnabled(false);
        outputASCII.setBounds(34, 320, 286, 25);
        this.frmCseGeometric.getContentPane().add(outputASCII);

        /*
         * Plain text
         */
        JTextPane txtpnNumberOfSlices = new JTextPane();
        txtpnNumberOfSlices
                .setBackground(UIManager.getColor("DesktopIcon.background"));
        txtpnNumberOfSlices.setFont(new Font("Dialog", Font.PLAIN, 11));
        txtpnNumberOfSlices.setText("Number of Slices");
        txtpnNumberOfSlices.setBounds(56, 212, 102, 40);
        this.frmCseGeometric.getContentPane().add(txtpnNumberOfSlices);

        /*
         * Slider for setting up number of slices
         */
        JSlider numberOfSlices = new JSlider();
        numberOfSlices.setFont(new Font("Dialog", Font.BOLD, 10));
        numberOfSlices.setPaintTicks(true);
        numberOfSlices.setPaintLabels(true);
        numberOfSlices.setMajorTickSpacing(20);
        numberOfSlices.setEnabled(false);
        numberOfSlices.setMinorTickSpacing(5);

        numberOfSlices.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent arg0) {
                MainClass.this.numberOfSlices = numberOfSlices.getValue();
            }
        });
        numberOfSlices.setValue(20);
        this.numberOfSlices = numberOfSlices.getValue();
        numberOfSlices.setBounds(170, 205, 140, 58);
        this.frmCseGeometric.getContentPane().add(numberOfSlices);

        /*
         * Clear button
         */
        JButton clear = new JButton("Clear");
        clear.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent arg0) {
                /*
                 * Clear linked list.
                 */
                MainClass.this.curve.clear();

                /*
                 * Clear canvas.
                 */

                PointsOperation.updatePoints(
                        MainClass.this.curve.controlPoints(), -1,
                        canvas.getWidth(), canvas.getHeight(),
                        canvas.getGraphics());

                /*
                 * Reset buttons.
                 */
                add_point.setSelected(true);
                bezier_curve.setSelected(true);

            }
        });
        clear.setBounds(34, 540, 277, 25);
        this.frmCseGeometric.getContentPane().add(clear);

        Canvas3D canvas3D = new Canvas3D(
                SimpleUniverse.getPreferredConfiguration());
        canvas3D.setBackground(UIManager.getColor("Button.foreground"));
        canvas3D.setEnabled(false);
        canvas3D.setBounds(364, 460, 850, 300);
        this.frmCseGeometric.getContentPane().add(canvas3D);

        /*
         * Confirm to launch canvas3D
         */
        JButton btnConfirm = new JButton("Confirm");
        btnConfirm.setEnabled(false);
        btnConfirm.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {

                //                viewFrom[0] = Double
                //                        .parseDouble(MainClass.this.fromX.getText());
                //                viewFrom[1] = Double
                //                        .parseDouble(MainClass.this.fromY.getText());
                //                viewFrom[2] = Double
                //                        .parseDouble(MainClass.this.fromZ.getText());
                //
                //                viewTo[0] = Double.parseDouble(MainClass.this.toX.getText());
                //                viewTo[1] = Double.parseDouble(MainClass.this.toY.getText());
                //                viewTo[2] = Double.parseDouble(MainClass.this.toZ.getText());

                /*
                 * Create surface
                 */
                if (MainClass.this.curve.controlPoints().size() > 1) {

                    Surface surface = new Surface(MainClass.this.curve,
                            MainClass.this.surfaceType,
                            MainClass.this.outputASCII);

                    /*
                     * Clear Canvas3D
                     */
                    canvas3D.getGraphics().clearRect(0, 0, canvas3D.getWidth(),
                            canvas3D.getHeight());

                    // Generate surface methods
                    if (surface.surfaceType() == SurfaceType.EXTRUSION) {
                        surface.surfaceType().updateCanvas3D(
                                MainClass.this.curve, 2,
                                canvas3D.getGraphics());
                    } else {
                        surface.surfaceType().updateCanvas3D(
                                MainClass.this.curve,
                                MainClass.this.numberOfSlices,
                                canvas3D.getGraphics());
                    }
                }

                // CoordinateSystem coord = new CoordinateSystem(viewFrom, viewTo);
                //                coord.drawCoordinateSystem(canvas3D.getGraphics());

            }
        });
        btnConfirm.setBounds(34, 350, 100, 24);
        this.frmCseGeometric.getContentPane().add(btnConfirm);

        /*
         * Generate curves or generate surfaces or solids
         */
        JButton generateCurves = new JButton("Generate Curves");
        generateCurves.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                /*
                 * Enable all curve choices
                 */
                add_point.setEnabled(true);
                insert_point.setEnabled(true);
                edit_point.setEnabled(true);
                duplicate.setEnabled(true);
                delete.setEnabled(true);
                uniform_cubic_bSpline.setEnabled(true);
                bezier_curve.setEnabled(true);

                /*
                 * Disable all surface or solid choices
                 */
                canvas3D.setEnabled(false);
                extrusion.setEnabled(false);
                sweep.setEnabled(false);
                revolution.setEnabled(false);
                numberOfSlices.setEnabled(false);
                outputASCII.setEnabled(false);
                btnConfirm.setEnabled(false);
            }
        });
        generateCurves.setBounds(34, 570, 277, 25);
        this.frmCseGeometric.getContentPane().add(generateCurves);

        JButton generateSurfacesSolids = new JButton(
                "Generate Surfaces or Solids");
        generateSurfacesSolids.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                /*
                 * Disable all curve choices
                 */
                add_point.setEnabled(false);
                insert_point.setEnabled(false);
                edit_point.setEnabled(false);
                duplicate.setEnabled(false);
                delete.setEnabled(false);
                uniform_cubic_bSpline.setEnabled(false);
                bezier_curve.setEnabled(false);

                /*
                 * Enable all surface or solid choices
                 */
                canvas3D.setEnabled(true);
                extrusion.setEnabled(true);
                sweep.setEnabled(true);
                revolution.setEnabled(true);
                numberOfSlices.setEnabled(true);
                outputASCII.setEnabled(true);
                btnConfirm.setEnabled(true);

                /*
                 * Back up original control points
                 */
                List<Point3i> backup = new LinkedList<>();
                backup.addAll(MainClass.this.curve.controlPoints());

                // Set up coordinate system
                CoordinateSystem.buildSystem(canvas3D.getGraphics(),
                        canvas3D.getWidth(), canvas3D.getHeight(),
                        MainClass.this.curve);

                /*
                 * Restore control points
                 */
                MainClass.this.curve.controlPoints().clear();
                MainClass.this.curve.controlPoints().addAll(backup);
            }

        });
        generateSurfacesSolids.setBounds(34, 600, 277, 25);
        this.frmCseGeometric.getContentPane().add(generateSurfacesSolids);

    }
}
