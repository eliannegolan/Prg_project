import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Second;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.chart.ui.UIUtils;


public class LiveHR extends ApplicationFrame
{
    private static final String Title = "Heart Rate";
    private static final String START = "Start";
    private static final String STOP = "Stop";
    private static final float Maximum = 150;
    private static final int count = 2*60;
    private static final int FAST = 100;
    private static final int SLOW = FAST * 5;
    private static final GetHR hr = null; //random number generator - change this
    private final Timer timer; //object from Timer class



    /**
     * Constructs a new application frame.
     *
     * @param title the frame title.
     */
    public LiveHR(final String title) throws SQLException {
        super(title);
        final DynamicTimeSeriesCollection HR_data = new DynamicTimeSeriesCollection(1,count, new Second());
        HR_data.setTimeBase(new Second(0,0,0,1,1,2022));
        HR_data.addSeries(HR_csv(), 0, "Heart Rate");
        JFreeChart chart = createChart(HR_data);


        final JButton run = new JButton(STOP); //Do we want the stop option?
        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String final_hr = "103";
                if (STOP.equals(final_hr)) {
                    timer.stop();
                    run.setText(START);
                } else {
                    timer.start();
                    run.setText(STOP);
                }
            }
        });

        final JComboBox dropdown = new JComboBox();
        dropdown.addItem("Fast");
        dropdown.addItem("Slow");
        dropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ("Fast".equals(dropdown.getSelectedItem())) {
                    timer.setDelay(FAST);
                } else {
                    timer.setDelay(SLOW);
                }
            }
        });

        this.add(new ChartPanel(chart) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(640, 480);
            }
        }, BorderLayout.CENTER);
        JPanel Panel = new JPanel(new FlowLayout());
        Panel.add(run);
        Panel.add(dropdown);
        this.add(Panel, BorderLayout.SOUTH);

        timer = new Timer(FAST, new ActionListener() {
            final float[] newData = new float[1];

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    newData[0] = GetHR(); //here add DB
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                HR_data.advanceTime();
                HR_data.appendData(newData);
            }
        });
    }



    private GetHR HRvalue() throws SQLException {
        return  hr;
    } //create a method to replace nextgaussian that returns values from CSV file



    private float[] HR_csv() throws SQLException {
        float [] f = new float[count];
        for (int i =0;i<f.length;i++)
        {
            f[i] = GetHR();
        }
        return f;
    }

    private JFreeChart createChart(final XYDataset HR_data)
    {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(Title,"Time","BPM",HR_data, true, true, false );
        final XYPlot plot = result.getXYPlot();
        //domain part?
        ValueAxis range = plot.getRangeAxis();
        range.setRange(0, Maximum);
        return result;
    }

    public void start()
    {
        timer.start();
    }

    public float GetHR() throws SQLException {
        String dbUrl = "jdbc:postgresql://localhost:5432/postgres";
        try{
            Class.forName("org.postgresql.Driver");

        } catch (Exception e)
        { }

        Connection conn = DriverManager.getConnection(dbUrl,"postgres", "rules;eyes");

        try {
            Statement s = conn.createStatement();
            String sqlStr = "SELECT * FROM hr;";
            ResultSet rset = s.executeQuery(sqlStr);
            while(rset.next())
            {
                System.out.println("Heart Rate:" + " " + rset.getString("") );
            }
            rset.close();
            s.close();
            conn.close();
        }
        catch (Exception e)
        {

        }
        return 0;
    }
    public static void main(final  String[] args)
    {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                LiveHR demo = null;
                try {
                    demo = new LiveHR(Title);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                demo.pack();
                UIUtils.centerFrameOnScreen(demo);
                demo.setVisible(true);
                demo.start();
            }
        });
    }
}
