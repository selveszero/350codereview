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

// Set the current user to be null to indicate that it's not yet created. If the currentUser does not
// equal null, then that means it exists. 
var currentUser = null;

// The adminStatus indicates whether or not a given user is an admin account or not. The admin account, unlike regular accounts, 
// has the ability to approve or disapprove created events. 
var adminStatus = false;

var alert = require('alert-node');

var path = require('path');

/***************************************/

// Route for creating a new account
// This is triggered when a user clicks the "Create Account" button on the homepage.
app.use('/create', (req, res) => {
	// If the user enters the secret code to become an admin (i.e. "admin"), then they will successfully be created as an admin. 
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

	// Match the newly created person's email to the existing database to see if the email (which must be unique) already exists. 
	Person.findOne({email: newPerson.email}, (err, person) => {
		if (person != null) {
			// If a duplicate email is found, then send an error.
			console.log("Duplicate email found!");
			res.status(204).send();
			alert('Email already in use! Try again');
		} else {
			// If the email is not a duplicate, then save it to the database. 
			newPerson.save( (err) => {
				if (err) {
				    res.type('html').status(200);
				    res.write('uh oh: ' + err);
				    console.log(err);
				    res.end();
				}
				else {
				    // display the "successfully created" page using EJS
				    res.render('created', {person : newPerson});
				}
			});
		}
	});
    }
);

// Route for approving an event
// Triggered when an admin account clicks the "approve" button next to a given event
app.use('/approveEvent/:eventId', function(req, res) {
	console.log('Approving your event!');

	// Find the event based on its ID, and save it as an approved event.
	Event.findOne({_id: req.params.eventId}, (err, event) => {
		// The "approved" field is set to true if a given event is approved. By default it is false. 
		event.approved = true;

		// Once it is approved, save the event. 
		event.save( (err) => {
			if (err) {
				console.log(err);
				res.end();
			} else {
			// Display the approvedEvent page using EJS
		    res.render('approvedEvent', {event : event});
			}
		});
	});
})

// Route for disapproving an event
// Triggered when an admin account clicks the "disapprove" button next to a given event
app.use('/disapproveEvent/:eventId', function(req, res) {
	console.log('Event Not Approved');

	// Just like the approveEvent route above, find the event and set the "approved" field to false. 
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

// Route for rendering the editEvent EJS file
// Triggered when the owner of the event chooses to edit the event. 
app.use('/editEvent/:eventId', function(req, res) {
	var eventQuery = {};
	if (req.params.eventId) {
	    // if there's a name in the query parameter, use it here
	    eventQuery = { "_id" : req.params.eventId };
	}

	// Find the event by its ID and render the editEvent EJS file
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

// Route for updating the edited event's fields
// Triggered after the event owner finishes editing the event. 
app.use('/editedEvent/:eventId', (req, res) => {
	console.log('Editing your event!');
	// Find the event based on its ID and update its fields based on the user's new inputs. 
	Event.findOne({_id: req.params.eventId}, (err, event) => {
		event.name = req.body.name,
		event.date = req.body.date,
		event.location = req.body.location,
		event.food = req.body.foodType,
		event.approved = false;

		// Save the new event fields in the database
		event.save( (err) => {
			if (err) {
				console.log(err);
				res.end();
			} else {
			// Display the createdEvent EJS file after you've saved the event. 
		    res.render('createdEvent', {event : event});
			}
		});
	});
});

// Route for deleting an event 
// Triggered when the event owner presses the "delete" button next to a given event. 
app.use('/deleteEvent/:eventId', function(req, res) {
	console.log('Deleting event!');
	var eventQuery = {};
	if (req.params.eventId) {
	    // if there's a name in the query parameter, use it here
	    eventQuery = { "_id" : req.params.eventId };
	}

	// Find the event by its ID and delete it from the database
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
					// If the event is successfully deleted, render the deletedEvent EJS file
					res.render('deletedEvent');
				}
			});
		}
	});
});

// Route to render the createEventForm EJS file
// Triggered when a user clicks the "Create Event" button on the login homepage
app.use('/createEventForm', (req, res) => {
	res.render('createEventForm', {person: currentUser})
})

// Route to create a new event and tie it to its creator (which is important because only the creator of an event can edit or delete it)
// Triggered when a user creates and submits an event. 
app.use('/createEvent/:personEmail', (req, res) => {
	console.log(req.params.person);

	// Save the event's fields 
	var newEvent = new Event ({
		// "eventOwner" is effectively the ID of each event, since every email must be unique. With this field, we can determine which event belongs to which user. 
		eventOwner: req.params.personEmail,
		name: req.body.name,
		date: req.body.date,
		location: req.body.location,
		food: req.body.foodType,
		approved: false,
	  });

	// Save the event to the database
	newEvent.save( (err) => {
		if (err) {
		    res.type('html').status(200);
		    res.write('uh oh: ' + err);
		    console.log(err);
		    res.end();
		}
		else {
		    // If the event is successfully created, render the createdEvent EJS file
		    res.render('createdEvent', {event : newEvent});
		}
	    });
    }
);

