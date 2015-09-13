$.ajax({
	type : "GET",
	url : "./rest/SessionCheck/",
	contentType : "application/json",
	success : function(data, textStatus, jqXHR) {
		if (data.sessionValid && data.redirect) {
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

$("form").submit(function(e){
	e.preventDefault();
	var user = {
			username: $('#username').val(),
			password: $('#password').val()
	};
	$.ajax({
		type : "POST",
		url : "./rest/SignIn/",
		contentType : "application/json",
		data : JSON.stringify(user),
		success : function(data, textStatus, jqXHR) {
			if (data.redirect) {
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
});
