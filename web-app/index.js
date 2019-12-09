// set up Express
var express = require('express');
var app = express();

// set up EJS
app.set('view engine', 'ejs');

// set up BodyParser
var bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: true }));

// import the Person class from Person.js
var Person = require('./Person.js');

// import the Event class from Event.js
var Event = require('./Event.js');

var currentUser = null;

var adminStatus = false;

var alert = require('alert-node');

var path = require('path');

/***************************************/

// route for creating a new person
// this is the action of the "create new person" form
app.use('/create', (req, res) => {
	if (req.body.adminCode === "admin") {
		console.log("New admin created!");
		adminStatus = true;
	} else {
		adminStatus = false;
	}
	// construct the Person from the form data which is in the request body
	var newPerson = new Person ({
		name: req.body.name,
		email: req.body.email,
		password: req.body.password,
		pwConfirm: req.body.passwordConfirm,
		school: req.body.school,
		admin: adminStatus,
	    });

	Person.findOne({email: newPerson.email}, (err, person) => {
		if (person != null) {
			console.log("Duplicate email found!");
			res.status(204).send();
			alert('Email already in use! Try again');
		} else {
			// save the person to the database
			newPerson.save( (err) => {
				if (err) {
				    res.type('html').status(200);
				    res.write('uh oh: ' + err);
				    console.log(err);
				    res.end();
				}
				else {
				    // display the "successfull created" page using EJS
				    res.render('created', {person : newPerson});
				}
			});
		}
	});
    }
);

app.use('/approveEvent/:eventId', function(req, res) {
	console.log('Approving your event!');
	Event.findOne({_id: req.params.eventId}, (err, event) => {
		event.approved = true;

		event.save( (err) => {
			if (err) {
				console.log(err);
				res.end();
			} else {
			// display the "successfull created" page using EJS
		    res.render('approvedEvent', {event : event});
			}
		});
	});
})

app.use('/disapproveEvent/:eventId', function(req, res) {
	console.log('Event Not Approved');

	Event.findOne({_id: req.params.eventId}, (err, event) => {
		event.approved = false;
		event.save( (err) => {
			if (err) {
				console.log(err);
				res.end();
			} else {
		    	res.render('disapprovedEvent', {event : event, email : event.eventOwner, sender : currentUser.email});
			}
		});
	});
});

// app.use('/disapproveEvent/:eventId', function(req,res) {
// 	console.log('Disapproving your event!');
// 	Event.findOne({_id: req.params.eventId}, (err, event) => {
// 		event.approved = false;

// 		event.save( (err) => {
// 			if (err) {
// 				console.log(err);
// 				res.end();
// 			} else {
// 		    	res.render('disapprovedEvent', {event : event});
// 			}
// 		});
// 	});
// })

app.use('/editEvent/:eventId', function(req, res) {
	var eventQuery = {};
	if (req.params.eventId) {
	    // if there's a name in the query parameter, use it here
	    eventQuery = { "_id" : req.params.eventId };
	}

	Event.find( eventQuery, (err, event) => {
		if (err) {
		    console.log('uh oh' + err);
		    res.json({});
		}
		else {
			console.log(event);
			res.render('editEvent', {event: event[0]});
		}

	});
});

app.use('/editedEvent/:eventId', (req, res) => {
	console.log('Editing your event!');
	Event.findOne({_id: req.params.eventId}, (err, event) => {
		event.name = req.body.name,
		event.date = req.body.date,
		event.location = req.body.location,
		event.food = req.body.foodType,
		event.approved = false;

		event.save( (err) => {
			if (err) {
				console.log(err);
				res.end();
			} else {
			// display the "successfull created" page using EJS
		    res.render('createdEvent', {event : event});
			}
		});
	});
});


app.use('/deleteEvent/:eventId', function(req, res) {
	console.log('Deleting event!!!!!!!');
	var eventQuery = {};
	if (req.params.eventId) {
	    // if there's a name in the query parameter, use it here
	    eventQuery = { "_id" : req.params.eventId };
	}

	Event.find( eventQuery, (err, event) => {
		if (err) {
		    console.log('uh oh' + err);
		    res.json({});
		}
		else {
			console.log(event);
			Event.deleteOne({_id: req.params.eventId}, (err, event) => {
				if (err) {
					console.log('Failed to delete!');
					res.end();
				}
				else {
					res.render('deletedEvent');
				}
			});
		}
	});
});

