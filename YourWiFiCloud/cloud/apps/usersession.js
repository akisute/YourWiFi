"use strict";

exports.loginGETHandler = function(request, response) {
  if (Parse.User.current()) {
    res.redirect(303, '/');
  } else {
    response.render("login");
  }
}

exports.loginPOSTHandler = function(request, response) {
  Parse.User.logIn(request.body.username, request.body.password)
  .then(function() {
    response.redirect(303, '/');
  }).fail(function(error) {
    // TODO: add error parameters to login template
    response.render("login");
  });
}

exports.logoutGETHandler = function(request, response) {
  Parse.User.logOut();
  res.redirect(303, '/');
}
