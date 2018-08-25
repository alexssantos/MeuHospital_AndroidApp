package seven.team.com.meuhospital.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import seven.team.com.meuhospital.R;
import seven.team.com.meuhospital.model.Hospital;


/*
    STEPS - IMPLEMENT ADAPTER
        1 - extends
        2 - Create ViewHolder
        3 - Implement 3 required Methods
        4 - CREATE AN RECEIVED LIST WITH CONSTRUCTOR
        5 - CONFIGURE METHODS

 */

public class HospitaisListAdapter extends RecyclerView.Adapter<HospitaisListAdapter.HospitaisListViewHolder> {

    private List<Hospital> hosptalList;

    public HospitaisListAdapter(List<Hospital> hosptalList) {
        this.hosptalList = hosptalList;
    }

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

        Hospital hospital = hosptalList.get(position);
        holder.idHospital.setText(String.valueOf(hospital.getIdHospital()));
        holder.nome.setText(hospital.getNome());
        holder.tipo.setText(hospital.getTipo());
        String distancia = String.valueOf(hospital.getDistancia()) + " Km";
        holder.distancia.setText(distancia);
    }

    @Override
    public int getItemCount() {
        return hosptalList.size();
    }

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
}
