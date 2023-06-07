package nemosofts.driving.exam.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import nemosofts.driving.exam.R;
import nemosofts.driving.exam.item.ItemCat;
import nemosofts.driving.exam.utils.ApplicationUtil;

public class AdapterCategory extends RecyclerView.Adapter {

    private final List<ItemCat> arrayList;
    private final RecyclerItemClickListener listener;

    public AdapterCategory(List<ItemCat> arrayList, RecyclerItemClickListener listener) {
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cat,parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        final ItemCat item = arrayList.get(position);

        ((ViewHolder) holder).cat_text.setText(item.getName());
        Picasso.get()
                .load(arrayList.get(position).getImage())
                .placeholder(R.drawable.material_design_default)
                .into(((ViewHolder) holder).cat_image);

        ((ViewHolder) holder).rl_item_cat.setOnClickListener(v -> listener.onClickListener(item, position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView cat_text;
        private final ImageView cat_image;
        private final RelativeLayout rl_item_cat;

        public ViewHolder(View itemView) {
            super(itemView);
            cat_text = itemView.findViewById(R.id.cat_text);
            cat_image = itemView.findViewById(R.id.item_img);
            rl_item_cat = itemView.findViewById(R.id.rl_item_cat);
        }
    }

    public interface RecyclerItemClickListener{
        void onClickListener(ItemCat itemData, int position);
    }

}
