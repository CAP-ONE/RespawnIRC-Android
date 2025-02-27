package com.franckrj.respawnirc.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.text.Spannable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.widget.EditText;

import androidx.annotation.ColorInt;
import androidx.emoji.text.EmojiCompat;

import com.franckrj.respawnirc.MainActivity;
import com.franckrj.respawnirc.R;
import com.franckrj.respawnirc.WebBrowserActivity;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utils {
    public static String colorToString(@ColorInt int colorValue) {
        return String.format("#%06X", 0xFFFFFF & colorValue);
    }

    public static String colorToStringWithAlpha(@ColorInt int colorValue) {
        return String.format("#%08X", colorValue);
    }

    public static int roundToInt(double valToRound) {
        return (int) (valToRound + 0.5);
    }

    public static long roundToLong(double valToRound) {
        return (long) (valToRound + 0.5);
    }

    public static boolean stringsAreEquals(String str1, String str2) {
        return (Objects.equals(str1, str2));
    }

    public static String truncateString(String baseString, int maxSize, String endingPartIfCuted) {
        if (baseString.length() > maxSize) {
            baseString = baseString.substring(0, maxSize - endingPartIfCuted.length()) + endingPartIfCuted;
        }

        return baseString;
    }

    public static boolean stringIsEmptyOrNull(String thisString) {
        //noinspection SimplifiableIfStatement
        if (thisString == null) {
            return true;
        }
        return thisString.isEmpty();
    }

    public static void showSoftKeyboard(Activity forThisActivity) {
        InputMethodManager inputManager = (InputMethodManager) forThisActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void hideSoftKeyboard(Activity fromThisActivity) {
        InputMethodManager inputManager = (InputMethodManager) fromThisActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = fromThisActivity.getCurrentFocus();
        if (inputManager != null && focusedView != null) {
            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void replaceSpanByAnotherSpan(Spannable inThisSpan, Object oldSpan, Object newSpan) {
        int start = inThisSpan.getSpanStart(oldSpan);
        int end = inThisSpan.getSpanEnd(oldSpan);
        int flags = inThisSpan.getSpanFlags(oldSpan);
        inThisSpan.setSpan(newSpan, start, end, flags);
        inThisSpan.removeSpan(oldSpan);
    }

    public static String encodeStringToUrlString(String baseString) {
        try {
            baseString = URLEncoder.encode(baseString, "UTF-8");
        } catch (Exception e) {
            baseString = "";
        }
        return baseString;
    }

    public static String decodeUrlStringToString(String baseString) {
        try {
            baseString = URLDecoder.decode(baseString, "UTF-8");
        } catch (Exception e) {
            baseString = "";
        }
        return baseString;
    }

    public static ArrayList<String> mapStringArrayList(List<String> baseList, StringModifier mapFunction) {
        ArrayList<String> newList = new ArrayList<>();

        for (String currentString : baseList) {
            newList.add(mapFunction.changeString(currentString));
        }

        return newList;
    }

    public static String imageLinkToFileName(String link) {
        if (link.startsWith("http://image.noelshack.com/minis/")) {
            return "nlsk_mini/" + link.substring(("http://image.noelshack.com/minis/").length()).replace("/", "_");
        } else if (link.startsWith("http://image.noelshack.com/fichiers-xs/")) {
            return "nlsk_xs/" + link.substring(("http://image.noelshack.com/fichiers-xs/").length()).replace("/", "_");
        } else if (link.startsWith("http://image.noelshack.com/fichiers/")) {
            return "nlsk_big/" + link.substring(("http://image.noelshack.com/fichiers/").length()).replace("/", "_");
        } else if (link.startsWith("https://image.jeuxvideo.com/avatar")) {
            return "vtr_sm/" + link.substring(("https://image.jeuxvideo.com/avatar").length()).replace("/", "_");
        } else if (link.startsWith("http://image.jeuxvideo.com/avatar")) {
            return "vtr_sm/" + link.substring(("http://image.jeuxvideo.com/avatar").length()).replace("/", "_");
        } else {
            return "";
        }
    }

    public static void setupCookiesForJvc(CookieManager cookieManager) {
        cookieManager.removeAllCookies(null);
        cookieManager.setCookie("https://www.jeuxvideo.com", "_cmpQcif3pcsupported=1");
        cookieManager.setCookie("https://www.jeuxvideo.com", "euconsent=BO1ximpO1ximpAKAiCENDQAAAAAweAAA");
        cookieManager.setCookie("https://www.jeuxvideo.com", "googlepersonalization=O1ximpO1ximpAA");
        cookieManager.setCookie("https://www.jeuxvideo.com", "noniabvendorconsent=O1ximpO1ximpAKAiAA8AAA");
        cookieManager.setCookie("https://www.jeuxvideo.com", "visitor_country=FR");
        cookieManager.setCookie("https://www.jeuxvideo.com", "_gcl_au=1.1.932555791.1601975855");
        cookieManager.setCookie("https://www.jeuxvideo.com", "_ga=GA1.2.314248087.1601975855");
        cookieManager.setCookie("https://www.jeuxvideo.com", "_gid=GA1.2.241172429.1601975855");
        cookieManager.setCookie("https://www.jeuxvideo.com", "_gat=1");
        cookieManager.setCookie("https://www.jeuxvideo.com", "didomi_token=eyJ1c2VyX2lkIjoiMTc0ZmQzM2EtMjRjNy02OWEyLWE1YjMtZjdlYzA3YWYxNjU3IiwiY3JlYXRlZCI6IjIwMjAtMTAtMDZUMDk6MjA6MDYuMzU4WiIsInVwZGF0ZWQiOiIyMDIwLTEwLTA2VDA5OjIwOjA2LjM1OFoiLCJ2ZXJzaW9uIjoyLCJwdXJwb3NlcyI6eyJlbmFibGVkIjpbImRldmljZV9jaGFyYWN0ZXJpc3RpY3MiLCJnZW9sb2NhdGlvbl9kYXRhIl19LCJ2ZW5kb3JzIjp7ImVuYWJsZWQiOlsiZ29vZ2xlIiwiYzpkbXB3ZWJlZGktblRCSEFrNDQiLCJjOmJhdGNoLWJKdEd0dHhMIiwiYzphbWF6b250YW0tZVk0aU40TlYiLCJjOndhcm5lcmJyby1BUEpXeUFHUCIsImM6c25hcGNoYXQtaFcyck1KZlkiLCJjOnRpa3Rvay1XYnlwQTNaZCIsImM6dHdpdHRlci14YkRFeEpQayIsImM6ZmFjZWJvb2std0RpR25KV1YiXX0sInZlbmRvcnNfbGkiOnsiZW5hYmxlZCI6WyJnb29nbGUiXX19");
        cookieManager.setCookie("https://www.jeuxvideo.com", "euconsent-v2=CO62iPAO62iPAAHABBENA5CsAP_AAH_AAAAAGvNf_X_fb2_j-_5999t0eY1f9_6_v2wzjgeds-8Nyd_X_L8X62MyvB36pq4KuR4Eu3LBAQdlHOHcTQmQ4IkVqTLsbk2Mq7NKJ7LEilMbM2dYGH9vn9XTuZKY70_s___z_3-_-___77f_r-3_3_A14Akw1L4CDMSxgJJo0qhRAhCuJDoAQAUUIwtElhASuCnZXAR6ggQAIDUBGBECDEFGLIIAAAAAkoiAEgPBAIgCIBAACAFaAhAARIAgsAJAwCAAUA0LACKIJQJCDI4KjlECAqRaKCeSMCSC52MMIAAA.f_gAD_gAAAAA");
        cookieManager.setCookie("https://jeuxvideo.com", "_gcl_au=1.1.1298996599.1593456467");
        cookieManager.setCookie("https://jeuxvideo.com", "_gcl_au=1.1.932555791.1601975855");
        cookieManager.setCookie("https://jeuxvideo.com", "_ga=GA1.2.314248087.1601975855");
        cookieManager.setCookie("https://jeuxvideo.com", "_gid=GA1.2.241172429.1601975855");
        cookieManager.setCookie("https://jeuxvideo.com", "_gat=1");
        cookieManager.setCookie("https://jeuxvideo.com", "didomi_token=eyJ1c2VyX2lkIjoiMTc0ZmQzM2EtMjRjNy02OWEyLWE1YjMtZjdlYzA3YWYxNjU3IiwiY3JlYXRlZCI6IjIwMjAtMTAtMDZUMDk6MjA6MDYuMzU4WiIsInVwZGF0ZWQiOiIyMDIwLTEwLTA2VDA5OjIwOjA2LjM1OFoiLCJ2ZXJzaW9uIjoyLCJwdXJwb3NlcyI6eyJlbmFibGVkIjpbImRldmljZV9jaGFyYWN0ZXJpc3RpY3MiLCJnZW9sb2NhdGlvbl9kYXRhIl19LCJ2ZW5kb3JzIjp7ImVuYWJsZWQiOlsiZ29vZ2xlIiwiYzpkbXB3ZWJlZGktblRCSEFrNDQiLCJjOmJhdGNoLWJKdEd0dHhMIiwiYzphbWF6b250YW0tZVk0aU40TlYiLCJjOndhcm5lcmJyby1BUEpXeUFHUCIsImM6c25hcGNoYXQtaFcyck1KZlkiLCJjOnRpa3Rvay1XYnlwQTNaZCIsImM6dHdpdHRlci14YkRFeEpQayIsImM6ZmFjZWJvb2std0RpR25KV1YiXX0sInZlbmRvcnNfbGkiOnsiZW5hYmxlZCI6WyJnb29nbGUiXX19");
        cookieManager.setCookie("https://jeuxvideo.com", "euconsent-v2=CO62iPAO62iPAAHABBENA5CsAP_AAH_AAAAAGvNf_X_fb2_j-_5999t0eY1f9_6_v2wzjgeds-8Nyd_X_L8X62MyvB36pq4KuR4Eu3LBAQdlHOHcTQmQ4IkVqTLsbk2Mq7NKJ7LEilMbM2dYGH9vn9XTuZKY70_s___z_3-_-___77f_r-3_3_A14Akw1L4CDMSxgJJo0qhRAhCuJDoAQAUUIwtElhASuCnZXAR6ggQAIDUBGBECDEFGLIIAAAAAkoiAEgPBAIgCIBAACAFaAhAARIAgsAJAwCAAUA0LACKIJQJCDI4KjlECAqRaKCeSMCSC52MMIAAA.f_gAD_gAAAAA");
    }

    public static void openCorrespondingBrowser(PrefsManager.LinkType linkTypeToOpenInternalBrowser, String link, Activity parentActivity) {
        boolean itsAJVCLink = link.matches("(?i)^http(s)?://((www|m)\\.)?jeuxvideo\\.com$") ||
                              link.matches("(?i)^http(s)?://((www|m)\\.)?jeuxvideo\\.com/.*");

        if (linkTypeToOpenInternalBrowser.type == PrefsManager.LinkType.ALL_LINKS ||
                (linkTypeToOpenInternalBrowser.type == PrefsManager.LinkType.JVC_LINKS_ONLY && itsAJVCLink)) {
            openLinkInInternalBrowser(link, parentActivity);
        } else {
            openLinkInExternalBrowser(link, parentActivity);
        }
    }

    public static void openLinkInExternalBrowser(String link, Activity parentActivity) {
        try {
            Intent browserIntent;

            if (JVCParser.checkIfItsOpennableFormatedLink(JVCParser.formatThisUrlToClassicJvcUrl(link))) {
                browserIntent = Intent.createChooser(new Intent(Intent.ACTION_VIEW, Uri.parse(link)), parentActivity.getString(R.string.chooseBrowser));
            } else {
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            }

            parentActivity.startActivity(browserIntent);
        } catch (Exception e) {
            //rien
        }
    }

    public static void openLinkInInternalBrowser(String link, Activity parentActivity) {
        Intent newBrowserIntent = new Intent(parentActivity, WebBrowserActivity.class);
        newBrowserIntent.putExtra(WebBrowserActivity.EXTRA_URL_LOAD, link);
        parentActivity.startActivity(newBrowserIntent);
    }

    public static void shareThisLink(String link, Activity parentActivity) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, link);
        parentActivity.startActivity(Intent.createChooser(sharingIntent, parentActivity.getString(R.string.share)));
    }

    public static void putStringInClipboard(String textToCopy, Activity fromThisActivity) {
        ClipboardManager clipboard = (ClipboardManager) fromThisActivity.getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText(textToCopy, textToCopy);
            clipboard.setPrimaryClip(clip);
        }
    }

    public static void insertStringInEditText(EditText currentEditText, String stringToInsert, int posOfCenterOfString) {
        int currentCursorPos = currentEditText.getSelectionStart();
        int currentEndOfSelec = currentEditText.getSelectionEnd();
        if (currentCursorPos == -1) {
            currentCursorPos = 0;
        }
        if (posOfCenterOfString < 0) {
            posOfCenterOfString = stringToInsert.length();
        }
        if (currentEndOfSelec > currentCursorPos && posOfCenterOfString < stringToInsert.length()) {
            String firstStringToAdd = stringToInsert.substring(0, posOfCenterOfString);
            String secondStringToAdd = stringToInsert.substring(posOfCenterOfString);
            currentEditText.getText().insert(currentEndOfSelec, secondStringToAdd);
            currentEditText.getText().insert(currentCursorPos, firstStringToAdd);
            currentEditText.setSelection(currentEndOfSelec + posOfCenterOfString);
        } else {
            currentEditText.getText().insert(currentCursorPos, stringToInsert);
            currentEditText.setSelection(currentCursorPos + posOfCenterOfString);
        }
    }

    public static CharSequence applyEmojiCompatIfPossible(CharSequence baseMessage) {
        if (EmojiCompat.get().getLoadState() == EmojiCompat.LOAD_STATE_SUCCEEDED) {
            return EmojiCompat.get().process(baseMessage);
        } else {
            return baseMessage;
        }
    }

    @TargetApi(25)
    public static void updateShortcuts(Activity parentActivity, ShortcutManager shortcutManager, int sizeOfForumFavArray) {
        ArrayList<ShortcutInfo> listOfShortcuts = new ArrayList<>();
        int sizeOfShortcutArray = Math.min(sizeOfForumFavArray, 4);

        for (int i = 0; i < sizeOfShortcutArray; ++i) {
            String currentShortcutLink = PrefsManager.getStringWithSufix(PrefsManager.StringPref.Names.FORUM_FAV_LINK, String.valueOf(i));
            String currentShortcutName = PrefsManager.getStringWithSufix(PrefsManager.StringPref.Names.FORUM_FAV_NAME, String.valueOf(i));
            ShortcutInfo newShortcut = new ShortcutInfo.Builder(parentActivity, String.valueOf(i) + "_" + currentShortcutLink)
                    .setShortLabel(currentShortcutName)
                    .setLongLabel(currentShortcutName)
                    .setIcon(Icon.createWithResource(parentActivity, R.mipmap.ic_shortcut_forum))
                    .setIntent(new Intent(MainActivity.ACTION_OPEN_SHORTCUT, Uri.parse(currentShortcutLink), parentActivity, MainActivity.class)).build();

            listOfShortcuts.add(newShortcut);
        }

        try {
            shortcutManager.setDynamicShortcuts(listOfShortcuts);
        } catch (Exception e) {
            /* À ce qu'il parait ça peut crash "when the user is locked", je sais pas ce que ça
             * veut dire donc dans le doute je mets ça là. */
        }
    }

    public interface StringModifier {
        String changeString(String baseString);
    }
}
