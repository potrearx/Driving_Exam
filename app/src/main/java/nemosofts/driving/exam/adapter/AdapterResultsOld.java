package nemosofts.driving.exam.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import nemosofts.driving.exam.R;
import nemosofts.driving.exam.item.ItemResult;


public class AdapterResultsOld extends RecyclerView.Adapter<AdapterResultsOld.MyViewHolder> {

    private final ArrayList<ItemResult> arrayList;

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_result;
        private final TextView tv_time;
        private final TextView tv_date;
        private final TextView tv_number;

        MyViewHolder(View view) {
            super(view);
            tv_result = view.findViewById(R.id.tv_result);
            tv_time = view.findViewById(R.id.tv_time);
            tv_date = view.findViewById(R.id.tv_date);
            tv_number = view.findViewById(R.id.tv_number);
        }
    }

    public AdapterResultsOld(ArrayList<ItemResult> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_results_old, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ItemResult itemResult = arrayList.get(position);

        holder.tv_result.setText(itemResult.getExam_result());
        holder.tv_time.setText(itemResult.getTime());
        holder.tv_date.setText(itemResult.getDate());
        holder.tv_number.setText(itemResult.getId());
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}