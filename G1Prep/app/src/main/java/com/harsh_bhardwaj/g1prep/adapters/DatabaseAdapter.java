package com.harsh_bhardwaj.g1prep.adapters;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.harsh_bhardwaj.g1prep.models.QuestionModel;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

public class DatabaseAdapter {
    DatabaseHelper databaseHelper;
    String[] allColumns = {
            DatabaseHelper.ID,
            DatabaseHelper.QUERY,
            DatabaseHelper.QUERY_STATEMENT,
            DatabaseHelper.SOLUTION,
            DatabaseHelper.CORRECT_ANSWER,
            DatabaseHelper.TOPIC,
            DatabaseHelper.NOTES,
            DatabaseHelper.MARKED,
            DatabaseHelper.TIME_TAKEN,
            DatabaseHelper.FLAGGED};

    Context context;

    public DatabaseAdapter(Context context) {
        databaseHelper = new DatabaseHelper(context);
        this.context = context;
    }

    public ArrayList<QuestionModel> getAllData() {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        int id = 1;

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, allColumns, null, null, null, null, DatabaseHelper.ID);

        ArrayList<QuestionModel> questionModels = new ArrayList<>();
        while (cursor.moveToNext()) {
            questionModels.add(getDataForASingleRow(id));
            id++;
        }
        return questionModels;
    }

    public QuestionModel getDataForASingleRow(int id) {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, allColumns, DatabaseHelper.ID + "=?", selectionArgs, null, null, null);

        cursor.moveToNext();

        QuestionModel temporary = getQuestionModelFromCursor(cursor);
        Log.d("QUESTION", id + ": " + temporary.getQueryStatement());
        return temporary;
    }

    public float[] getFromTopic(int i) {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        String toAdd = "";
        if (i == 0) {
            toAdd = "'%Rules'";
        } else if (i == 1) {
            toAdd = "'%Symbols'";
        }
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, allColumns,
                DatabaseHelper.TOPIC + " LIKE " + toAdd,
                null,
                null,
                null,
                null);

        float[] arr = new float[4];
        for (int j = 0; j < 4; j++) {
            arr[j] = 0;
        }
        while (cursor.moveToNext()) {
            QuestionModel temp = getQuestionModelFromCursor(cursor);
            if (TextUtils.isEmpty(temp.getMarked()) && TextUtils.isEmpty(temp.getTimeTaken())) {
                arr[0]++;
            } else if (TextUtils.isEmpty(temp.getMarked()) && !TextUtils.isEmpty(temp.getTimeTaken())) {
                arr[1]++;
            } else if (!TextUtils.isEmpty(temp.getMarked()) && temp.getMarked().equals(temp.getCorrect())) {
                arr[3]++;
            } else {
                arr[2]++;
            }
        }
        return arr;
    }

    public float averageTimeTaken(int i) {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        String toAdd = "";
        if (i == 0) {
            toAdd = "'%Rules'";
        } else if (i == 1) {
            toAdd = "'%Symbols'";
        }
        int count = 0;
        long timeTaken = 0;
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, allColumns,
                DatabaseHelper.TOPIC + " LIKE " + toAdd + " AND " +
                        DatabaseHelper.TIME_TAKEN + " IS NOT NULL ",
                null,
                null,
                null,
                null);
        while (cursor.moveToNext()) {
            QuestionModel temp = getQuestionModelFromCursor(cursor);
            if (!TextUtils.isEmpty(temp.getTimeTaken())) {
                String[] arr = temp.getTimeTaken().split(":");
                int min = Integer.parseInt(arr[0]);
                int sec = Integer.parseInt(arr[1]);
                timeTaken += min * 60 + sec;
                count++;
            }
        }
        if (count != 0) {
            return timeTaken / count;
        } else {
            return 0;
        }
    }

    public int getNumUnattempted() {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, allColumns,
                DatabaseHelper.MARKED + " IS NULL "
                , null,
                null, null,
                DatabaseHelper.ID);

        ArrayList<QuestionModel> questionModels = new ArrayList<>();
        while (cursor.moveToNext()) {
            questionModels.add(getQuestionModelFromCursor(cursor));
        }
        return questionModels.size();
    }


    public ArrayList<QuestionModel> getAllMatching(String textToSearch, int[] optionSelected) {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        String flagStatement = "";
        String correctStatement = "";
        String incorrectStatement = "";
        String unattemptedStatement = "";
        String topicsSelection = "";

        if (optionSelected[0] == 1) {
            flagStatement += " AND " + DatabaseHelper.FLAGGED + " =1";
        }
        if (optionSelected[1] == 1) {
            correctStatement += " AND " + DatabaseHelper.MARKED + " IS NOT NULL AND " + DatabaseHelper.MARKED + "=" + DatabaseHelper.CORRECT_ANSWER;
        }
        if (optionSelected[2] == 1) {
            incorrectStatement += " AND " + DatabaseHelper.MARKED + " IS NOT NULL AND " + DatabaseHelper.MARKED + "!=" + DatabaseHelper.CORRECT_ANSWER;
        }
        if (optionSelected[3] == 1) {
            unattemptedStatement += " AND " + DatabaseHelper.MARKED + " IS NULL ";
        }
        if (optionSelected[4] == 1) {
            topicsSelection += " AND " + DatabaseHelper.TOPIC + " LIKE " + "'%Rules' ";
        }
        if (optionSelected[5] == 1) {
            topicsSelection += " AND " + DatabaseHelper.TOPIC + " LIKE " + "'%Symbols' ";
        }

        if ((!TextUtils.isEmpty(unattemptedStatement) && !TextUtils.isEmpty(correctStatement)) ||
                (!TextUtils.isEmpty(incorrectStatement) && !TextUtils.isEmpty(correctStatement)) ||
                (!TextUtils.isEmpty(unattemptedStatement) && !TextUtils.isEmpty(incorrectStatement))) {
            return new ArrayList<>();
        }

        ArrayList<QuestionModel> questionModels = new ArrayList<>();
        if (!TextUtils.isEmpty(textToSearch)) {
            Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, allColumns,
                    DatabaseHelper.QUERY + " LIKE '%" + textToSearch + "%' OR " +
                            DatabaseHelper.SOLUTION + " LIKE '%" + textToSearch + "%' OR " +
                            DatabaseHelper.ID + " LIKE '%" + textToSearch + "%' OR " +
                            DatabaseHelper.NOTES + " LIKE '%" + textToSearch + "%' " +
                            flagStatement + correctStatement + incorrectStatement + unattemptedStatement + topicsSelection
                    , null,
                    null, null,
                    DatabaseHelper.ID);

            while (cursor.moveToNext()) {
                questionModels.add(getQuestionModelFromCursor(cursor));
            }
            return questionModels;
        } else {
            Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, allColumns,
                    DatabaseHelper.ID + " LIKE '%' " +
                            flagStatement + correctStatement + incorrectStatement + unattemptedStatement + topicsSelection
                    , null,
                    null, null,
                    DatabaseHelper.ID);

            while (cursor.moveToNext()) {
                questionModels.add(getQuestionModelFromCursor(cursor));
            }
            return questionModels;
        }
    }

    public QuestionModel getQuestionModelFromCursor(Cursor cursor) {
        QuestionModel temporary = new QuestionModel();
        @SuppressLint("Range") int questionNumber = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ID));
        temporary.setId(questionNumber);

        @SuppressLint("Range") String query = cursor.getString(cursor.getColumnIndex(DatabaseHelper.QUERY));
        temporary.setQuery(query);

        @SuppressLint("Range") String queryStatement = cursor.getString(cursor.getColumnIndex(DatabaseHelper.QUERY_STATEMENT));
        temporary.setQueryStatement(queryStatement);

        @SuppressLint("Range") String solution = cursor.getString(cursor.getColumnIndex(DatabaseHelper.SOLUTION));
        temporary.setSolution(solution);

        @SuppressLint("Range") String correctAnswer = cursor.getString(cursor.getColumnIndex(DatabaseHelper.CORRECT_ANSWER));
        temporary.setCorrect(correctAnswer);

        @SuppressLint("Range") String topic = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TOPIC));
        temporary.setTopic(topic);

        @SuppressLint("Range") String notes = cursor.getString(cursor.getColumnIndex(DatabaseHelper.NOTES));
        temporary.setNotes(notes);

        @SuppressLint("Range") String marked = cursor.getString(cursor.getColumnIndex(DatabaseHelper.MARKED));
        temporary.setMarked(marked);

        @SuppressLint("Range") String timeTaken = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME_TAKEN));
        temporary.setTimeTaken(timeTaken);

        @SuppressLint("Range") int flagged = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.FLAGGED));
        temporary.setFlagged(flagged);

        return temporary;

    }

    public int updateFlagged(int id, int flag) {

        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.FLAGGED, flag);

        String[] whereArgs = {String.valueOf(id)};
        int count = database.update(DatabaseHelper.TABLE_NAME, contentValues,
                DatabaseHelper.ID + " =?", whereArgs);
        return count;
    }

    public int updateMarked(int id, String marked) {

        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.MARKED, marked);

        String[] whereArgs = {String.valueOf(id)};
        int count = database.update(DatabaseHelper.TABLE_NAME, contentValues,
                DatabaseHelper.ID + " =?", whereArgs);
        return count;
    }


    public int updateTime(int id, String timeValue) {

        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.TIME_TAKEN, timeValue);

        String[] whereArgs = {String.valueOf(id)};
        int count = database.update(DatabaseHelper.TABLE_NAME, contentValues,
                DatabaseHelper.ID + " =?", whereArgs);
        return count;
    }

    public int updateNotes(int id, String newNotes) {

        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.NOTES, newNotes);

        String[] whereArgs = {String.valueOf(id)};
        int count = database.update(DatabaseHelper.TABLE_NAME, contentValues,
                DatabaseHelper.ID + " =?", whereArgs);
        return count;
    }

    static class DatabaseHelper extends SQLiteAssetHelper {
        Context context;
        private static final String DATABASE_NAME = "questionsdb.db";
        private static final String TABLE_NAME = "questions";
        private static final int DATABASE_VERSION = 1;
        private static final String ID = "ID";
        private static final String QUERY = "query";
        private static final String QUERY_STATEMENT = "statement";
        private static final String SOLUTION = "solution";
        private static final String CORRECT_ANSWER = "correct";
        private static final String TOPIC = "topic";
        private static final String NOTES = "notes";
        private static final String MARKED = "marked";
        private static final String TIME_TAKEN = "time_txt";
        private static final String FLAGGED = "flagged";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }
    }

}
