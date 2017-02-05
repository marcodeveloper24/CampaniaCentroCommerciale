package com.developer.marcocicala.centrocampania;

/**
 * Created by utente on 10/01/2017.
 */
import android.widget.Filter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hp on 3/17/2016.
 */
public class CustomFilter extends Filter{
    RVAdapterNegozi adapter;
    ArrayList<HashMap<String,String>> filterList;
    public CustomFilter(ArrayList<HashMap<String,String>> filterList,RVAdapterNegozi adapter)
    {
        this.adapter=adapter;
        this.filterList=filterList;
    }
    //FILTERING OCURS
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
        //CHECK CONSTRAINT VALIDITY
        if(constraint != null && constraint.length() > 0)
        {
            //CHANGE TO UPPER
            constraint=constraint.toString().toUpperCase();
            //STORE OUR FILTERED PLAYERS
            ArrayList<HashMap<String,String>> filteredNegozi = new ArrayList<>();
            for (int i=0;i<filterList.size();i++)
            {

                //CHECK
                if(filterList.get(i).get("nome").toUpperCase().contains(constraint))
                {
                    //ADD PLAYER TO FILTERED PLAYERS
                    filteredNegozi.add(filterList.get(i));
                }


            }
            results.count=filteredNegozi.size();
            results.values=filteredNegozi;
        }else
        {
            results.count=filterList.size();
            results.values=filterList;
        }
        return results;
    }
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.hashMaps = (ArrayList<HashMap<String,String>>) results.values;
        //REFRESH
        adapter.notifyDataSetChanged();
    }
}