// Route for showing all existing users in the database
app.use('/all', (req, res) => {

	// find all the Person objects in the database
	Person.find( {}, (err, persons) => {
		if (err) {
		    res.type('html').status(200);
		    console.log('uh oh' + err);
		    res.write(err);
		}
		else {
			// If there are no people in the database, then display an error. 
		    if (persons.length == 0) {
				res.type('html').status(200);
				res.write('There are no people');
				res.end();
				return;
		    }
		    // If there are people in the database, render the "all" EJS file, which displays all of the existing users in a list
		    res.render('all', { persons: persons });

		}
	    }).sort({ 'age': 'asc' }); // this sorts the users BEFORE rendering the results
    });

// Route to show all existing events
app.use('/allEvents', (req, res) => {

	// find all the Person objects in the database
	Event.find( {}, (err, events) => {
		if (err) {
		    res.type('html').status(200);
		    console.log('uh oh' + err);
		    res.write(err);
		}
		else {
			// If the user is an admin, then display all of the events on their homepage. An admin should see all events because it is responsible for approving or disapproving events. 
		    if (adminStatus) {
		    	// If there are no events, then render the homepage with no events. 
				if (events.length == 0) {
		    		res.render('adminHomepage', {person : currentUser, events: []});
			    }
			    	res.render('adminHomepage', {person : currentUser, events: events});
			} 
			// If the user is not an admin, then only display events belonging to that user. 
			else {
				// If there are no events, then render the homepage with no events. 
				if (events.length == 0) {
					res.render('userHomepage', { events: [] , person: currentUser});
			    }
			    	res.render('userHomepage', { events: events , person: currentUser});
			}

		}
	});
});

// Route to login to an existing user. 
// Triggered when the "Login" button is pressed on the homepage. 
app.use('/login', (req, res) => {
	console.log("Looking for user!");
	var queryUser = {};

	// Set the query parameter with which we'll find the user to be logged in
	if (req.body.username) {
		queryUser = {"email": req.body.username };
	}

	// Search through every user to see if the inputted username and password match any of the existing users in the database (if so, then the user will be logged in)
	Person.find({}, (err, persons) => {
		if (err) {
			console.log("uh oh" + err);
			res.status(400).send('An error occured!');
		}
		else if (persons.length == 0) {
		    // There's no one in the database, so send back an alert 
		    res.send("No users!");
		}
		else if (persons.length > 0) {
			persons.forEach( (person) => {
				// If the email does not exist, then throw an alert and do not allow the user to login
				if (req.body.username == person.email) {
					// If the email exists, then check if the password is correct.
					console.log("Email found!");
					if (person.password != req.body.pwLogin) {
						// If the password is not correct, then alert the user and prevent the login attempt. 
						console.log("Incorrect password!");
						res.status(204).send();
						alert('Incorrect username or password! Try again!');
					} else {
						// If the password matches, then set the currentUser (which is by default set to null) equal to the current person
						currentUser = person;
						// If the person is an admin, set their adminStatus field to true. 
						if (person.admin) {
							adminStatus = true;
						} else {
							adminStatus = false;
						}
					}
				}
			});

			// If the currentUser was never set to a person, then it will stay as the default of null. 
			// If the currentUser is null, then that means the email/username does not exist.
			if (currentUser == null) {
				console.log("Incorrect username!");
				res.status(204).send();
				alert('Incorrect username or password! Try again!');
			} 
			// If the currentUser is not null, then, at this point of the code, we have a valid user! (Since the check for the password match was above)
			// Thus, we can now login and display the user's homepage. 
			else {
				// Find all of the events that will be displayed on the user's homepage. 
				Event.find( {}, (err, events) => {
					if (err) {
					    console.log('uh oh' + err);
					}
					else {
						console.log(adminStatus);
						// If the user is an admin, then display all of the events. 
						if (adminStatus) {
							if (events.length == 0) {
					    		res.render('adminHomepage', {person : currentUser, events: []});
						    }
						    	res.render('adminHomepage', {person : currentUser, events: events});
						} 
						// If the user is not an admin, then display only the events belonging to that user. 
						else {
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

// Route to logout of an account. 
// Triggered when the "Logout" button is pressed on the user's homepage. 
app.use('/logout', (req, res) => {
	console.log("Logging out!");
	// Set the currentUser back to null so that you can't just log back in. 
	currentUser = null;
	// Go back to home. 
	res.sendFile(path.join(__dirname, '/public', 'homepage.html'));
})

// Route that's used by the Android app to retrieve all of the events. 
app.use('/getAllEvents', (req, res) => {
	Event.find({"approved": true}, (err, events) => {
		res.json({"events": events});
	});
});

// Route that's used by the Android app to get all of the events belonging to a given user.
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
