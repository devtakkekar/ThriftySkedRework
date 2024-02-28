package com.example.thriftyskedrework;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thriftyskedrework.databinding.FragmentAddTransactionBinding;
import com.example.thriftyskedrework.databinding.ListDialogBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class AddTransactionFragment extends BottomSheetDialogFragment {


    public AddTransactionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    FragmentAddTransactionBinding binding;
    RecyclerTransaction recyclerTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentAddTransactionBinding.inflate(inflater);

        recyclerTransaction = new RecyclerTransaction();

        binding.incomeBtn.setOnClickListener(view -> {
            binding.incomeBtn.setBackground(getContext().getDrawable(R.drawable.income_selector));
            binding.expenseBtn.setBackground(getContext().getDrawable(R.drawable.default_selector));
            binding.expenseBtn.setTextColor(getContext().getColor(R.color.textColor));
            binding.incomeBtn.setTextColor(getContext().getColor(R.color.greenColor));

            recyclerTransaction.setType(Constants.INCOME);

        });

        binding.expenseBtn.setOnClickListener(view -> {
            binding.incomeBtn.setBackground(getContext().getDrawable(R.drawable.default_selector));
            binding.expenseBtn.setBackground(getContext().getDrawable(R.drawable.expense_selector));
            binding.incomeBtn.setTextColor(getContext().getColor(R.color.textColor));
            binding.expenseBtn.setTextColor(getContext().getColor(R.color.redColor));

            recyclerTransaction.setType(Constants.EXPENSE);

        });

        binding.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(getContext());
                datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
                    Calendar calendar=Calendar.getInstance();
                    calendar.set(Calendar.DAY_OF_MONTH, view.getDayOfMonth());
                    calendar.set(Calendar.MONTH, view.getMonth());
                    calendar.set(Calendar.YEAR, view.getYear());

                    String dateToShow = Helper.formatDate(calendar.getTime());

                    binding.date.setText(dateToShow);


                    recyclerTransaction.setDate(calendar.getTime());
                    recyclerTransaction.setId(calendar.getTime().getTime());

                });
                datePickerDialog.show();
            }
        });

        binding.category.setOnClickListener(c->{
            ListDialogBinding dialogBinding=ListDialogBinding.inflate(inflater);
            AlertDialog categoryDialog=new AlertDialog.Builder(getContext()).create();
            categoryDialog.setView(dialogBinding.getRoot());



            CategoryAdapter categoryAdapter= new CategoryAdapter(getContext(), Constants.categories, new CategoryAdapter.CategoryClickListener() {
                @Override
                public void onCategoryClicked(Category category) {
                    binding.category.setText(category.getCategoryName());
                    recyclerTransaction.setCategory(category.getCategoryName());
                    categoryDialog.dismiss();
                }
            });
            dialogBinding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
            dialogBinding.recyclerView.setAdapter(categoryAdapter);
            categoryDialog.show();
        });

        binding.account.setOnClickListener(c->{
            ListDialogBinding dialogBinding=ListDialogBinding.inflate(inflater);
            AlertDialog accountsDialog=new AlertDialog.Builder(getContext()).create();
            accountsDialog.setView(dialogBinding.getRoot());

            ArrayList<Account> accounts= new ArrayList<>();
            accounts.add(new Account(0,"Cash"));
            accounts.add(new Account(0,"Bank"));
            accounts.add(new Account(0,"GPay"));
            accounts.add(new Account(0,"PayTM"));
            accounts.add(new Account(0,"Other"));

            AccountsAdapter adapter=new AccountsAdapter(getContext(), accounts, new AccountsAdapter.AccountsClickListener() {
                @Override
                public void onAccountSelected(Account account) {
                    binding.account.setText(account.getAccountName());
                    recyclerTransaction.setAccount(account.getAccountName());
                    accountsDialog.dismiss();

                }
            });
            dialogBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            dialogBinding.recyclerView.setAdapter(adapter);
            accountsDialog.show();

        });

        binding.button.setOnClickListener(c->{
            double amount=Double.parseDouble(binding.amount.getText().toString());
            if (recyclerTransaction.getType().equals(Constants.EXPENSE)){
                recyclerTransaction.setAmount(amount*-1);
            }else {
                recyclerTransaction.setAmount(amount);
            }
            ((Transactions)getActivity()).viewModel.addTransaction(recyclerTransaction);
            ((Transactions)getActivity()).getTransactions();
            dismiss();
        });

        return binding.getRoot();
    }
}