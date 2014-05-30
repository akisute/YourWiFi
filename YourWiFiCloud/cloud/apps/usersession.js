"use strict";

exports.loginGETHandler = function(request, response) {
  if (Parse.User.current()) {
    response.redirect(303, '/');
  } else {
    response.render("login");
  }
}

exports.loginPOSTHandler = function(request, response) {
  Parse.User.logIn(request.body.username, request.body.password)
  .then(function() {
    response.redirect(303, '/');
  }).fail(function(error) {
    response.render("login", {
      error : error
    });
  });
}

exports.logoutGETHandler = function(request, response) {
  Parse.User.logOut();
  response.redirect(303, '/');
}
