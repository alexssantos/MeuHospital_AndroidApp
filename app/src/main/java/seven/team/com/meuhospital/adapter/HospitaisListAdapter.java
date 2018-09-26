package seven.team.com.meuhospital.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import seven.team.com.meuhospital.R;
import seven.team.com.meuhospital.activity.HospitalsListActivity;
import seven.team.com.meuhospital.activity.MainActivity;
import seven.team.com.meuhospital.model.HospitalModel;


/*
    STEPS - IMPLEMENT ADAPTER
        1 - extends
        2 - Create ViewHolder
        3 - Implement 3 required Methods
        4 - CREATE AN RECEIVED LIST WITH CONSTRUCTOR
        5 - CONFIGURE METHODS

 */

public class HospitaisListAdapter extends RecyclerView.Adapter<HospitaisListAdapter.HospitaisListViewHolder> {

    private List<HospitalModel> hosptalList;
    private List<HospitalModel> hospitalListSearch;

    public HospitaisListAdapter(List<HospitalModel> hosptalList) {
        this.hosptalList = hosptalList;
        this.hospitalListSearch = new ArrayList<>();
        this.hospitalListSearch = HospitalsListActivity.hospitalModelList;
    }


    /////////////////////  Implemented Methods  /////////////////////////////////
    @NonNull
    @Override
    public HospitaisListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_list_hospitais,
                         parent,
                        false);

        return  new HospitaisListViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull HospitaisListViewHolder holder, int position) {

        HospitalModel hospitalModel = hosptalList.get(position);
        holder.idHospital.setText(String.valueOf(hospitalModel.getIdHospital()));
        holder.nome.setText(hospitalModel.getNome());
        holder.tipo.setText(hospitalModel.getTipo());
        String distancia = String.valueOf(hospitalModel.getDistancia()) + " Km";
        holder.distancia.setText(distancia);
    }

    @Override
    public int getItemCount() {
        return hosptalList.size();
    }


    ////////////////////// Other //////////////////////////

    public class HospitaisListViewHolder extends RecyclerView.ViewHolder{

        TextView idHospital;
        TextView nome;
        TextView tipo;
        TextView distancia;

        public HospitaisListViewHolder(View itemView) {
            super(itemView);

            idHospital = itemView.findViewById(R.id.textId);
            distancia = itemView.findViewById(R.id.textDistance);
            nome = itemView.findViewById(R.id.textName);
            tipo = itemView.findViewById(R.id.textType);

        }
    }

    // Filter Class
    public void filter(String typed) {
        typed = typed.toLowerCase(Locale.getDefault());
        HospitalsListActivity.hospitalModelList.clear();

        if (typed.length() == 0) {
            HospitalsListActivity.hospitalModelList.addAll(hospitalListSearch);
        } else {
            for (HospitalModel wp : hospitalListSearch) {
                if (wp.getNome().toLowerCase(Locale.getDefault()).contains(typed)) {
                    HospitalsListActivity.hospitalModelList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
