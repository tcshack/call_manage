package com.chooloo.www.callmanager.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chooloo.www.callmanager.R;
import com.chooloo.www.callmanager.database.entity.Contact;
import com.chooloo.www.callmanager.ui.activity.MainActivity;
import com.chooloo.www.callmanager.ui.fragment.base.AbsPageFragment;
import com.chooloo.www.callmanager.viewmodel.SharedDialViewModel;
import com.chooloo.www.callmanager.viewmodel.SharedSearchViewModel;

import timber.log.Timber;

/**
 * Works only in MainActivity
 */
public class ContactsPageFragment extends AbsPageFragment {

    private Context mContext;

    private ContactsFragment mContactsFragment;

    public ContactsPageFragment(Context context) {
        this.mContext = context;
        mContactsFragment = new ContactsFragment(mContext);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_page_layout, mContactsFragment).commit();
    }

    @Override
    public void onStart() {
        super.onStart();

        // dialer view model
        SharedDialViewModel sharedDialViewModel = ViewModelProviders.of(getActivity()).get(SharedDialViewModel.class);
        sharedDialViewModel.getNumber().observe(this, s -> {
            if (s == "") s = null;
            mContactsFragment.load(s, null);
        });

        // search bar view model
        SharedSearchViewModel sharedSearchViewModel = ViewModelProviders.of(getActivity()).get(SharedSearchViewModel.class);
        sharedSearchViewModel.getText().observe(this, t -> {
            if (t == "") t = null;
            mContactsFragment.load(null, t);
        });

        mContactsFragment.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        sharedDialViewModel.setIsOutOfFocus(false);
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        sharedDialViewModel.setIsOutOfFocus(true);
                        sharedSearchViewModel.setIsFocused(false);
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        sharedDialViewModel.setIsOutOfFocus(true);
                        sharedSearchViewModel.setIsFocused(false);
                    default:
                        sharedDialViewModel.setIsOutOfFocus(false);
                }
            }
        });
    }

    @Override
    public void onRightClick() {
        ((MainActivity) getActivity()).expandDialer(true);
    }

    @Override
    public void onLeftClick() {
        ((MainActivity) getActivity()).toggleSearchBar();
    }

    @Override
    public int[] getIconsResources() {
        return new int[]{
                R.drawable.ic_dialpad_black_24dp,
                R.drawable.ic_search_black_24dp
        };
    }
}
