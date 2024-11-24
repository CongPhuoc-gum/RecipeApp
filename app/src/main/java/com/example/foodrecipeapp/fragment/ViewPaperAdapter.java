package com.example.foodrecipeapp.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ViewPaperAdapter extends FragmentStatePagerAdapter {

    public ViewPaperAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return 5;
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentHome();
            case 1:
                return new FragmentLibrary();
            case 2:
                return new FragmentMessage();
            case 3:
                return new FragmentFavorites();
            case 4:
                return new FragmentProfile();
            default:
                return new FragmentHome();
        }
    }
}
