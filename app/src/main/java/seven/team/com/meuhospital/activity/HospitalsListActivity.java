package seven.team.com.meuhospital.activity;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import seven.team.com.meuhospital.R;
import seven.team.com.meuhospital.adapter.HospitaisListAdapter;
import seven.team.com.meuhospital.model.HospitalModel;

public class HospitalsListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    //widgets
    private RecyclerView recListHospitais;
    private SearchView editsearch;

    //statics
    public static List<HospitalModel> hospitalModelList = new ArrayList<>();

    //vals
    HospitaisListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospitals_list);

        recListHospitais = findViewById(R.id.recListHospitais);

        //Lista Passada
        this.criarHospitaisMocados();

        //config Adapter
        adapter = new HospitaisListAdapter(hospitalModelList);

        //Config RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recListHospitais.hasFixedSize();
        recListHospitais.setLayoutManager(layoutManager);
        recListHospitais.addItemDecoration( new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recListHospitais.setAdapter(adapter);

        recListHospitais.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recListHospitais, new ClickListener() {

            @Override
            public void onClick(View view, int position) {
                Toast.makeText(HospitalsListActivity.this, hospitalModelList.get(position).getNome(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        editsearch = (SearchView) findViewById(R.id.search);
        editsearch.setOnQueryTextListener(this);
    }

    public void criarHospitaisMocados(){

        for (int i= 0; i<=20; i++){
            HospitalModel hospitalModel = new HospitalModel( i , "hospital", "HospitalModel", 0.75*i+1);
            hospitalModelList.add(hospitalModel);
        }
    }


    ///////////////---  SEARCHVIEW IMPLEMENTS  ---/////////////////

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String texted = newText;
        adapter.filter(texted);

        return false;
    }


    ///////////////  INTERFACE  ///////////////////////

    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }


    //////////////////  NEW CLASS  //////////////////////

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}
