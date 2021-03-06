package com.haykabelyan.jsoupproject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textView;
    Button parse, squad;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parse = (Button) findViewById(R.id.button1);
        squad = (Button) findViewById(R.id.button2);
        textView = (TextView) findViewById(R.id.textView1);
        parse.setOnClickListener(this);
        squad.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
                new ParseTask(true).execute();
                break;
            case R.id.button2:
                new ParseTask(false).execute();
                break;
        }
    }

    class ParseTask extends AsyncTask<Void, Void, Void> {
        String title, body, teamSquad = "";
        ArrayList<FootballPlayer> squadPlayers;
        int numberOfPlayers, numberOfGoalkeepers, numberOfDefenders, numberOfMidfielders, numberOfForwards;
        boolean squad;
        boolean hasSquad;

        public ParseTask(boolean squad) {
            this.squad = squad;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Document doc = null;//Здесь хранится будет разобранный html документ
            try {
                doc = Jsoup.connect("https://en.m.wikipedia.org/wiki/Juventus").
                        userAgent("Opera/12.02 (Android 4.1; Linux; Opera Mobi/ADR-1111101157; U; en-US) Presto/2.9.201 Version/12.02").get();
                String temp = doc.html().replace("<tr class=\"vcard agent\">", "asdfghhgfdsa").replace("</table>", "asdfghhgfdsa");
                doc = Jsoup.parse(temp); //Parse again
            } catch (IOException e) {
                //Если не получилось считать
                e.printStackTrace();
            }

            //Если всё считалось, что вытаскиваем из считанного html документа заголовок
            if (doc != null) {
                title = doc.title();
//                body = doc.text();
                body = doc.body().text().replace("asdfghhgfdsa", "\n");
                squadPlayers = new ArrayList<>(99);
                if (body.contains("No. Position Player"))
                    hasSquad = true;
                for (int i = 1; i < 100; i++) {
                    if (body.contains(" " + i + " GK ")) {
                        numberOfGoalkeepers++;
                        numberOfPlayers++;
                        squadPlayers.add(new FootballPlayer(i + "", "GK", "g"));
                    }
                    if (body.contains(" " + i + " DF ")) {
                        numberOfDefenders++;
                        numberOfPlayers++;
                        squadPlayers.add(new FootballPlayer(i + "", "DF", "d"));
                    }
                    if (body.contains(" " + i + " MF ")) {
                        numberOfMidfielders++;
                        numberOfPlayers++;
                        squadPlayers.add(new FootballPlayer(i + "", "MF", "m"));
                    }
                    if (body.contains(" " + i + " FW ")) {
                        numberOfForwards++;
                        numberOfPlayers++;
                        squadPlayers.add(new FootballPlayer(i + "", "FW", "f"));
                    }
                }
                if (squadPlayers.size() > 0)
                    for (int i = 0; i < squadPlayers.size() - 1; i++) {
                        String playerName = body.substring((4 + (Integer.parseInt(squadPlayers.get(i).getNumber()) < 10 ? 1 : 2))
                                        + body.indexOf(" " + squadPlayers.get(i).getNumber() + " " + squadPlayers.get(i).getPosition()),
                                nearestNewLineIndex(body, " " + squadPlayers.get(i).getNumber()
                                        + " " + squadPlayers.get(i).getPosition())).trim();
                        if (playerName.contains("(") && playerName.contains(")") &&
                                playerName.indexOf("(") < playerName.indexOf(")"))
                            playerName = playerName.
                                    replace(playerName.substring(playerName.indexOf("("), playerName.indexOf(")") + 1), "");
                        if (playerName.contains("[") && playerName.contains("]") &&
                                playerName.indexOf("[") < playerName.indexOf("]"))
                            playerName = playerName.
                                    replace(playerName.substring(playerName.indexOf("["), playerName.indexOf("]") + 1), "");
                        squadPlayers.get(i).setFullName(playerName);
                    }
                if (squadPlayers.size() > 1) {
                    String playerName = body.substring((4 + (Integer.parseInt(squadPlayers.get(squadPlayers.size() - 1)
                                    .getNumber()) < 10 ? 1 : 2)) + body.indexOf(" " + squadPlayers.get(squadPlayers.size() - 1)
                                    .getNumber() + " " + squadPlayers.get(squadPlayers.size() - 1).getPosition()),
                            nearestNewLineIndex(body, " " + squadPlayers.get(squadPlayers.size() - 1).getNumber() +
                                    " " + squadPlayers.get(squadPlayers.size() - 1).getPosition())).trim();
                    if (playerName.contains("(") && playerName.contains(")") &&
                            playerName.indexOf("(") < playerName.indexOf(")"))
                        playerName = playerName.
                                replace(playerName.substring(playerName.indexOf("("), playerName.indexOf(")") + 1), "");
                    if (playerName.contains("[") && playerName.contains("]") &&
                            playerName.indexOf("[") < playerName.indexOf("]"))
                        playerName = playerName.
                                replace(playerName.substring(playerName.indexOf("["), playerName.indexOf("]") + 1), "");
                    squadPlayers.get(squadPlayers.size() - 1).setFullName(playerName);
                }
            } else {
                title = "Error";
                body = "";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            if (title.length() > 12)
                title = title.substring(0, title.length() - 12);
            textView.setText(title);
            if (body != null) {
                textView.setText(textView.getText().toString() + "\n \n");
                if (squad)
                    textView.setText(textView.getText().toString() + body);
                else {
                    if (hasSquad) {
                        textView.setText(textView.getText().toString() + "Current squad \n\n");
                        textView.setText(textView.getText().toString() + "The team contains " +
                                numberOfPlayers + " players (" + numberOfGoalkeepers +
                                " goalkeepers, " + numberOfDefenders + " defenders, " +
                                numberOfMidfielders + " midfielders, " + numberOfForwards +
                                " forwards). \n");
                        for (int i = 0; i < numberOfPlayers; i++)
                            teamSquad = teamSquad + squadPlayers.get(i) + "\n";
                        teamSquad = teamSquad + "\n";
                        textView.setText(textView.getText().toString() + "\n" + teamSquad);
                    } else {
                        textView.setText(textView.getText().toString() + "\n no squad.");
                    }
                }
            }
            //Тут выводим итоговые данные
        }
    }

    public int nearestNewLineIndex(String string, String subString) {
        int newLinesAfterSubstring = 0;
        int index = 0;
        if (!string.contains("\n") || !string.contains(subString))
            return 0;
        else
            for (int i = string.indexOf(subString); i < string.length(); i++)
                if (string.charAt(i) == '\n') {
                    newLinesAfterSubstring++;
                    index = i;
                    i = string.length();
                }
        if (newLinesAfterSubstring == 0)
            return 0;
        else return index;
    }
}