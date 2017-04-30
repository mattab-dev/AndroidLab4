package pl.edu.pwr.wiz.laboratorium4;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private CRMDbAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new CRMDbAdapter(this);
        dbHelper.open();

        // Czyścimy dane
        dbHelper.deleteAllClients();

        // Dodajemy przykladowe dane
        dbHelper.insertSomeClients();

        // Tworzymy listę na podstawie danych w bazie SQLite
        displayListView();
    }

    private void displayListView() {
        Cursor cursor = dbHelper.fetchAllClients();

        // Kolumny do podpięcia
        String[] columns = new String[] {
                CRMDbAdapter.Klienci._ID,
                CRMDbAdapter.Klienci.COLUMN_NAME_NAZWA,
                CRMDbAdapter.Klienci.COLUMN_NAME_ADRES
                // TODO dodać kolumnę z telefonem
        };

        // ID zasobów z pliku client_info.xml
        int[] to = new int[] {
                R.id.id,
                R.id.nazwa,
                R.id.adres
                // TODO dodać kolumnę z telefonem
        };

        // Tworzymy adapter z kursorem wskazującym na nasze dane
        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.client_info,
                cursor,
                columns,
                to,
                0);

        // Podpinamy adapter do listy
        ListView listView = (ListView) findViewById(R.id.listaKlientow);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Pobierz dane z wybranej pozycji
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // Pobieramy numer telefonu z bazy dla wybranego klienta i wyświetlamy w Toast
                String telefon = cursor.getString(cursor.getColumnIndexOrThrow("telefon"));
                Toast.makeText(getApplicationContext(), telefon, Toast.LENGTH_SHORT).show();
            }
        });


        EditText myFilter = (EditText) findViewById(R.id.filter);
        myFilter.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                dataAdapter.getFilter().filter(s.toString());
            }
        });

        dataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                return dbHelper.fetchClientsByName(constraint.toString());

                /* TODO zmienić sposób wyszukiwania, aby uwzględniało adres */
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.order_id_asc:
                dbHelper.setOrderBy(CRMDbAdapter.Klienci._ID + " ASC");
                displayListView();
                return true;

            case R.id.order_id_desc:
                dbHelper.setOrderBy(CRMDbAdapter.Klienci._ID + " DESC");
                displayListView();
                return true;

            /* TODO: Dodać obsługę sortowania po nazwie */

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}