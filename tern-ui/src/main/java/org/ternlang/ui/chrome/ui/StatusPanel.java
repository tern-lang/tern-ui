// Copyright (c) 2014 The Chromium Embedded Framework Authors. All rights
// reserved. Use of this source code is governed by a BSD-style license that
// can be found in the LICENSE file.

package org.ternlang.ui.chrome.ui;

import javax.swing.*;

@SuppressWarnings("serial")
public class StatusPanel extends JPanel {
    private final JProgressBar progressBar_;

    public StatusPanel() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        add(Box.createHorizontalStrut(5));
        add(Box.createHorizontalStrut(5));

        progressBar_ = new JProgressBar();
        add(progressBar_);
        add(Box.createHorizontalStrut(5));
        add(Box.createHorizontalStrut(5));
    }

    public void setIsInProgress(boolean inProgress) {
        progressBar_.setIndeterminate(inProgress);
    }
}
