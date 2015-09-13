package com.ericpol.hotmeals;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ericpol.hotmeals.Entities.Dish;
import com.ericpol.hotmeals.Entities.Order;
import com.ericpol.hotmeals.Entities.Supplier;
import com.google.gson.Gson;

import org.apache.http.client.methods.HttpPostHC4;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class LowerBarFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lower_bar, container, false);
        if (getActivity().getIntent().hasExtra("order")) {
            double price = ((ConfirmOrderListFragment) getFragmentManager().findFragmentById(R.id.fragment_confirm)).getTotalPrice();
            ((TextView) rootView.findViewById(R.id.total_price_value)).setText(Double.toString(price));
            ((Button) rootView.findViewById(R.id.subimit_order_button)).setText(getResources().getString(R.string.submit_order_button_text));
            ((Button) rootView.findViewById(R.id.subimit_order_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast toast = Toast.makeText(getActivity(), getResources().getString(R.string.successful_submit_toast) , Toast.LENGTH_SHORT);
                    toast.show();
                    Intent intent = new Intent(getActivity(), SuppliersActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        } else {
            ((Button) rootView.findViewById(R.id.subimit_order_button)).setText(getResources().getString(R.string.check_out_button_text));
            ((Button) rootView.findViewById(R.id.subimit_order_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int supplierId = getActivity().getIntent().getIntExtra("supplier_id", -1);
                    DishesListFragment fragment = (DishesListFragment) getFragmentManager().findFragmentById(R.id.fragment);
                    Intent intent = new Intent(getActivity(), ConfirmOrderActivity.class);
                    intent.putExtra("supplier_id", supplierId);
                    intent.putExtra("order", fragment.formOrder());
                    startActivity(intent);
                }
            });
        }
        return rootView;
    }

    private void POST(Order order) {
        Gson gson = new Gson();
        String json = gson.toJson(order);
    }


}
