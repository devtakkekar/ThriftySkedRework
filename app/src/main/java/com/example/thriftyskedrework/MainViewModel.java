package com.example.thriftyskedrework;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainViewModel extends AndroidViewModel {

    public MutableLiveData<RealmResults<RecyclerTransaction>> transactions = new MutableLiveData<>();

    public MutableLiveData<Double> totalIncome = new MutableLiveData<>();
    public MutableLiveData<Double> totalExpense = new MutableLiveData<>();
    public MutableLiveData<Double> totalAmount = new MutableLiveData<>();

    Realm realm;

    Calendar calendar;
    public MainViewModel(@NonNull Application application) {
        super(application);
        Realm.init(application);
        setupDatabase();

        }
        public void getTransactions(Calendar calendar){
            this.calendar=calendar;
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            double income = 0;
            double expense = 0;
            double total = 0;

            RealmResults<RecyclerTransaction> newTransactions = null;

            if (Constants.SELECTED_TAB == Constants.DAILY) {

                newTransactions = realm.where(RecyclerTransaction.class)
                        .greaterThanOrEqualTo("date", calendar.getTime())
                        .lessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                        .findAll();

                income = realm.where(RecyclerTransaction.class)
                        .greaterThanOrEqualTo("date", calendar.getTime())
                        .lessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                        .equalTo("type", Constants.INCOME)
                        .sum("amount")
                        .doubleValue();

                expense = realm.where(RecyclerTransaction.class)
                        .greaterThanOrEqualTo("date", calendar.getTime())
                        .lessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                        .equalTo("type", Constants.EXPENSE)
                        .sum("amount")
                        .doubleValue();

                total = realm.where(RecyclerTransaction.class)
                        .greaterThanOrEqualTo("date", calendar.getTime())
                        .lessThan("date", new Date(calendar.getTime().getTime() + (24 * 60 * 60 * 1000)))
                        .sum("amount")
                        .doubleValue();



            } else if (Constants.SELECTED_TAB == Constants.MONTHLY) {
                calendar.set(Calendar.DAY_OF_MONTH,0);
                Date startTime = calendar.getTime();

                calendar.add(Calendar.MONTH,1);
                Date endTime = calendar.getTime();

                newTransactions = realm.where(RecyclerTransaction.class)
                        .greaterThanOrEqualTo("date", startTime)
                        .lessThan("date", endTime)
                        .findAll();

                income = realm.where(RecyclerTransaction.class)
                        .greaterThanOrEqualTo("date", startTime)
                        .lessThan("date", endTime)
                        .equalTo("type", Constants.INCOME)
                        .sum("amount")
                        .doubleValue();

                expense = realm.where(RecyclerTransaction.class)
                        .greaterThanOrEqualTo("date", startTime)
                        .lessThan("date", endTime)
                        .equalTo("type", Constants.EXPENSE)
                        .sum("amount")
                        .doubleValue();

                total = realm.where(RecyclerTransaction.class)
                        .greaterThanOrEqualTo("date", startTime)
                        .lessThan("date", endTime)
                        .sum("amount")
                        .doubleValue();
            }
            totalIncome.setValue(income);
            totalExpense.setValue(expense);
            totalAmount.setValue(total);
            transactions.setValue(newTransactions);
            //RealmResults<RecyclerTransaction>newTransactions=realm.where(RecyclerTransaction.class)
                   // .equalTo("date",calendar.getTime())
                   // .findAll();
        }

        public void addTransaction(RecyclerTransaction recyclerTransaction){
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(recyclerTransaction);
            realm.commitTransaction();
        }

        public void deleteTransaction(RecyclerTransaction recyclerTransaction){
            realm.beginTransaction();
            recyclerTransaction.deleteFromRealm();
            realm.commitTransaction();
            getTransactions(calendar);
        }

        public void addTransactions(){
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(new RecyclerTransaction(Constants.INCOME,"Business","Cash",new Date(),500,new Date().getTime()));
            realm.copyToRealmOrUpdate(new RecyclerTransaction(Constants.EXPENSE,"Investment","Bank",new Date(),-900,new Date().getTime()));
            realm.copyToRealmOrUpdate(new RecyclerTransaction(Constants.INCOME,"Rent","Other",new Date(),500,new Date().getTime()));
            realm.copyToRealmOrUpdate(new RecyclerTransaction(Constants.INCOME,"Business","Card",new Date(),500,new Date().getTime()));
            realm.commitTransaction();
        }
        void setupDatabase() {

            realm = Realm.getDefaultInstance();
    }
}
