var mongoose = require('mongoose');

// the host:port must match the location where you are running MongoDB
// the "myDatabase" part can be anything you like
mongoose.connect('mongodb://localhost:27017/myDatabase');

var Schema = mongoose.Schema;

var personSchema = new Schema({
	name: {type: String},
	email: {type: String},
	password: {type: String},
	pwConfirm: {type: String},
	school: {type: String},
	admin: {type: Boolean},
	favorited: {type: [String]}
    });

// export personSchema as a class called Person
module.exports = mongoose.model('Person', personSchema);

personSchema.methods.standardizeName = function() {
    this.name = this.name.toLowerCase();
    return this.name;
}