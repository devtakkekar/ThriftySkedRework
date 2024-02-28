package com.example.thriftyskedrework;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thriftyskedrework.databinding.RowTransactionBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import io.realm.RealmResults;

public class RecyclerTransactionsAdapter extends RecyclerView.Adapter<RecyclerTransactionsAdapter.TransactionViewHolder>{

    Context context;
    RealmResults<RecyclerTransaction> transactions;
    public RecyclerTransactionsAdapter(Context context, RealmResults<RecyclerTransaction>transactions){
        this.context= context;
        this.transactions=transactions;

    }
    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionViewHolder(LayoutInflater.from(context).inflate(R.layout.row_transaction, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        RecyclerTransaction recyclerTransactions = transactions.get(position);

        holder.binding.transactionAmount.setText(String.valueOf(recyclerTransactions.getAmount()));
        holder.binding.accountCategory.setText(recyclerTransactions.getAccount());

        holder.binding.transactionDate.setText(Helper.formatDate(recyclerTransactions.getDate()));
        holder.binding.transactionCategory.setText(recyclerTransactions.getCategory());

        Category transactionCategory = Constants.getCategoryDetails(recyclerTransactions.getCategory());

        holder.binding.categoryIcon.setImageResource(transactionCategory.getCategoryImage());
        holder.binding.categoryIcon.setBackgroundTintList(context.getColorStateList(transactionCategory.getCategoryColor()));

        holder.binding.accountCategory.setBackgroundTintList(context.getColorStateList(Constants.getAccountsColor(recyclerTransactions.getAccount())));




        if(recyclerTransactions.getType().equals(Constants.INCOME)){
            holder.binding.transactionAmount.setTextColor(context.getColor(R.color.greenColor));
        } else if(recyclerTransactions.getType().equals(Constants.EXPENSE)){
            holder.binding.transactionAmount.setTextColor(context.getColor(R.color.redColor));
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog deleteDialog = new AlertDialog.Builder(context).create();
                deleteDialog.setTitle("Delete Transaction");
                deleteDialog.setMessage("Are you sure you want to delete this transaction?");
                deleteDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", (dialogInterface, i) -> {
                    ((Transactions)context).viewModel.deleteTransaction(recyclerTransactions);

                });
                deleteDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", (dialogInterface, i) -> {
                    deleteDialog.dismiss();

                });
                deleteDialog.show();
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public class TransactionViewHolder extends RecyclerView.ViewHolder {

        RowTransactionBinding binding;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=RowTransactionBinding.bind(itemView);
        }
    }
}
