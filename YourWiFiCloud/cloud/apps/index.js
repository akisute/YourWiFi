"use strict";

exports.GETHandler = function(request, response) {
  response.render("index", {
    user : Parse.User.current()
  });
}

exports.RedirectHandler = function(request, response) {
  response.redirect(303, '/');
}
