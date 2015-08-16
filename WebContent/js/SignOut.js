$("#sign-out").click(function(event) {
	$.ajax({
		type : "POST",
		url : "./rest/SignOut/",
		success : function(data, textStatus, jqXHR) {
			customAlert(data.message);
			window.location.href = "./index.html";
		},
		error : function(jqXHR, textStatus, errorThrown) {
			customAlert(jqXHR.message, true);
		}
	});
});
