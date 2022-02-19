// Search all Italian Horror movies
find_and = function () {
	return db.movies.find({
		$and: [
            
			{ countries: { $elemMatch: { $eq: "Italy" } } },
			{ genres: { $elemMatch: { $eq: "Horror" } } },
		],
	});
};

// Search all American movies of Action or Fantasy genres
find_and_or = function () {
	return db.movies.find({
		$and: [
			
            { languages: { $elemMatch: { $eq: "English" } } },
			
            {
				$or: [
					{
						genres: {
							$elemMatch: { $eq: "Fantasy" },
						},
					},
					{
						genres: {
							$elemMatch: { $eq: "Action" },
						},
					},
				],
			},

		],
	});
};

// Search all movies from Portugal released between august 2014 and august 2016
find_and_gte_lt = function () {
	return db.movies.find({
		$and: [
			
            { countries: { $elemMatch: { $eq: "Portugal" } } },
			
            {
				released: {
					$gte: ISODate("2014-08-01T00:00:00Z"),  // greater than or equal
					$lt: ISODate("2016-09-01T00:00:00Z"),   // less than
				},
			},

		],
	});
};

// Search all movies where Leonardo DiCaprio acted but not directed in
find_and_eq_ne = function () {
	return db.movies.find({
		$and: [
			{
                cast: {$elemMatch: { $eq: "Leonardo DiCaprio" }, },
			},
            
			{
				directors: { $elemMatch: { $ne: "Leonardo DiCaprio" }, },
			},
		],
	});
};

// List 5 movies directed and written by Stanley Kubrick
find_and_limit = function () {
	return db.movies.find({
        $and: [

            { writers: { $elemMatch: { $eq: "Stanley Kubrick (screenplay)" } }, },
            { directors: { $elemMatch: { $eq: "Stanley Kubrick" } }, },
		],
	}).limit(5);
};

// List all Thriller movies with less than 5.5 rating ordered by rating
find_eq_lt_sort = function () {
	return db.movies.find({
		$and: [

			{ genres: { $elemMatch: { $eq: "Thriller" }, }, },

			{ 'imdb.rating': { $lt: 5.5 }, },
		],
	}).sort( { 'imdb.rating': 1 } );
};