package com.alyoksi.lab3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public OkHttpClient client = new OkHttpClient();
    public Map<String, String> currency = new HashMap<>();

    public String cur_val1, cur_val2;

    public TextView title, val2;
    public EditText val1;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.textView);
        val1 = findViewById(R.id.editTextText3);
        val2 = findViewById(R.id.textView3);

        Spinner cur1 = (Spinner) findViewById(R.id.cur1);
        Spinner cur2 = (Spinner) findViewById(R.id.cur2);

        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.currency,
                android.R.layout.simple_spinner_item
        );
        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner.
        cur1.setAdapter(adapter);
        cur1.setOnItemSelectedListener(new fromSpinnerClass());

        // Apply the adapter to the spinner.
        cur2.setAdapter(adapter);
        cur2.setOnItemSelectedListener(new toSpinnerClass());

        currency.put("RUB", "1.0");
        get();
    }
    public void get(){
        Request request = new Request.Builder().url("https://www.cbr.ru/scripts/XML_daily.asp").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        try {
                            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                            DocumentBuilder db = dbf.newDocumentBuilder();
                            assert response.body() != null;
                            InputStream is = response.body().byteStream();
                            Document doc = db.parse(is);
                            doc.getDocumentElement().normalize();

                            NodeList nodes = doc.getElementsByTagName("Valute");
                            for (int i = 0; i < nodes.getLength(); i++) {
                                Node node = nodes.item(i);

                                if (node.getNodeType() == Node.ELEMENT_NODE) {
                                    Element element = (Element) node;
                                    currency.put(getValue("CharCode", element), getValue("Value", element));
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void convertCurrency(View v){
        String str_val1 = val1.getText().toString();
        if(str_val1.equals("")) return;

        //title.setText(String.valueOf(currency.get(cur_val2).replace(',','.')));
        float res =  floatFromString(str_val1)*
               floatFromString(currency.get(cur_val1)) / floatFromString(currency.get(cur_val2));
        val2.setText(String.valueOf(res));
    }

    private float floatFromString(String input){
        float cur1_cnt = 0.0f;
        try{
            cur1_cnt = Float.parseFloat(input.replace(',', '.'));
        } catch (Exception e){
            return cur1_cnt;
        }
        return cur1_cnt;
    }
    private static String getValue(String tag, Element element) {
        NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodes.item(0);
        return node.getNodeValue();
    }

    class fromSpinnerClass implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
            cur_val1 = parent.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    }

    class toSpinnerClass implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
            cur_val2 = parent.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    }

}