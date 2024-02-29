package com.example.thriftyskedrework;

import static com.example.thriftyskedrework.Constants.SELECTED_ANALYTICS_TYPE;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anychart.AnyChart;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.example.thriftyskedrework.databinding.FragmentAnalyticsBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmResults;


public class AnalyticsFragment extends Fragment {


    public AnalyticsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentAnalyticsBinding binding;

    Calendar calendar;
    /*
    0 = Daily
    1 = Monthly
     */


    public MainViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAnalyticsBinding.inflate(inflater);

        viewModel=new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        calendar= Calendar.getInstance();
        updateDate();

        binding.incomeBtn.setOnClickListener(view -> {
            binding.incomeBtn.setBackground(getContext().getDrawable(R.drawable.income_selector));
            binding.expenseBtn.setBackground(getContext().getDrawable(R.drawable.default_selector));
            binding.expenseBtn.setTextColor(getContext().getColor(R.color.white));
            binding.incomeBtn.setTextColor(getContext().getColor(R.color.greenColor));

            SELECTED_ANALYTICS_TYPE = Constants.INCOME;
            updateDate();

        });

        binding.expenseBtn.setOnClickListener(view -> {
            binding.incomeBtn.setBackground(getContext().getDrawable(R.drawable.default_selector));
            binding.expenseBtn.setBackground(getContext().getDrawable(R.drawable.expense_selector));
            binding.incomeBtn.setTextColor(getContext().getColor(R.color.white));
            binding.expenseBtn.setTextColor(getContext().getColor(R.color.redColor));

            SELECTED_ANALYTICS_TYPE = Constants.EXPENSE;
            updateDate();

        });

        binding.nextDateBtn.setOnClickListener(c->{
            if (Constants.SELECTED_TAB_ANALYTICS == Constants.DAILY) {
                calendar.add(Calendar.DATE, 1);
            }else if (Constants.SELECTED_TAB_ANALYTICS == Constants.MONTHLY){
                calendar.add(Calendar.MONTH, 1);
            }
            updateDate();

        });

        binding.previousDateBtn.setOnClickListener(c->{
            if (Constants.SELECTED_TAB_ANALYTICS == Constants.DAILY) {
                calendar.add(Calendar.DATE, -1);
            }else if (Constants.SELECTED_TAB_ANALYTICS == Constants.MONTHLY){
                calendar.add(Calendar.MONTH, -1);
            }
            updateDate();

        });

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("Monthly")){
                    Constants.SELECTED_TAB_ANALYTICS= 1;
                    updateDate();
                }else if (tab.getText().equals("Daily")){
                    Constants.SELECTED_TAB_ANALYTICS = 0;
                    updateDate();

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Pie pie = AnyChart.pie();

        viewModel.categoriesTransactions.observe(getViewLifecycleOwner(), new Observer<RealmResults<RecyclerTransaction>>() {
            @Override
            public void onChanged(RealmResults<RecyclerTransaction> recyclerTransactions) {


                if (recyclerTransactions.size() > 0){
                    binding.emptyState2.setVisibility(View.GONE);
                    binding.anyChart.setVisibility(View.VISIBLE);

                    List<DataEntry> data = new ArrayList<>();

                    Map<String,Double> categoryMap = new HashMap<>();
                    for (RecyclerTransaction recyclerTransaction : recyclerTransactions) {
                        String category = recyclerTransaction.getCategory();
                        double amount = recyclerTransaction.getAmount();
                        if (categoryMap.containsKey(category)){
                            double currentTotal = categoryMap.get(category).doubleValue();
                            currentTotal += Math.abs(amount);

                            categoryMap.put(category,currentTotal);
                        }else {
                            categoryMap.put(category,Math.abs(amount));
                        }
                    }

                    for (Map.Entry<String,Double> entry : categoryMap.entrySet()){
                        data.add(new ValueDataEntry(entry.getKey(), entry.getValue()));
                    }
                    pie.data(data);

                }else {
                    binding.emptyState2.setVisibility(View.VISIBLE);
                    binding.anyChart.setVisibility(View.GONE);
                }


            }
        });

        viewModel.getTransactions(calendar,SELECTED_ANALYTICS_TYPE);

        int color = getResources().getColor(R.color.darkestpurple);
        String colorHex = String.format("#%06X", (0xFFFFFF & color));
        pie.background(colorHex);

        binding.anyChart.setChart(pie);

        return binding.getRoot();
    }

    void updateDate(){
        if (Constants.SELECTED_TAB_ANALYTICS == Constants.DAILY) {
            binding.currentDate.setText(Helper.formatDate(calendar.getTime()));
        }else if (Constants.SELECTED_TAB_ANALYTICS == Constants.MONTHLY){
            binding.currentDate.setText(Helper.formatDateByMonth(calendar.getTime()));
        }
        viewModel.getTransactions(calendar,SELECTED_ANALYTICS_TYPE);
    }
}