app.use('/createEventForm', (req, res) => {
	res.render('createEventForm', {person: currentUser})
})

app.use('/createEvent/:personEmail', (req, res) => {
	console.log(req.params.person);
	var newEvent = new Event ({
		eventOwner: req.params.personEmail,
		name: req.body.name,
		date: req.body.date,
		location: req.body.location,
		food: req.body.foodType,
		approved: false,
	  });

	// save the person to the database
	newEvent.save( (err) => {
		if (err) {
		    res.type('html').status(200);
		    res.write('uh oh: ' + err);
		    console.log(err);
		    res.end();
		}
		else {
		    // display the "successfull created" page using EJS
		    res.render('createdEvent', {event : newEvent});
		}
	    });
    }
);

// route for showing all the people
app.use('/all', (req, res) => {

	// find all the Person objects in the database
	Person.find( {}, (err, persons) => {
		if (err) {
		    res.type('html').status(200);
		    console.log('uh oh' + err);
		    res.write(err);
		}
		else {
		    if (persons.length == 0) {
			res.type('html').status(200);
			res.write('There are no people');
			res.end();
			return;
		    }
		    // use EJS to show all the people
		    res.render('all', { persons: persons });

		}
	    }).sort({ 'age': 'asc' }); // this sorts them BEFORE rendering the results
    });

// route for showing all the events
app.use('/allEvents', (req, res) => {

	// find all the Person objects in the database
	Event.find( {}, (err, events) => {
		if (err) {
		    res.type('html').status(200);
		    console.log('uh oh' + err);
		    res.write(err);
		}
		else {
		    if (adminStatus) {
				if (events.length == 0) {
		    		res.render('adminHomepage', {person : currentUser, events: []});
			    }
			    	res.render('adminHomepage', {person : currentUser, events: events});
			} else {
				if (events.length == 0) {
					res.render('userHomepage', { events: [] , person: currentUser});
			    }
			    	res.render('userHomepage', { events: events , person: currentUser});
			}

		}
	});
});

app.use('/login', (req, res) => {
	console.log("Looking for user!");
	var queryUser = {};
	if (req.body.username) {
		queryUser = {"email": req.body.username };
	}

	Person.find({}, (err, persons) => {
		if (err) {
			console.log("uh oh" + err);
			res.status(400).send('An error occured!');
		}
		else if (persons.length == 0) {
		    // no objects found, so send back empty json
		    res.send("No users!");
		}
		else if (persons.length > 0) {
			persons.forEach( (person) => {
				// If the email does not exist, then throw an alert and do not allow the user to login
				if (req.body.username == person.email) {
					console.log("Email found!");
					if (person.password != req.body.pwLogin) {
						console.log("Incorrect password!");
						res.status(204).send();
						alert('Incorrect username or password! Try again!');
					} else {
						currentUser = person;
						if (person.admin) {
							adminStatus = true;
						} else {
							adminStatus = false;
						}
					}
				}

			});

			if (currentUser == null) {
				console.log("Incorrect username!");
				res.status(204).send();
				alert('Incorrect username or password! Try again!');
			} else {
				Event.find( {}, (err, events) => {
					if (err) {
					    console.log('uh oh' + err);
					}
					else {
						console.log(adminStatus);
						// If the user is an admin then his/her homepage will differ from that of a regular user
						if (adminStatus) {
							if (events.length == 0) {
					    		res.render('adminHomepage', {person : currentUser, events: []});
						    }
						    	res.render('adminHomepage', {person : currentUser, events: events});
						} else {
							if (events.length == 0) {
					    		res.render('userHomepage', {person : currentUser, events: []});
						    }
						    	res.render('userHomepage', {person : currentUser, events: events});
						}
					}
				 });
			}
		}
	});
});

app.use('/logout', (req, res) => {
	console.log("Logging out!");
	currentUser = null;
	res.sendFile(path.join(__dirname, '/public', 'homepage.html'));
})

