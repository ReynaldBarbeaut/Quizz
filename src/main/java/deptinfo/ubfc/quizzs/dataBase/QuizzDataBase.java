package deptinfo.ubfc.quizzs.dataBase;
/*
    Barbeaut Reynald

    This file represent the data base which is used in this application

 */
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Serializable;
import java.util.List;

import deptinfo.ubfc.quizzs.usefulClass.Question;
import deptinfo.ubfc.quizzs.usefulClass.Quizz;

public class QuizzDataBase extends SQLiteOpenHelper implements Serializable {
    //Attributes
    SQLiteDatabase db;
    private static final String DATABASE_NAME = "quizz.db";
    private static final int DATABASE_VERSION = 1;
    private static final String LOG_TAG = "DataBase";

    private static final String DATABASE_CREATE_QUIZZS = "create table quizzs "
            + "(idQuizz integer primary key autoincrement,"
            + " quizz text not null) ;";

    private static final String DATABASE_CREATE_QUESTIONS = "create table questions "
            + "(idQuestion integer primary key autoincrement,"
            + "idQuizz integer not null,"
            + "idReponse integer not null,"
            + "question text not null) ;";

    private static final String DATABASE_CREATE_REPONSES = "create table reponses "
            + "(idReponse integer primary key autoincrement,"
            + "idQuestion integer not null,"
            + "reponse text not null)";

    public QuizzDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Getting informations of a quizz
    public Cursor getCursor() {
        this.db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM quizzs",null);
    }


    //Creating the data base
    @Override
    public void onCreate(SQLiteDatabase database) {
        this.db = database;
        database.execSQL(DATABASE_CREATE_QUIZZS) ;
        database.execSQL(DATABASE_CREATE_QUESTIONS) ;
        database.execSQL(DATABASE_CREATE_REPONSES) ;
        db.execSQL("INSERT INTO quizzs (quizz) VALUES ('Quizz de bienvenue')");
        db.execSQL("INSERT INTO questions (idQuizz, question, idReponse) VALUES (1,'Un triangle isocèle est un triangle qui a :', 1)");
        db.execSQL("INSERT INTO reponses (idQuestion, reponse) VALUES (1,'Deux côtés de même mesure')");
        db.execSQL("INSERT INTO reponses (idQuestion, reponse) VALUES (1,'Trois côtés de même mesure')");
        db.execSQL("INSERT INTO reponses (idQuestion, reponse) VALUES (1,'Un angle de 90°')");

        db.execSQL("INSERT INTO questions (idQuizz, question, idReponse) VALUES (1,'Calculer ; 12+95+62', 3)");
        db.execSQL("INSERT INTO reponses (idQuestion, reponse) VALUES (2,'213')");
        db.execSQL("INSERT INTO reponses (idQuestion, reponse) VALUES (2,'127')");
        db.execSQL("INSERT INTO reponses (idQuestion, reponse) VALUES (2,'169')");

        db.execSQL("INSERT INTO questions (idQuizz, question, idReponse) VALUES (1,'A + 6 = 3; A = ?', 1)");
        db.execSQL("INSERT INTO reponses (idQuestion, reponse) VALUES (3,'-3')");
        db.execSQL("INSERT INTO reponses (idQuestion, reponse) VALUES (3,'3')");
        db.execSQL("INSERT INTO reponses (idQuestion, reponse) VALUES (3,'2')");
    }


    //On upgrade method
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //Insert a quizz in the data base
    public void insertQuizz(String quizzName){
        quizzName = quizzName.replaceAll("'","''");
        db.execSQL("INSERT INTO quizzs (quizz) VALUES ('"+quizzName+"')");
    }

    //Insert a question in the data base
    public void insertQuestion(int idQuizz, String question,int idReponse){
        question = question.replaceAll("'","''");
        db.execSQL("INSERT INTO questions (idQuizz, question, idReponse) VALUES ("+idQuizz+",'"+question+"', "+idReponse+")");
    }


