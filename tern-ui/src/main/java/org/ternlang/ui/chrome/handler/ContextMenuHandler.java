// Copyright (c) 2014 The Chromium Embedded Framework Authors. All rights
// reserved. Use of this source code is governed by a BSD-style license that
// can be found in the LICENSE file.

package org.ternlang.ui.chrome.handler;

import java.awt.Frame;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefContextMenuParams;
import org.cef.callback.CefMenuModel;
import org.cef.callback.CefMenuModel.MenuId;
import org.cef.handler.CefContextMenuHandler;
import org.ternlang.ui.chrome.dialog.SearchDialog;
import org.ternlang.ui.chrome.dialog.ShowTextDialog;

public class ContextMenuHandler implements CefContextMenuHandler {
    private final Frame owner_;
    private Map<Integer, String> suggestions_ = new HashMap<Integer, String>();

    public ContextMenuHandler(Frame owner) {
        owner_ = owner;
    }

    @Override
    public void onBeforeContextMenu(
            CefBrowser browser, CefFrame frame, CefContextMenuParams params, CefMenuModel model) {
        model.clear();

        model.addItem(MenuId.MENU_ID_CUT, "Cut");
        model.setEnabled(MenuId.MENU_ID_CUT, true);
        model.addItem(MenuId.MENU_ID_COPY, "Copy");
        model.setEnabled(MenuId.MENU_ID_COPY, true);
        model.addItem(MenuId.MENU_ID_PASTE, "Paste");
        model.setEnabled(MenuId.MENU_ID_PASTE, true);
        model.addItem(MenuId.MENU_ID_DELETE, "Delete");
        model.setEnabled(MenuId.MENU_ID_DELETE, true);
        model.addSeparator();
        model.addItem(MenuId.MENU_ID_SELECT_ALL, "Select All");
        model.setEnabled(MenuId.MENU_ID_SELECT_ALL, true);

        // Navigation menu
//        model.addItem(MenuId.MENU_ID_BACK, "Back");
//        model.setEnabled(MenuId.MENU_ID_BACK, browser.canGoBack());
//
//        model.addItem(MenuId.MENU_ID_FORWARD, "Forward");
//        model.setEnabled(MenuId.MENU_ID_FORWARD, browser.canGoForward());
//
//        model.addSeparator();
//        model.addItem(MenuId.MENU_ID_FIND, "Find...");
//        if (params.hasImageContents() && params.getSourceUrl() != null)
//            model.addItem(MenuId.MENU_ID_USER_FIRST, "Download Image...");
//        model.addItem(MenuId.MENU_ID_VIEW_SOURCE, "View Source...");
//
//        Vector<String> suggestions = new Vector<String>();
//        params.getDictionarySuggestions(suggestions);
//
//        // Spell checking menu
//        model.addSeparator();
//        if (suggestions.size() == 0) {
//            model.addItem(MenuId.MENU_ID_NO_SPELLING_SUGGESTIONS, "No suggestions");
//            model.setEnabled(MenuId.MENU_ID_NO_SPELLING_SUGGESTIONS, false);
//            return;
//        }
//
//        int id = MenuId.MENU_ID_SPELLCHECK_SUGGESTION_0;
//        for (String suggestedWord : suggestions) {
//            model.addItem(id, suggestedWord);
//            suggestions_.put(id, suggestedWord);
//            if (++id > MenuId.MENU_ID_SPELLCHECK_SUGGESTION_LAST) break;
//        }
    }
    private static final int MENU_ID_VIEW_SOURCE = MenuId.MENU_ID_VIEW_SOURCE;
    private static final int MENU_ID_FIND = MenuId.MENU_ID_FIND;
    private static final int MENU_ID_USER_FIRST = MenuId.MENU_ID_USER_FIRST;
    
    @Override
    public boolean onContextMenuCommand(CefBrowser browser, CefFrame frame,
            CefContextMenuParams params, int commandId, int eventFlags) {
//        switch (commandId) {
//            case MENU_ID_VIEW_SOURCE:
//                ShowTextDialog visitor =
//                        new ShowTextDialog(owner_, "Source of \"" + browser.getURL() + "\"");
//                browser.getSource(visitor);
//                return true;
//            case MENU_ID_FIND:
//                SearchDialog search = new SearchDialog(owner_, browser);
//                search.setVisible(true);
//                return true;
//            case MENU_ID_USER_FIRST:
//                browser.startDownload(params.getSourceUrl());
//                return true;
//            default:
//                if (commandId >= MenuId.MENU_ID_SPELLCHECK_SUGGESTION_0) {
//                    String newWord = suggestions_.get(commandId);
//                    if (newWord != null) {
//                        System.err.println(
//                                "replacing " + params.getMisspelledWord() + " with " + newWord);
//                        browser.replaceMisspelling(newWord);
//                        return true;
//                    }
//                }
//                return false;
//        }
        return false;
    }

    @Override
    public void onContextMenuDismissed(CefBrowser browser, CefFrame frame) {
        suggestions_.clear();
    }
}
