package com.franckrj.respawnirc;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.franckrj.respawnirc.base.AbsHomeIsBackActivity;
import com.franckrj.respawnirc.utils.IgnoreListManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ManageIgnoreListActivity extends AbsHomeIsBackActivity {
    private TextView emptyListMessageText = null;
    private IgnoreListAdapter adapterForIgnoreList = null;
    private boolean listHasChanged = false;

    private final IgnoreListAdapter.OnPseudoRemovedListener listenerForPseudoRemoved = new IgnoreListAdapter.OnPseudoRemovedListener() {
        @Override
        public void onPseudoRemoved(String pseudo) {
            IgnoreListManager.removePseudoFromIgnoredList(pseudo);
            listHasChanged = true;

            if (adapterForIgnoreList.listIsEmpty()) {
                emptyListMessageText.setVisibility(View.VISIBLE);
            }
        }
    };

    private void generateListOfIgnoredPseudos() {
        ArrayList<String> newListOfIgnoredPseudos = new ArrayList<>(Arrays.asList(IgnoreListManager.getListOfIgnoredPseudosInLCAsArray()));
        Collections.sort(newListOfIgnoredPseudos);
        adapterForIgnoreList.setListOfIgnoredPseudos(newListOfIgnoredPseudos);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manageignorelist);
        initToolbar(R.id.toolbar_manageignorelist);

        adapterForIgnoreList = new IgnoreListAdapter(this);
        RecyclerView ignoreListView = findViewById(R.id.ignore_list_manageignorelist);
        emptyListMessageText = findViewById(R.id.text_emptylist_manageignorelist);

        generateListOfIgnoredPseudos();
        adapterForIgnoreList.setOnPseudoRemovedListener(listenerForPseudoRemoved);
        ignoreListView.setNestedScrollingEnabled(false);
        ignoreListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        ignoreListView.setLayoutManager(new LinearLayoutManager(this));
        ignoreListView.setAdapter(adapterForIgnoreList);

        if (adapterForIgnoreList.listIsEmpty()) {
            emptyListMessageText.setVisibility(View.VISIBLE);
        } else {
            emptyListMessageText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        if (listHasChanged) {
            IgnoreListManager.saveListOfIgnoredPseudos();
            listHasChanged = false;
        }

        super.onPause();
    }

    private static class IgnoreListAdapter extends RecyclerView.Adapter<IgnoreListAdapter.IgnoreViewHolder> {
        private LayoutInflater serviceInflater;
        private ArrayList<String> listOfIgnoredPseudos = new ArrayList<>();
        private OnPseudoRemovedListener listenerForPseudoRemoved = null;

        private final View.OnClickListener removeButtonClickedListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getTag() != null && view.getTag() instanceof Integer) {
                    int position = (Integer) view.getTag();
                    if (position != -1) {
                        removeItem(position);
                    }
                }
            }
        };

        public IgnoreListAdapter(Activity parentActivity) {
            serviceInflater = (LayoutInflater) parentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setOnPseudoRemovedListener(OnPseudoRemovedListener newListenerForPseudoRemoved) {
            listenerForPseudoRemoved = newListenerForPseudoRemoved;
        }

        public void setListOfIgnoredPseudos(ArrayList<String> newList) {
            listOfIgnoredPseudos = newList;
            notifyDataSetChanged();
        }

        public void removeItem(int position) {
            if (position < listOfIgnoredPseudos.size()) {
                String pseudoRemoved = listOfIgnoredPseudos.get(position);

                listOfIgnoredPseudos.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, listOfIgnoredPseudos.size() - position);

                if (listenerForPseudoRemoved != null) {
                    listenerForPseudoRemoved.onPseudoRemoved(pseudoRemoved);
                }
            }
        }

        public boolean listIsEmpty() {
            return listOfIgnoredPseudos.isEmpty();
        }

        @NonNull
        @Override
        public IgnoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new IgnoreViewHolder(serviceInflater.inflate(R.layout.ignorelist_row, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull IgnoreViewHolder holder, int position) {
            holder.setCurrentPseudo(listOfIgnoredPseudos.get(position), position);
        }

        @Override
        public int getItemCount() {
            return listOfIgnoredPseudos.size();
        }

        public class IgnoreViewHolder extends RecyclerView.ViewHolder {
            private TextView mainText;
            private ImageButton actionButton;

            public IgnoreViewHolder(View mainView) {
                super(mainView);
                mainText = mainView.findViewById(R.id.text_main_ignorelist_row);
                actionButton = mainView.findViewById(R.id.image_button_action_ignorelist_row);

                actionButton.setTag(-1);
                actionButton.setOnClickListener(removeButtonClickedListener);
            }

            public void setCurrentPseudo(String newPseudo, int newPosition) {
                mainText.setText(newPseudo);
                actionButton.setTag(newPosition);
            }
        }

        public interface OnPseudoRemovedListener {
            void onPseudoRemoved(String pseudo);
        }
    }
}
