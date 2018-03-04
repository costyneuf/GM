
import java.awt.Canvas;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;

import org.eclipse.swt.graphics.Point;

import curve.Curve;
import curve.CurveType;
import curve.PointType;
import curve.PointsOperation;

public class MainClass {

    private Curve curve = new Curve();
    protected JFrame frmCseGeometric;
    private JTextField textField;
    private ButtonGroup curveChoice = new ButtonGroup();
    private ButtonGroup pointChoice = new ButtonGroup();
    private ButtonGroup surfaceChoice = new ButtonGroup();

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

        JTextPane Text1 = new JTextPane();
        Text1.setBackground(UIManager.getColor("Button.background"));
        Text1.setText("Subdivision Iterations:");
        Text1.setBounds(34, 345, 144, 21);
        this.frmCseGeometric.getContentPane().add(Text1);

        this.textField = new JTextField();
        this.textField.setText("4");
        this.textField.setToolTipText("");
        this.textField.setBounds(180, 345, 40, 25);
        this.frmCseGeometric.getContentPane().add(this.textField);
        this.textField.setColumns(10);

        /*
         * Set up a canvas where the points will be input and the curve will be
         * displayed.
         */
        Canvas canvas = new Canvas();

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent arg0) {
                if (MainClass.this.curve.pointType() == PointType.EDIT) {
                    MainClass.this.curve
                            .editUpdate(new Point(arg0.getX(), arg0.getY()));

                    PointsOperation.updatePoints(canvas.getGraphics(),
                            MainClass.this.curve.controlPoints(),
                            MainClass.this.curve.currentIndex(),
                            canvas.getWidth(), canvas.getHeight());
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
                        new Point(arg0.getX(), arg0.getY()));
                PointsOperation.updatePoints(canvas.getGraphics(),
                        MainClass.this.curve.controlPoints(),
                        MainClass.this.curve.currentIndex(), canvas.getWidth(),
                        canvas.getHeight());
                if (MainClass.this.curve.controlPoints().size() > 1) {
                    MainClass.this.curve.curveType().updateCurve(
                            MainClass.this.curve, canvas.getGraphics());
                }

            }

        });
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    MainClass.this.curve.changeSubdivision(Integer
                            .parseInt(MainClass.this.textField.getText()));

                    if (MainClass.this.curve
                            .curveType() == CurveType.DECASTELJAU) {

                        PointsOperation.updatePoints(canvas.getGraphics(),
                                MainClass.this.curve.controlPoints(),
                                MainClass.this.curve.currentIndex(),
                                canvas.getWidth(), canvas.getHeight());

                        MainClass.this.curve.curveType().updateCurve(
                                MainClass.this.curve, canvas.getGraphics());

                    }

                } catch (NumberFormatException e1) {

                } catch (NullPointerException e2) {

                }
            }
        });
        refreshButton.setBounds(225, 345, 95, 25);
        this.frmCseGeometric.getContentPane().add(refreshButton);
        canvas.setBackground(
                UIManager.getColor("ComboBox.selectionForeground"));
        canvas.setBounds(364, 57, 869, 715);
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
                PointsOperation.updatePoints(canvas.getGraphics(),
                        MainClass.this.curve.controlPoints(),
                        MainClass.this.curve.currentIndex(), canvas.getWidth(),
                        canvas.getHeight());

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
                PointsOperation.updatePoints(canvas.getGraphics(),
                        MainClass.this.curve.controlPoints(),
                        MainClass.this.curve.currentIndex(), canvas.getWidth(),
                        canvas.getHeight());

                if (MainClass.this.curve.controlPoints().size() > 1) {
                    MainClass.this.curve.curveType().updateCurve(
                            MainClass.this.curve, canvas.getGraphics());
                }

            }
        });
        delete.setBounds(34, 510, 277, 25);
        this.frmCseGeometric.getContentPane().add(delete);

        JRadioButton uniform_sub_quadric_bSpline = new JRadioButton(
                "Uniform Subdivision Quadric B-Spline");
        uniform_sub_quadric_bSpline.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                MainClass.this.curve
                        .changeCurveStatus(CurveType.QUADRICBSPLINE);

                PointsOperation.updatePoints(canvas.getGraphics(),
                        MainClass.this.curve.controlPoints(),
                        MainClass.this.curve.currentIndex(), canvas.getWidth(),
                        canvas.getHeight());
                if (MainClass.this.curve.controlPoints().size() > 1) {
                    MainClass.this.curve.curveType().updateCurve(
                            MainClass.this.curve, canvas.getGraphics());
                }

            }
        });

        uniform_sub_quadric_bSpline.setBounds(34, 150, 324, 23);
        this.frmCseGeometric.getContentPane().add(uniform_sub_quadric_bSpline);
        this.curveChoice.add(uniform_sub_quadric_bSpline);

        JRadioButton deCasteljau_sub_curve = new JRadioButton(
                "de Casteljau Subdivision Curve");
        deCasteljau_sub_curve.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                MainClass.this.curve.changeCurveStatus(CurveType.DECASTELJAU);

                PointsOperation.updatePoints(canvas.getGraphics(),
                        MainClass.this.curve.controlPoints(),
                        MainClass.this.curve.currentIndex(), canvas.getWidth(),
                        canvas.getHeight());
                if (MainClass.this.curve.controlPoints().size() > 1) {
                    MainClass.this.curve.curveType().updateCurve(
                            MainClass.this.curve, canvas.getGraphics());
                }

            }
        });
        deCasteljau_sub_curve.setBounds(34, 130, 324, 23);
        this.frmCseGeometric.getContentPane().add(deCasteljau_sub_curve);
        this.curveChoice.add(deCasteljau_sub_curve);

        JRadioButton uniform_cubic_bSpline = new JRadioButton(
                "Uniform Cubic B-Spline");
        uniform_cubic_bSpline.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                MainClass.this.curve.changeCurveStatus(CurveType.CUBICBSPLINE);

                PointsOperation.updatePoints(canvas.getGraphics(),
                        MainClass.this.curve.controlPoints(),
                        MainClass.this.curve.currentIndex(), canvas.getWidth(),
                        canvas.getHeight());
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

                PointsOperation.updatePoints(canvas.getGraphics(),
                        MainClass.this.curve.controlPoints(),
                        MainClass.this.curve.currentIndex(), canvas.getWidth(),
                        canvas.getHeight());
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
        revolution.setEnabled(false);
        revolution.setBounds(34, 180, 324, 23);
        this.frmCseGeometric.getContentPane().add(revolution);
        this.surfaceChoice.add(revolution);

        JRadioButton extrusion = new JRadioButton("Extrusion");
        extrusion.setEnabled(false);
        extrusion.setBounds(34, 240, 324, 23);
        this.frmCseGeometric.getContentPane().add(extrusion);
        this.surfaceChoice.add(extrusion);

        JRadioButton sweep = new JRadioButton("Sweep Operators");
        sweep.setEnabled(false);
        sweep.setBounds(34, 260, 324, 23);
        this.frmCseGeometric.getContentPane().add(sweep);
        this.surfaceChoice.add(sweep);

        JCheckBox outputASCII = new JCheckBox("Output ASCII Vertex-Face File");
        outputASCII.setEnabled(false);
        outputASCII.setBounds(34, 298, 286, 25);
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
        numberOfSlices.setValue(20);
        numberOfSlices.setEnabled(false);
        numberOfSlices.setBounds(170, 205, 140, 40);
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
                PointsOperation.updatePoints(canvas.getGraphics(),
                        MainClass.this.curve.controlPoints(), -1,
                        canvas.getWidth(), canvas.getHeight());

                /*
                 * Reset buttons.
                 */
                add_point.setSelected(true);
                bezier_curve.setSelected(true);

            }
        });
        clear.setBounds(34, 540, 277, 25);
        this.frmCseGeometric.getContentPane().add(clear);

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
                MainClass.this.textField.setEnabled(true);
                add_point.setEnabled(true);
                insert_point.setEnabled(true);
                edit_point.setEnabled(true);
                duplicate.setEnabled(true);
                delete.setEnabled(true);
                uniform_sub_quadric_bSpline.setEnabled(true);
                deCasteljau_sub_curve.setEnabled(true);
                uniform_cubic_bSpline.setEnabled(true);
                bezier_curve.setEnabled(true);
                refreshButton.setEnabled(true);

                /*
                 * Disable all surface or solid choices
                 */

                extrusion.setEnabled(false);
                sweep.setEnabled(false);
                revolution.setEnabled(false);
                numberOfSlices.setEnabled(false);
                outputASCII.setEnabled(false);
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
                MainClass.this.textField.setEnabled(false);
                add_point.setEnabled(false);
                insert_point.setEnabled(false);
                edit_point.setEnabled(false);
                duplicate.setEnabled(false);
                delete.setEnabled(false);
                uniform_sub_quadric_bSpline.setEnabled(false);
                deCasteljau_sub_curve.setEnabled(false);
                uniform_cubic_bSpline.setEnabled(false);
                bezier_curve.setEnabled(false);
                refreshButton.setEnabled(false);

                /*
                 * Enable all surface or solid choices
                 */

                extrusion.setEnabled(true);
                sweep.setEnabled(true);
                revolution.setEnabled(true);
                numberOfSlices.setEnabled(true);
                outputASCII.setEnabled(true);

            }
        });
        generateSurfacesSolids.setBounds(34, 600, 277, 25);
        this.frmCseGeometric.getContentPane().add(generateSurfacesSolids);
    }
}
