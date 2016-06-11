///<reference path="def/jquery.d.ts" />
///<reference path="CustomAlert.ts" />
///<reference path="def/js-cookie.d.ts" />

$.ajax({
	type : "GET",
	url : "./rest/SessionCheck/User",
	contentType : "application/json",
	success : function(data, textStatus, jqXHR) {
		if (!data.sessionValid && data.redirect) {
			window.location.href = data.redirectURL;
		}
	},
	error : function(jqXHR, textStatus, errorThrown) {
		if (jqXHR.responseJSON) {
			customAlert(jqXHR.responseJSON.message, true);
		} else {
			customAlert(jqXHR.status + ': ' + jqXHR.statusText, true);
		}
	}
});

$('#username').text(Cookies.get('username'));
