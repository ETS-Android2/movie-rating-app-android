const express = require('express')
const res = require('express/lib/response')
const router = express.Router()

const Movie = require('../models/movie.model')


var admin = require("firebase-admin");

var serviceAccount = require("../fcm_config/<your fcm config file name>.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://notification-project-15175-default-rtdb.europe-west1.firebasedatabase.app"
});

var topic = 'general';


// getting all movies 
router.get('/', async (req, res) => {
    try {
        const movies = await Movie.find()
        res.json(movies)
    } catch(err) {
        res.status(500).json({message: err.message})
    }
})

// getting one movie based on id
router.get('/:id', getMovie, (req, res) => {

    res.send(res.movie)

})

// creating a movie
router.post('/', async (req, res) => {

    const movie = new Movie({
        movieName: req.body.movieName,
        yearOfApparition: req.body.yearOfApparition,
        movieImageUrl: req.body.movieImageUrl,
        movieDuration: req.body.movieDuration,
        movieDirector: req.body.movieDirector,
        movieMainActors: req.body.movieMainActors,
        movieDescription: req.body.movieDescription,
        movieUserRating: req.body.movieUserRating,
        movieNumberOfRatings: req.body.movieNumberOfRatings
    })

    try {
        const newMovie = await movie.save()
        res.status(201).json(newMovie)
        //console.log('That worked')

        var post_message = {
            notification: {
              title: 'New movie added',
              body:  req.body.movieName + ' was added to the application. Review it right now!'
            },
            topic: topic
          }
        admin.messaging().send(post_message)
            .then((response) => {
        // Response is a message ID string.
                console.log('Successfully sent message:', response);
            })
            .catch((error) => {
                console.log('Error sending message:', error);
            });
    } catch(err){
        res.status(400).json({message : err.message})
    }
})

// updating a movie based on id
router.patch('/:id', getMovie, async (req, res) => {
    
    if(req.body.movieUserRating != null){
        res.movie.movieUserRating = req.body.movieUserRating
    }
    if(req.body.movieNumberOfRatings != null){
        res.movie.movieNumberOfRatings = req.body.movieNumberOfRatings
    }

    try {
        const updatedMovie = await res.movie.save()
        var patch_message = {
            notification: {
              title: 'A movie was updated',
              body: res.movie.movieName + ' was just rated by a user. Check it out!'
            },
            topic: topic
          }
        admin.messaging().send(patch_message)
            .then((response) => {
        // Response is a message ID string.
                console.log('Successfully sent message:', response);
            })
            .catch((error) => {
                console.log('Error sending message:', error);
            });
        res.json(updatedMovie)
    } catch(err) {
        res.status(400).json({message : err.message})
    }

})

// delete a movie based on id
router.delete('/:id', getMovie, async (req, res) => {
    try {
        await res.movie.remove()
        res.json({message: 'Deleted movie'})
        var delete_message = {
            notification: {
              title: 'A movie was deleted',
              body:  'Oops... Unfortunately ' + res.movie.movieName + ' was deleted from the application.'
            },
            topic: topic
          }
        admin.messaging().send(delete_message)
            .then((response) => {
        // Response is a message ID string.
                console.log('Successfully sent message:', response);
            })
            .catch((error) => {
                console.log('Error sending message:', error);
            });
    } catch(err) { 
        res.status(500).json({message : err.message})
    }
})

async function getMovie(req, res, next) {
    let movie
    try{
        movie = await Movie.findById(req.params.id)
        if(movie == null){
            return res.status(404).json({message : "Movie not found"})
        }
    } catch(err){
        return res.status(500).json({message: err.message})
    }

    res.movie = movie
    next()

}

module.exports = router