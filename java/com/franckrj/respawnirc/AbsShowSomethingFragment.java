package com.franckrj.respawnirc;

import android.app.Fragment;

public abstract class AbsShowSomethingFragment extends Fragment {
    public abstract void setPageLink(String newForumPageLink);
    public abstract void clearContent();
}