package deptinfo.ubfc.quizzs;
/*
    Barbeaut Reynald

    This file is used in the main activity

 */

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import deptinfo.ubfc.quizzs.dataBase.QuizzDataBase;
import deptinfo.ubfc.quizzs.usefulClass.Quizz;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //Attributes
    MyAdapter adapter;
    QuizzDataBase quizzDB ;
    private Button download;
    private ImageButton addQuizzButton;
    private TextView newQuizz;
    private static final String LOG_TAG = "HttpClientGET";
    private List<String> quizzs = new ArrayList<String>();
    private List<String> idQuizzs = new ArrayList<String>();
    private static final int CODE_ADDQUIZZ = 1;


    //Function which is called on the creation of the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        download = (Button) findViewById(R.id.buttonDownload);
        download.setOnClickListener(this);

        addQuizzButton = findViewById(R.id.addButton);
        addQuizzButton.setOnClickListener(this);

        newQuizz = findViewById(R.id.addNewQuizz);


        quizzDB = new QuizzDataBase(this);
        quizzDB.chargerQuizzs(quizzs);
        quizzDB.chargerIdQuizzs(idQuizzs);

        RecyclerView recyclerView = findViewById(R.id.listQuizz);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(this, quizzs, idQuizzs, quizzDB);
        recyclerView.setAdapter(adapter);
    }


    //Test the buttons of the activity
    public void onClick(View v) {
        List<String> quizzsName;
        List<String> quizzsId;
        //If it's the download button it downloads the quizzs
        if(v.getId() == this.download.getId()){
            Toast.makeText(this, "Quizzs en cours de téléchargements. ", Toast.LENGTH_SHORT).show();
            HttpPage tacheHttpPage = new HttpPage() ;
            tacheHttpPage.execute();

        }else if (v.getId() == this.addQuizzButton.getId()){
            //If it's the add quizz button we had it to data base
            addNewQuizz();
        }

    }


    //Add a new quizz
    public void addNewQuizz(){
        String newQuizz = String.valueOf(this.newQuizz.getText());
        if(newQuizz.isEmpty()){
            Toast.makeText(this, "Le nouveau quizz ne doit pas être vide !", Toast.LENGTH_SHORT).show();
        }else{
            adapter.add(newQuizz);
            this.newQuizz.setText("");
            Toast.makeText(this, "Le quizz a bien été ajouté !", Toast.LENGTH_SHORT).show();
        }

    }


    //This function is used to download the quizzs
    private void getPage(String adresse) {
        BufferedReader bufferedReader = null;
        HttpURLConnection urlConnection = null ;


        try {
            URL url = new URL(adresse);
            urlConnection = (HttpURLConnection) url.openConnection();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = urlConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(inputStream);
                doc.getDocumentElement().normalize();
                //This function parse the xml file and get all the information to add it to the database

                NodeList quizzList = doc.getElementsByTagName("Quizz");

                for (int i = 0, len = quizzList.getLength(); i < len; i++) {
                    Node quizz = quizzList.item(i);
                    //Adding a quizz in the database
                    String quizzName = ((Element) quizz).getAttribute("type");
                    quizzDB.insertQuizz(quizzName);
                    int idQuizz = quizzDB.getLastQuizz();

                    if(quizz.hasChildNodes()){
                        NodeList questions = quizz.getChildNodes();

                        for (int j = 0; j < questions.getLength(); j++){

                            Node question = questions.item(j);

                            if(question.getNodeType() == 1){

                                if(question.hasChildNodes()){
                                    NodeList propositions = question.getChildNodes();
                                    int idQuestion = 0;
                                    for(int c = 0; c < propositions.getLength(); c++){
                                        Node questionChild = propositions.item(c);

                                        if(c == 0){
                                            //Adding a question in the database
                                            String questionContent = questionChild.getNodeValue().trim();
                                            quizzDB.insertQuestion(idQuizz,questionContent,0);
                                            idQuestion = quizzDB.getLastQuestion();
                                        }else if(questionChild.getNodeType() == 1){

                                            if(((Element) questionChild).getTagName().equals("Reponse")){
                                                //Adding the good answer to a question in the database
                                                String goodReponse = ((Element) questionChild).getAttribute("valeur");
                                                quizzDB.updateGoodReponse(idQuestion,Integer.parseInt(goodReponse));
                                            }

                                            if(questionChild.hasChildNodes()){
                                                NodeList enfantPropositions = questionChild.getChildNodes();

                                                for(int h = 0; h < enfantPropositions.getLength(); h++){
                                                    Node proposition = enfantPropositions.item(h);

                                                    if(proposition.getNodeName().equals("Proposition")){
                                                        //Adding an answer to the database
                                                        String propositionContent = proposition.getTextContent().trim();
                                                        quizzDB.insertReponse(idQuestion,propositionContent);
                                                    }
                                                }

                                            }
                                        }


                                    }

                                }
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
            if (urlConnection != null) urlConnection.disconnect();
        }
    }

    private class HttpPage extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground (Void... params) {
            getPage("https://dept-info.univ-fcomte.fr/joomla/images/CR0700/Quizzs.xml");
            return null;
        }
        @Override
        protected void onPostExecute (Void result) {
            //The RecyclerView is updated after the quizz have been downloaded
            List<String> quizzsName;
            List<String> quizzsId;

            quizzsName = new ArrayList<String>();
            quizzsId = new ArrayList<String>();

            quizzDB.chargerQuizzs(quizzsName);
            quizzDB.chargerIdQuizzs(quizzsId);
            adapter.updateData(quizzsName,quizzsId);
        }
    }


}