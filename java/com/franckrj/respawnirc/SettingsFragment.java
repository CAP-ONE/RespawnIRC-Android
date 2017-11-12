package com.franckrj.respawnirc;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.SimpleArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.franckrj.respawnirc.utils.PrefsManager;
import com.franckrj.respawnirc.utils.ThemeManager;
import com.franckrj.respawnirc.utils.Utils;
import com.takisoft.fix.support.v7.preference.EditTextPreference;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers;

public class SettingsFragment extends PreferenceFragmentCompatDividers implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String ARG_FILE_TO_LOAD = "com.franckrj.respawnirc.settingsfragment.ARG_FILE_TO_LOAD";

    private SimpleArrayMap<String, MinMaxInfos> listOfMinMaxInfos = new SimpleArrayMap<>();

    private final Preference.OnPreferenceClickListener subScreenPreferenceClicked = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (getActivity() instanceof NewSettingsFileNeedALoad) {
                if (preference.getKey().equals(getString(R.string.subScreenSettingsGeneral))) {
                    ((NewSettingsFileNeedALoad) getActivity()).getNewSettingsFileId(R.xml.general_settings, preference.getTitle().toString());
                    return true;
                } else if (preference.getKey().equals(getString(R.string.subScreenSettingsTheme))) {
                    ((NewSettingsFileNeedALoad) getActivity()).getNewSettingsFileId(R.xml.theme_settings, preference.getTitle().toString());
                    return true;
                } else if (preference.getKey().equals(getString(R.string.subScreenSettingsMessageStyle))) {
                    ((NewSettingsFileNeedALoad) getActivity()).getNewSettingsFileId(R.xml.messagestyle_settings, preference.getTitle().toString());
                    return true;
                } else if (preference.getKey().equals(getString(R.string.subScreenSettingsBehaviour))) {
                    ((NewSettingsFileNeedALoad) getActivity()).getNewSettingsFileId(R.xml.behaviour_settings, preference.getTitle().toString());
                    return true;
                } else if (preference.getKey().equals(getString(R.string.subScreenSettingsImageLink))) {
                    ((NewSettingsFileNeedALoad) getActivity()).getNewSettingsFileId(R.xml.imagelink_settings, preference.getTitle().toString());
                    return true;
                } else if (preference.getKey().equals(getString(R.string.subScreenSettingsIgnored))) {
                    ((NewSettingsFileNeedALoad) getActivity()).getNewSettingsFileId(R.xml.ignored_settings, preference.getTitle().toString());
                    return true;
                } else if (preference.getKey().equals(getString(R.string.subScreenSettingsManageIgnoreList))) {
                    startActivity(new Intent(getActivity(), ManageIgnoreListActivity.class));
                    return true;
                } else if (preference.getKey().equals(getString(R.string.subScreenSettingsHelp))) {
                    HelpSettingsDialogFragment helpDialogFragment = new HelpSettingsDialogFragment();
                    helpDialogFragment.show(getActivity().getSupportFragmentManager(), "HelpSettingsDialogFragment");
                    return true;
                } else if (preference.getKey().equals(getString(R.string.subScreenSettingsShowWebsite))) {
                    Utils.openLinkInExternalBrowser("https://pijon.fr/RespawnIRC-Android/", getActivity());
                    return true;
                }
            }
            return false;
        }
    };

    @Override
    public void onCreatePreferencesFix(Bundle savedInstanceState, String rootKey) {
        int idOfFileToLoad = R.xml.main_settings;

        if (getArguments() != null) {
            idOfFileToLoad = getArguments().getInt(ARG_FILE_TO_LOAD, R.xml.main_settings);
        }

        getPreferenceManager().setSharedPreferencesName(getString(R.string.preference_file_key));
        setPreferencesFromResource(idOfFileToLoad, rootKey);
        initPrefsInfos(getPreferenceScreen());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            return super.onCreateView(inflater, container, savedInstanceState);
        } finally {
            setDividerPreferences(DIVIDER_PREFERENCE_BETWEEN);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);

        if (pref instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) pref;
            MinMaxInfos prefMinMax = listOfMinMaxInfos.get(editTextPref.getKey());
            if (prefMinMax != null) {
                int prefValue = 0;
                if (!editTextPref.getText().isEmpty()) {
                    try {
                        prefValue = Integer.parseInt(editTextPref.getText());
                    } catch (Exception e) {
                        prefValue = 999999999;
                    }
                }
                if (prefValue < prefMinMax.min) {
                    prefValue = prefMinMax.min;
                } else if (prefValue > prefMinMax.max) {
                    prefValue = prefMinMax.max;
                }
                editTextPref.setText(String.valueOf(prefValue));
            }
        } else if (key.equals(getString(R.string.settingsThemeUsed))) {
            ThemeManager.updateThemeUsed();

            if (getActivity() != null) {
                getActivity().recreate();
            }
        }

        updatePrefSummary(pref);
    }

    private void initPrefsInfos(Preference pref) {
        if (pref instanceof PreferenceGroup) {
            PreferenceGroup prefGroup = (PreferenceGroup) pref;
            final int currentPreferenceCount = prefGroup.getPreferenceCount();
            for (int i = 0; i < currentPreferenceCount; i++) {
                initPrefsInfos(prefGroup.getPreference(i));
            }
        } else {
            initClickedListenerIfNeeded(pref);
            initFilterIfNeeded(pref);
            updatePrefDefaultValue(pref);
            updatePrefSummary(pref);
        }
    }

    private void initClickedListenerIfNeeded(Preference pref) {
        if (!pref.isPersistent() && pref.getKey().startsWith("subScreenSettings.")) {
            pref.setOnPreferenceClickListener(subScreenPreferenceClicked);
        }
    }

    private void initFilterIfNeeded(Preference pref) {
        if (pref instanceof EditTextPreference) {
            PrefsManager.StringPref currentPrefsInfos = PrefsManager.getStringInfos(pref.getKey());
            if (currentPrefsInfos.isInt) {
                listOfMinMaxInfos.put(pref.getKey(), new MinMaxInfos(currentPrefsInfos.minVal, currentPrefsInfos.maxVal));
            }
        }
    }

    private void updatePrefDefaultValue(Preference pref) {
        if (pref instanceof CheckBoxPreference) {
            CheckBoxPreference checkBoxPref = (CheckBoxPreference) pref;
            checkBoxPref.setChecked(PrefsManager.getBool(checkBoxPref.getKey()));
        } else if (pref instanceof SwitchPreferenceCompat) {
            SwitchPreferenceCompat switchPref = (SwitchPreferenceCompat) pref;
            switchPref.setChecked(PrefsManager.getBool(switchPref.getKey()));
        } else if (pref instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) pref;
            editTextPref.setText(PrefsManager.getString(editTextPref.getKey()));
        } else if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            listPref.setValue(PrefsManager.getString(listPref.getKey()));
        }
    }

    private void updatePrefSummary(Preference pref) {
        if (pref instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) pref;
            MinMaxInfos prefMinMax = listOfMinMaxInfos.get(editTextPref.getKey());
            if (prefMinMax != null) {
                editTextPref.setSummary("Entre " + String.valueOf(prefMinMax.min) + " et " + String.valueOf(prefMinMax.max) + " : " + editTextPref.getText());
            }
        }
    }

    private static class MinMaxInfos {
        public final int min;
        public final int max;

        MinMaxInfos(int newMin, int newMax) {
            min = newMin;
            max = newMax;
        }
    }

    public interface NewSettingsFileNeedALoad {
        void getNewSettingsFileId(int fileID, String newTitle);
    }

    public static class HelpSettingsDialogFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.help).setMessage(R.string.help_dialog_settings)
                    .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            return builder.create();
        }
    }
}
