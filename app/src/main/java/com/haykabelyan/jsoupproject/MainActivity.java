package com.haykabelyan.jsoupproject;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button1);
        textView = (TextView) findViewById(R.id.textView1);
    }

    public void parsing(View v) {
        ParseTask parseTask = new ParseTask();
        parseTask.execute();
    }

    class ParseTask extends AsyncTask<Void, Void, Void> {
        String title;
        String body;

        @Override
        protected Void doInBackground(Void... params) {
            Document doc = null;//Здесь хранится будет разобранный html документ
            try {
                //Считываем заглавную страницу http://harrix.org
                doc = Jsoup.connect("https://en.wikipedia.org/wiki/World_War_II").get();
            } catch (IOException e) {
                //Если не получилось считать
                e.printStackTrace();
            }

            //Если всё считалось, что вытаскиваем из считанного html документа заголовок
            if (doc != null) {
                title = doc.title();
                body = doc.text();
            } else {
                title = "Error";
                body = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            textView.setText(title);
            if (body != null) {
                textView.setText(textView.getText().toString() + "\n");
                textView.setText(textView.getText().toString() + body);
            }
            //Тут выводим итоговые данные
        }
    }
}