package sparta.realm.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class selected_files_adapter extends  RecyclerView.Adapter<selected_files_adapter.view> {

    Context cntxt;
    public ArrayList<HashMap.Entry<File,String>> items= new ArrayList<>();
    onItemClickListener listener;

    public interface onItemClickListener{

        void onItemClick(HashMap.Entry<File,String> obj);
    }


  public selected_files_adapter(HashMap<File,String> items, onItemClickListener listener)
    {
        Iterator it=items.entrySet().iterator();
        while (it.hasNext())
        {
            this.items.add((HashMap.Entry<File,String>)it.next());

        }


        this.listener = listener;


    }

    @NonNull
    @Override
    public view onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.cntxt=parent.getContext();
        View view = LayoutInflater.from(cntxt).inflate(R.layout.item_selected_string, parent, false);

        return new view(view);
    }

    @Override
    public void onBindViewHolder(@NonNull view holder, int position) {
      String obj= items.get(position).getValue();



        holder.name.setText(obj);


holder.cancel.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        listener.onItemClick(items.get(position));

    }
});



    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class view extends RecyclerView.ViewHolder implements View.OnClickListener {
      public TextView name;
      public ImageView cancel;





             view(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);

            cancel = itemView.findViewById(R.id.cancel);







        }

        @Override
        public void onClick(View view) {

      }
    }
}
