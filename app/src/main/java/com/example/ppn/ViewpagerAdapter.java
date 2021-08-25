package com.example.ppn;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 *
 * A class that control the {@link androidx.viewpager2.widget.ViewPager2} that control the fragment the user see, the movement is based on the {@link android.widget.TableLayout} position
 *
 */
public class ViewpagerAdapter extends FragmentStateAdapter {

    public ViewpagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager,lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) { //get the tab position a knows what fragment to show
            case 0:
                return new HomePage();

             case 1:
                return new KeyWords();
             case 2:
                return new PointsAndGroups();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

}
