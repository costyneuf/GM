
import java.awt.Canvas;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
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
    private JFrame frame;
    private JTextField textField;
    private ButtonGroup curveChoice = new ButtonGroup();
    private ButtonGroup pointChoice = new ButtonGroup();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    MainClass window = new MainClass();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

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
        this.frame = new JFrame("CSE 5543 Geometric Modeling - LAB 1");
        this.frame.setBounds(100, 100, 1264, 847);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.getContentPane().setLayout(null);

        JTextPane Text1 = new JTextPane();
        Text1.setBackground(UIManager.getColor("Button.background"));
        Text1.setText("Subdivision Iterations:");
        Text1.setBounds(34, 345, 144, 21);
        this.frame.getContentPane().add(Text1);

        this.textField = new JTextField();
        this.textField.setText("4");
        this.textField.setToolTipText("");
        this.textField.setBounds(180, 345, 40, 25);
        this.frame.getContentPane().add(this.textField);
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
        canvas.setBackground(
                UIManager.getColor("ComboBox.selectionForeground"));
        canvas.setBounds(364, 57, 869, 715);
        this.frame.getContentPane().add(canvas);

        JRadioButton add_point = new JRadioButton("Add points");
        add_point.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                PointType.setPointStatus(MainClass.this.curve, PointType.ADD);

            }
        });
        add_point.setSelected(true);
        add_point.setBounds(34, 380, 149, 23);
        this.frame.getContentPane().add(add_point);
        this.pointChoice.add(add_point);

        JRadioButton insert_point = new JRadioButton("Insert before selected");
        insert_point.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                PointType.setPointStatus(MainClass.this.curve,
                        PointType.INSERT);

            }
        });
        insert_point.setBounds(34, 410, 224, 23);
        this.frame.getContentPane().add(insert_point);
        this.pointChoice.add(insert_point);

        JRadioButton edit_point = new JRadioButton("Edit points");
        edit_point.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                PointType.setPointStatus(MainClass.this.curve, PointType.EDIT);

            }
        });
        edit_point.setBounds(34, 440, 149, 23);
        this.frame.getContentPane().add(edit_point);
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
        this.frame.getContentPane().add(duplicate);

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
        delete.setBounds(34, 520, 277, 25);
        this.frame.getContentPane().add(delete);

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
        uniform_sub_quadric_bSpline.setBounds(34, 270, 324, 23);
        this.frame.getContentPane().add(uniform_sub_quadric_bSpline);
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
        deCasteljau_sub_curve.setBounds(34, 210, 324, 23);
        this.frame.getContentPane().add(deCasteljau_sub_curve);
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
        uniform_cubic_bSpline.setBounds(34, 150, 324, 23);
        this.frame.getContentPane().add(uniform_cubic_bSpline);
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
        this.frame.getContentPane().add(bezier_curve);
        this.curveChoice.add(bezier_curve);

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
        clear.setBounds(34, 560, 277, 25);
        this.frame.getContentPane().add(clear);

        /*
         * Set up a refresh button. The button will parse the input in the
         * message box to the curve.
         */
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
        this.frame.getContentPane().add(refreshButton);

    }
}