    //Insert a proposition in the data base
    public void insertReponse(int idQuestion, String reponse){
        reponse = reponse.replaceAll("'","''");
        db.execSQL("INSERT INTO reponses (idQuestion, reponse) VALUES ("+idQuestion+",'"+reponse+"')");
    }


    //Update the good answer of a question in the database
    public void updateGoodReponse(int idQuestion, int idReponse){
        db.execSQL("UPDATE questions SET idReponse="+idReponse+" WHERE idQuestion="+idQuestion);
    }


    //Update a quizz in the database
    public void updateQuizz(int idQuizz, String name){
        name = name.replaceAll("'","''");
        db.execSQL("UPDATE quizzs SET quizz='"+name+"' WHERE idQuizz="+idQuizz);
    }

    //Update a question in the database
    public void updateQuestion(int idQuestion, String name){
        name = name.replaceAll("'","''");
        db.execSQL("UPDATE questions SET question='"+name+"' WHERE idQuestion="+idQuestion);
    }


    //Update a answer in the database
    public void updateReponse(int idReponse, String name){
        name = name.replaceAll("'","''");
        db.execSQL("UPDATE reponses SET reponse='"+name+"' WHERE idReponse="+idReponse);
    }

    //Delete a quizz
    public void deleteQuizz(int idQuizz){
        db.execSQL("DELETE FROM quizzs WHERE idQuizz="+idQuizz);
    }

    //Delete a question
    public void deleteQuestion(int idQuizz){
        db.execSQL("DELETE FROM questions WHERE idQuizz="+idQuizz);
    }

    //Delete all answers of a question
    public void deleteReponse(int idQuestion){
        db.execSQL("DELETE FROM reponses WHERE idQuestion="+idQuestion);
    }

    //Delete an answer
    public void deleteUneReponse(int idReponse){
        db.execSQL("DELETE FROM reponses WHERE idReponse="+idReponse);
    }


    //Delete a quizz, its questions and the answers to its questions
    public void deleteOnCascade(int idQuizz){
        this.db = this.getWritableDatabase();
        deleteQuizz(idQuizz);
        Cursor cursor = this.db.rawQuery("SELECT idQuestion FROM questions WHERE idQuizz="+idQuizz,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            deleteReponse(Integer.parseInt((cursor.getString(0))));
            cursor.moveToNext();
        }
        cursor.close();
        deleteQuestion(idQuizz);
    }


    //Delete everything
    public void deleteEverything (){
        db.execSQL("DELETE FROM quizzs;");
        db.execSQL(" DELETE FROM questions;");
        db.execSQL(" DELETE FROM reponses;");
    }



    //Get the good proposition of a question
    public int getGoodProposition(int idQuestion){
        this.db = this.getWritableDatabase();
        Cursor cursor = this.db.rawQuery("SELECT idReponse FROM questions WHERE idQuestion="+idQuestion,null);
        cursor.moveToFirst();
        int id = Integer.parseInt(((cursor.getString(0))));
        cursor.close();
        return  id;
    }

    //Get the id of the last inserted quizz
    public int getLastQuizz(){
        this.db = this.getWritableDatabase();
        Cursor cursor = this.db.rawQuery("SELECT MAX(idQuizz) FROM quizzs",null);
        cursor.moveToFirst();
        int id = Integer.parseInt(((cursor.getString(0))));
        cursor.close();
        return  id;
    }


    //Get the name of a quizz
    public String getQuizzName(int id){
        this.db = this.getWritableDatabase();
        Cursor cursor = this.db.rawQuery("SELECT quizz FROM quizzs WHERE idQuizz="+id,null);
        cursor.moveToFirst();
        String name = cursor.getString(0);
        cursor.close();
        return  name;
    }

    //Get the last question which was inserted
    public int getLastQuestion(){
        this.db = this.getWritableDatabase();
        Cursor cursor = this.db.rawQuery("SELECT MAX(idQuestion) FROM questions",null);
        cursor.moveToFirst();
        int id = Integer.parseInt(((cursor.getString(0))));
        cursor.close();
        return  id;
    }


