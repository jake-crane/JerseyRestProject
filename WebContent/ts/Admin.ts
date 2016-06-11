///<reference path="def/jquery.d.ts" />
///<reference path="def/js-cookie.d.ts" />
///<reference path="IUser.ts" />
///<reference path="IMessage.ts" />
///<reference path="CustomAlert.ts" />
$.ajax({
    type: "GET",
    url: "./rest/SessionCheck/Admin",
    contentType: "application/json",
    success: function(data, textStatus, jqXHR: JQueryXHR) {
        if (!data.sessionValid && data.redirect) {
            window.location.href = data.redirectURL;
        }
    },
    error: function(jqXHR: JQueryXHR, textStatus, errorThrown) {
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
        type: "GET",
        url: "./rest/Users/",
        success: function(data, textStatus, jqXHR) {
            updateDOM(data);
        },
        error: function(jqXHR, textStatus, errorThrown) {
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
                + '<td>' + '<input type="text" maxlength="10" value="' + val.userId + '"/>' + '</td>'
                + '<td>' + '<input type="text" value="' + val.username + '"/>' + '</td>'
                + '<td>' + '<input type="text" value="' + val.firstName + '"/>' + '</td>'
                + '<td>' + '<input type="text" value="' + (val.middleName || '') + '"/>' + '</td>'
                + '<td>' + '<input type="text" value="' + val.lastName + '"/>' + '</td>'
                + '<td>' + '<input type="text" value="' + val.address + '"/>' + '</td>'
                + '<td>' + '<input type="text" value="' + (val.aptSuiteOther || '') + '"/>' + '</td>'
                + '<td>' + '<input type="text" value="' + val.city + '"/>' + '</td>'
                + '<td>' + '<input type="text" value="' + val.state + '"/>' + '</td>'
                + '<td>' + '<input type="text" value="' + val.zipCode + '"/>' + '</td>'
                + '<td>' + '<input type="text" value="' + val.phoneNumber + '"/>' + '</td>'
                + '<td>' + '<input type="text" value="' + (val.emailAddress || '') + '"/>' + '</td>'
                + '<td>' + val.birthDate + '</td>'
                + '<td><button id="delete_' + val.username + '" class="btn btn-warning btn-xs" data-toggle="confirmation" data-placement="top">Delete</button></td>'
                + '</tr>');
            var deleteButton = $('#delete_' + val.username);
            deleteButton.confirmation({
                onConfirm: function(event) {
                    deleteUser(val.username);
                },
                onCancel: function(event) { }
            });
        });
    }
}

$('#update').click(function(event) {
    update();
});

function createUser() {
    var myData = <User>{};
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
        type: "PUT",
        url: "./rest/Users/",
        contentType: "application/json",
        data: JSON.stringify(myData),
        success: function(data, textStatus, jqXHR) {
            update();
            generateNewUsername();
            customAlert(data.message);
        },
        error: function(jqXHR, textStatus, errorThrown) {
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
        type: "DELETE",
        url: "./rest/Users/" + username,
        //jqXHR: JQueryXHR, settings: JQueryAjaxSettings
        success: function(data: IMessage, textStatus, jqXHR: JQueryXHR) {
            update();
            customAlert(data.message);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            if (jqXHR.responseJSON) {
                customAlert(jqXHR.responseJSON.message, true);
            } else {
                customAlert(jqXHR.status + ': ' + jqXHR.statusText, true);
            }
        }
    });
}

$("form").submit(function(e) {
    e.preventDefault();
    createUser();
});

$('#username').text(Cookies.get('username'));

generateNewUsername();
update();
