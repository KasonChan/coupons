package com.kasonchan.coupons;

import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar;

@SuppressWarnings("deprecation")
public class TabListener extends Activity implements ActionBar.TabListener {

  private Fragment fragment;

  // Contructor.
  public TabListener(Fragment fragment) {
    this.fragment = fragment;
  }

  // When a tab is tapped, replace with specified fragment
  @Override
  public void onTabSelected(Tab tab, FragmentTransaction ft) {

    ft.replace(R.id.activity_main, fragment);
  }

  // When a tab is unselected, hide it from the user's view
  @Override
  public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    ft.remove(fragment);
  }

  @Override
  public void onTabReselected(Tab tab, FragmentTransaction ft) {

  }
}
