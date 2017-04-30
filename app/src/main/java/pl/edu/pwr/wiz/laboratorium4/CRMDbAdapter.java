package pl.edu.pwr.wiz.laboratorium4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/* Klasa wspomagająca obsługę bazy SQLite */
public class CRMDbAdapter {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "CRM.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Klienci.TABLE_NAME + " (" +
                    Klienci._ID + " INTEGER PRIMARY KEY," +
                    Klienci.COLUMN_NAME_NAZWA + " TEXT," +
                    Klienci.COLUMN_NAME_ADRES + " TEXT," +
                    Klienci.COLUMN_NAME_TELEFON + " TEXT)";

    /* Definiujemy nazwy kolumn i tabeli */
    public static class Klienci implements BaseColumns {
        public static final String TABLE_NAME = "klienci";
        public static final String COLUMN_NAME_NAZWA = "nazwa";
        public static final String COLUMN_NAME_ADRES = "adres";
        public static final String COLUMN_NAME_TELEFON = "telefon";
    }

    private static final String TAG = "CRMDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;

    private String orderBy = null;

    public class DatabaseHelper extends SQLiteOpenHelper {
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + Klienci.TABLE_NAME;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    public CRMDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public CRMDbAdapter open() throws SQLException {
        mDbHelper = new CRMDbAdapter.DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public long createClient(String nazwa, String adres, String telefon) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(Klienci.COLUMN_NAME_NAZWA, nazwa);
        initialValues.put(Klienci.COLUMN_NAME_ADRES, adres);
        initialValues.put(Klienci.COLUMN_NAME_TELEFON, telefon);

        return mDb.insert(Klienci.TABLE_NAME, null, initialValues);
    }

    public boolean deleteAllClients() {
        int doneDelete = 0;
        doneDelete = mDb.delete(Klienci.TABLE_NAME, null , null);

        Log.w(TAG, Integer.toString(doneDelete));   // Logujemy ilosc usunietych wpisow
        return doneDelete > 0;
    }

    public Cursor fetchClientsByName(String inputText) throws SQLException {
        Log.w(TAG, "Szukamy: " + inputText);

        Cursor mCursor = null;

        if (inputText == null || inputText.length () == 0) {
            mCursor = mDb.query(Klienci.TABLE_NAME, new String[] {Klienci._ID, Klienci.COLUMN_NAME_NAZWA,
                    Klienci.COLUMN_NAME_ADRES, Klienci.COLUMN_NAME_TELEFON}, null, null, null, null, orderBy, null);

        } else {
            mCursor = mDb.query(Klienci.TABLE_NAME, new String[] {Klienci._ID, Klienci.COLUMN_NAME_NAZWA,
                    Klienci.COLUMN_NAME_ADRES, Klienci.COLUMN_NAME_TELEFON},
                    Klienci.COLUMN_NAME_NAZWA + " like '%" + inputText + "%'",
                    null, null, null, orderBy, null);
        }

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    /* TODO - dodać funkcję do wyszukiwania po adresie i nazwie równocześnie */

    public Cursor fetchAllClients() {
        Cursor mCursor = mDb.query(Klienci.TABLE_NAME, new String[]{Klienci._ID, Klienci.COLUMN_NAME_NAZWA,
                Klienci.COLUMN_NAME_ADRES, Klienci.COLUMN_NAME_TELEFON}, null, null, null, null, orderBy, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    public void setOrderBy(String txt) {
        orderBy = txt;
    }

    public void insertSomeClients() {
        createClient("Majster","ul. Wielkopolska 12, 52-123 Wrocław","71 22 00 22");
        createClient("Mechanik","ul. Grunwaldzka 10, 51-053 Wrocław","71 22 00 30");
        createClient("Sklep spożywczy","ul. Parafialna 5, 51-015 Wrocław","512 010 203");
        createClient("Warzywniak","al. Hallera 104, 51-312 Wrocław","71 80 90 70");
        createClient("Aldar Części Samochodowe","ul. Kochanowskiego 8, 43-200 Pszczyna","32 210 11 08");
        createClient("Auto-Moto Sklep z Częściami Samochodowymi","ul. Józefowska 39, Katowice","32 353 50 43");
        createClient("Hotel Kamiza","Turzyn 192 A","29 742 41 98");
        createClient("Dolnośląska Grupa Apteczna S.A. LEKOSFERA","ul. Czeremchowa 6, Wrocław","71 332 99 30");
        createClient("Apteka Pod Niedźwiadkiem","ul. Pomorska 91, 90-225 Łódź","42 678 23 66");
        createClient("Bastion","ul. Gościnna 13 a, 09-500 Gostynin","517 916 050");
        createClient("Pruję Szyję Z Tego Żyję","ul. Więckowskiego 32, 90-728 Łódź","42 632 63 75");
        createClient("DanceStore Sp. z o.o.","ul. Burakowska 14 pok. 19 I p., 01-066 Warszawa","501 850 614");
    }
}