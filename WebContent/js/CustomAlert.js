function customAlert(text, errorBoolean) {
	var alertDiv = $('#alert');
	alertDiv.removeAttr('hidden');
	if (errorBoolean == true) {
		alertDiv.removeClass( "alert-success" ).addClass( "alert-danger" );
	} else {
		alertDiv.removeClass( "alert-danger" ).addClass( "alert-success" );
	}
	if (text === undefined) {
		text = 'undefined';
	}
	alertDiv.text(text);
}
