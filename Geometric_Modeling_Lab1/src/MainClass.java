import java.awt.Canvas;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;

import org.eclipse.swt.graphics.Point;

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

                } catch (NumberFormatException e1) {

                } catch (NullPointerException e2) {

                }
            }
        });
        refreshButton.setBounds(225, 345, 95, 25);
        this.frame.getContentPane().add(refreshButton);

        /*
         * Set up a canvas where the points will be input and the curve will be
         * displayed.
         */
        Canvas canvas = new Canvas();
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {

                // Add a point into the linked list.
                PointsOperation.addPoints(MainClass.this.curve,
                        new Point(arg0.getX(), arg0.getY()),
                        canvas.getGraphics());

            }

        });
        canvas.setBackground(
                UIManager.getColor("ComboBox.selectionForeground"));
        canvas.setBounds(364, 57, 869, 715);
        this.frame.getContentPane().add(canvas);

        JRadioButton add_point = new JRadioButton("Add points");
        add_point.setSelected(true);
        add_point.setBounds(34, 380, 149, 23);
        this.frame.getContentPane().add(add_point);

        JRadioButton insert_point = new JRadioButton("Insert before selected");
        insert_point.setBounds(34, 410, 224, 23);
        this.frame.getContentPane().add(insert_point);

        JRadioButton edit_point = new JRadioButton("Edit points");
        edit_point.setBounds(34, 440, 149, 23);
        this.frame.getContentPane().add(edit_point);

        JButton duplicate = new JButton("Duplicate at selected");
        duplicate.setBounds(34, 480, 277, 25);
        this.frame.getContentPane().add(duplicate);

        JButton delete = new JButton("Delete selected");
        delete.setBounds(34, 520, 277, 25);
        this.frame.getContentPane().add(delete);

        JButton clear = new JButton("Clear");
        clear.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                MainClass.this.curve.clear();
                Graphics2D g2 = (Graphics2D) canvas.getGraphics();
                g2.setColor(UIManager.getColor("ComboBox.selectionForeground"));
                g2.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            }
        });
        clear.setBounds(34, 560, 277, 25);
        this.frame.getContentPane().add(clear);

        JRadioButton uniform_sub_quadric_bSpline = new JRadioButton(
                "Uniform Subdivision Quadric B-Spline");
        uniform_sub_quadric_bSpline.setBounds(34, 270, 324, 23);
        this.frame.getContentPane().add(uniform_sub_quadric_bSpline);

        JRadioButton deCasteljau_sub_curve = new JRadioButton(
                "de Casteljau Subdivision Curve");
        deCasteljau_sub_curve.setBounds(34, 210, 324, 23);
        this.frame.getContentPane().add(deCasteljau_sub_curve);

        JRadioButton uniform_cubic_bSpline = new JRadioButton(
                "Uniform Cubic B-Spline");
        uniform_cubic_bSpline.setBounds(34, 150, 324, 23);
        this.frame.getContentPane().add(uniform_cubic_bSpline);

        JRadioButton bezier_curve = new JRadioButton("Bezier Curve");
        bezier_curve.setSelected(true);
        bezier_curve.setBounds(34, 90, 324, 23);
        this.frame.getContentPane().add(bezier_curve);
    }
}
