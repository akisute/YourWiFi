"use strict";

var express = require('express');
var app = express();

// Global app configuration section
app.set('views', 'cloud/views');
app.set('view engine', 'jade');
app.use(express.bodyParser());

// URL routing section
var app_index = require("cloud/apps/index.js");

app.get('/', app_index.handler);
app.get('/index', app_index.handler);
app.get('/index.html', app_index.handler);

// Begin listening
app.listen();
