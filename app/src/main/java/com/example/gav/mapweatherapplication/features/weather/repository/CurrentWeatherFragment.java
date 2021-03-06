package com.example.gav.mapweatherapplication.features.weather.repository;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gav.mapweatherapplication.R;
import com.example.gav.mapweatherapplication.features.weather.WeatherContract;
import com.example.gav.mapweatherapplication.features.weather.model.ResultItem;
import com.example.gav.mapweatherapplication.features.weather.model.current.CurrentWeatherResponse;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CurrentWeatherFragment extends Fragment implements WeatherContract.View{

    public static final String TAG = "CurrentWeatherFragment";
    private WeatherContract.Presenter presenter;
    private int mode;
    private double latitude;
    private double longitude;

    private Unbinder unbinder;

    @BindView(R.id.tvTemperature)
    protected TextView tvTemperature;

    @BindView(R.id.tvLocation)
    protected TextView tvLocation;

    @BindView(R.id.tvHumidity)
    protected TextView tvHumidity;

    @BindView(R.id.tvPressure)
    protected TextView tvPressure;

    @BindView(R.id.tvWind)
    protected TextView tvWind;

    @BindView(R.id.pbLoadWeather)
    protected ProgressBar pbLoadWeather;

    public static CurrentWeatherFragment newInstance(double latitude, double longitude, int mode) {
        CurrentWeatherFragment fragment = new CurrentWeatherFragment();
        fragment.latitude = latitude;
        fragment.longitude = longitude;
        fragment.mode = mode;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_weather, container, false);
        unbinder = ButterKnife.bind(this, view);
        initViews(view);
        presenter.onCreate();
        presenter.loadWeather(latitude, longitude, mode);
        return view;
    }

    private void initViews(View view) {
    }

    @Override
    public void showWeather(List<ResultItem> weatherItems) {}

    @Override
    public void showWeather(CurrentWeatherResponse currentWeatherResponse) {
        tvLocation.setText(currentWeatherResponse.getName());
        tvTemperature.setText(
                Integer.toString(
                        (int)Math.round(currentWeatherResponse.getMain().getTemp())
                ) + getString(R.string.celsium)
        );
        tvWind.setText(
                Double.toString(
                        currentWeatherResponse.getWind().getSpeed()
                ) + getString(R.string.ms)
        );
        tvHumidity.setText(
                Integer.toString(
                        currentWeatherResponse.getMain().getHumidity()
                ) + getString(R.string.percent)
        );
        tvPressure.setText(
                Integer.toString(
                        (int)currentWeatherResponse.getMain().getPressure()
                ) + getString(R.string.mm)
        );
    }

    @Override
    public void errorShowWeather(Throwable throwable) {
        hideProgressbar();
        View viewRoot = getView();
        if (viewRoot != null) {
            Snackbar
                    .make(viewRoot, getActivity().getResources().getString(R.string.repeat_text), Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", view -> {
                        presenter.loadWeather(latitude, longitude, mode);
                    }).show();
        }
    }

    @Override
    public void showProgressbar() {
        pbLoadWeather.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressbar() {
        pbLoadWeather.setVisibility(View.INVISIBLE);
    }

    @Override
    public void updateToolbar(String name) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setTitle(name);
        }
    }

    @Override
    public void setPresenter(WeatherContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        presenter.onDestroy();
    }
}
