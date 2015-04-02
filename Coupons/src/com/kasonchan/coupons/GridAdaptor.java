package com.kasonchan.coupons;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GridAdaptor extends BaseAdapter {
  private Context                                  mContext;
  private final ArrayList<HashMap<String, String>> coupons;

  public GridAdaptor(Context c, ArrayList<HashMap<String, String>> coupons) {
    mContext = c;
    this.coupons = coupons;

    // Log coupon
    Log.i("constructor", String.valueOf(coupons.size()));
    Log.i("constructor", coupons.toString());
  }

  @Override
  public int getCount() {
    return coupons.size();
  }

  @Override
  public Object getItem(int position) {
    return null;
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @SuppressLint({ "InflateParams", "ViewHolder" })
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    final String MERCHANT_NAME = "name";
    final String FOLLOWS_COUNT = "followsCount";
    final String DESCRIPTION = "description";

    View grid;
    LayoutInflater inflater = (LayoutInflater) mContext
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    grid = new View(mContext);
    grid = inflater.inflate(R.layout.coupon, null);

    if (convertView == null) {
      TextView merchant = (TextView) grid.findViewById(R.id.coupon_merchant);
      TextView description = (TextView) grid
          .findViewById(R.id.coupon_description);
      TextView followsCount = (TextView) grid
          .findViewById(R.id.coupon_followsCount);
      View shadow = (View) grid.findViewById(R.id.coupon_shadow);

      String descriptionValue = coupons.get(position).get(DESCRIPTION);

      merchant.setText(coupons.get(position).get(MERCHANT_NAME));
      description.setText(descriptionValue);
      followsCount.setText(coupons.get(position).get(FOLLOWS_COUNT));

      // Log getView
      Log.i("getView", position + ": " + coupons.get(position).toString());

      // Set amount off shadow to red
      // Set percent off shadow to blue
      // Set others shows to yellow
      if (descriptionValue.startsWith("$") && descriptionValue.endsWith(" Off")) {
        shadow.setBackgroundColor(mContext.getResources().getColor(
            R.color.red_accent_2));
      } else if (descriptionValue.endsWith(" Off")) {
        shadow.setBackgroundColor(mContext.getResources().getColor(
            R.color.blue_accent_2));
      } else {
        shadow.setBackgroundColor(mContext.getResources().getColor(
            R.color.yellow_accent_2));
      }

    } else {
      grid = (View) convertView;
    }

    // When scroll it updates the coupons
    TextView merchant = (TextView) grid.findViewById(R.id.coupon_merchant);
    TextView description = (TextView) grid
        .findViewById(R.id.coupon_description);
    TextView followsCount = (TextView) grid
        .findViewById(R.id.coupon_followsCount);
    View shadow = (View) grid.findViewById(R.id.coupon_shadow);

    String descriptionValue = coupons.get(position).get(DESCRIPTION);

    merchant.setText(coupons.get(position).get(MERCHANT_NAME));
    description.setText(descriptionValue);
    followsCount.setText(coupons.get(position).get(FOLLOWS_COUNT));

    // Log getView
    Log.i("getView", position + ": " + coupons.get(position).toString());

    // Set amount off shadow to red
    // Set percent off shadow to blue
    // Set others shows to yellow
    if (descriptionValue.startsWith("$") && descriptionValue.endsWith(" Off")) {
      shadow.setBackgroundColor(mContext.getResources().getColor(
          R.color.red_accent_2));
    } else if (descriptionValue.endsWith(" Off")) {
      shadow.setBackgroundColor(mContext.getResources().getColor(
          R.color.blue_accent_2));
    } else {
      shadow.setBackgroundColor(mContext.getResources().getColor(
          R.color.yellow_accent_2));
    }

    return grid;
  }
}
