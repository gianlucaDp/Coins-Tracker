package com.gianlucadp.coinstracker;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.gianlucadp.coinstracker.model.Transaction;
import com.gianlucadp.coinstracker.model.TransactionGroup;
import com.gianlucadp.coinstracker.model.TransactionValue;
import com.gianlucadp.coinstracker.supportClasses.DatabaseManager;
import com.gianlucadp.coinstracker.supportClasses.Utilities;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class AppBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AddNewGroupFragment.OnGroupCreatedListener {
    public static final int RC_SIGN_IN = 1;
    private static final String CURRENT_FRAGMENT_KEY = "CF_KEY";


    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    //User info
    private static String mUsername;
    private static String mUserId;


    //Firebase variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    private DatabaseReference mDatabase;
    private DatabaseReference mDataBaseTransactionGroups;
    private DatabaseReference mDataBaseTransactions;
    private ChildEventListener mTransactionGroupsEventListener;
    private ChildEventListener mTransactionsEventListener;


    //data
    private Map<String, TransactionGroup> mRevenuesGroups;
    private Map<String, TransactionGroup> mDepositsGroups;
    private Map<String, TransactionGroup> mExpensesGroups;
    private Map<String, Transaction> mTransactions;

    private String currentFragment = TransactionsListFragment.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //To enable Android Iconics
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new IconicsLayoutInflater2(getDelegate()));
        super.onCreate(savedInstanceState);


        if (savedInstanceState!=null){
            Utilities.readMapTGFromBundle(savedInstanceState,mRevenuesGroups);
            Utilities.readMapTGFromBundle(savedInstanceState,mDepositsGroups);
            Utilities.readMapTGFromBundle(savedInstanceState,mExpensesGroups);
            Utilities.readMapTFromBundle(savedInstanceState,mTransactions);
            currentFragment = savedInstanceState.getString(CURRENT_FRAGMENT_KEY);

        }

        //Initialize view
        setContentView(R.layout.activity_app_base);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Initialize Firebase db
        if (mDatabase == null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            if (savedInstanceState == null) {
                database.setPersistenceEnabled(true); //To work with offline data too
            }
            mDatabase = database.getReference();
            DatabaseManager.setDatabase(mDatabase);
        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        //Login procedure
        mFirebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) { //Login successful
                    onSignedInitialize(user);

                } else {    //Logout procedure
                    onSignedOutCleanup();
                    //Show login screen
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setLogo(R.mipmap.ic_launcher_round)
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        //Show default main screen at launch
        if (savedInstanceState == null) {
            //Attach the transaction list fragment
            TransactionsListFragment mainFragment = new TransactionsListFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.fm_fragments_container, mainFragment)
                    .commit();


            mRevenuesGroups = new TreeMap<>();
            mDepositsGroups = new TreeMap<>();
            mExpensesGroups = new TreeMap<>();
            mTransactions = new TreeMap<>();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Utilities.writeMapTGAsBundle(outState,mRevenuesGroups);
        Utilities.writeMapTGAsBundle(outState,mDepositsGroups);
        Utilities.writeMapTGAsBundle(outState,mExpensesGroups);
        Utilities.writeMapTAsBundle(outState,mTransactions);
        outState.putString(CURRENT_FRAGMENT_KEY,currentFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mFirebaseAuthListener);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    private void onSignedInitialize(FirebaseUser user) {
        mUsername = user.getDisplayName();
        mUserId = user.getUid();
        // Add a reference to transaction groups and transactions
        DatabaseReference[] references = DatabaseManager.addUser(mUserId);
        mDataBaseTransactionGroups = references[0];
        mDataBaseTransactions = references[1];
        attachDatabaseReadListener();

    }


    private void onSignedOutCleanup() {
        mUsername = null;
        mUserId = null;
        detachDatabaseReadListener();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_item_main_page) {
            if (!currentFragment.equals(TransactionsListFragment.class.getSimpleName())) {
                Fragment newFragment = new TransactionsListFragment().newInstance(new ArrayList<TransactionGroup>(mRevenuesGroups.values()),new ArrayList<TransactionGroup>(mDepositsGroups.values()),new ArrayList<TransactionGroup>(mExpensesGroups.values()));
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.fm_fragments_container, newFragment);
                transaction.commit();

                currentFragment = TransactionsListFragment.class.getSimpleName();
            }

        } else if (id == R.id.menu_item_history) {

            HashMap<String,TransactionGroup> mergedMap = Utilities.mergeMaps(mRevenuesGroups,mDepositsGroups,mExpensesGroups);
            Fragment newFragment = new TransactionsHistoryFragment().newInstance(new ArrayList<Transaction>(mTransactions.values()),mergedMap);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.fm_fragments_container, newFragment);
            transaction.commit();

            currentFragment = TransactionsHistoryFragment.class.getSimpleName();

        } else if (id == R.id.menu_item_statistics) {

            if (!currentFragment.equals(StatisticsFragment.class.getSimpleName())) {
                Fragment newFragment = new StatisticsFragment().newInstance(new ArrayList<Transaction>(mTransactions.values()));
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.fm_fragments_container, newFragment);
                transaction.commit();

                currentFragment = StatisticsFragment.class.getSimpleName();
            }
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void attachDatabaseReadListener() {
        //Handle the listener for transactions groups
        if (mTransactionGroupsEventListener == null) {
            mTransactionGroupsEventListener = new ChildEventListener() {

                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    TransactionGroup transactionGroup = dataSnapshot.getValue(TransactionGroup.class);
                    transactionGroup.setFirebaseId(dataSnapshot.getKey());
                    addValuesToGroup(dataSnapshot, transactionGroup);

                    if (transactionGroup.getType() != null) {
                        TransactionsListFragment transactionsListFragment = (TransactionsListFragment) getSupportFragmentManager().findFragmentById(R.id.fm_fragments_container);
                        switch (transactionGroup.getType()) {
                            case REVENUE:
                                mRevenuesGroups.put(dataSnapshot.getKey(), transactionGroup);
                                transactionsListFragment.getRevenueAdapter().addItem(transactionGroup);
                                Log.d("AAA","revenue added");

                                break;
                            case DEPOSIT:
                                mDepositsGroups.put(dataSnapshot.getKey(), transactionGroup);
                                transactionsListFragment.getDepositAdapter().addItem(transactionGroup);
                                Log.d("AAA","deposit added");

                                break;
                            case EXPENSE:
                                mExpensesGroups.put(dataSnapshot.getKey(), transactionGroup);
                                transactionsListFragment.getExpenseAdapter().addItem(transactionGroup);
                                Log.d("AAA","expense added");
                                break;
                            default:
                                break;
                        }
                    }
                }

                private void addValuesToGroup(@NonNull DataSnapshot dataSnapshot, TransactionGroup transactionGroup) {
                    if (dataSnapshot.hasChild("flows")){
                        DataSnapshot flowsSnapShot = dataSnapshot.child("flows");
                        if (flowsSnapShot.hasChildren()) {
                            Iterable<DataSnapshot> flowChildren = flowsSnapShot.getChildren();
                            for (DataSnapshot flow : flowChildren) {
                                TransactionValue tv = flow.getValue(TransactionValue.class);
                                tv.setTransactionValueFirebaseId(flow.getKey());
                                transactionGroup.addTransactionValue(tv);

                            }
                        }
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    TransactionGroup transactionGroup = dataSnapshot.getValue(TransactionGroup.class);
                    transactionGroup.setFirebaseId(dataSnapshot.getKey());
                    addValuesToGroup(dataSnapshot, transactionGroup);

                    if (transactionGroup.getType() != null) {
                        TransactionsListFragment transactionsListFragment = (TransactionsListFragment) getSupportFragmentManager().findFragmentById(R.id.fm_fragments_container);
                        switch (transactionGroup.getType()) {
                            case REVENUE:
                                Log.d("AAA","revenue changed");
                                mRevenuesGroups.put(dataSnapshot.getKey(), transactionGroup);
                                transactionsListFragment.getRevenueAdapter().updateGroup(transactionGroup);
                                break;
                            case DEPOSIT:
                                mDepositsGroups.put(dataSnapshot.getKey(), transactionGroup);
                                transactionsListFragment.getDepositAdapter().updateGroup(transactionGroup);
                                Log.d("AAA","deposit changed");
                                break;
                            case EXPENSE:
                                mExpensesGroups.put(dataSnapshot.getKey(), transactionGroup);
                                transactionsListFragment.getExpenseAdapter().updateGroup(transactionGroup);
                                Log.d("AAA","expense changed");
                                break;
                            default:
                                break;
                        }
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };

            mDataBaseTransactionGroups.addChildEventListener(mTransactionGroupsEventListener);
            mDataBaseTransactionGroups.addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //When all the data is loaded

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        //Handle the listener for transactions
        if (mTransactionsEventListener == null) {
            mTransactionsEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    Transaction transaction = dataSnapshot.getValue(Transaction.class);
                    transaction.setFirebaseId(dataSnapshot.getKey());
                    mTransactions.put(dataSnapshot.getKey(), transaction);
                    Log.d("AAA", "added transaction");
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    //
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };

            mDataBaseTransactions.addChildEventListener(mTransactionsEventListener);
        }
    }

    private void detachDatabaseReadListener() {

        if (mTransactionGroupsEventListener != null) {
            mDataBaseTransactionGroups.removeEventListener(mTransactionGroupsEventListener);
            mTransactionGroupsEventListener = null;
        }

        if (mTransactionsEventListener != null) {
            mDataBaseTransactions.removeEventListener(mTransactionsEventListener);
            mTransactionsEventListener = null;
        }
    }


    @Override
    public void onGroupCreated(TransactionGroup transactionGroup) {
        DatabaseManager.addTransactionGroup(transactionGroup);
    }



}
