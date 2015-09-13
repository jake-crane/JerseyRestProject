$.ajax({
	type : "GET",
	url : "./rest/SessionCheck/Admin",
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

function generateNewUsername() {
	var newUsernameInput = $('#newUsername');
	newUsernameInput.val("TestName" + Math.floor((Math.random() * 10000)));
}

function update() {
	var table = $("#mainTable");

	$.ajax({
		type : "GET",
		url : "./rest/Users/",
		success : function(data, textStatus, jqXHR) {
			updateDOM(data);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			if (jqXHR.responseJSON) {
				customAlert(jqXHR.responseJSON.message, true);
			} else {
				customAlert(jqXHR.status + ': ' + jqXHR.statusText, true);
			}
		}
	});

	function updateDOM(users) {
		table.find('tr:gt(0)').remove();
		table.find('caption').remove();
		table.append('<caption>' + '<h3>Users</h3>' + '<div>Last Updated ' + new Date() + '</div>' + '</caption>');
		$.each(users, function(key, val) {
			table.append('<tr id="' + val.username + '">'
					+ '<td>' + val.userId + '</td>'
					+ '<td>' + val.username + '</td>'
					+ '<td>' + val.firstName + '</td>'
					+ '<td>' + (val.middleName || '') + '</td>'
					+ '<td>' + val.lastName + '</td>'
					+ '<td>' + val.address + '</td>'
					+ '<td>' + (val.aptSuiteOther || '') + '</td>'
					+ '<td>' + val.city + '</td>'
					+ '<td>' + val.state + '</td>'
					+ '<td>' + val.zipCode + '</td>'
					+ '<td>' + val.phoneNumber + '</td>'
					+ '<td>' + (val.emailAddress || '') + '</td>'
					+ '<td>' + val.birthDate + '</td>'
					+ '<td><button id="delete_' + val.username + '" class="btn btn-warning btn-xs" data-toggle="confirmation" data-placement="top">Delete</button></td>'
					+ '</tr>');
			var deleteButton = $('#delete_' + val.username);
			deleteButton.confirmation({
				onConfirm: function(event) {
					deleteUser(val.username);
				},
				onCancel: function(event) {}
			});
		});
	}
}

$('#update').click(function(event) {
	update();
});

function createUser() {
	var myData = new Object();
	myData.username = $("#newUsername").val();
	myData.password = $("#newPassword").val();
	myData.firstName = $("#newFirstName").val();
	myData.middleName = $("#newMiddleName").val();
	myData.lastName = $("#newLastName").val();
	myData.address = $("#newAddress").val();
	myData.aptSuiteOther = $("#newAptSuiteOther").val();
	myData.city = $("#newCity").val();
	myData.state = $("#newState").val();
	myData.zipCode = $("#newZipCode").val();
	myData.phoneNumber = $("#newPhoneNumber").val();
	myData.emailAddress = $("#newEmailAddress").val();
	myData.birthDate = new Date($("#newBirthDate").val());
	$.ajax({
		type : "PUT",
		url : "./rest/Users/",
		contentType : "application/json",
		data : JSON.stringify(myData),
		success : function(data, textStatus, jqXHR) {
			update();
			generateNewUsername();
			customAlert(data.message);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			if (jqXHR.responseJSON) {
				customAlert(jqXHR.responseJSON.message, true);
			} else {
				customAlert(jqXHR.status + ': ' + jqXHR.statusText, true);
			}
		}
	});
}

function deleteUser(username) {
	$.ajax({
		type : "DELETE",
		url : "./rest/Users/" + username,
		success : function(data, textStatus, jqXHR) {
			update();
			customAlert(data.message);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			if (jqXHR.responseJSON) {
				customAlert(jqXHR.responseJSON.message, true);
			} else {
				customAlert(jqXHR.status + ': ' + jqXHR.statusText, true);
			}
		}
	});
}

$("form").submit(function(e){
	e.preventDefault();
	createUser();
});

$('#username').text(Cookies.get('username'));

generateNewUsername();
update();
