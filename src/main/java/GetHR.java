import java.sql.*;

public class GetHR {
    GetHR hr = new GetHR();



        public GetHR() throws SQLException {


            String dbUrl = "jdbc:postgresql://localhost:5432/postgres";
            try {
                Class.forName("org.postgresql.Driver");

            } catch (Exception e) {
            }

            Connection conn = DriverManager.getConnection(dbUrl, "postgres", "rules;eyes");

            try {
                Statement s = conn.createStatement();
                String sqlStr = "SELECT * FROM hr;";
                ResultSet rset = s.executeQuery(sqlStr);
                while (rset.next()) {
                    System.out.println("Heart Rate:" + " " + rset.getString(""));
                }
                rset.close();
                s.close();
                conn.close();
            } catch (Exception e) {

            }

        }
    }

