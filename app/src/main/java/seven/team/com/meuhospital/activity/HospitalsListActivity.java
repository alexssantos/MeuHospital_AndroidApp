package seven.team.com.meuhospital.activity;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import seven.team.com.meuhospital.R;
import seven.team.com.meuhospital.adapter.HospitaisListAdapter;
import seven.team.com.meuhospital.model.HospitalModel;

public class HospitalsListActivity extends AppCompatActivity {

    private RecyclerView recListHospitais;
    private List<HospitalModel> hospitalModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospitals_list);

        recListHospitais = findViewById(R.id.recListHospitais);

        //Lista Passada
        this.criarHospitaisMocados();

        //config Adapter
        HospitaisListAdapter adapter = new HospitaisListAdapter(hospitalModelList);

        //Config RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recListHospitais.hasFixedSize();
        recListHospitais.setLayoutManager(layoutManager);
        recListHospitais.addItemDecoration( new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recListHospitais.setAdapter(adapter);

    }

    public void criarHospitaisMocados(){

        for (int i= 0; i<=20; i++){
            HospitalModel hospitalModel = new HospitalModel( i , "HospitalModel" + i, "HospitalModel", 0.75*i+1);
            hospitalModelList.add(hospitalModel);
        }

    }
}