// used by app to get retrieve all events
app.use('/getAllEvents', (req, res) => {
	Event.find({"approved": true}, (err, events) => {
		res.json({"events": events});
	});
});

// used by app to retrieve user owned events
app.use('/getMyEvents', (req, res) => {
	var queryObject = {};
	if (req.query.id) {
		queryObject = {"eventOwner": req.query.id, "approved": true};
	}

	Event.find(queryObject, (err, events) => {
		if (err) {
			res.json({});
		} else if (events.length == 0) {
			res.json({});
		} else {
			res.json({"events": events});
		}
	});
});

// used by app to handle favoriting events
app.use('/favoriteEvent', (req, res) => {
	var queryObject = {};
	if (req.body.email) {
		queryObject = {"email": req.body.email};
	}

	var event_id = req.body.event_id;

	Person.findOne(queryObject, (err, person) => {
		if (err) {
			res.json({});
		} else {
			console.log(event_id);
			console.log(person.favorited.includes(event_id));
			if (person.favorited.includes(event_id)) {
				Person.updateOne(queryObject, { $pull: { favorited: event_id }}, (err, result) => {
						console.log(person.favorited);
						res.json({"result": "removed"});
				});
			} else {
				Person.updateOne(queryObject, { $push: { favorited: event_id}}, (err, result) => {
						console.log(person.favorited);
						res.json({"result": "added"});
				});
			}
		}
	});
});

// used by app to retrieve favorited events
app.use('/getFavoriteEvents', (req, res) => {
	var queryObject = {};
	if (req.query.email) {
		queryObject = {"email": req.query.email};
	}

	Person.findOne(queryObject, (err, person) => {
		if (err) {
			res.json({});
		} else {
			Event.find({_id: { $in : person.favorited }}, (err, events) => {
				if (err) {
					res.json({});
				} else {
					res.json({"events": events});
				}
			});
		}
	});
});

// route for accessing data via the web api
// to use this, make a request for /api to get an array of all Person objects
// or /api?name=[whatever] to get a single object
app.use('/api', (req, res) => {

	// construct the query object
	var queryObject = {};
	if (req.query.email) {
	    // if there's a name in the query parameter, use it here
	    queryObject = { "email" : req.query.email };
	}

	Person.find(queryObject, (err, persons) => {
		console.log(persons);
		if (err) {
		    console.log('uh oh' + err);
		    res.json({});
		}
		else if (persons.length == 0) {
		    // no objects found, so send back empty json
		    res.json({});
		}
		else if (persons.length == 1 ) {
		    var person = persons[0];
		    // send back a single JSON object
		    res.json( { "email" : person.email , "password" : person.password, "id": person._id } );
		}
		else {
			res.json({});
		}
	});

	// Person.find( queryObject, (err, persons) => {
	// 	console.log(persons);
	// 	if (err) {
	// 	    console.log('uh oh' + err);
	// 	    res.json({});
	// 	}
	// 	else if (persons.length == 0) {
	// 	    // no objects found, so send back empty json
	// 	    res.json({});
	// 	}
	// 	else if (persons.length == 1 ) {
	// 	    var person = persons[0];
	// 	    // send back a single JSON object
	// 	    res.json( { "name" : person.name , "password" : person.password } );
	// 	}
	// 	else {
	// 	    // construct an array out of the result
	// 	    var returnArray = [];
	// 	    persons.forEach( (person) => {
	// 		    returnArray.push( { "name" : person.name, "password" : person.password } );
	// 		});
	// 	    // send it back as JSON Array
	// 	    res.json(returnArray);
	// 	}
	//
	//     });
    });

app.use('/displayEvent/:eventId', function(req, res) {
	console.log('Displaying Event');
	var eventQuery = {};
	if (req.params.eventId) {
	   // if there's a name in the query parameter, use it here
	   eventQuery = { "_id" : req.params.eventId };
	}
	Event.find(eventQuery, (err, event) => {
		if (err) {
		console.log(err);
		res.json({});
	}
	else {
		console.log(event);
		res.render('displayEvent', {event : event});
	}
	});
});


/*************************************************/

app.use('/public', express.static('public'));

app.use('/', (req, res) => { res.redirect('/public/homepage.html'); } );

app.listen(3000,  () => {
	console.log('Listening on port 3000');
    });
