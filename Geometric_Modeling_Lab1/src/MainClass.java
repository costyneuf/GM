import java.awt.Canvas;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.SystemColor;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class MainClass {

    private JFrame frame;
    private JTextField textField;

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
        this.frame = new JFrame();
        this.frame.setBounds(100, 100, 1264, 847);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.getContentPane().setLayout(null);

        JButton Tech_Bezier_Curve = new JButton("Bezier Curve");
        Tech_Bezier_Curve.setBounds(34, 57, 277, 25);
        this.frame.getContentPane().add(Tech_Bezier_Curve);

        JButton Tech_UniCubic_BSpline = new JButton("Uniform Cubic B-Spline");
        Tech_UniCubic_BSpline.setBounds(34, 116, 277, 25);
        this.frame.getContentPane().add(Tech_UniCubic_BSpline);

        JButton Tech_UniQua_BSpline = new JButton("Uniform Quadratic B-Spline");
        Tech_UniQua_BSpline.setBounds(34, 170, 277, 25);
        this.frame.getContentPane().add(Tech_UniQua_BSpline);

        JButton Tech_Closed_Quadratic_BSpline = new JButton(
                "Closed Uniform Quadratic B-Spline");
        Tech_Closed_Quadratic_BSpline.setBounds(34, 230, 277, 25);
        this.frame.getContentPane().add(Tech_Closed_Quadratic_BSpline);

        JButton Tech_Closed_Cubic_BSpline = new JButton(
                "Closed Uniform Cubic B-Spline");
        Tech_Closed_Cubic_BSpline.setBounds(34, 292, 277, 25);
        this.frame.getContentPane().add(Tech_Closed_Cubic_BSpline);

        JTextPane Text1 = new JTextPane();
        Text1.setBackground(UIManager.getColor("Button.background"));
        Text1.setText("Subdivision Iterations:");
        Text1.setBounds(34, 342, 144, 21);
        this.frame.getContentPane().add(Text1);

        this.textField = new JTextField();
        this.textField.setBounds(187, 338, 65, 55);
        this.frame.getContentPane().add(this.textField);
        this.textField.setColumns(10);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBounds(34, 368, 117, 25);
        this.frame.getContentPane().add(refreshButton);

        Canvas canvas = new Canvas();
        canvas.setBackground(
                UIManager.getColor("ComboBox.selectionForeground"));
        canvas.setBounds(364, 57, 869, 715);
        this.frame.getContentPane().add(canvas);

        JMenu editMenu = new JMenu("Edit");
        editMenu.setHorizontalAlignment(SwingConstants.LEFT);
        editMenu.setBounds(34, 424, 95, 19);
        this.frame.getContentPane().add(editMenu);

        JMenuItem edit_Add = new JMenuItem("Add Points");
        edit_Add.setSelected(true);
        editMenu.add(edit_Add);

        JMenuItem edit_Insert = new JMenuItem("Insert Before Selected");
        edit_Insert.setSelected(true);
        edit_Insert.setBackground(SystemColor.inactiveCaptionBorder);
        editMenu.add(edit_Insert);

        JMenuItem edit_Edit = new JMenuItem("New menu item");
        edit_Edit.setSelected(true);
        editMenu.add(edit_Edit);

        JMenu curveMenu = new JMenu("Curve");
        curveMenu.setHorizontalAlignment(SwingConstants.LEFT);
        curveMenu.setBounds(34, 491, 95, 19);
        this.frame.getContentPane().add(curveMenu);

        JMenuItem curve_Duplicate = new JMenuItem("Duplicate Selected Point");
        curveMenu.add(curve_Duplicate);

        JMenuItem curve_Delete = new JMenuItem("Delete Selected Point");
        curve_Delete.setBackground(Color.LIGHT_GRAY);
        curveMenu.add(curve_Delete);

        JMenuItem curve_Remove = new JMenuItem("Remove All");
        curveMenu.add(curve_Remove);
    }
}