    //Get the last answer which was inserted
    public int getLastReponse(){
        this.db = this.getWritableDatabase();
        Cursor cursor = this.db.rawQuery("SELECT MAX(idReponse) FROM reponses",null);
        cursor.moveToFirst();
        int id = Integer.parseInt(((cursor.getString(0))));
        cursor.close();
        return  id;
    }


    //Get all the propositions of a question
    public void getPropositions(List<String> lcs, int idQuestion){
        this.db = this.getWritableDatabase();
        Cursor cursor = this.db.rawQuery("SELECT reponse FROM reponses WHERE idQuestion="+idQuestion,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String reponse = cursor.getString(0) ;
            lcs.add(reponse);
            cursor.moveToNext();
        }
        cursor.close();
    }


    //Get all the questions of a quizz
    public void getQuestions(List<Question> questions, int idQuizz){
        String questionName;
        int idReponse, idQuestion;
        questions.clear();
        Question question;
        this.db = this.getWritableDatabase();
        Cursor cursor = this.db.rawQuery("SELECT idQuestion, question, idReponse FROM questions WHERE idQuizz="+idQuizz,null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            idQuestion = cursor.getInt(0);
            questionName = cursor.getString(1) ;
            idReponse = cursor.getInt(2);
            question = new Question(questionName);
            question.setBonneReponse(idReponse);
            //Adding the propositions to the questions
            getPropositions(question.propositions,idQuestion);
            questions.add(question);
            cursor.moveToNext();
        }
        cursor.close();

    }


    //Get a quizz, its questions and its propositions
    public Quizz getQuizz(int idQuizz){
        Quizz quizz = new Quizz(getQuizzName(idQuizz));
        getQuestions(quizz.questions,idQuizz);
        return quizz;
    }


    //Load the name of quizzs in a list
    public void chargerQuizzs(List<String> lcs) {
        lcs.clear();
        Cursor cursor = this.getCursor() ;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String quizzs = cursor.getString(1) ;
            lcs.add(quizzs);
            cursor.moveToNext();
        }
        cursor.close();
    }


    //Load the id of quizzs in a list
    public void chargerIdQuizzs(List<String> lcs) {
        lcs.clear();
        Cursor cursor = this.getCursor() ;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String quizzs = cursor.getString(0) ;
            lcs.add(quizzs);
            cursor.moveToNext();
        }
        cursor.close();
    }

    //Load the name of questions in a list
    public void chargerQuestions(List<String> lcs, int idQuizz) {
        lcs.clear();
        this.db = this.getWritableDatabase();
        Cursor cursor = this.db.rawQuery("SELECT question FROM questions WHERE idQuizz="+idQuizz,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String questions = cursor.getString(0) ;
            lcs.add(questions);
            cursor.moveToNext();
        }
        cursor.close();
    }


    //Load the id of questions in a list
    public void chargerIdQuestions(List<String> lcs, int idQuizz) {
        lcs.clear();
        this.db = this.getWritableDatabase();
        Cursor cursor = this.db.rawQuery("SELECT idQuestion FROM questions WHERE idQuizz="+idQuizz,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String questions = cursor.getString(0) ;
            lcs.add(questions);
            cursor.moveToNext();
        }
        cursor.close();
    }


    //Load the name of propositions in a list
    public void chargerPropositions(List<String> lcs, int idQuestion) {
        lcs.clear();
        this.db = this.getWritableDatabase();
        Cursor cursor = this.db.rawQuery("SELECT reponse FROM reponses WHERE idQuestion="+idQuestion,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String proposition = cursor.getString(0) ;
            lcs.add(proposition);
            cursor.moveToNext();
        }
        cursor.close();
    }


    //Load the id of questions in a list
    public void chargerIdPropositions(List<String> lcs, int idQuestion) {
        lcs.clear();
        this.db = this.getWritableDatabase();
        Cursor cursor = this.db.rawQuery("SELECT idReponse FROM reponses WHERE idQuestion="+idQuestion,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String id = cursor.getString(0) ;
            lcs.add(id);
            cursor.moveToNext();
        }
        cursor.close();
    }
}
