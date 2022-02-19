// Average rating of Chris Evans movies
agg_ce = function () {
	return db.movies.aggregate(
		
        { $unwind: "$cast" },
		
        {
			$match: {
				cast: { $eq: "Chris Evans", },
			},
		},

		{ $group: { _id: "$cast", avgRate: { $avg: "$imdb.rating" } } }
	);
};

// Number of movies by all actors ordered descent
agg_actors = function () {
	return db.movies.aggregate(

		{ $unwind: "$cast" },
		{ $group: { _id: "$cast", count: { $sum: 1 } } },
		{ $sort: { count: -1 } }
	);
};

// Movies rated more than 7.0 and have only one director
agg_rating_1director = function () {
	return db.movies.aggregate(
		{
			$unwind: "$directors",
		},
		{
			$group: {
				_id: "$title",
				avgRate: { $first: "$imdb.rating" },
				unique_directors: { $addToSet: "$directors" },
			},
		},
		{
			$project: {
				title: 1,
				avgRate: 1,
				countDirector: { $size: "$unique_directors" },
			},
		},
		{
			$match: {
				avgRate: {
					$gt: 7,
				},
				countDirector: { $eq: 1 },
			},
		}
	);
};

// Average rating of movies made in 1996
agg_avg_year = function () {
	return db.movies.aggregate(
		{
			$group: {
				_id: "$year",
				avgRate: { $avg: "$imdb.rating" },
			},
		},
		{
			$match: {
				_id: {
					$eq: 1996,
				},
			},
		}
	);
};


// List directors who have at least 2 adventure films rated lower than 6.5
agg_dir_ratings = function () {
	return db.movies.aggregate(
		
        { $unwind: "$directors", },

		{
			$match: {
				genres: {
					$elemMatch: { $eq: "Adventure" },
				},
			},
		},

		{ $unwind: "$genres", },
        
		{
			$group: {
				_id: "$directors",
				avgRate: { $avg: "$imdb.rating" },
				countAdventureGenre: { $sum: 1 },
			},
		},

		{
			$match: {
				avgRate: { $lt: 6.5, },
				countAdventureGenre: {	$gt: 2, },
			},
		}
	);
};

// The Fast and the Furious movie with the most comments
agg_most_comments = function () {
	return db.movies.aggregate(
		{
			$match: {
				title: { $regex: ".*The Fast and the Furious.*" },
			},
		},
		{$unwind: "$comments"},
		{
			$group: {
				_id: "$title",
				countComments: { $sum: 1 },
			},
		},
		{ $sort: { countComments: -1 } },
		{ $limit: 1 }
	);
};