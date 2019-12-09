var mongoose = require('mongoose');

// the host:port must match the location where you are running MongoDB
// the "myDatabase" part can be anything you like
mongoose.connect('mongodb://localhost:27017/myDatabase');

var Schema = mongoose.Schema;

var eventSchema = new Schema({
	eventOwner: {type: String, required: true},
	name: {type: String, required: true},
	date: {type: String, required: true},
	location: {type: String, required: true},
	food: {type: String, required: true},
	approved: {type: Boolean, required: true},
    });

// export personSchema as a class called Person
module.exports = mongoose.model('Event', eventSchema);
