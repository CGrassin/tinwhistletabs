package fr.charleslabs.tinwhistletabs.android;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import fr.charleslabs.tinwhistletabs.R;
import fr.charleslabs.tinwhistletabs.music.MusicSheet;

public class SheetsAdapter extends BaseAdapter implements Filterable{
    private List<MusicSheet> sheets;
    private List<MusicSheet> sheetsFiltered;
    private Context context;
    private View noResult;

    public SheetsAdapter(Context context,List<MusicSheet> sheets, View noResult) {
        this.sheets = sheets;
        this.sheetsFiltered = sheets;
        this.context = context;
        this.noResult = noResult;
    }

    @Override
    public int getCount() {
        return sheetsFiltered.size();
    }

    @Override
    public Object getItem(int position) {
        return sheetsFiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        //Set up the view
        if(convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_layout,parent,false);

        ComponentViewHolder viewHolder = (ComponentViewHolder)convertView.getTag();
        if(viewHolder==null){
            viewHolder = new ComponentViewHolder();
            viewHolder.sheetName = convertView.findViewById(R.id.mainActivity_SheetName);
            viewHolder.sheetAuthor = convertView.findViewById(R.id.mainActivity_SheetAuthor);
            viewHolder.sheetImage = convertView.findViewById(R.id.mainActivity_sheetPicture);
            convertView.setTag(viewHolder);
        }

        //Fetch the item in the component list
        final MusicSheet sheet = sheetsFiltered.get(position);

        //Fill up the view
        viewHolder.sheetName.setText(sheet.getTitle());
        viewHolder.sheetAuthor.setText(sheet.getType()+", "+sheet.getAuthor());
        switch (sheet.getType()) {
            case "Reel":
                viewHolder.sheetImage.setImageResource(R.drawable.reel);
                break;
            case "Jig":
                viewHolder.sheetImage.setImageResource(R.drawable.jig);
                break;
            case "Slip Jig":
                viewHolder.sheetImage.setImageResource(R.drawable.slipjig);
                break;
            case "Slide":
                viewHolder.sheetImage.setImageResource(R.drawable.slide);
                break;
            case "Polka":
                viewHolder.sheetImage.setImageResource(R.drawable.polka);
                break;
            case "March":
                viewHolder.sheetImage.setImageResource(R.drawable.march);
                break;
            case "Hornpipe":
                viewHolder.sheetImage.setImageResource(R.drawable.hornpipe);
                break;
            case "Song":
                viewHolder.sheetImage.setImageResource(R.drawable.song);
                break;
            case "Waltz":
                viewHolder.sheetImage.setImageResource(R.drawable.waltz);
                break;
            case "Misc.":
            default:
                viewHolder.sheetImage.setImageResource(R.drawable.misc);
                break;
        }

        //Return the view
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if(constraint == null || constraint.length() == 0){
                    filterResults.count = sheets.size();
                    filterResults.values = sheets;
                }else{
                    List<MusicSheet> resultsModel = new ArrayList<>();
                    String searchStr = constraint.toString().toLowerCase();

                    for(MusicSheet sheet : sheets)
                        if(sheet.filter(searchStr)) resultsModel.add(sheet);

                    filterResults.count = resultsModel.size();
                    filterResults.values = resultsModel;
                    Log.d("filter","Found " + filterResults.count + " with " + constraint.toString());
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                sheetsFiltered = (List<MusicSheet>) results.values;
                notifyDataSetChanged();
                if(noResult != null)
                    if(sheetsFiltered.isEmpty())
                        noResult.setVisibility(View.VISIBLE);
                    else
                        noResult.setVisibility(View.GONE);
            }
        };
    }

    // View Holder
    private static class ComponentViewHolder{
        TextView sheetName;
        TextView sheetAuthor;
        ImageView sheetImage;
    }
}