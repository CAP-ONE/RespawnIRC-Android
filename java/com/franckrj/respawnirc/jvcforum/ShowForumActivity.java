package com.franckrj.respawnirc.jvcforum;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.franckrj.respawnirc.MainActivity;
import com.franckrj.respawnirc.R;
import com.franckrj.respawnirc.jvctopic.ShowTopicActivity;
import com.franckrj.respawnirc.jvcforum.jvcforumtools.JVCForumGetter;
import com.franckrj.respawnirc.jvcforum.jvcforumtools.ShowForumFragment;
import com.franckrj.respawnirc.AbsShowSomethingFragment;
import com.franckrj.respawnirc.PageNavigationUtil;
import com.franckrj.respawnirc.utils.AddOrRemoveThingToFavs;
import com.franckrj.respawnirc.utils.JVCParser;
import com.franckrj.respawnirc.AbsNavigationViewActivity;
import com.franckrj.respawnirc.utils.Utils;

public class ShowForumActivity extends AbsNavigationViewActivity implements ShowForumFragment.NewTopicWantRead, JVCForumGetter.NewForumNameAvailable,
                                                    JVCForumGetter.ForumLinkChanged, PageNavigationUtil.PageNavigationFunctions,
                                                    AddOrRemoveThingToFavs.ActionToFavsEnded, JVCForumGetter.NewNumberOfMPSetted {
    public static final String EXTRA_NEW_LINK = "com.franckrj.respawnirc.EXTRA_NEW_LINK";

    private static final String SAVE_CURRENT_FORUM_TITLE = "saveCurrentForumTitle";
    private static final String SAVE_REFRESH_NEEDED_NEXT_RESUME = "saveRefreshNeededOnNextResume";
    private static final String SAVE_CURRENT_NUMBER_OF_MP = "saveCurrentNumberOfMP";

    private String currentTitle = "";
    private AddOrRemoveThingToFavs currentTaskForFavs = null;
    private PageNavigationUtil pageNavigation = null;
    private boolean refreshNeededOnNextResume = false;
    private boolean useInternalNavigatorForDefaultOpening = false;
    private String currentNumberOfMP = null;

    public ShowForumActivity() {
        idOfBaseActivity = R.id.action_forum_navigation;
        pageNavigation = new PageNavigationUtil(this);
        pageNavigation.setLastPageNumber(100);
    }

    private void setNewForumLink(String newLink) {
        currentTitle = getString(R.string.app_name);
        setTitle(currentTitle);
        pageNavigation.setCurrentLink(newLink);
        pageNavigation.updateAdapterForPagerView();
        pageNavigation.updateCurrentItemAndButtonsToCurrentLink();
        if (pageNavigation.getCurrentItemIndex() > 0) {
            pageNavigation.clearPageForThisFragment(0);
        }
    }

    private void setTopicOrForum(String link, boolean updateForumFragIfNeeded, String topicName) {
        if (link != null) {
            if (!link.isEmpty()) {
                link = JVCParser.formatThisUrl(link);
            }

            if (JVCParser.checkIfItsForumLink(link)) {
                if (!JVCParser.getPageNumberForThisForumLink(link).isEmpty()) {
                    setNewForumLink(link);
                    return;
                }
            } else if (!JVCParser.getPageNumberForThisTopicLink(link).isEmpty()) {
                Intent newShowTopicIntent = new Intent(this, ShowTopicActivity.class);

                if (updateForumFragIfNeeded) {
                    setNewForumLink(JVCParser.getForumForTopicLink(link));
                }

                if (topicName != null) {
                    newShowTopicIntent.putExtra(ShowTopicActivity.EXTRA_TOPIC_NAME, topicName);
                }
                if (!currentTitle.equals(getString(R.string.app_name))) {
                    newShowTopicIntent.putExtra(ShowTopicActivity.EXTRA_FORUM_NAME, currentTitle);
                }

                newShowTopicIntent.putExtra(ShowTopicActivity.EXTRA_TOPIC_LINK, link);
                startActivity(newShowTopicIntent);
                return;
            }
        }

        Toast.makeText(this, R.string.errorInvalidLink, Toast.LENGTH_SHORT).show();
    }

    private void stopAllCurrentTasks() {
        if (currentTaskForFavs != null) {
            currentTaskForFavs.cancel(true);
            currentTaskForFavs = null;
        }
    }

    private ShowForumFragment getCurrentFragment() {
        return (ShowForumFragment) pageNavigation.getCurrentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pageNavigation.initializePagerView((ViewPager) findViewById(R.id.pager_showforum));
        pageNavigation.initializeNavigationButtons((Button) findViewById(R.id.firstpage_button_showforum), (Button) findViewById(R.id.previouspage_button_showforum),
                        (Button) findViewById(R.id.currentpage_button_showforum), (Button) findViewById(R.id.nextpage_button_showforum), null);
        pageNavigation.updateAdapterForPagerView();

        pageNavigation.setCurrentLink(sharedPref.getString(getString(R.string.prefForumUrlToFetch), ""));
        if (savedInstanceState == null) {
            currentTitle = getString(R.string.app_name);
            onNewIntent(getIntent());
            pageNavigation.updateCurrentItemAndButtonsToCurrentLink();
        } else {
            currentTitle = savedInstanceState.getString(SAVE_CURRENT_FORUM_TITLE, getString(R.string.app_name));
            refreshNeededOnNextResume = savedInstanceState.getBoolean(SAVE_REFRESH_NEEDED_NEXT_RESUME, false);
            getNewNumberOfMP(savedInstanceState.getString(SAVE_CURRENT_NUMBER_OF_MP, null));
            pageNavigation.updateNavigationButtons();
        }
        setTitle(currentTitle);
    }

    @Override
    public void onNewIntent(Intent newIntent) {
        super.onNewIntent(newIntent);
        String newLinkToGo = newIntent.getStringExtra(EXTRA_NEW_LINK);

        if (newLinkToGo != null) {
            setTopicOrForum(newLinkToGo, true, null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences.Editor sharedPrefEdit = sharedPref.edit();
        sharedPrefEdit.putInt(getString(R.string.prefLastActivityViewed), MainActivity.ACTIVITY_SHOW_FORUM);
        sharedPrefEdit.apply();

        if (refreshNeededOnNextResume) {
            refreshNeededOnNextResume = false;
            if (getCurrentFragment() != null) {
                getCurrentFragment().refreshForum();
            }
        }

        useInternalNavigatorForDefaultOpening = sharedPref.getBoolean(getString(R.string.settingsUseInternalNavigator), Boolean.valueOf(getString(R.string.useInternalNavigatorDefault)));
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAllCurrentTasks();
        if (!pageNavigation.getCurrentLink().isEmpty()) {
            SharedPreferences.Editor sharedPrefEdit = sharedPref.edit();
            sharedPrefEdit.putString(getString(R.string.prefForumUrlToFetch), setShowedPageNumberForThisLink(pageNavigation.getCurrentLink(), pageNavigation.getCurrentItemIndex() + 1));
            sharedPrefEdit.apply();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_CURRENT_FORUM_TITLE, currentTitle);
        outState.putBoolean(SAVE_REFRESH_NEEDED_NEXT_RESUME, refreshNeededOnNextResume);
        outState.putString(SAVE_CURRENT_NUMBER_OF_MP, currentNumberOfMP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_showforum, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.action_change_forum_fav_value_showforum).setEnabled(false);

        if (getCurrentFragment() != null) {
            menu.findItem(R.id.action_send_topic_showforum).setEnabled(!Utils.stringIsEmptyOrNull(getCurrentFragment().getLatestListOfInputInAString()) && !pageNavigation.getCurrentLink().isEmpty());

            if (!pseudoOfUser.isEmpty() && getCurrentFragment().getIsInFavs() != null) {
                menu.findItem(R.id.action_change_forum_fav_value_showforum).setEnabled(true);
                if (getCurrentFragment().getIsInFavs()) {
                    menu.findItem(R.id.action_change_forum_fav_value_showforum).setTitle(R.string.removeOfFavs);
                } else {
                    menu.findItem(R.id.action_change_forum_fav_value_showforum).setTitle(R.string.addToFavs);
                }
            }
        } else {
            menu.findItem(R.id.action_send_topic_showforum).setEnabled(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_forum_fav_value_showforum:
                if (currentTaskForFavs == null) {
                    currentTaskForFavs = new AddOrRemoveThingToFavs(!getCurrentFragment().getIsInFavs(), this);
                    currentTaskForFavs.execute(JVCParser.getForumIDOfThisForum(pageNavigation.getCurrentLink()), getCurrentFragment().getLatestAjaxInfos().pref, sharedPref.getString(getString(R.string.prefCookiesList), ""));
                } else {
                    Toast.makeText(ShowForumActivity.this, R.string.errorActionAlreadyRunning, Toast.LENGTH_SHORT).show();
                }

                return true;
            case R.id.action_open_in_browser_showforum:
                if (!useInternalNavigatorForDefaultOpening) {
                    Utils.openLinkInExternalNavigator(pageNavigation.getCurrentLink(), this);
                } else {
                    Utils.openLinkInInternalNavigator(pageNavigation.getCurrentLink(), this);
                }
                return true;
            case R.id.action_send_topic_showforum:
                Intent newSendTopicIntent = new Intent(this, SendTopicToForumActivity.class);
                newSendTopicIntent.putExtra(SendTopicToForumActivity.EXTRA_FORUM_NAME, currentTitle);
                newSendTopicIntent.putExtra(SendTopicToForumActivity.EXTRA_FORUM_LINK, pageNavigation.getCurrentLink());
                newSendTopicIntent.putExtra(SendTopicToForumActivity.EXTRA_INPUT_LIST, getCurrentFragment().getLatestListOfInputInAString());
                startActivity(newSendTopicIntent);
                refreshNeededOnNextResume = true;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void initializeViewAndToolbar() {
        setContentView(R.layout.activity_showforum);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_showforum);
        setSupportActionBar(myToolbar);

        ActionBar myActionBar = getSupportActionBar();
        if (myActionBar != null) {
            myActionBar.setHomeButtonEnabled(true);
            myActionBar.setDisplayHomeAsUpEnabled(true);
        }

        layoutForDrawer = (DrawerLayout) findViewById(R.id.layout_drawer_showforum);
        navigationForDrawer = (NavigationView) findViewById(R.id.navigation_view_showforum);
    }

    @Override
    protected void newForumOrTopicToRead(String link, boolean itsAForum, boolean isWhenDrawerIsClosed) {
        if (itsAForum && !isWhenDrawerIsClosed) {
            setTopicOrForum(link, true, null);
        } else if (!itsAForum && isWhenDrawerIsClosed) {
            setTopicOrForum(link, true, null);
        }
    }

    @Override
    public void setReadNewTopic(String newTopicLink, String newTopicName) {
        setTopicOrForum(newTopicLink, false, newTopicName);
    }

    @Override
    public void getNewForumName(String newForumName) {
        if (!newForumName.isEmpty()) {
            currentTitle = newForumName;
        } else {
            currentTitle = getString(R.string.app_name);
        }
        setTitle(currentTitle);
    }

    @Override
    public void updateForumLink(String newForumLink) {
        pageNavigation.setCurrentLink(newForumLink);
    }

    @Override
    public void extendPageSelection(View buttonView) {
        //rien
    }

    @Override
    public AbsShowSomethingFragment createNewFragmentForRead(String possibleForumLink) {
        ShowForumFragment currentFragment = new ShowForumFragment();

        if (possibleForumLink != null) {
            Bundle argForFrag = new Bundle();
            argForFrag.putString(ShowForumFragment.ARG_FORUM_LINK, possibleForumLink);
            currentFragment.setArguments(argForFrag);
        }

        return currentFragment;
    }

    @Override
    public int getShowablePageNumberForThisLink(String link) {
        return ((Integer.parseInt(JVCParser.getPageNumberForThisForumLink(link)) - 1) / 25) + 1;
    }

    @Override
    public String setShowedPageNumberForThisLink(String link, int newPageNumber) {
        return JVCParser.setPageNumberForThisForumLink(link, ((newPageNumber - 1) * 25) + 1);
    }

    @Override
    public void getActionToFavsResult(String resultInString, boolean itsAnError) {
        if (itsAnError) {
            if (resultInString.isEmpty()) {
                resultInString = getString(R.string.errorInfosMissings);
            }
            Toast.makeText(this, resultInString, Toast.LENGTH_SHORT).show();
        } else {
            if (currentTaskForFavs.getAddToFavs()) {
                resultInString = getString(R.string.favAdded);
            } else {
                resultInString = getString(R.string.favRemoved);
            }
            Toast.makeText(this, resultInString, Toast.LENGTH_SHORT).show();
            getCurrentFragment().setIsInFavs(currentTaskForFavs.getAddToFavs());
        }
        currentTaskForFavs = null;
    }

    @Override
    public void getNewNumberOfMP(String newNumber) {
        currentNumberOfMP = newNumber;
        updateMpNumberShowed(newNumber);
    }
}
