// This is a test harness for your module
// You should do something interesting in this harness 
// to test out the module and to provide instructions 
// to users on how to use it by example.


// open a single window
var win = Ti.UI.createWindow({
	backgroundColor:'red'
});

var webView = Ti.UI.createWebView({
	url : 'http://apple.com'
});

win.add(webView);
win.open();

webView.addEventListener('load', generate);

// TODO: write your module tests here


function generate () {
	if (Ti.Platform.name == "android") {
		var fileName = 'myPDF.pdf';
		
		var pdfcreator = require('com.pablog178.pdfcreator.android');
		
		pdfcreator.generatePDF({
			view : webView,
			fileName : fileName
		});

		var f = Ti.Filesystem.getFile(Ti.Filesystem.applicationDataDirectory, fileName);
	    // Ti.Android.currentActivity.startActivity(Ti.Android.createIntent({
	    //     action: Ti.Android.ACTION_VIEW,
	    //     type: 'application/pdf',
	    //     data: f.getNativePath()
	    // }));
		
	}
}

