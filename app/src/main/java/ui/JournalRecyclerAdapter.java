package ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mysavedrive.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import model.Journal;

public class JournalRecyclerAdapter extends RecyclerView.Adapter<JournalRecyclerAdapter.ViewHolder>
{

    private Context context;
    private List<Journal> journalList;

    public JournalRecyclerAdapter(Context context, List<Journal> journalList)
    {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public JournalRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.journal_row, viewGroup, false);



        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalRecyclerAdapter.ViewHolder viewHolder, int position)
    {//bind thw wighets with the data
        Journal journal = journalList.get(position);//getting 1 item of the journal from our list
        String imageUrl;

        viewHolder.title.setText(journal.getTitle());
        viewHolder.drives.setText(journal.getThought());// Check this point id correct
        viewHolder.name.setText(journal.getUserName());
        imageUrl = journal.getImageUrl();
        //3/13/45 min ago...
        //external source https://medium.com/@shaktisinh/time-a-go-in-android-8bad8b171f87
        //String timeAgo = (String) DateUtils.getRelativeTimeSpanString(journal.getTimeAdded().getSeconds() *1000);
        //viewHolder.dateAdded.setText(timeAgo);

        /*
        Use Picasso library to download and show image
         */
        Picasso.get().load(imageUrl).placeholder(R.drawable.ic_launcher_background).fit().into(viewHolder.image);//the placeholder replace the image
        // window in case we dont have image to show







    }

    @Override
    public int getItemCount()//return the size of our list
    {
        return journalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView title, drives, dateAdded, name;

        public ImageView image;
        String userId;
        String userName;
        public ImageButton shareButton;

        public ViewHolder(@NonNull View itemView, Context ctx)//making sure that the user can pass a context and move to the next activity
        {
            super(itemView);
            context = ctx;

            title = itemView.findViewById(R.id.journal_title_list);
            drives = itemView.findViewById(R.id.journal_event_list);
            dateAdded = itemView.findViewById(R.id.journal_timestamp_list);
            image = itemView.findViewById(R.id.journal_image_list);
            name = itemView.findViewById(R.id.journal_row_username);
            shareButton = itemView.findViewById(R.id.journal_row_share_button);
            shareButton.setOnClickListener(new View.OnClickListener()//Active the share button to work
            {
                @Override
                public void onClick(View v)
                {
                   // context.startActivity();

                }
            });

        }
    }
}
