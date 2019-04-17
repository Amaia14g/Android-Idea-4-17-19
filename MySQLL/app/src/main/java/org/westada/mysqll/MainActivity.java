package org.westada.mysqll;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    itemAdapter itemAdapter;
    Context thisContext;
    ListView myListView;
    TextView progressTextView;
    Map<String, Double> fruitsMap = new LinkedHashMap<String, Double>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources res = getResources();
        myListView = (ListView) findViewById(R.id.myListView);
        progressTextView = (TextView) findViewById(R.id.progressTextView);
        thisContext = this;

        progressTextView.setText("");
        Button btn = (Button) findViewById(R.id.getDataButton);
        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                GetData retrieveData = new GetData();
                retrieveData.execute("");

            }
        });
    }
    private class GetData extends AsyncTask<String, String, String> {

        String msg = "";
        static final String JDBC_DRIVER = "10.109.7.20";
        static final String DB_URL = "" +
                DBStrings.DATABASE_URL + "/" +
                DBStrings.DATABASE_NAME;

        protected void onPreExecute() {
            progressTextView.setText("Connecting to database...");
        }

        @Override
        protected String doInBackground(String... strings) {
            Connection conn = null;
            Statement stat = null;

            try{
                Class.forName(JDBC_DRIVER);
                conn = DriverManager.getConnection(DB_URL, DBStrings.USERNAME, DBStrings.PASSWORD);

                stat = ((Connection) conn).createStatement();
                String sql = "SELECT + FROM  fruits";
                ResultSet rs = ((Statement) stat).executeQuery(sql);

                while (rs.next()) {
                    String name = rs.getString("name");
                    double price = rs.getDouble("price");

                    fruitsMap.put(name, price);
                }

                msg = "Process complete.";

                rs.close();
                stat.close();
                conn.close();

            }catch (SQLException connError){
                msg = "An exception was thrown for JDBC.";
                connError.printStackTrace();
            }catch (ClassNotFoundException e) {
                msg = "A class not found exception was thrown.";
                e.printStackTrace();
            }finally {

                try {
                    if (stat != null) {
                        stat.close();
                    }
                }catch (SQLException e) {
                    e.printStackTrace();

                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }catch (SQLException e) {
                    e.printStackTrace();

                }
            }
            return null;
        }
        protected void onPostExecute(String msg) {

            progressTextView.setText(this.msg);

            if(fruitsMap.size() > 0) {

                itemAdapter = new itemAdapter(thisContext, fruitsMap);
                myListView.setAdapter(itemAdapter);
            }
        }
    }

}// END of activity
