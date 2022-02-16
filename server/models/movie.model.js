const mongoose = require('mongoose')

const movieSchema = new mongoose.Schema({
    movieName:{
        type: String,
        required: true
    },
    yearOfApparition:{
        type: String,
        required: true
    },
    movieImageUrl:{
        type: String,
        required: true
    },
    movieDuration:{
        type: String, 
        required: true
    },
    movieDirector:{
        type: String,
        required: true
    },
    movieMainActors: [{
        type: String,
        required: true
    }],
    movieDescription:{
        type: String,
        required: true
    },
    movieUserRating:{
        type: mongoose.Types.Decimal128,
        default: 0 
    },
    movieNumberOfRatings:{
        type: Number,
        default: 0
    }
})

module.exports = mongoose.model('Movie', movieSchema)