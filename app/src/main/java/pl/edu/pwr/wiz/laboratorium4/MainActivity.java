package pl.edu.pwr.wiz.laboratorium4;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.SearchView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private CRMDbAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;
    private String SearchQuery = "";

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

//        handleIntent(getIntent());
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        // Tworzymy listę na podstawie danych w bazie SQLite
        displayListView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent");

        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            SearchQuery = intent.getStringExtra(SearchManager.QUERY);
        }

        Log.d(TAG, "handleIntent, SearchQuery: " + SearchQuery);
    }

    private void displayListView() {
        Cursor cursor;

        if(SearchQuery.isEmpty()) {
            cursor = dbHelper.fetchAllClients();
        } else {
            cursor = dbHelper.fetchClientsByName(SearchQuery);
        }

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Ustawiamy wyszukiwarkę
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQuery(SearchQuery, false);
        searchView.setIconifiedByDefault(true);

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d(TAG, "searchView.onClose event launched");

                SearchQuery = "";
                displayListView();
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

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