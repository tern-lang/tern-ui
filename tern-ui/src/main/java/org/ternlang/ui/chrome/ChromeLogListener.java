package org.ternlang.ui.chrome;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;

public class ChromeLogListener implements ChromeFrameListener {
	
	@Override
	public void onAddressChange(CefBrowser browser, CefFrame frame, String location) {
		System.err.println("onAddressChange(" + location + ")");
	}

	@Override
	public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {
		System.err.println("onLoadingStateChange(" + isLoading + ")");
	}

	@Override
	public void onStatusMessage(CefBrowser browser, String message) {
		System.err.println("onStatusMessage(" + message + ")");
	}

}
