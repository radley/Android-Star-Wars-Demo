package dev.radley.omgstarwars.activity.detail;


import android.widget.TextView;


import androidx.lifecycle.ViewModelProviders;

import java.io.Serializable;

import dev.radley.omgstarwars.R;
import dev.radley.omgstarwars.model.viewmodel.detail.VehicleDetailViewModel;

public class VehicleActivity extends BaseDetailActivity {

    protected VehicleDetailViewModel mViewModel;

    @Override
    protected void init(Serializable resource) {

        mViewModel = ViewModelProviders.of(this).get(VehicleDetailViewModel.class);
        mViewModel.setModel(resource);

        mActionBar.setTitle(mViewModel.getTitle());
        updateHeroImage(mViewModel.getHeroImage(), mViewModel.getHeroPlaceholderRes(), mViewModel.getHeroFallbackRes());
    }

    @Override
    protected void populateDetails() {

        insertDetailView(R.layout.view_detail_vehicle);

        ((TextView) mDetailView.findViewById(R.id.model)).setText(mViewModel.getModel());
        ((TextView) mDetailView.findViewById(R.id.manufacturer)).setText(mViewModel.getManufacturer());
        ((TextView) mDetailView.findViewById(R.id.crew)).setText(getFormattedNumber(mViewModel.getCrew()));
        ((TextView) mDetailView.findViewById(R.id.passengers)).setText(getFormattedNumber(mViewModel.getPassengers()));
        ((TextView) mDetailView.findViewById(R.id.consumables)).setText(getFormattedNumber(mViewModel.getConsumables()));
        ((TextView) mDetailView.findViewById(R.id.length)).setText(mViewModel.getLength());
        ((TextView) mDetailView.findViewById(R.id.cargo_capacity)).setText(mViewModel.getCargoCapacity());
        ((TextView) mDetailView.findViewById(R.id.cost_in_credits)).setText(mViewModel.getCostInCredits());
        ((TextView) mDetailView.findViewById(R.id.max_atmosphering_speed)).setText(mViewModel.getMaxAtmospheringSpeed());



        addListViews();
    }

    @Override
    protected void addListViews() {

        if(mViewModel.hasRelatedFilms()) {
            addFilmList(mViewModel.getFilmsUrls(), mViewModel.getFilmsRowTitle());
        }

        if(mViewModel.hasRelatedPeople()){
            addPeopleList(mViewModel.getPeopleUrls(), mViewModel.getPeopleRowTitle());
        }
    }
}
