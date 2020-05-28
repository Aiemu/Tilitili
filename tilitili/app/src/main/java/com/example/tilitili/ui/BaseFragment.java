package com.example.tilitili.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.tilitili.LoginActivity;
import com.example.tilitili.UserManagerApplication;
import com.example.tilitili.data.User;
import com.lidroid.xutils.ViewUtils;

public abstract class BaseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(inflater,container,savedInstanceState);
        ViewUtils.inject(this, view);
        initToolBar();
        init();
        return view;
    }

    public void  initToolBar(){
    }

    public abstract View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public abstract void init();
    public void startActivity(Intent intent, boolean isNeedLogin){
        if(isNeedLogin){
            User user = UserManagerApplication.getInstance().getUser();
            if(user !=null){
                super.startActivity(intent);
            }
            else{
//                UserManagerApplication.getInstance().putIntent(intent);
                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                super.startActivity(loginIntent);
            }
        }
        else{
            super.startActivity(intent);
        }
    }
}
